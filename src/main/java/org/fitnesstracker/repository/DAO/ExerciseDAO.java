package org.fitnesstracker.repository.DAO;

import org.fitnesstracker.repository.entities.ExerciseEntity;
import org.fitnesstracker.util.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExerciseDAO implements DAOInterface<ExerciseEntity> {

    private Connection connection = ConnectionHandler.getConnection();

    @Override
    public Integer create(ExerciseEntity exerciseEntity) throws SQLException {

        String sql = "INSERT INTO exercises (exercises) VALUES (?) RETURNING exercise_id";

        try(PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, exerciseEntity.getName());

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt("exercise_id");
                }
            }
        }
        return null;
    }

    @Override
    public Optional<ExerciseEntity> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM exercises WHERE exercise_id = ?";

        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    ExerciseEntity exerciseEntity = new ExerciseEntity();
                    exerciseEntity.setExerciseId(rs.getInt("exercise_id"));
                    exerciseEntity.setName(rs.getString("name"));
                    exerciseEntity.setTargetMuscle(rs.getString("target_muscle"));
                    exerciseEntity.setCaloriesPerMin(rs.getInt("calories_per_min"));

                    return Optional.of(exerciseEntity);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ExerciseEntity> findAll() throws SQLException {
        List<ExerciseEntity> exercises = new ArrayList<>();

        String sql = "SELECT * FROM exercises";
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                ExerciseEntity exerciseEntity = new ExerciseEntity();
                exerciseEntity.setExerciseId(rs.getInt("exercise_id"));
                exerciseEntity.setName(rs.getString("name"));
                exerciseEntity.setTargetMuscle(rs.getString("target_muscle"));
                exerciseEntity.setCaloriesPerMin(rs.getInt("calories_per_min"));
                exercises.add(exerciseEntity);
            }
        }
        return exercises;
    }

    @Override
    public ExerciseEntity updateById(ExerciseEntity entity) throws SQLException {
        return null;
    }

    @Override
    public void deleteById(Integer id) throws SQLException {

    }

    public Optional<ExerciseEntity> findByExerciseName(String exerciseName) throws SQLException {
        String sql = "SELECT * FROM exercises WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, exerciseName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ExerciseEntity exerciseEntity = new ExerciseEntity();
                    exerciseEntity.setExerciseId(rs.getInt("exercise_id"));
                    exerciseEntity.setName(rs.getString("name"));
                    exerciseEntity.setTargetMuscle(rs.getString("target_muscle"));
                    exerciseEntity.setCaloriesPerMin(rs.getInt("calories_per_min"));

                    return Optional.of(exerciseEntity);
                }
            }
        }
        return Optional.empty();
    }
}
