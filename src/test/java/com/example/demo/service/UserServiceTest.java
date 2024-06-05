package com.example.demo.service;

import com.example.demo.entity.Phone;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.presentation.presenter.PhonePresenter;
import com.example.demo.presentation.presenter.UserPresenter;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserPresenter userPresenter;

    private User user;
    private User user2;
    private List<User> userList;
    private final String EMAIL = "test@test.com";
    private final String EMAIL2 = "other@test.com";
    private final String PASSWORD = "pass123";

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        userPresenter = new UserPresenter();
        userPresenter.setName("test");
        userPresenter.setEmail(EMAIL);
        userPresenter.setPassword(PASSWORD);
        Set<PhonePresenter> phonePresenters = new HashSet<>();
        phonePresenters.add(PhonePresenter.builder()
                .number("123")
                .cityCode("123")
                .countryCode("123")
                .build());
        userPresenter.setPhones(phonePresenters);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("test");
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail(EMAIL2);
        user2.setPassword(PASSWORD);
        user2.setCreated(LocalDateTime.now());
        user2.setModified(LocalDateTime.now());
        user2.setLastLogin(LocalDateTime.now());

        Set<Phone> phones = new HashSet<>();
        phones.add(Phone.builder().number("123124").build());
        user.setPhones(new HashSet<>());

        userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);
        ReflectionTestUtils.setField(userServiceImpl, "passwordRegex", "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$");

    }

    @Test
    @DisplayName("Create User - Success")
    public void createUserTest() throws CustomException {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any())).then(invocation -> {
            User user = invocation.getArgument(0);
            assertTrue(user.isActive());
            assertEquals(EMAIL, user.getEmail());
            assertNotNull(user.getPassword());
            assertNotNull(user.getModified());
            assertNotNull(user.getCreated());
            assertNotNull(user.getLastLogin());
            assertNotNull(user.getToken());
            assertEquals(user.getPhones().size(), 1);
            user.getPhones().forEach(phone -> {
                assertNotNull(phone.getNumber());
                assertNotNull(phone.getCityCode());
                assertNotNull(phone.getCountryCode());
            });
            return user;
        });

        User createdUser = userServiceImpl.createUser(userPresenter);

        assertNotNull(createdUser);
    }

    @Test
    @DisplayName("Create User - Invalid Email")
    public void createUserInvalidEmailTest() {
        userPresenter.setEmail("invalidEmail");

        Executable executable = () -> userServiceImpl.createUser(userPresenter);

        CustomException exception = assertThrows(CustomException.class, executable);
        assertEquals("Correo inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Create User - Invalid Password")
    public void createUserInvalidPasswordTest() {
        userPresenter.setPassword("pass");

        Executable executable = () -> userServiceImpl.createUser(userPresenter);

        CustomException exception = assertThrows(CustomException.class, executable);
        assertEquals("Contraseña inválida", exception.getMessage());
    }

    @Test
    @DisplayName("Create User - Email already registered")
    public void createUserAlreadyRegisteredEmailTest() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        Executable executable = () -> userServiceImpl.createUser(userPresenter);

        CustomException exception = assertThrows(CustomException.class, executable);
        assertEquals("El correo ya está registrado", exception.getMessage());
    }

    @Test
    @DisplayName("Update User - Success")
    public void updateUserSuccessTest() throws CustomException {
        when(userRepository.findById(userPresenter.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userServiceImpl.updateUser(userPresenter);

        assertNotNull(updatedUser);
        assertEquals(EMAIL, updatedUser.getEmail());
        assertTrue(updatedUser.getPassword().startsWith("$2a$"));
    }

    @Test
    @DisplayName("Update User - Invalid Email")
    public void updateUserInvalidEmailTest() {
        userPresenter.setEmail("invalidEmail");

        Executable executable = () -> userServiceImpl.updateUser(userPresenter);

        CustomException exception = assertThrows(CustomException.class, executable);
        assertEquals("Correo inválido", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Update User - Invalid Password")
    public void updateUserInvalidPasswordTest() {
        userPresenter.setPassword("pass");

        Executable executable = () -> userServiceImpl.updateUser(userPresenter);

        CustomException exception = assertThrows(CustomException.class, executable);
        assertEquals("Contraseña inválida", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Update User - User not found")
    public void updateUserUserNotFoundTest() {
        when(userRepository.findById(userPresenter.getId())).thenReturn(Optional.empty());

        Executable executable = () -> userServiceImpl.updateUser(userPresenter);

        CustomException exception = assertThrows(CustomException.class, executable);
        assertEquals("Usuario no encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Update User - Email already registered")
    public void updateUserAlreadyRegisteredEmailTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user2));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        userPresenter.setEmail(EMAIL);
        userPresenter.setId(user.getId());

        Executable executable = () -> userServiceImpl.updateUser(userPresenter);

        CustomException exception = assertThrows(CustomException.class, executable);
        assertEquals("El correo ya está registrado", exception.getMessage());
    }

    @Test
    @DisplayName("Get all users - Success")
    public void getAllUsersSuccessTest() {
        when(userRepository.findAllWithPhones()).thenReturn(userList);

        List<User> result = userServiceImpl.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(EMAIL, result.get(0).getEmail());
        assertEquals(EMAIL2, result.get(1).getEmail());
    }

    @Test
    @DisplayName("Get all users - Empty list")
    public void getAllUsersEmptyTest() {
        when(userRepository.findAllWithPhones()).thenReturn(new ArrayList<>());

        List<User> result = userServiceImpl.getAllUsers();

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Delete User - Success")
    public void deleteUserTest() throws CustomException {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userServiceImpl.deleteUser(id);

        verify(userRepository, atLeastOnce()).findById(any());
        verify(userRepository, atLeastOnce()).delete(any());

    }

    @Test
    @DisplayName("Delete User - User not found")
    public void deleteUserNotFoundTest() {
        UUID id = UUID.randomUUID();

        Executable executable = () -> userServiceImpl.deleteUser(id);

        CustomException exception = assertThrows(CustomException.class, executable);
        assertEquals("Usuario no encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }
}

