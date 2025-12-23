package org.fitnesstracker.controller;

import org.fitnesstracker.repository.entities.ExerciseEntity;
import org.fitnesstracker.service.ExerciseService;
import org.fitnesstracker.service.models.Exercise;
import org.fitnesstracker.util.InputHandler;

import java.util.List;
import java.util.Optional;

public class ExerciseController {

    private final ExerciseService exerciseService;

    // Dependency Injection via Constructor
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    public void handleInput(){
        boolean running = true;
        while(running){
            printMenu();
            int choice = InputHandler.getIntInput("Enter your choice: ");
            switch(choice){
                case 1 -> addExercise();
                case 2 -> searchExerciseByName();
                case 3 -> searchExerciseById();
                case 4 -> getAllExercises();
                case 5 -> updateExercise();
                case 6 -> deleteExercise();
                case 0 -> {
                    System.out.println("Leaving Exercise Services");
                    running = false;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void printMenu(){
        System.out.println("=== EXERCISE SERVICES MENU ===");
        System.out.println("1. Add New Exercise");
        System.out.println("2. Search Exercise by Name");
        System.out.println("3. Search Exercise by Id");
        System.out.println("4. Get All Exercises");
        System.out.println("5. Update Exercise");
        System.out.println("6. Delete Exercise");
        System.out.println("0. Exit");
    }

    // --- 1. ADD EXERCISE ---
    private void addExercise(){
        String name = InputHandler.getStringInput("What is the new Exercise name?");
        String muscle = InputHandler.getStringInput("What is the Target Muscle?");
        int calories = InputHandler.getIntInput("What are the Calories/Min?");

        if (name.trim().isEmpty() || muscle.trim().isEmpty() || calories <= 0) {
            System.out.println("Invalid input. Name and Muscle cannot be empty, Calories must be positive.");
            return;
        }

        ExerciseEntity exerciseEntity = new ExerciseEntity();
        exerciseEntity.setName(name);
        exerciseEntity.setTargetMuscle(muscle);
        exerciseEntity.setCaloriesPerMin(calories);

        try {
            // Service returns ID or throws RuntimeException
            Integer newExerciseId = exerciseService.createEntity(exerciseEntity);

            if (newExerciseId != null) {
                System.out.println("New Exercise Created with ID: " + newExerciseId);
            } else {
                System.err.println("Exercise creation failed unexpectedly.");
            }

        } catch (RuntimeException e) {
            System.err.println("Failed to create Exercise: " + e.getMessage());
        }
    }

    // --- 2. SEARCH BY NAME ---
    private void searchExerciseByName() {
        try {
            String exerciseName = InputHandler.getStringInput("What is the exercise name?");

            Optional<Exercise> exercise = exerciseService.getModelByExerciseName(exerciseName);

            if(exercise.isPresent()){
                System.out.println("=== EXERCISE FOUND ===");
                System.out.println(exercise.get());
            } else {
                System.out.println("Exercise with name '" + exerciseName + "' not found.");
            }
        } catch (RuntimeException e) {
            System.err.println("Error searching by name: " + e.getMessage());
        }
    }

    // --- 3. SEARCH BY ID ---
    private void searchExerciseById() {
        try {
            Integer exerciseId = InputHandler.getIntInput("What is the exercise ID?");

            // Service method throws RuntimeException if the database access fails
            Optional<Exercise> exercise = exerciseService.getModelById(exerciseId);

            if(exercise.isPresent()){
                System.out.println("=== EXERCISE FOUND ===");
                System.out.println(exercise.get());
            } else {
                System.out.println("Exercise with ID " + exerciseId + " not found.");
            }
        } catch (RuntimeException e) {
            System.err.println("Error searching by ID: " + e.getMessage());
        }
    }

    // --- 4. GET ALL EXERCISES ---
    private void getAllExercises() {
        try {
            List<Exercise> exercises = exerciseService.getAllModels();
            if (exercises.isEmpty()) {
                System.out.println("No exercises found in the system.");
            } else {
                System.out.println("=== ALL EXERCISES ===");
                exercises.forEach(e -> System.out.printf("ID: %d, Name: %s, Muscle: %s, Calories/Min: %d\n",
                        e.getExerciseId(), e.getName(),
                        e.getTargetMuscle(), e.getCaloriesPerMin()));
            }
        } catch (RuntimeException e) {
            // CATCH POINT: Persistence or Service Layer Error
            System.err.println("Error fetching all exercises: " + e.getMessage());
        }
    }

    // --- 5. UPDATE EXERCISE ---
    private void updateExercise() {
        System.out.println("--- UPDATE EXERCISE ---");
        int exerciseId = InputHandler.getIntInput("Exercise ID to update: ");

        String newName = InputHandler.getStringInput("New Name: ");
        String newMuscle = InputHandler.getStringInput("New Target Muscle: ");
        int newCalories = InputHandler.getIntInput("New Calories/Min: ");

        ExerciseEntity updateEntity = new ExerciseEntity();
        updateEntity.setExerciseId(exerciseId);
        updateEntity.setName(newName);
        updateEntity.setTargetMuscle(newMuscle);
        updateEntity.setCaloriesPerMin(newCalories);

        try {
            // The service handles checking for existence and performing the update
            ExerciseEntity updated = exerciseService.updateEntity(exerciseId, updateEntity);

            if (updated != null) {
                System.out.println("Exercise ID " + exerciseId + " successfully updated.");
            }

        } catch (RuntimeException e) {
            System.err.println("Failed to update Exercise: " + e.getMessage());
        }
    }

    // --- 6. DELETE EXERCISE ---
    private void deleteExercise() {
        try {
            Integer exerciseId = InputHandler.getIntInput("Exercise ID to delete: ");

            boolean wasDeleted = exerciseService.deleteEntity(exerciseId);

            if (wasDeleted) {
                System.out.println("Exercise ID " + exerciseId + " successfully deleted.");
            } else {
                System.out.println("Exercise ID " + exerciseId + " not found.");
            }

        } catch (RuntimeException e) {
            // CATCH POINT: Persistence Error (DB transaction failure)
            System.err.println("Failed to delete Exercise: " + e.getMessage());
        }
    }
}