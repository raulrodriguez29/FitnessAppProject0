import org.fitnesstracker.repository.DAO.ExerciseDAO;
import org.fitnesstracker.repository.entities.UserEntity;
import org.fitnesstracker.repository.entities.RoutineEntity;
import org.fitnesstracker.repository.entities.ExerciseEntity;
import org.fitnesstracker.service.UserService;
import org.fitnesstracker.service.ExerciseService;
import org.fitnesstracker.service.RoutineService;
import org.fitnesstracker.service.models.User;
import org.fitnesstracker.service.models.Routine;
import org.fitnesstracker.service.models.Exercise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {

    @Mock
    private ExerciseDAO exerciseDAO;

    @InjectMocks
    private ExerciseService exerciseService;

    // We declare the objects here so all test methods can access them
    private ExerciseEntity testExerciseEntity;
    private Exercise testExerciseModel;

    @BeforeEach
    void setup() {
        // Setup Test ExerciseEntity (The Database version)
        testExerciseEntity = new ExerciseEntity();
        testExerciseEntity.setExerciseId(1);
        testExerciseEntity.setName("Bench Press");
        testExerciseEntity.setTargetMuscle("Chest");
        testExerciseEntity.setCaloriesPerMin(7);

        // Setup Test ExerciseModel (The User-facing version)
        testExerciseModel = new Exercise();
        testExerciseModel.setExerciseId(1);
        testExerciseModel.setName("Bench Press");
        testExerciseModel.setTargetMuscle("Chest");
    }

    @Test
    void createEntity_Success_ReturnsNewId() throws SQLException {
        // AAA
        // Arrange
        when(exerciseDAO.create(testExerciseEntity)).thenReturn(100);

        // Act
        Integer result = exerciseService.createEntity(testExerciseEntity);

        // Assert
        assertEquals(100, result);
        // Verify behavior: Check the DAO was called exactly once
        verify(exerciseDAO, times(1)).create(testExerciseEntity);
    }

    @Test
    void convertEntityToModel_Success_ReturnsExerciseModel() {
        // Arrange is handled in @BeforeEach
        //testExerciseEntity = new ExerciseEntity();
        //testExerciseEntity.setExerciseId(1);
        //testExerciseEntity.setName("Bench Press");
        //testExerciseEntity.setTargetMuscle("Chest");
        //testExerciseEntity.setCaloriesPerMin(7);

        // Act
        Optional<Exercise> result = exerciseService.convertEntityToModel(testExerciseEntity);

        // Assert
        assertTrue(result.isPresent());
        Exercise exercise = result.get();
        assertEquals("Bench Press", exercise.getName());
        assertEquals(1, exercise.getExerciseId());
        assertEquals("Chest", exercise.getTargetMuscle());
        assertEquals(7, exercise.getCaloriesPerMin());
    }

    @Test
    void getModelById_Success_ReturnsExerciseModel() throws SQLException {
        // Arrange
        when(exerciseDAO.findById(1)).thenReturn(Optional.of(testExerciseEntity));

        // Act
        Optional<Exercise> result = exerciseService.getModelById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Bench Press", result.get().getName());

        verify(exerciseDAO, times(1)).findById(1);
    }

    @Test
    void getModelById_NotFound_ReturnsEmptyOptional() throws SQLException {
        // Arrange
        when(exerciseDAO.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            exerciseService.getModelById(99);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            // Success: the catch block proves the exception was thrown
            assertTrue(e.getMessage().contains("not found"));
        }

        verify(exerciseDAO, times(1)).findById(99);
    }
}