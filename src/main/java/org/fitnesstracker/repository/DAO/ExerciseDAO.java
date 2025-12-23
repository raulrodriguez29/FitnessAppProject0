package org.fitnesstracker.repository.DAO;

import org.fitnesstracker.repository.entities.ExerciseEntity;
import org.fitnesstracker.util.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExerciseDAO implements DAOInterface<ExerciseEntity> {

    private ExerciseEntity mapRowToExercise(ResultSet rs) throws SQLException {
        ExerciseEntity entity = new ExerciseEntity();
        entity.setExerciseId(rs.getInt("exercise_id"));
        entity.setName(rs.getString("name"));
        entity.setTargetMuscle(rs.getString("target_muscle"));
        entity.setCaloriesPerMin(rs.getInt("calories_per_min"));
        return entity;
    }

    // --- 1. CREATE ---
    @Override
    public Integer create(ExerciseEntity exerciseEntity) throws SQLException {
        String sql = "INSERT INTO exercises (name, target_muscle, calories_per_min) VALUES (?, ?, ?) RETURNING exercise_id";

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, exerciseEntity.getName());
            stmt.setString(2, exerciseEntity.getTargetMuscle());
            stmt.setInt(3, exerciseEntity.getCaloriesPerMin());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("exercise_id");
                }
            }
        }
        return null;
    }

    // --- 2. FIND BY ID ---
    @Override
    public Optional<ExerciseEntity> findById(Integer exerciseId) throws SQLException {
        String sql = "SELECT * FROM exercises WHERE exercise_id = ?";

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exerciseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToExercise(rs));
                }
            }
        }
        return Optional.empty();
    }

    // --- Custom: FIND BY NAME ---
    public Optional<ExerciseEntity> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM exercises WHERE name = ?";

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToExercise(rs));
                }
            }
        }
        return Optional.empty();
    }

    // --- 3. FIND ALL ---
    @Override
    public List<ExerciseEntity> findAll() throws SQLException {
        List<ExerciseEntity> exercises = new ArrayList<>();
        String sql = "SELECT * FROM exercises";

        try (Connection connection = ConnectionHandler.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                exercises.add(mapRowToExercise(rs));
            }
        }
        return exercises;
    }

    // --- 4. UPDATE BY ID ---
    @Override
    public ExerciseEntity updateById(ExerciseEntity exerciseEntity) throws SQLException {
        String sql = "UPDATE exercises SET name = ?, target_muscle = ?, calories_per_min = ? WHERE exercise_id = ?";

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, exerciseEntity.getName());
            stmt.setString(2, exerciseEntity.getTargetMuscle());
            stmt.setInt(3, exerciseEntity.getCaloriesPerMin());
            stmt.setInt(4, exerciseEntity.getExerciseId());

            int rowsAffected = stmt.executeUpdate();
            return (rowsAffected > 0) ? exerciseEntity : null;
        }
    }

    // --- 5. DELETE BY ID ---
    @Override
    public boolean deleteById(Integer exerciseId) throws SQLException {
        String sql = "DELETE FROM exercises WHERE exercise_id = ?";

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exerciseId);
            return stmt.executeUpdate() > 0;
        }
    }
}