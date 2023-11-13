package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class LoginTokenTest {
    @Autowired
    private MockMvc mvc;

    // test_user does not exist - should report forbidden
    @Test
    public void loginTryUnauthorized() throws Exception {
        mvc.perform(get("/api/user/test_user")).andExpect(status().isUnauthorized());
    }

    // create user, login, pass token on - should report OK as users exists and has proper JWT
    @Test
    public void loginWithAuthCheck() throws Exception {
        // Create user
        String userJson = "{\"username\": \"test_user\", \"password\": \"P@ssword\", \"confirmPassword\": \"P@ssword\"}";

        mvc.perform(post("/api/user/create").content(userJson).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk());

        // Check with correct password
        String loginJson = "{\"username\": \"test_user\", \"password\": \"P@ssword\"}";

        MvcResult result = mvc.perform(post("/login")
                .content(loginJson).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andReturn();

        String jwt = result.getResponse().getHeader("Authorization");

        mvc.perform(get("/api/user/test_user")
                .header("Authorization", jwt)).andExpect(status().isOk());

        // Check with wrong password
        String loginJsonBad = "{\"username\": \"test_user\", \"password\": \"P@ssword1\"}";

        MvcResult resultBad = mvc.perform(post("/login")
                .content(loginJsonBad).contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(status().isUnauthorized()).andReturn();
    }
}
