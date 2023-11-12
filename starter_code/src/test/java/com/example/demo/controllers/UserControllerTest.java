package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private CartRepository cartRepo;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserController userController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Mockito.reset(userRepo);
        Mockito.reset(cartRepo);
        Mockito.reset(encoder);
    }

    @Test
    public void checkUserByName() throws Exception {
        String userName = "test_user";
        when(userRepo.findByUsername(userName)).thenReturn(getUser());

        User u = userController.findByUserName(userName).getBody();

        assertNotNull(u);
        assertEquals(userName, u.getUsername());
    }

    @Test
    public void checkUserById() throws Exception {
        Long userId = 1L;
        String userName = "test_user";

        when(userRepo.findById(userId)).thenReturn(Optional.of(getUser()));

        User u = userController.findById(userId).getBody();

        assertNotNull(u);
        assertEquals(userName, u.getUsername());
    }

    @Test
    public void createUserHappyPath() throws Exception {
        when(encoder.encode("P@ssword")).thenReturn("thisIsHashed");

        CreateUserRequest r = new CreateUserRequest();

        r.setUsername("test_user");
        r.setPassword("P@ssword");
        r.setConfirmPassword("P@ssword");

        ResponseEntity<?> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = null;
        if (response.getBody() instanceof User) {
            u = (User) response.getBody();
        }

        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test_user", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void createUserTooShort() throws Exception {
        CreateUserRequest r = new CreateUserRequest();

        r.setUsername("test_user");
        r.setPassword("test");
        r.setConfirmPassword("test");

        ResponseEntity<?> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        String str = null;
        if (response.getBody() instanceof String) {
            str = (String) response.getBody();
        }

        assertNotNull(str);
        assertEquals("Error creating User, password must contain at least 7 characters. Cannot create U"
                + "ser 'test_user'.", str);
    }

    @Test
    public void createUserNoMatch() throws Exception {
        CreateUserRequest r = new CreateUserRequest();

        r.setUsername("test_user");
        r.setPassword("P@ssword");
        r.setConfirmPassword("Password");

        ResponseEntity<?> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        String str = null;
        if (response.getBody() instanceof String) {
            str = (String) response.getBody();
        }

        assertNotNull(str);
        assertEquals("Error creating User, the password and confirmPassword do not match. Cannot create User 'test_user'.", str);
    }

    @Test
    public void createUserNoPassword() throws Exception {
        CreateUserRequest r = new CreateUserRequest();

        r.setUsername("test_user");
        r.setPassword(null);
        r.setConfirmPassword(null);

        ResponseEntity<?> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        String str = null;
        if (response.getBody() instanceof String) {
            str = (String) response.getBody();
        }

        assertNotNull(str);
        assertEquals("Error creating User, the password and confirmPassword must be set. Cannot create User 'test_user'.", str);
    }

    private User getUser() {
        Long userId = 1L;
        String userName = "test_user";
        User user = new User();
        user.setId(userId);
        user.setUsername(userName);

        return user;
    }
}
