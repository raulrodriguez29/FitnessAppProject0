package org.fitnesstracker.repository.DAO;

import org.fitnesstracker.repository.entities.ExerciseEntity;
import org.fitnesstracker.repository.entities.RoutineEntity;
import org.fitnesstracker.repository.entities.RoutineExerciseDetailsEntity;
import org.fitnesstracker.util.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoutineDAO implements DAOInterface<RoutineEntity> {

    // Helper method to map a ResultSet row to a RoutineEntity object
    private RoutineEntity mapRowToRoutine(ResultSet rs) throws SQLException {
        RoutineEntity routineEntity = new RoutineEntity();
        routineEntity.setRoutineId(rs.getInt("routine_id"));
        routineEntity.setUserId(rs.getInt("user_id"));
        routineEntity.setRoutineName(rs.getString("routine_name"));
        routineEntity.setDifficultyLevel(rs.getString("difficulty_level"));
        return routineEntity;
    }

    // --- 1. CREATE: Transactional Insert (Throws SQLException) ---
    @Override
    public Integer create(RoutineEntity routineEntity) throws SQLException {
        String routineSql = "INSERT INTO routines (user_id, routine_name, difficulty_level) VALUES (?, ?, ?) RETURNING routine_id";
        String detailSql = "INSERT INTO routine_exercises (routine_id, exercise_id, sets, reps) VALUES (?, ?, ?, ?)";

        Integer routineId = null;
        Connection connection = null;

        try {
            connection = ConnectionHandler.getConnection();
            connection.setAutoCommit(false); // START TRANSACTION

            // A. Insert Routine Header and get ID using executeQuery()
            try (PreparedStatement routineStmt = connection.prepareStatement(routineSql)) {
                routineStmt.setInt(1, routineEntity.getUserId());
                routineStmt.setString(2, routineEntity.getRoutineName());
                routineStmt.setString(3, routineEntity.getDifficultyLevel());

                try (ResultSet rs = routineStmt.executeQuery()) {
                    if (rs.next()) {
                        routineId = rs.getInt("routine_id");
                        routineEntity.setRoutineId(routineId);
                    } else {
                        throw new SQLException("Routine insertion failed, no ID obtained.");
                    }
                }
            }

            // B. Insert All Routine Exercise Details (Batch Insert)
            if (routineId != null && routineEntity.getExercises() != null && !routineEntity.getExercises().isEmpty()) {
                try (PreparedStatement detailStmt = connection.prepareStatement(detailSql)) {
                    for (RoutineExerciseDetailsEntity detail : routineEntity.getExercises()) {

                        // FIX: Check for primitive int ID <= 0 to avoid compiler error
                        if (detail.getExercise() == null || detail.getExercise().getExerciseId() <= 0) {
                            throw new SQLException("Cannot create routine: Nested Exercise ID is missing or invalid (must be > 0).");
                        }

                        detailStmt.setInt(1, routineId);
                        detailStmt.setInt(2, detail.getExercise().getExerciseId());
                        detailStmt.setInt(3, detail.getSets());
                        detailStmt.setInt(4, detail.getReps());
                        detailStmt.addBatch();
                    }
                    detailStmt.executeBatch();
                }
            }

            connection.commit(); // COMMIT TRANSACTION
            return routineId;

        } catch (SQLException e) {
            // Rollback logic is encapsulated here
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            // DAO throws the checked exception for the Service layer to handle
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    // --- 2. FIND BY ID (Loads Header Only) ---
    @Override
    public Optional<RoutineEntity> findById(Integer routineId) throws SQLException {
        String sql = "SELECT * FROM routines WHERE routine_id = ?";

        // No internal catch block; SQLException propagates
        try(Connection connection = ConnectionHandler.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, routineId);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return Optional.of(mapRowToRoutine(rs));
                }
            }
        }
        return Optional.empty();
    }

    // --- 3. GET EXERCISES (Many-to-Many Retrieval) ---
    public List<RoutineExerciseDetailsEntity> getExercisesForRoutine(int routineId) throws SQLException {
        List<RoutineExerciseDetailsEntity> details = new ArrayList<>();

        String sql = "SELECT re.sets, re.reps, " +
                "e.exercise_id, e.name, e.target_muscle, e.calories_per_min " +
                "FROM routine_exercises re " +
                "JOIN exercises e ON re.exercise_id = e.exercise_id " +
                "WHERE re.routine_id = ?";

        // No internal catch block; SQLException propagates
        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, routineId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ExerciseEntity linkedExercise = new ExerciseEntity(
                            rs.getInt("exercise_id"),
                            rs.getString("name"),
                            rs.getString("target_muscle"),
                            rs.getInt("calories_per_min")
                    );

                    RoutineExerciseDetailsEntity detail = new RoutineExerciseDetailsEntity(
                            linkedExercise,
                            rs.getInt("sets"),
                            rs.getInt("reps")
                    );
                    details.add(detail);
                }
            }
        }
        return details;
    }

    // --- 4. FIND ALL ---
    @Override
    public List<RoutineEntity> findAll() throws SQLException {
        List<RoutineEntity> routines = new ArrayList<>();
        String sql = "SELECT * FROM routines";

        // No internal catch block; SQLException propagates
        try(Connection connection = ConnectionHandler.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while(rs.next()){
                routines.add(mapRowToRoutine(rs));
            }
        }
        return routines;
    }

    // --- 5. UPDATE BY ID: Complex Transaction (Throws SQLException) ---
    @Override
    public RoutineEntity updateById(RoutineEntity routineEntity) throws SQLException {
        String updateRoutineSql = "UPDATE routines SET user_id = ?, routine_name = ?, difficulty_level = ? WHERE routine_id = ?";
        String deleteDetailsSql = "DELETE FROM routine_exercises WHERE routine_id = ?";
        String insertDetailsSql = "INSERT INTO routine_exercises (routine_id, exercise_id, sets, reps) VALUES (?, ?, ?, ?)";

        Connection connection = null;

        try {
            connection = ConnectionHandler.getConnection();
            connection.setAutoCommit(false); // START TRANSACTION

            Integer routineId = routineEntity.getRoutineId();
            if (routineId == null) {
                throw new SQLException("Cannot update routine; Routine ID is missing.");
            }

            // A. Update Routine Header
            try (PreparedStatement updateStmt = connection.prepareStatement(updateRoutineSql)) {
                updateStmt.setInt(1, routineEntity.getUserId());
                updateStmt.setString(2, routineEntity.getRoutineName());
                updateStmt.setString(3, routineEntity.getDifficultyLevel());
                updateStmt.setInt(4, routineId);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Update failed, routine ID " + routineId + " not found.");
                }
            }

            // B. Delete All Old Routine Exercise Details (Clean slate)
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteDetailsSql)) {
                deleteStmt.setInt(1, routineId);
                deleteStmt.executeUpdate();
            }

            // C. Insert All New Routine Exercise Details (Batch Re-Insert)
            List<RoutineExerciseDetailsEntity> details = routineEntity.getExercises();
            if (details != null && !details.isEmpty()) {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertDetailsSql)) {
                    for (RoutineExerciseDetailsEntity detail : details) {

                        // FIX: Check for primitive int ID <= 0 to avoid compiler error
                        if (detail.getExercise() == null || detail.getExercise().getExerciseId() <= 0) {
                            throw new SQLException("Cannot update routine: Nested Exercise ID is missing or invalid (must be > 0).");
                        }

                        insertStmt.setInt(1, routineId);
                        insertStmt.setInt(2, detail.getExercise().getExerciseId());
                        insertStmt.setInt(3, detail.getSets());
                        insertStmt.setInt(4, detail.getReps());
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
            }

            connection.commit(); // COMMIT TRANSACTION
            return routineEntity;

        } catch (SQLException e) {
            // Rollback logic is encapsulated here
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            // DAO throws the checked exception for the Service layer to handle
            throw e;

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    // --- 6. DELETE BY ID: Multi-Step Delete with Transaction (Throws SQLException) ---
    @Override
    public boolean deleteById(Integer routineId) throws SQLException {
        String deleteDetailsSql = "DELETE FROM routine_exercises WHERE routine_id = ?";
        String deleteRoutineSql = "DELETE FROM routines WHERE routine_id = ?";

        Connection connection = null;
        boolean deleted = false;

        try {
            connection = ConnectionHandler.getConnection();
            connection.setAutoCommit(false); // START TRANSACTION

            // A. Delete junction table entries first (Foreign Key safety)
            try (PreparedStatement detailsStmt = connection.prepareStatement(deleteDetailsSql)) {
                detailsStmt.setInt(1, routineId);
                detailsStmt.executeUpdate();
            }

            // B. Delete routine header
            int affectedRows;
            try (PreparedStatement routineStmt = connection.prepareStatement(deleteRoutineSql)) {
                routineStmt.setInt(1, routineId);
                affectedRows = routineStmt.executeUpdate();
                if (affectedRows > 0) deleted = true;
            }

            connection.commit(); // COMMIT TRANSACTION
            return deleted;

        } catch (SQLException e) {
            // Rollback logic is encapsulated here
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            // DAO throws the checked exception for the Service layer to handle
            throw e;

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    // --- 7. FIND BY ROUTINE NAME ---
    public Optional<RoutineEntity> findByRoutineName(String routineName) throws SQLException {
        String sql = "SELECT * FROM routines WHERE routine_name = ?";

        // No internal catch block; SQLException propagates
        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, routineName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToRoutine(rs));
                }
            }
        }
        return Optional.empty();
    }
}