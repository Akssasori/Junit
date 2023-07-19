package com.example.swplanetapi;

import com.example.swplanetapi.domain.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static com.example.swplanetapi.common.PlanetConstants.TATOOINE;
import static org.assertj.core.api.Assertions.assertThat;

import static com.example.swplanetapi.common.PlanetConstants.PLANET;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = { "/import_planets.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/remove_planets.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PlanetIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createPlanet_ReturnsCreated() {
        ResponseEntity<Planet> sut = restTemplate.postForEntity("/planets", PLANET, Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(sut.getBody().getId()).isNotNull();
        assertThat(sut.getBody().getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getBody().getClimate()).isEqualTo(PLANET.getClimate());
        assertThat(sut.getBody().getTerrain()).isEqualTo(PLANET.getTerrain());
    }

    @Test
    public void getPlanet_ReturnsPlanet() {
        ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/1", Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isEqualTo(TATOOINE);
    }
    @Test
    public void getPlanetByName_ReturnsPlanet() {
        ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/name/Tatooine", Planet.class);
        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanets_ReturnsAllPlanets() {

        String url = "/planets?" + String.format("terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate());
        ResponseEntity<Planet[]> sut = restTemplate.getForEntity(url, Planet[].class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Arrays.stream(sut.getBody()).findFirst().get()).isEqualTo(TATOOINE);
        assertThat(sut.getBody().length).isEqualTo(1);

        ResponseEntity<Planet[]> sut2 = restTemplate.getForEntity("/planets", Planet[].class);

        assertThat(sut2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut2.getBody().length).isGreaterThan(1);
        assertThat(sut2.getBody()).hasSize(3);

    }

    @Test
    public void listPlanets_ByClimate_ReturnsPlanets() {

        String url = "/planets?" + String.format("climate=%s", TATOOINE.getClimate());
        ResponseEntity<Planet[]> sut = restTemplate.getForEntity(url, Planet[].class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Arrays.stream(sut.getBody()).findFirst().get()).isEqualTo(TATOOINE);
        assertThat(sut.getBody().length).isEqualTo(1);

    }

    @Test
    public void listPlanets_ByTerrain_ReturnsPlanets() {

        String url = "/planets?" + String.format("terrain=%s", TATOOINE.getTerrain());
        ResponseEntity<Planet[]> sut = restTemplate.getForEntity(url, Planet[].class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Arrays.stream(sut.getBody()).findFirst().get()).isEqualTo(TATOOINE);
        assertThat(sut.getBody().length).isEqualTo(1);
    }

    @Test
    public void removePlanet_ReturnsNoContent() {

        ResponseEntity<Void> sut = restTemplate.exchange("/planets/1", HttpMethod.DELETE , null, Void.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
