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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.swplanetapi.common.PlanetConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
        Planet invalidPlanet = new Planet("", "", "");

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

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanets() throws Exception {

        when(planetService.get(1L)).thenReturn(Optional.of(PLANET));

//        mockMvc.perform(MockMvcRequestBuilders.get("/planets")
//                .param("id", "1"))
//                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/planets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));

    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsNotFound() throws Exception {

//        when(planetService.get(anyLong())).thenThrow(RuntimeException.class);
        // sem p when o serviço no teste não retorna nada sendo assim ja daria o erro que queremos para ele laçar
        //not found

        mockMvc.perform(MockMvcRequestBuilders.get("/planets/1"))
                .andExpect(status().isNotFound());

    }
    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() throws Exception {

        when(planetService.getByName("jupiter")).thenReturn(Optional.of(PLANET));

        mockMvc.perform(MockMvcRequestBuilders.get("/planets/name/jupiter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));

    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsPlanet() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/planets/name/name"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void listPlanets_ReturnsFilteredPlanets() throws Exception {

        when(planetService.list(null,null)).thenReturn(PLANETS);
        when(planetService.list(TATOOINE.getTerrain(), TATOOINE.getClimate())).thenReturn(List.of(TATOOINE));

        mockMvc.perform(MockMvcRequestBuilders.get("/planets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(MockMvcRequestBuilders.get("/planets?" + String.format("terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value(TATOOINE));
    }

    @Test
    public void listPlanets_ReturnsNoPlanets() throws Exception {

        when(planetService.list(null,null)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/planets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void removePlanet_WithExistingId_RemovesPlanetFromDatabase() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/planets/1"))
                .andExpect(status().isNoContent());

    }

    @Test
    public void removePlanet_WithUneExistingId_ThrowsException() throws Exception {

        doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/planets/1"))
                .andExpect(status().isNoContent());
    }


}
