package org.fitnesstracker.service;

import org.fitnesstracker.repository.DAO.UserDAO;
import org.fitnesstracker.repository.entities.UserEntity;
import org.fitnesstracker.service.interfaces.ServiceInterface;
import org.fitnesstracker.service.models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService implements ServiceInterface<UserEntity, User> {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Integer createEntity(UserEntity userEntity) {
        try{
            return userDAO.create(userEntity);

        }catch(SQLException e){

            throw new RuntimeException("Database error creating user entity.", e);
        }
    }

    @Override
    public Optional<UserEntity> getEntityById(Integer userId) {
        try{
            Optional<UserEntity> userEntity = userDAO.findById(userId);

            return userEntity;
        }catch(SQLException e){
            throw new RuntimeException("Database error retrieving User by ID: " + userId, e);
        }
    }

    @Override
    public List<UserEntity> getAllEntities() {
        try{
            List<UserEntity> userEntities = userDAO.findAll();
            return userEntities;
        }catch(SQLException e){

            throw new RuntimeException("Database error retrieving all User Entities.", e);
        }
    }

    @Override
    public UserEntity updateEntity(Integer userId, UserEntity newEntity) {
        Optional<UserEntity> existingUserOptional = getEntityById(userId);

        if (existingUserOptional.isEmpty()) {
            throw new RuntimeException("Update failed: UserEntity with userId " + userId + " not found.");
        }

        UserEntity existingUserEntity = existingUserOptional.get();
        existingUserEntity.setUsername(newEntity.getUsername());
        existingUserEntity.setEmail(newEntity.getEmail());

        try {
            UserEntity updatedEntity = userDAO.updateById(existingUserEntity);

            return updatedEntity;

        } catch (SQLException e) {
            throw new RuntimeException("Database error updating UserEntity with userId " + userId, e);
        }
    }

    @Override
    public boolean deleteEntity(Integer userId) {
        try {
            boolean wasDeleted = userDAO.deleteById(userId);

            if (!wasDeleted) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Database error deleting UserEntity with userId: " + userId, e);
        }
    }

    @Override
    public Optional<User> convertEntityToModel(UserEntity userEntity) {
        User user = new User();
        user.setUserId(userEntity.getUserId());
        user.setUsername(userEntity.getUsername());
        user.setEmail(userEntity.getEmail());
        user.setJoinDate(userEntity.getJoinDate());
        return Optional.of(user);
    }

    @Override
    public Optional<User> getModelById(Integer userId) {
        Optional<UserEntity> userEntityOptional = getEntityById(userId);

        if (userEntityOptional.isEmpty()) {
            throw new RuntimeException("UserEntity not found for userId: " + userId);
        }

        UserEntity userEntity = userEntityOptional.get();
        Optional<User> userOptional = convertEntityToModel(userEntity);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("UserEntity conversion failed for userId: " + userId);
        }

        return userOptional;
    }

    public Optional<User> getModelByUserName(String userName) {
        Optional<UserEntity> userEntityOptional = getEntityByUserName(userName);

        if (userEntityOptional.isEmpty()) {
            throw new RuntimeException("UserEntity not found for username: " + userName);
        }

        UserEntity userEntity = userEntityOptional.get();
        Optional<User> userOptional = convertEntityToModel(userEntity);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("UserEntity conversion failed for username: " + userName);
        }

        return userOptional;

    }

    public Optional<User> getModelByUserEmail(String userEmail) {
        Optional<UserEntity> userEntityOptional = getEntityByUserEmail(userEmail);
        if (userEntityOptional.isEmpty()) {
            throw new RuntimeException("UserEntity not found for user email: " + userEmail);
        }

        UserEntity userEntity = userEntityOptional.get();
        Optional<User> userOptional = convertEntityToModel(userEntity);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("UserEntity conversion failed for username: " + userEmail);
        }

        return userOptional;
    }

    private Optional<UserEntity> getEntityByUserEmail(String userEmail) {
        try {
            return userDAO.findByEmail(userEmail);
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding user by user email " + userEmail, e);
        }
    }

    private Optional<UserEntity> getEntityByUserName(String userName) {
        try {
            return userDAO.findByUserName(userName);
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding user by username " + userName, e);
        }
    }

    public List<User> getAllModels() {
        List<UserEntity> userEntities = getAllEntities();
        List<User> users = new ArrayList<>();
        for(UserEntity userEntity : userEntities){
            Optional<User> user = convertEntityToModel(userEntity);
            if(user.isPresent()){
                users.add(user.get());
            }
        }
        return users;
    }
}
