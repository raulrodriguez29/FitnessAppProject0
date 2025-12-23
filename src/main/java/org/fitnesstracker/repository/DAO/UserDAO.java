package org.fitnesstracker.repository.DAO;

import org.fitnesstracker.repository.entities.UserEntity;
import org.fitnesstracker.util.ConnectionHandler;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements DAOInterface<UserEntity> {

    // --- Helper Method for Mapping User Entity ---
    private UserEntity mapRowToUser(ResultSet rs) throws SQLException {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(rs.getInt("user_id"));
        userEntity.setUsername(rs.getString("username"));
        userEntity.setEmail(rs.getString("email"));

        Timestamp joinTimestamp = rs.getTimestamp("join_date");
        if (joinTimestamp != null) {
            userEntity.setJoinDate(joinTimestamp.toLocalDateTime());
        }
        return userEntity;
    }

    // --- 1. CREATE ---
    @Override
    public Integer create(UserEntity userEntity) throws SQLException {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?) RETURNING user_id";

        try(Connection connection = ConnectionHandler.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, userEntity.getUsername());
            stmt.setString(2, userEntity.getEmail());

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt("user_id");
                }
            }
        }
        return null;
    }

    // --- 2. FIND BY ID ---
    @Override
    public Optional<UserEntity> findById(Integer userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("ID from DB: " + rs.getInt("user_id"));
                    System.out.println("Email from DB: " + rs.getString("email"));
                    System.out.println("Date from DB: " + rs.getTimestamp("join_date"));
                    return Optional.of(mapRowToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    // --- 6. FIND BY USERNAME ---
    public Optional<UserEntity> findByUserName(String userName) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<UserEntity> findByEmail(String userEmail) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        }
        return Optional.empty();
    }


    // --- 3. FIND ALL ---
    @Override
    public List<UserEntity> findAll() throws SQLException {
        List<UserEntity> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try(Connection connection = ConnectionHandler.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while(rs.next()){
                users.add(mapRowToUser(rs));
            }
        }
        return users;
    }

    // --- 4. UPDATE BY ID ---
    @Override
    public UserEntity updateById(UserEntity userEntity) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ? WHERE user_id = ?";

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userEntity.getUsername());
            stmt.setString(2, userEntity.getEmail());
            stmt.setInt(3, userEntity.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return (rowsAffected > 0) ? userEntity : null;
        }
    }

    // --- 5. DELETE BY ID ---
    @Override
    public boolean deleteById(Integer userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

}