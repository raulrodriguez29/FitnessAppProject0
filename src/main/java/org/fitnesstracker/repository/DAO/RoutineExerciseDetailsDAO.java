package org.fitnesstracker.repository.DAO;

import org.fitnesstracker.repository.entities.RoutineExerciseDetailsEntity;
import org.fitnesstracker.util.ConnectionHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RoutineExerciseDetailsDAO implements DAOInterface<RoutineExerciseDetailsEntity> {

    private Connection connection = ConnectionHandler.getConnection();

    @Override
    public Integer create(RoutineExerciseDetailsEntity entity) throws SQLException {
        return 0;
    }

    @Override
    public Optional<RoutineExerciseDetailsEntity> findById(Integer id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<RoutineExerciseDetailsEntity> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public RoutineExerciseDetailsEntity updateById(RoutineExerciseDetailsEntity entity) throws SQLException {
        return null;
    }

    @Override
    public void deleteById(Integer id) throws SQLException {

    }
}
