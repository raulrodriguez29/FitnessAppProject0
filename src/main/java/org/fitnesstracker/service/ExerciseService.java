package org.fitnesstracker.service;

import org.fitnesstracker.repository.DAO.ExerciseDAO;
import org.fitnesstracker.repository.entities.ExerciseEntity;
import org.fitnesstracker.service.interfaces.ServiceInterface;
import org.fitnesstracker.service.models.Exercise;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExerciseService implements ServiceInterface<ExerciseEntity, Exercise> {

    // --- Dependency Injection Setup (Matching UserService) ---
    private final ExerciseDAO exerciseDAO;

    // Constructor Injection is the preferred way to manage dependencies
    public ExerciseService(ExerciseDAO exerciseDAO) {
        this.exerciseDAO = exerciseDAO;
    }

    // --- 1. CREATE ---
    @Override
    public Integer createEntity(ExerciseEntity exerciseEntity) {
        try {
            return exerciseDAO.create(exerciseEntity);
        } catch (SQLException e) {
            // Wrap the Persistence Error and throw
            throw new RuntimeException("Database error creating Exercise entity.", e);
        }
    }

    // --- 2. READ (Entity) ---
    @Override
    public Optional<ExerciseEntity> getEntityById(Integer exerciseId) {
        try {
            // DAO handles the retrieval; we let Optional.empty() be returned if not found.
            return exerciseDAO.findById(exerciseId);
        } catch (SQLException e) {
            // Wrap the Persistence Error and throw
            throw new RuntimeException("Database error retrieving Exercise by ID: " + exerciseId, e);
        }
    }

    // --- 3. READ ALL (Entity) ---
    @Override
    public List<ExerciseEntity> getAllEntities() {
        try {
            return exerciseDAO.findAll();
        } catch (SQLException e) {
            // Wrap the Persistence Error and throw
            throw new RuntimeException("Database error retrieving all Exercise Entities.", e);
        }
    }

    // --- 4. UPDATE (Entity) ---
    @Override
    public ExerciseEntity updateEntity(Integer exerciseId, ExerciseEntity newEntity) {
        // 1. Initial Lookup and Business Failure Check (Resource Not Found)
        Optional<ExerciseEntity> existingExerciseOptional = getEntityById(exerciseId);

        if (existingExerciseOptional.isEmpty()) {
            throw new RuntimeException("Update failed: ExerciseEntity with ID " + exerciseId + " not found.");
        }

        ExerciseEntity existingExerciseEntity = existingExerciseOptional.get();

        // 2. Application of Changes (Only update fields intended for change)
        existingExerciseEntity.setName(newEntity.getName());
        existingExerciseEntity.setTargetMuscle(newEntity.getTargetMuscle());
        existingExerciseEntity.setCaloriesPerMin(newEntity.getCaloriesPerMin());
        // Do not update the ID from the newEntity!

        // 3. Persistence
        try {
            return exerciseDAO.updateById(existingExerciseEntity);
        } catch (SQLException e) {
            // Persistence Failure
            throw new RuntimeException("Database error updating ExerciseEntity with ID " + exerciseId, e);
        }
    }

    // --- 5. DELETE ---
    @Override
    public boolean deleteEntity(Integer exerciseId) {
        try {
            boolean wasDeleted = exerciseDAO.deleteById(exerciseId);

            // 1. Check for Business Error (Resource Not Found)
            if (!wasDeleted) {
                return false;
            }

            // Success
            return true;
        } catch (SQLException e) {
            // 2. Handle Persistence Error
            throw new RuntimeException("Database error deleting ExerciseEntity with ID: " + exerciseId, e);
        }
    }

    // --- 6. CONVERSION (Entity to Model) ---
    @Override
    public Optional<Exercise> convertEntityToModel(ExerciseEntity exerciseEntity) {
        if (exerciseEntity == null) return Optional.empty();

        Exercise exerciseModel = new Exercise();
        exerciseModel.setExerciseId(exerciseEntity.getExerciseId());
        exerciseModel.setName(exerciseEntity.getName());
        exerciseModel.setCaloriesPerMin(exerciseEntity.getCaloriesPerMin());
        exerciseModel.setTargetMuscle(exerciseEntity.getTargetMuscle());

        return Optional.of(exerciseModel);
    }

    // --- 7. READ (Model by ID) ---
    @Override
    public Optional<Exercise> getModelById(Integer exerciseId) {
        // 1. Fetch the Entity (Persistence errors are already handled/wrapped in getEntityById)
        Optional<ExerciseEntity> exerciseEntityOptional = getEntityById(exerciseId);

        // 2. CHECK 1: Business Logic Failure (Resource not found)
        if (exerciseEntityOptional.isEmpty()) {
            throw new RuntimeException("ExerciseEntity not found for ID: " + exerciseId);
        }

        // Safely extract the entity
        ExerciseEntity exerciseEntity = exerciseEntityOptional.get();

        // 3. CHECK 2: Application Failure (Conversion failed)
        Optional<Exercise> exerciseOptional = convertEntityToModel(exerciseEntity);

        if (exerciseOptional.isEmpty()) {
            throw new RuntimeException("ExerciseEntity conversion failed for ID: " + exerciseId);
        }

        return exerciseOptional;
    }

    // --- 8. READ (Model by Name) ---
    public Optional<Exercise> getModelByExerciseName(String exerciseName) {
        Optional<ExerciseEntity> exerciseEntityOptional = getEntityByExerciseName(exerciseName);

        // Check 1: Was the ExerciseEntity retrieved from the DAO?
        if (exerciseEntityOptional.isEmpty()) {
            throw new RuntimeException("ExerciseEntity not found for name: " + exerciseName);
        }
        // Use the retrieved entity
        ExerciseEntity exerciseEntity = exerciseEntityOptional.get();

        // Check 2: Did the conversion to the Exercise Model succeed?
        Optional<Exercise> exerciseOptional = convertEntityToModel(exerciseEntity);

        if (exerciseOptional.isEmpty()) {
            throw new RuntimeException("ExerciseEntity conversion failed for name: " + exerciseName);
        }
        // Return the successfully converted Model
        return exerciseOptional;
    }

    // --- Private DAO Helper ---
    private Optional<ExerciseEntity> getEntityByExerciseName(String exerciseName) {
        try {
            return exerciseDAO.findByName(exerciseName);
        } catch (SQLException e) {
            // Persistence Failure
            throw new RuntimeException("Database error finding exercise by name " + exerciseName, e);
        }
    }

    // --- 9. READ ALL (Model) ---
    public List<Exercise> getAllModels() {
        // getAllEntities handles its own persistence error wrapping
        List<ExerciseEntity> exerciseEntities = getAllEntities();
        List<Exercise> exercises = new ArrayList<>();

        for (ExerciseEntity exerciseEntity : exerciseEntities) {
            Optional<Exercise> exercise = convertEntityToModel(exerciseEntity);
            if (exercise.isPresent()) {
                exercises.add(exercise.get());
            }
        }
        return exercises;
    }
}