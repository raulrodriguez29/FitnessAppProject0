package org.fitnesstracker.repository.DAO;

import org.fitnesstracker.repository.entities.ExerciseEntity;
import org.fitnesstracker.util.ConnectionHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExerciseDAO implements DAOInterface<ExerciseEntity> {

    private Connection connection = ConnectionHandler.getConnection();

    @Override
    public Integer create(ExerciseEntity entity) throws SQLException {
        return 0;
    }

    @Override
    public Optional<ExerciseEntity> findById(Integer id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<ExerciseEntity> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public ExerciseEntity updateById(ExerciseEntity entity) throws SQLException {
        return null;
    }

    @Override
    public void deleteById(Integer id) throws SQLException {

    }
}
