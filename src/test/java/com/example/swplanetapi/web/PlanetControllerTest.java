package com.example.swplanetapi.web;

import com.example.swplanetapi.domain.Planet;
import com.example.swplanetapi.domain.PlanetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static com.example.swplanetapi.common.PlanetConstants.PLANET;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc; // responsavel por mockar o comportamento do controller com o perform
    @Autowired
    private ObjectMapper objectMapper;  // usado par passar o objeto para string no content
    @MockBean
    private PlanetService planetService;


    @Test
    public void createPlanet_WithValidData_ReturnsCreated() throws Exception {

        when(planetService.create(PLANET)).thenReturn(PLANET);

        mockMvc.perform(MockMvcRequestBuilders.post("/planets")
                .content(objectMapper.writeValueAsString(PLANET))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(PLANET));

    }
    @Test
    public void createPlanet_WithInValidData_ReturnsBadRequest() throws Exception {

        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("","","");

        mockMvc.perform(MockMvcRequestBuilders.post("/planets")
                        .content(objectMapper.writeValueAsString(emptyPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(MockMvcRequestBuilders.post("/planets")
                        .content(objectMapper.writeValueAsString(invalidPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void createPlanet_WithExistingName_ReturnsConflict() throws Exception {

        when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/planets")
                .content(objectMapper.writeValueAsString(PLANET))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

    }

}