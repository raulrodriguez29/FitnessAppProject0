package org.fitnesstracker.repository.DAO;

import org.fitnesstracker.repository.entities.RoutineEntity;
import org.fitnesstracker.util.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoutineDAO implements DAOInterface<RoutineEntity> {

    private Connection connection = ConnectionHandler.getConnection();

    @Override
    public Integer create(RoutineEntity routineEntity) throws SQLException {
        String sql = "INSERT INTO routines (routines) VALUES (?) RETURNING routine_id";

        try(PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, routineEntity.getRoutineName());

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt("routine_id");
                }
            }
        }
        return null;
    }

    @Override
    public Optional<RoutineEntity> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM routine WHERE routine_id = ?";

        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    RoutineEntity routineEntity = new RoutineEntity();
                    routineEntity.setRoutineId(rs.getInt("routine_id"));
                    routineEntity.setUserId(rs.getInt("user_id"));
                    routineEntity.setRoutineName(rs.getString("routine_name"));
                    routineEntity.setDifficultyLevel(rs.getString("difficulty_level"));

                    return Optional.of(routineEntity);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<RoutineEntity> findAll() throws SQLException {
        List<RoutineEntity> routines = new ArrayList<>();

        String sql = "SELECT * FROM routines";
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                RoutineEntity routineEntity = new RoutineEntity();
                routineEntity.setRoutineId(rs.getInt("routine_id"));
                routineEntity.setUserId(rs.getInt("user_id"));
                routineEntity.setRoutineName(rs.getString("routine_name"));
                routineEntity.setDifficultyLevel(rs.getString("difficulty_level"));
                routines.add(routineEntity);
            }
        }
        return routines;
    }

    @Override
    public RoutineEntity updateById(RoutineEntity entity) throws SQLException {
        return null;
    }

    @Override
    public void deleteById(Integer id) throws SQLException {

    }

    public Optional<RoutineEntity> findByRoutineName(String routineName) throws SQLException {
        String sql = "SELECT * FROM routines WHERE routine_name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, routineName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RoutineEntity routineEntity = new RoutineEntity();
                    routineEntity.setRoutineId(rs.getInt("routine_id"));
                    routineEntity.setUserId(rs.getInt("user_id"));
                    routineEntity.setRoutineName(rs.getString("routine_name"));
                    routineEntity.setDifficultyLevel(rs.getString("difficulty_level"));

                    return Optional.of(routineEntity);
                }
            }
        }
        return Optional.empty();
    }
}
