import org.fitnesstracker.repository.DAO.ExerciseDAO;
import org.fitnesstracker.repository.DAO.UserDAO;
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
public class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    private UserEntity testUserEntity;
    private User testUserModel;

    @BeforeEach
    void setup() {
        // Setup Test UserEntity (Database structure)
        testUserEntity = new UserEntity();
        testUserEntity.setUserId(1);
        testUserEntity.setUsername("FitWarrior");
        testUserEntity.setEmail("warrior@fitness.com");

        // Setup Test UserModel (User-facing structure)
        testUserModel = new User();
        testUserModel.setUserId(1);
        testUserModel.setUsername("FitWarrior");
    }

    // --- CREATE TESTS ---

    @Test
    void createEntity_Success_ReturnsNewId() throws SQLException {
        // AAA
        // Arrange
        when(userDAO.create(testUserEntity)).thenReturn(50);

        // Act
        Integer result = userService.createEntity(testUserEntity);

        // Assert
        assertEquals(50, result);
        verify(userDAO, times(1)).create(testUserEntity);
    }


    // --- READ/SEARCH TESTS ---

    @Test
    void getModelByUserName_UserExists_ReturnsMappedModel() throws SQLException {
        // Arrange
        String username = "FitWarrior";
        when(userDAO.findByUserName(username)).thenReturn(Optional.of(testUserEntity));

        // Act
        Optional<User> result = userService.getModelByUserName(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userDAO, times(1)).findByUserName(username);
    }

    @Test
    void getModelById_UserNotFound_ThrowsRuntimeException() throws SQLException {
        // Arrange
        int missingId = 404;
        when(userDAO.findById(missingId)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            userService.getModelById(missingId);
            fail("Expected RuntimeException for missing user");
        } catch (RuntimeException e) {
            // Test passes
            assertTrue(e.getMessage().contains("not found"));
        }

        verify(userDAO, times(1)).findById(missingId);
    }

    // --- DELETE TESTS ---

    @Test
    void deleteEntity_Success_ReturnsTrue() throws SQLException {
        // Arrange
        int deleteId = 1;
        when(userDAO.deleteById(deleteId)).thenReturn(true);

        // Act
        boolean result = userService.deleteEntity(deleteId);

        // Assert
        assertTrue(result);
        verify(userDAO, times(1)).deleteById(deleteId);
    }
}