package api.scrum.user.service;

import api.scrum.exceptions.BusinessException;
import api.scrum.project.model.Project;
import api.scrum.project.view.ProjectBaseView;
import api.scrum.relation_user_project.repository.RelationUserProjectRepository;
import api.scrum.user.model.User;
import api.scrum.user.repository.UserRepository;
import api.scrum.user.service.impl.UserServiceImpl;
import api.scrum.user.view.UserBaseView;
import api.scrum.user.view.UserView;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RelationUserProjectRepository relationUserProjectRepository;

    @Autowired
    @InjectMocks
    private UserServiceImpl userService;

    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        modelMapper = new ModelMapper();
    }

    @Test
    @DisplayName("Create user with valid parameters")
    void createUserWithValidParameters() {
        UserView userView = new UserView();
        userView.setName("user_test");
        userView.setEmail("usertest@gmail.com");
        userView.setPassword("123456");

        User userModel = this.modelMapper.map(userView,User.class);

        when(userRepository.save(any(User.class))).thenReturn(userModel);

        UserBaseView userSaved = userService.create(userView);

        assertEquals(userModel.getName(),userSaved.getName());
        assertEquals(userModel.getEmail(),userSaved.getEmail());
        assertEquals(userModel.getPassword(),userSaved.getPassword());
        verify(userRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("Create New User with Valid Parameters")
    void createNewUserWithValidParameters() {
        // Mocking data
        UserView userView = new UserView();
        userView.setName("John Doe");
        userView.setEmail("john.doe@example.com");
        userView.setPassword("password");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setName(userView.getName());
        savedUser.setEmail(userView.getEmail());
        savedUser.setPassword(userView.getPassword());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Test the service method and assert the result
        UserBaseView result = userService.create(userView);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(userView.getName(), result.getName());
        assertEquals(userView.getEmail(), result.getEmail());
        //assertNull(result.getPassword());  // Ensure password is not returned
    }
    @Test
    @DisplayName("Update existing user")
    void updateExistingUser() {
        // Dados do usuário existente
        UUID userId = UUID.randomUUID();
        String existingName = "existingUser";
        String existingEmail = "existingUser@example.com";
        String existingPassword = "existingPassword";

        // Dados do usuário a ser atualizado
        UserView userViewToUpdate = new UserView();
        userViewToUpdate.setId(userId);
        userViewToUpdate.setName("updatedUser");
        userViewToUpdate.setEmail("updatedUser@example.com");
        userViewToUpdate.setPassword("updatedPassword");

        // Usuário existente no banco de dados
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName(existingName);
        existingUser.setEmail(existingEmail);
        existingUser.setPassword(existingPassword);


        // Configuração do comportamento do repositório
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser); // Pode ajustar conforme necessário

        // Chamada da função de atualização
        UserBaseView updatedUser = userService.update(userViewToUpdate);

        // Verificações
        assertEquals(userViewToUpdate.getName(), updatedUser.getName());
        assertEquals(userViewToUpdate.getEmail(), updatedUser.getEmail());
        assertEquals(userViewToUpdate.getPassword(), updatedUser.getPassword());

        // Verifica se o repositório foi chamado corretamente
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Try to update non-existent user")
    void updateNonExistingUser() {
        // Dados do usuário a ser atualizado
        UserView userViewToUpdate = new UserView();
        userViewToUpdate.setId(UUID.randomUUID());
        userViewToUpdate.setName("updatedUser");
        userViewToUpdate.setEmail("updatedUser@example.com");
        userViewToUpdate.setPassword("updatedPassword");

        // Configuração do comportamento do repositório
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Chamada da função de atualização e esperamos uma exceção BusinessException
        assertThrows(BusinessException.class, () -> userService.update(userViewToUpdate));

        // Verifica se o repositório foi chamado corretamente
        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Update Existing User with Valid Parameters")
    void updateExistingUserWithValidParameters() {
        // Mocking data
        UserView userView = new UserView();
        userView.setId(UUID.randomUUID());  // Set an existing user ID
        userView.setName("Updated Name");
        userView.setEmail("updated.email@example.com");
        userView.setPassword("updatedpassword");

        User existingUser = new User();
        existingUser.setId(userView.getId());
        existingUser.setName("Original Name");
        existingUser.setEmail("original.email@example.com");
        existingUser.setPassword("originalpassword");

        when(userRepository.findById(userView.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Test the service method and assert the result
        UserBaseView result = userService.update(userView);
        assertEquals(userView.getId(), result.getId());
        assertEquals(userView.getName(), result.getName());
        assertEquals(userView.getEmail(), result.getEmail());
        //assertNull(result.getPassword());  // Ensure password is not returned
    }
    
    @Test
    @DisplayName("Update Existing User with Only One Parameter and Return UserBaseView")
    void updateExistingUserWithOnlyOneParameterAndReturnUserBaseView() {
        // Mocking data
        UUID userId = UUID.randomUUID();
        UserView userView = new UserView();
        userView.setId(userId);  // Set an existing user ID
        userView.setEmail("updated.email@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Original Name");
        existingUser.setEmail("original.email@example.com");
        existingUser.setPassword("originalpassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Test the service method and assert the result
        UserBaseView result = userService.update(userView);
        assertEquals(userId, result.getId());
        assertEquals(userView.getEmail(), result.getEmail());
        assertEquals(existingUser.getName(), result.getName());
        //assertNull(result.getPassword());
    }

    @Test
    @DisplayName("Update Existing User with Empty Parameters and Return UserBaseView")
    void updateExistingUserWithEmptyParametersAndReturnUserBaseView() {
        // Mocking data
        UUID userId = UUID.randomUUID();
        UserView userView = new UserView();
        userView.setId(userId);  // Set an existing user ID

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Original Name");
        existingUser.setEmail("original.email@example.com");
        existingUser.setPassword("originalpassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Test the service method and assert the result
        UserBaseView result = userService.update(userView);
        assertEquals(userId, result.getId());
        assertEquals(existingUser.getName(), result.getName());
        assertEquals(existingUser.getEmail(), result.getEmail());
        //assertNull(result.getPassword());  // Ensure password is not returned
    }

    @Test
    @DisplayName("Reads existing user")
    void readExistingUser() {
        // Dados do usuário existente
        UUID userId = UUID.randomUUID();
        String existingName = "existingUser";
        String existingEmail = "existingUser@example.com";
        String existingPassword = "existingPassword";

        // Usuário existente no banco de dados
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName(existingName);
        existingUser.setEmail(existingEmail);
        existingUser.setPassword(existingPassword);

        // Configuração do comportamento do repositório
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Chamada da função de leitura
        UserBaseView readUser = userService.read(userId);

        // Verificações
        assertEquals(existingUser.getName(), readUser.getName());
        assertEquals(existingUser.getEmail(), readUser.getEmail());
        assertEquals(existingUser.getPassword(), readUser.getPassword());

        // Verifica se o repositório foi chamado corretamente
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Try to read non-existent user")
    void readNonExistingUser() {
        // Configuração do comportamento do repositório
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Chamada da função de leitura e esperamos uma exceção BusinessException
        UUID id = UUID.randomUUID();
        Assertions.assertThrows(BusinessException.class, () -> userService.read(id));

        // Verifica se o repositório foi chamado corretamente
        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Read Existing User by ID")
    void readExistingUserById() {
        // Mocking data
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Doe");
        existingUser.setEmail("john.doe@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Test the service method and assert the result
        UserBaseView result = userService.read(userId);
        assertEquals(userId, result.getId());
        assertEquals(existingUser.getName(), result.getName());
        assertEquals(existingUser.getEmail(), result.getEmail());
        assertNull(result.getPassword());  // Ensure password is not returned
    }

    @Test
    @DisplayName("Delete existing user")
    void deleteExistingUser() {
        // Dados do usuário existente
        UUID userId = UUID.randomUUID();
        String existingName = "existingUser";
        String existingEmail = "existingUser@example.com";
        String existingPassword = "existingPassword";

        // Usuário existente no banco de dados
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName(existingName);
        existingUser.setEmail(existingEmail);
        existingUser.setPassword(existingPassword);

        // Configuração do comportamento do repositório
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).delete(existingUser);

        // Chamada da função de exclusão
        UserBaseView deletedUser = userService.delete(userId);

        // Verificações
        assertEquals(existingUser.getName(), deletedUser.getName());
        assertEquals(existingUser.getEmail(), deletedUser.getEmail());
        assertEquals(existingUser.getPassword(), deletedUser.getPassword());

        // Verifica se o repositório foi chamado corretamente
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    @DisplayName("Try to delete non-existent user")
    void deleteNonExistingUser() {
        // Configuração do comportamento do repositório
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Chamada da função de exclusão e esperamos uma exceção BusinessException
        UUID id = UUID.randomUUID();
        Assertions.assertThrows(BusinessException.class, () -> userService.delete(id));

        // Verifica se o repositório foi chamado corretamente
        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Delete Existing User by ID")
    void deleteExistingUserById() {
        // Mocking data
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Doe");
        existingUser.setEmail("john.doe@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Test the service method and assert the result
        UserBaseView result = userService.delete(userId);
        assertEquals(userId, result.getId());
        assertEquals(existingUser.getName(), result.getName());
        assertEquals(existingUser.getEmail(), result.getEmail());
        assertNull(result.getPassword());  // Ensure password is not returned
        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    @DisplayName("Find Projects Associated with Existing User by ID")
    void findProjectsAssociatedWithExistingUserById() {
        // Mocking data
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);

        Project project1 = new Project();
        project1.setId(UUID.randomUUID());
        project1.setName("Project 1");

        Project project2 = new Project();
        project2.setId(UUID.randomUUID());
        project2.setName("Project 2");

        List<Project> projectList = Arrays.asList(project1, project2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(relationUserProjectRepository.findProjectByUserId(userId)).thenReturn(Optional.of(projectList));

        // Test the service method and assert the result
        List<ProjectBaseView> result = userService.findProjects(userId);
        assertEquals(2, result.size());
        assertEquals("Project 1", result.get(0).getName());
        assertEquals("Project 2", result.get(1).getName());
    }

    @Test
    @DisplayName("Throw BusinessException for Invalid User Parameters on Create")
    void throwBusinessExceptionForInvalidUserParametersOnCreate() {
        // Mocking data
        UserView userView = new UserView();
        // Set incomplete parameters intentionally

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> userService.create(userView));
    }

    @Test
    @DisplayName("Throw BusinessException for Invalid User Parameters on Update")
    void throwBusinessExceptionForInvalidUserParametersOnUpdate() {
        // Mocking data
        UserView userView = new UserView();
        // Set incomplete parameters intentionally
        userView.setId(UUID.randomUUID());  // Set an existing user ID

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> userService.update(userView));
    }

    @Test
    @DisplayName("Throw BusinessException for User Not Found by ID")
    void throwBusinessExceptionForUserNotFoundById() {
        // Mocking data
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> userService.read(userId));
        assertThrows(BusinessException.class, () -> userService.update(new UserView()));  // Also test with update
        assertThrows(BusinessException.class, () -> userService.delete(userId));  // Also test with delete
        assertThrows(BusinessException.class, () -> userService.findProjects(userId));  // Also test with findProjects
    }

    @Test
    @DisplayName("Throw BusinessException for Deleting Nonexistent User by ID")
    void throwBusinessExceptionForDeletingNonexistentUserById() {
        // Mocking data
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> userService.delete(userId));
    }

    @Test
    @DisplayName("Throw BusinessException for Finding Projects Associated with Nonexistent User by ID")
    void throwBusinessExceptionForFindingProjectsAssociatedWithNonexistentUserById() {
        // Mocking data
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> userService.findProjects(userId));
    }

    @Test
    @DisplayName("Throw BusinessException for Creating New User with Null Parameters")
    void throwBusinessExceptionForCreatingNewUserWithNullParameters() {
        // Mocking data
        UserView userView = new UserView();
        // Set all parameters to null intentionally

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> userService.create(userView));
    }

    @Test
    @DisplayName("Throw BusinessException for Creating New User with Duplicate Email")
    void throwBusinessExceptionForCreatingNewUserWithDuplicateEmail() {
        // Mocking data
        UserView userView = new UserView();
        userView.setEmail("duplicate.email@example.com");

        when(userRepository.findByEmail(userView.getEmail())).thenReturn(Optional.of(new User()));

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> userService.create(userView));
    }

    @Test
    @DisplayName("Throw BusinessException for Creating New User with Invalid Email")
    void throwBusinessExceptionForCreatingNewUserWithInvalidEmail() {
        // Mocking data
        UserView userView = new UserView();
        userView.setEmail("invalid-email");  // Invalid email format

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> userService.create(userView));
    }

    @Test
    @DisplayName("Throw BusinessException for Creating New User with Invalid Password")
    void throwBusinessExceptionForCreatingNewUserWithInvalidPassword() {
        // Mocking data
        UserView userView = new UserView();
        userView.setEmail("valid.email@example.com");
        userView.setPassword("short");  // Invalid password (too short)

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> userService.create(userView));
    }
}