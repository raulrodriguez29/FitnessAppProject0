package org.fitnesstracker.service;

import org.fitnesstracker.repository.DAO.RoutineDAO;
import org.fitnesstracker.repository.DAO.UserDAO;
import org.fitnesstracker.repository.entities.ExerciseEntity;
import org.fitnesstracker.repository.entities.RoutineEntity;
import org.fitnesstracker.repository.entities.RoutineExerciseDetailsEntity;
import org.fitnesstracker.service.interfaces.ServiceInterface;
import org.fitnesstracker.service.models.Routine;
import org.fitnesstracker.service.models.RoutineExerciseDetails;
import org.fitnesstracker.service.models.Exercise;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class RoutineService implements ServiceInterface<RoutineEntity, Routine> {

    // --- Dependencies ---
    private final RoutineDAO routineDAO;
    // We need ExerciseService to convert the nested ExerciseEntity to Exercise Model
    private final ExerciseService exerciseService;
    private final UserDAO userDAO;

    // Constructor Injection
    public RoutineService(RoutineDAO routineDAO, ExerciseService exerciseService, UserDAO userDAO) {
        this.routineDAO = routineDAO;
        this.exerciseService = exerciseService;
        this.userDAO = userDAO;
    }

    // --- 1. CREATE ---
    @Override
    public Integer createEntity(RoutineEntity routineEntity) {
        // 2. INTEGRITY CHECK: Does the User exist?
        try {
            if (userDAO.findById(routineEntity.getUserId()).isEmpty()) {
                throw new RuntimeException("User ID " + routineEntity.getUserId() + " does not exist.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verifying User ID", e);
        }

        // 3. INTEGRITY CHECK: Do all Exercises exist?
        if (routineEntity.getExercises() != null) {
            for (RoutineExerciseDetailsEntity detail : routineEntity.getExercises()) {
                int exId = detail.getExercise().getExerciseId();
                if (exerciseService.getEntityById(exId).isEmpty()) {
                    throw new RuntimeException("Exercise ID " + exId + " does not exist.");
                }
            }
        }

        // 4. Persistence
        try {
            return routineDAO.create(routineEntity);
        } catch (SQLException e) {
            throw new RuntimeException("Database error creating Routine entity.", e);
        }
    }

    // --- 2. READ (Entity) - CRITICAL AGGREGATION METHOD ---
    @Override
    public Optional<RoutineEntity> getEntityById(Integer routineId) {
        try {
            // A. Get Routine Header
            Optional<RoutineEntity> routineEntityOptional = routineDAO.findById(routineId);

            if (routineEntityOptional.isEmpty()) {
                // Return empty Optional if header is not found
                return Optional.empty();
            }

            RoutineEntity routine = routineEntityOptional.get();

            // B. Get Associated Exercises (The Aggregation Step)
            // This method might throw a SQLException, so it's inside the try block.
            List<RoutineExerciseDetailsEntity> details = routineDAO.getExercisesForRoutine(routineId);

            // C. Attach the exercises to the main entity
            routine.setExercises(details);

            return Optional.of(routine);

        } catch (SQLException e) {
            // Wrap the Persistence Error and throw
            throw new RuntimeException("Database error retrieving Routine by ID: " + routineId, e);
        }
    }

    // --- 3. READ ALL (Entity) ---
    @Override
    public List<RoutineEntity> getAllEntities() {
        try {
            // NOTE: This usually only loads the header data for a list view (no exercises).
            return routineDAO.findAll();
        } catch (SQLException e) {
            // Wrap the Persistence Error and throw
            throw new RuntimeException("Database error retrieving all Routine Entities.", e);
        }
    }

    // --- 4. UPDATE (Entity) ---
    @Override
    public RoutineEntity updateEntity(Integer routineId, RoutineEntity newEntity) {
        // 1. Initial Lookup and Business Failure Check (Resource Not Found)
        Optional<RoutineEntity> existingRoutineOptional = getEntityById(routineId);

        if (existingRoutineOptional.isEmpty()) {
            throw new RuntimeException("Update failed: RoutineEntity with ID " + routineId + " not found.");
        }

        // Ensure the ID is set correctly for the DAO call
        newEntity.setRoutineId(routineId);

        try {
            return routineDAO.updateById(newEntity);
        } catch (SQLException e) {
            throw new RuntimeException("Database error updating RoutineEntity with ID " + routineId, e);
        }
    }

    // --- 5. DELETE ---
    @Override
    public boolean deleteEntity(Integer routineId) {
        try {
            boolean wasDeleted = routineDAO.deleteById(routineId);

            // Check for Business Error (Resource Not Found)
            if (!wasDeleted) {
                return false;
            }

            // Success
            return true;
        } catch (SQLException e) {
            // Handle Persistence Error
            throw new RuntimeException("Database error deleting RoutineEntity with ID: " + routineId, e);
        }
    }

    // --- 6. CONVERSION (Entity to Model) ---
    @Override
    public Optional<Routine> convertEntityToModel(RoutineEntity routineEntity) {
        if (routineEntity == null) return Optional.empty();

        Routine routineModel = new Routine();

        // 1. Map Routine Header Fields
        routineModel.setRoutineId(routineEntity.getRoutineId());
        routineModel.setUserId(routineEntity.getUserId());
        routineModel.setRoutineName(routineEntity.getRoutineName());
        routineModel.setDifficultyLevel(routineEntity.getDifficultyLevel());

        // 2. Process Nested Exercise Details (Now inline)
        if (routineEntity.getExercises() != null) {

            List<RoutineExerciseDetails> detailModels = new ArrayList<>();

            for (RoutineExerciseDetailsEntity detailEntity : routineEntity.getExercises()) {

                // --- INLINE LOGIC START ---

                // Safety check for the nested data
                if (detailEntity == null || detailEntity.getExercise() == null) continue;

                // A. Convert the nested ExerciseEntity using the ExerciseService
                Optional<Exercise> exerciseModel =
                        exerciseService.convertEntityToModel(detailEntity.getExercise());

                // If the nested conversion fails, skip this detail and continue the loop
                if (exerciseModel.isEmpty()) continue;

                // B. Create the Bridge Model (RoutineExerciseDetails)
                RoutineExerciseDetails model = new RoutineExerciseDetails();
                model.setExercise(exerciseModel.get());
                model.setSets(detailEntity.getSets());
                model.setReps(detailEntity.getReps());

                // --- INLINE LOGIC END ---

                // C. Add the successfully created model to the list
                detailModels.add(model);
            }

            routineModel.setExercises(detailModels);
        }

        return Optional.of(routineModel);
    }

    // --- 7. READ (Model by ID) ---
    @Override
    public Optional<Routine> getModelById(Integer routineId) {
        // 1. Fetch the Entity (Persistence errors handled/wrapped in getEntityById)
        Optional<RoutineEntity> routineEntityOptional = getEntityById(routineId);

        // 2. CHECK 1: Business Logic Failure (Resource not found)
        if (routineEntityOptional.isEmpty()) {
            throw new RuntimeException("RoutineEntity not found for ID: " + routineId);
        }

        // Safely extract the entity
        RoutineEntity routineEntity = routineEntityOptional.get();

        // 3. CHECK 2: Application Failure (Conversion failed)
        Optional<Routine> routineOptional = convertEntityToModel(routineEntity);

        if (routineOptional.isEmpty()) {
            throw new RuntimeException("RoutineEntity conversion failed for ID: " + routineId);
        }

        return routineOptional;
    }

    // --- 8. READ ALL (Model) ---
    public List<Routine> getAllModels() {
        // getAllEntities handles its own persistence error wrapping
        List<RoutineEntity> routineEntities = getAllEntities();
        List<Routine> routines = new ArrayList<>();

        for (RoutineEntity routineEntity : routineEntities) {
            Optional<Routine> routine = convertEntityToModel(routineEntity);
            if (routine.isPresent()) {
                routines.add(routine.get());
            }
        }
        return routines;
    }
}