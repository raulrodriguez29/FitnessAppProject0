package org.fitnesstracker.repository.DAO;

import org.fitnesstracker.repository.entities.UserEntity;
import org.fitnesstracker.util.ConnectionHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserDAO implements DAOInterface<UserEntity> {

    private Connection connection = ConnectionHandler.getConnection();

    @Override
    public Integer create(UserEntity entity) throws SQLException {
        return 0;
    }

    @Override
    public Optional<UserEntity> findById(Integer id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<UserEntity> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public UserEntity updateById(UserEntity entity) throws SQLException {
        return null;
    }

    @Override
    public void deleteById(Integer id) throws SQLException {

    }
}
