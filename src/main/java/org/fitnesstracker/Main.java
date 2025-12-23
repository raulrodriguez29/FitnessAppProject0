package org.fitnesstracker;

import org.fitnesstracker.repository.DAO.UserDAO;
import org.fitnesstracker.repository.DAO.ExerciseDAO;
import org.fitnesstracker.repository.DAO.RoutineDAO;

import org.fitnesstracker.service.UserService;
import org.fitnesstracker.service.ExerciseService;
import org.fitnesstracker.service.RoutineService;

import org.fitnesstracker.controller.UserController;
import org.fitnesstracker.controller.ExerciseController;
import org.fitnesstracker.controller.RoutineController;

import org.fitnesstracker.util.InputHandler; // Assume InputHandler is available

public class Main {

    // Controllers are the only entry points we need to run the application
    private final UserController userController;
    private final ExerciseController exerciseController;
    private final RoutineController routineController;

    // Main constructor handles the entire application assembly (Manual Dependency Injection)
    public Main() {
        // --- 1. INSTANTIATE THE DATA ACCESS LAYER (DAO) ---
        System.out.println("Initializing DAO layer...");
        UserDAO userDAO = new UserDAO();
        ExerciseDAO exerciseDAO = new ExerciseDAO();
        RoutineDAO routineDAO = new RoutineDAO();

        // --- 2. INSTANTIATE THE SERVICE LAYER (Business Logic) ---
        System.out.println("Initializing Service layer...");

        // Services are constructed with their required DAO dependencies
        UserService userService = new UserService(userDAO);
        ExerciseService exerciseService = new ExerciseService(exerciseDAO);

        // RoutineService is complex: it needs its DAO and the ExerciseService
        // (to convert nested ExerciseEntities)
        RoutineService routineService = new RoutineService(routineDAO, exerciseService, userDAO);

        // --- 3. INSTANTIATE THE CONTROLLER LAYER (Presentation/Input) ---
        System.out.println("Initializing Controller layer...");

        // Controllers are constructed with their required Service dependencies
        this.userController = new UserController(userService);
        this.exerciseController = new ExerciseController(exerciseService);
        this.routineController = new RoutineController(routineService);
    }

    // --- Application Menu Logic ---
    public void startApplication() {
        System.out.println("\nApplication ready. Starting Main Menu.");
        boolean running = true;
        while(running) {
            System.out.println("\n=== FITNESS TRACKER MAIN MENU ===");
            System.out.println("1. Manage Users");
            System.out.println("2. Manage Exercises");
            System.out.println("3. Manage Routines");
            System.out.println("0. Exit Application");

            int choice = InputHandler.getIntInput("Enter your choice: ");

            switch(choice) {
                case 1 -> userController.handleInput();
                case 2 -> exerciseController.handleInput();
                case 3 -> routineController.handleInput();
                case 0 -> {
                    System.out.println("Application shutting down. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting Fitness Tracker Application...");
        try {
            Main app = new Main();
            app.startApplication();
        } catch (RuntimeException e) {
            // Catches errors that happen during initialization (e.g., DB connection failure in a DAO constructor)
            System.err.println("CRITICAL APPLICATION FAILURE: Could not start the application.");
            System.err.println("Cause: " + e.getMessage());
            e.printStackTrace();
        }
    }
}