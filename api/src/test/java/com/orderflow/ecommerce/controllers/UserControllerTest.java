package com.orderflow.ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderflow.ecommerce.auxiliar.Factory;
import com.orderflow.ecommerce.dtos.UserDto;
import com.orderflow.ecommerce.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService service;

    private long existingId, nonExistingId, dependentId;
    private String existingEmail, nonExistingEmail;
    private UserDto userDto;
    private PageImpl<UserDto> page;

    @BeforeEach
    void setUp() throws Exception {
        userDto = Factory.createUserDto();
        existingId = userDto.id();
        nonExistingId = 2L;
        existingEmail = userDto.email();
        nonExistingEmail = "inexistent@mail.com";

        page = new PageImpl<>(List.of(userDto));

        when(service.findAllPaged(any())).thenReturn(page);

        when(service.findById(existingId)).thenReturn(userDto);
        when(service.findById(nonExistingId)).thenThrow(NoSuchElementException.class);

        when(service.findByEmail(existingEmail)).thenReturn(userDto);
        when(service.findByEmail(nonExistingEmail)).thenThrow(NoSuchElementException.class);

        when(service.insert(any())).thenReturn(userDto);

        when(service.update(eq(existingId), any())).thenReturn(userDto);
        when(service.update(eq(nonExistingId), any())).thenThrow(NoSuchElementException.class);

        doNothing().when(service).delete(existingId, false);
        doThrow(NoSuchElementException.class).when(service).delete(nonExistingId, true);
        doThrow(DataIntegrityViolationException.class).when(service).delete(dependentId, false);
    }

    @Test
    void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnUserWhenIdExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/users/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void findByIdShouldThrowNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/users/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void findByEmailShouldReturnUserWhenIdExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/users").param("email", existingEmail)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void findByEmailShouldThrowNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/users").param("email", nonExistingEmail)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }


    @Test
    public void updateShouldReturnUserDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userDto);
        ResultActions result = mockMvc.perform(put("/users/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());

    }

    @Test
    public void updateShouldThrowNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userDto);
        ResultActions result = mockMvc.perform(put("/users/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnCreatedAndUserDTO() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userDto);
        ResultActions result = mockMvc.perform(post("/users")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());

    }

    @Test
    public void deleteShouldReturnNoContentWhereIdExists() throws Exception {
        mockMvc.perform(delete("/users/{id}", existingId).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhereIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/users/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
    }

}
