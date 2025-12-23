package org.fitnesstracker.controller;

import org.fitnesstracker.repository.entities.RoutineEntity;
import org.fitnesstracker.repository.entities.RoutineExerciseDetailsEntity;
import org.fitnesstracker.repository.entities.ExerciseEntity;
import org.fitnesstracker.service.RoutineService;
import org.fitnesstracker.service.models.Routine;
import org.fitnesstracker.util.InputHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoutineController {

    private final RoutineService routineService;

    // Dependency Injection via Constructor
    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    public void handleInput(){
        boolean running = true;
        while(running){
            printMenu();
            int choice = InputHandler.getIntInput("Enter your choice: ");
            switch(choice){
                case 1 -> addRoutine();
                case 2 -> searchRoutineById();
                case 3 -> getAllRoutines();
                case 4 -> updateRoutine();
                case 5 -> deleteRoutine();
                case 0 -> {
                    System.out.println("Leaving Routine Services");
                    running = false;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void printMenu(){
        System.out.println("=== ROUTINE SERVICES MENU ===");
        System.out.println("1. Add New Routine");
        System.out.println("2. Search Routine by Id (View Details)");
        System.out.println("3. Get All Routines (Header Only)");
        System.out.println("4. Update Routine");
        System.out.println("5. Delete Routine");
        System.out.println("0. Exit");
    }

    // --- 1. ADD ROUTINE (Complex Input) ---
    private void addRoutine(){
        System.out.println("--- NEW ROUTINE SETUP ---");
        String name = InputHandler.getStringInput("Routine Name: ");

        // 1. Get and Clean input
        String difficulty = InputHandler.getStringInput("Difficulty Level (EASY/MEDIUM/HARD): ").trim().toUpperCase();

        // 2. Immediate Validation Check
        if (!difficulty.equals("EASY") && !difficulty.equals("MEDIUM") && !difficulty.equals("HARD")) {
            System.out.println(">>> Error: Invalid difficulty. Please enter EASY, MEDIUM, or HARD.");
            return; // Stop here so they have to start over
        }

        int userId = InputHandler.getIntInput("User ID to assign the routine to: ");

        if (name.trim().isEmpty() || userId <= 0) {
            System.out.println("Invalid input. Name and a valid User ID are required.");
            return;
        }

        RoutineEntity routineEntity = new RoutineEntity();
        routineEntity.setRoutineName(name);
        routineEntity.setDifficultyLevel(difficulty);
        routineEntity.setUserId(userId);

        // Collect nested exercises
        List<RoutineExerciseDetailsEntity> details = collectRoutineDetails();
        routineEntity.setExercises(details);

        try {
            // Service returns ID or throws RuntimeException (e.g., if User ID or Exercise ID is invalid)
            Integer newRoutineId = routineService.createEntity(routineEntity);

            if (newRoutineId != null) {
                System.out.println("New Routine Created with ID: " + newRoutineId);
            } else {
                System.err.println("Routine creation failed unexpectedly.");
            }

        } catch (RuntimeException e) {
            // CATCH POINT: Persistence Error (DB transaction failure) or Business Error (invalid nested data)
            System.err.println("Failed to create Routine: " + e.getMessage());
        }
    }

    // --- Helper to Collect Nested Exercises ---
    private List<RoutineExerciseDetailsEntity> collectRoutineDetails() {
        List<RoutineExerciseDetailsEntity> details = new ArrayList<>();
        boolean collecting = true;
        while (collecting) {
            System.out.println("\n-- ADD EXERCISE DETAIL (Enter 0 for Exercise ID to stop) --");

            int exerciseId = InputHandler.getIntInput("Exercise ID: ");
            if (exerciseId <= 0) {
                break;
            }

            int sets = InputHandler.getIntInput("Sets: ");
            int reps = InputHandler.getIntInput("Reps: ");

            if (sets <= 0 || reps <= 0) {
                System.err.println("Sets and Reps must be positive. Skipping this detail.");
                continue;
            }

            // The DAO/Service needs an ExerciseEntity object to extract the ID from
            ExerciseEntity linkedExercise = new ExerciseEntity();
            linkedExercise.setExerciseId(exerciseId);

            RoutineExerciseDetailsEntity detail = new RoutineExerciseDetailsEntity();
            detail.setExercise(linkedExercise);
            detail.setSets(sets);
            detail.setReps(reps);

            details.add(detail);
        }
        return details;
    }

    // --- 2. SEARCH ROUTINE BY ID ---
    private void searchRoutineById() {
        try {
            Integer routineId = InputHandler.getIntInput("What is the routine ID?");

            // Service method retrieves the Routine and aggregates all nested exercises
            Optional<Routine> routine = routineService.getModelById(routineId);

            if(routine.isPresent()){
                System.out.println("=== ROUTINE DETAILS FOUND ===");
                Routine r = routine.get();
                System.out.printf("ID: %d, Name: %s, Difficulty: %s, User ID: %d\n",
                        r.getRoutineId(), r.getRoutineName(),
                        r.getDifficultyLevel(), r.getUserId());

                System.out.println("-- EXERCISES --");
                if (r.getExercises() != null) {
                    r.getExercises().forEach(detail -> {
                        System.out.printf("  - %s (ID: %d): %d Sets, %d Reps\n",
                                detail.getExercise().getName(),
                                detail.getExercise().getExerciseId(),
                                detail.getSets(), detail.getReps());
                    });
                }
            } else {
                System.out.println("Routine with ID " + routineId + " not found.");
            }
        } catch (RuntimeException e) {
            // CATCH POINT: Persistence or Service Layer Error
            System.err.println("Error searching by ID: " + e.getMessage());
        }
    }

    // --- 3. GET ALL ROUTINES (Header Only) ---
    private void getAllRoutines() {
        try {
            // This call typically only loads header data (no nested exercises) for performance
            List<Routine> routines = routineService.getAllModels();
            if (routines.isEmpty()) {
                System.out.println("No routines found in the system.");
            } else {
                System.out.println("=== ALL ROUTINES ===");
                routines.forEach(r -> System.out.printf("ID: %d, Name: %s, Difficulty: %s\n",
                        r.getRoutineId(), r.getRoutineName(),
                        r.getDifficultyLevel()));
            }
        } catch (RuntimeException e) {
            System.err.println("Error fetching all routines: " + e.getMessage());
        }
    }

    // --- 4. UPDATE ROUTINE (Simplified) ---
    private void updateRoutine() {
        System.out.println("--- UPDATE ROUTINE ---");
        int routineId = InputHandler.getIntInput("Routine ID to update: ");

        // 1. Fetch current data to display/verify (Optional but good UX)
        try {
            // We use getEntityById here because we need the full structure to prepare the update entity
            Optional<RoutineEntity> currentRoutine = routineService.getEntityById(routineId);
            if (currentRoutine.isEmpty()) {
                System.out.println("Routine with ID " + routineId + " not found.");
                return;
            }
        } catch (RuntimeException e) {
            System.err.println("Error during update lookup: " + e.getMessage());
            return;
        }

        // 2. Collect new data
        String newName = InputHandler.getStringInput("New Name (or leave blank to skip): ");
        String newDifficulty = InputHandler.getStringInput("New Difficulty (or leave blank to skip): ").trim().toUpperCase();

        if (!newDifficulty.isEmpty()) {
            if (!newDifficulty.equals("EASY") && !newDifficulty.equals("MEDIUM") && !newDifficulty.equals("HARD")) {
                System.out.println(">>> Error: Update cancelled. Invalid difficulty choice.");
                return;
            }
        }
        // The most critical part: Replacing all nested details
        System.out.println("You must re-enter ALL exercises for this routine now (old list will be deleted).");
        List<RoutineExerciseDetailsEntity> newDetails = collectRoutineDetails();

        // 3. Prepare the new entity for the DAO call
        RoutineEntity updateEntity = new RoutineEntity();
        updateEntity.setRoutineId(routineId);

        // NOTE: The update must carry all necessary fields, even unchanged ones,
        // to overwrite the row completely. For simplicity, we assume we update the name/difficulty/exercises.
        updateEntity.setRoutineName(newName.trim().isEmpty() ? "UNCHANGED" : newName); // Simplified update logic
        updateEntity.setDifficultyLevel(newDifficulty.trim().isEmpty() ? "UNCHANGED" : newDifficulty);
        updateEntity.setUserId(InputHandler.getIntInput("New User ID (or original ID): ")); // Requires a valid ID

        updateEntity.setExercises(newDetails);

        try {
            routineService.updateEntity(routineId, updateEntity);
            System.out.println("Routine ID " + routineId + " successfully updated.");
        } catch (RuntimeException e) {
            // CATCH POINT: Not Found (business logic) or Transaction/Persistence Failure (DB)
            System.err.println("Failed to update Routine: " + e.getMessage());
        }
    }

    // --- 5. DELETE ROUTINE ---
    private void deleteRoutine() {
        try {
            Integer routineId = InputHandler.getIntInput("Routine ID to delete: ");

            boolean wasDeleted = routineService.deleteEntity(routineId);

            if (wasDeleted) {
                System.out.println("Routine ID " + routineId + " successfully deleted.");
            } else {
                // Not found is a business logic failure handled by the Service's return value
                System.out.println("Routine ID " + routineId + " not found.");
            }
        } catch (RuntimeException e) {
            // CATCH POINT: Persistence Error (DB transaction failure)
            System.err.println("Failed to delete Routine: " + e.getMessage());
        }
    }
}