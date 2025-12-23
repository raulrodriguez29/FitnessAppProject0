import org.fitnesstracker.repository.DAO.RoutineDAO;
import org.fitnesstracker.repository.DAO.UserDAO;
import org.fitnesstracker.repository.entities.RoutineEntity;
import org.fitnesstracker.repository.entities.UserEntity;
import org.fitnesstracker.service.ExerciseService;
import org.fitnesstracker.service.RoutineService;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoutineServiceTest {

    @Mock
    private RoutineDAO routineDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private RoutineService routineService;

    private RoutineEntity testRoutineEntity;
    private Routine testRoutineModel;

    @BeforeEach
    void setup() {
        // Setup Test RoutineEntity (Database Object)
        testRoutineEntity = new RoutineEntity();
        testRoutineEntity.setRoutineId(1);
        testRoutineEntity.setRoutineName("Morning Strength");
        testRoutineEntity.setUserId(10);

        // Setup Test RoutineModel (Application Object)
        testRoutineModel = new Routine();
        testRoutineModel.setRoutineId(1);
        testRoutineModel.setRoutineName("Morning Strength");
    }

    // --- CREATE TESTS ---

    @Test
    void createEntity_Success_ReturnsNewId() throws SQLException {
        // AAA
        // Arrange
        when(userDAO.findById(anyInt())).thenReturn(Optional.of(new UserEntity()));
        when(routineDAO.create(testRoutineEntity)).thenReturn(500);

        // Act
        Integer result = routineService.createEntity(testRoutineEntity);

        // Assert
        assertEquals(500, result);
        verify(routineDAO, times(1)).create(testRoutineEntity);
    }

    // --- READ / CONVERSION TESTS ---

    @Test
    void getModelById_Success_ReturnsRoutineModel() throws SQLException {
        // Arrange
        when(routineDAO.findById(1)).thenReturn(Optional.of(testRoutineEntity));

        // Act
        Optional<Routine> result = routineService.getModelById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Morning Strength", result.get().getRoutineName());
        verify(routineDAO, times(1)).findById(1);
    }

    @Test
    void deleteEntity_RoutineExists_ReturnsTrue() throws SQLException {
        // Arrange
        int deleteId = 1;
        when(routineDAO.deleteById(deleteId)).thenReturn(true);

        // Act
        boolean result = routineService.deleteEntity(deleteId);

        // Assert
        assertTrue(result);
        verify(routineDAO, times(1)).deleteById(deleteId);
    }
}