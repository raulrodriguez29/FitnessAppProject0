package org.fitnesstracker.repository.DAO;

import org.fitnesstracker.repository.entities.RoutineEntity;
import org.fitnesstracker.repository.entities.UserEntity;
import org.fitnesstracker.util.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements DAOInterface<UserEntity> {

    private Connection connection = ConnectionHandler.getConnection();

    @Override
    public Integer create(UserEntity userEntity) throws SQLException {
            String sql = "INSERT INTO users (users) VALUES (?) RETURNING user_id";

            try(PreparedStatement stmt = connection.prepareStatement(sql)){

                stmt.setString(1, userEntity.getUsername());

                try(ResultSet rs = stmt.executeQuery()){
                    if(rs.next()){
                        return rs.getInt("user_id");
                    }
                }
            }
            return null;
    }

    @Override
    public Optional<UserEntity> findById(Integer userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setUserId(rs.getInt("user_id"));
                    userEntity.setUsername(rs.getString("username"));
                    userEntity.setEmail(rs.getString("email"));
                    userEntity.setJoinDate(rs.getTimestamp("join_date").toLocalDateTime());

                    return Optional.of(userEntity);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserEntity> findAll() throws SQLException {
        List<UserEntity> users = new ArrayList<>();

        String sql = "SELECT * FROM users";
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                UserEntity userEntity = new UserEntity();
                userEntity.setUserId(rs.getInt("user_id"));
                userEntity.setUsername(rs.getString("username"));
                userEntity.setEmail(rs.getString("email"));
                userEntity.setJoinDate(rs.getTimestamp("join_date").toLocalDateTime());
                users.add(userEntity);
            }
        }
        return users;
    }

    @Override
    public UserEntity updateById(UserEntity entity) throws SQLException {
        return null;
    }

    @Override
    public void deleteById(Integer id) throws SQLException {

    }

    public Optional<UserEntity> findByUserName(String userName) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setUserId(rs.getInt("user_id"));
                    userEntity.setUsername(rs.getString("username"));
                    userEntity.setEmail(rs.getString("email"));
                    userEntity.setJoinDate(rs.getTimestamp("join_date").toLocalDateTime());

                    return Optional.of(userEntity);
                }
            }
        }
        return Optional.empty();
    }
}
