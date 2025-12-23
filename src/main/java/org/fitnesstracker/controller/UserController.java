package org.fitnesstracker.controller;

import org.fitnesstracker.repository.entities.ExerciseEntity;
import org.fitnesstracker.repository.entities.UserEntity;
import org.fitnesstracker.service.UserService;
import org.fitnesstracker.service.models.User;
import org.fitnesstracker.util.InputHandler;

import java.util.List;
import java.util.Optional;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void handleInput(){
        boolean running = true;
        while(running){
            printMenu();
            int choice = InputHandler.getIntInput("Enter your choice: ");
            switch(choice){
                case 1 -> addUser();
                case 2 -> searchUserByUsername();
                case 3 -> searchUserById();
                case 4 -> searchUserByUserEmail();
                case 5 -> updateUser();
                case 6 -> deleteUser();
                case 7 -> getAllUsers();
                case 0 -> {
                    System.out.println("Leaving User Services");
                    running = false;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void getAllUsers() {
        try {
            List<User> users = userService.getAllModels();
            if (users.isEmpty()) {
                System.out.println("No users found in the system.");
            } else {
                System.out.println("=== ALL USERS ===");
                for(User user : users){
                    System.out.println(user);
                }
            }
        } catch (RuntimeException e) {
            // CATCH POINT: Persistence or Service Layer Error
            System.err.println("Error fetching all users: " + e.getMessage());
        }
    }

    private void searchUserById() {
        try {
            Integer userId = InputHandler.getIntInput("What is the user ID?");

            // Service method throws RuntimeException if the database access fails
            Optional<User> user = userService.getModelById(userId);

            if(user.isPresent()){
                System.out.println("=== USER FOUND ===");
                System.out.println(user.get());
            } else {
                // Not found is a business logic failure handled by Optional.empty()
                System.out.println("User with ID " + userId + " not found.");
            }
        } catch (RuntimeException e) {
            // CATCH POINT: Persistence or Service Layer Error (e.g., DB error, conversion error)
            System.err.println("Error searching by ID: " + e.getMessage());
        }
    }

    private void searchUserByUsername() {
        try {
            String userName = InputHandler.getStringInput("What is the username?");

            // Service method throws RuntimeException if the database access fails
            Optional<User> user = userService.getModelByUserName(userName);

            if(user.isPresent()){
                System.out.println("=== USER FOUND ===");
                System.out.println(user.get());
            } else {
                System.out.println("User with name '" + userName + "' not found.");
            }
        } catch (RuntimeException e) {
            // CATCH POINT: Persistence or Service Layer Error (e.g., DB error, conversion error)
            System.err.println("Error searching by username: " + e.getMessage());
        }
    }

    private void searchUserByUserEmail() {
        try {
            String userEmail = InputHandler.getStringInput("What is the user email?");

            // Service method throws RuntimeException if the database access fails
            Optional<User> user = userService.getModelByUserEmail(userEmail);

            if(user.isPresent()){
                System.out.println("=== USER FOUND ===");
                System.out.println(user.get());
            } else {
                System.out.println("User with email '" + userEmail + "' not found.");
            }
        } catch (RuntimeException e) {
            // CATCH POINT: Persistence or Service Layer Error (e.g., DB error, conversion error)
            System.err.println("Error searching by user email: " + e.getMessage());
        }
    }

    private void updateUser() {
        System.out.println("--- UPDATE USER ---");
        int userId = InputHandler.getIntInput("User ID to update: ");

        String newName = InputHandler.getStringInput("New Name: ");
        String newEmail = InputHandler.getStringInput("New Email: ");

        UserEntity updateEntity = new UserEntity();
        updateEntity.setUserId(userId);
        updateEntity.setUsername(newName);
        updateEntity.setEmail(newEmail);

        try {
            // The service handles checking for existence and performing the update
            UserEntity updated = userService.updateEntity(userId, updateEntity);

            if (updated != null) {
                System.out.println("User ID " + userId + " successfully updated.");
            }

        } catch (RuntimeException e) {
            // CATCH POINT: Not Found (business logic failure in service) or Persistence failure
            System.err.println("Failed to update User: " + e.getMessage());
        }
    }

    private void deleteUser() {
        try {
            Integer userId = InputHandler.getIntInput("User ID to delete: ");

            boolean wasDeleted = userService.deleteEntity(userId);

            if (wasDeleted) {
                System.out.println("User ID " + userId + " successfully deleted.");
            } else {
                // Not found is a business logic failure handled by the Service's return value
                System.out.println("User ID " + userId + " not found.");
            }
        } catch (RuntimeException e) {
            // CATCH POINT: Persistence Error (DB transaction failure)
            System.err.println("Failed to delete User: " + e.getMessage());
        }
    }
    private void printMenu(){
        System.out.println("=== USER SERVICES MENU ===");
        System.out.println("1. Add User");
        System.out.println("2. Search User by Username");
        System.out.println("3. Search User by Id");
        System.out.println("4. Search User by Email");
        System.out.println("5. Update User by Id");
        System.out.println("6. Delete User by Id");
        System.out.println("7. Get All Users");
        System.out.println("0. Exit");
    }

    private void addUser(){
        String userName = InputHandler.getStringInput("What is the new username?");
        String email = InputHandler.getStringInput("What is the user's email?");

        // Basic Input Validation
        if (userName == null || userName.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            System.out.println("Username and email cannot be empty.");
            return;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userName);
        userEntity.setEmail(email);

        try {
            // CRITICAL FIX: The Service either returns the ID or throws RuntimeException
            Integer newUserId = userService.createEntity(userEntity);

            // If the service returns, it was successful.
            if (newUserId != null) {
                System.out.println("New User Created with ID: " + newUserId);
            } else {
                // This branch should theoretically not be hit if Service/DAO are perfect
                System.err.println("User creation failed unexpectedly.");
            }

        } catch (RuntimeException e) {
            // CATCH POINT: Persistence Error (from DAO) or Business Error (from Service)
            System.err.println("Failed to create User: " + e.getMessage());
        }
    }
}
