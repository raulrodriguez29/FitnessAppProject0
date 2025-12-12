package org.fitnesstracker.repository.DAO;

import org.fitnesstracker.repository.entities.RoutineEntity;
import org.fitnesstracker.util.ConnectionHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RoutineDAO implements DAOInterface<RoutineEntity> {

    private Connection connection = ConnectionHandler.getConnection();

    @Override
    public Integer create(RoutineEntity entity) throws SQLException {
        return 0;
    }

    @Override
    public Optional<RoutineEntity> findById(Integer id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<RoutineEntity> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public RoutineEntity updateById(RoutineEntity entity) throws SQLException {
        return null;
    }

    @Override
    public void deleteById(Integer id) throws SQLException {

    }
}
