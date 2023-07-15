package com.example.swplanetapi.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;

import java.util.*;
import java.util.function.Predicate;

import static com.example.swplanetapi.common.PlanetConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = PlanetService.class)
public class PlanetServiceTest {
    //@Autowired
    @InjectMocks // cria a dependencia dele e todas já são injetadas com mocks
    private PlanetService planetService;

    //@MockBean
    @Mock
    private PlanetRepository planetRepository;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {

        when(planetRepository.save(PLANET)).thenReturn(PLANET);

        Planet sut = planetService.create(PLANET);
        Assertions.assertThat(sut).isEqualTo(PLANET);

    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {

        when(planetRepository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);
        Assertions.assertThatThrownBy(() -> planetService.create(INVALID_PLANET));

    }
    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() {

        when(planetRepository.findById(anyLong())).thenReturn(Optional.of(PLANET));
        Optional<Planet> planet = planetService.get(1L);

        Assertions.assertThat(planet).isNotEmpty();
        Assertions.assertThat(planet.get()).isEqualTo(PLANET);

    }
    @Test
    public void getPlanet_ByUnexistingId_ReturnsPlanet() {
        when(planetRepository.findById(anyLong())).thenReturn(Optional.empty());
        Optional<Planet> planet = planetService.get(1L);

        Assertions.assertThat(planet).isEmpty();
        Assertions.assertThat(planet).isEqualTo(OPTIONAL_EMPYTY_PLANET);
    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() {

        when(planetRepository.findByName(anyString())).thenReturn(Optional.of(PLANET));
        Optional<Planet> planet = planetService.getByName("tatooine");

        Assertions.assertThat(planet).isNotEmpty();
        Assertions.assertThat(planet.get()).isEqualTo(PLANET);
    }
    @Test
    public void getPlanet_ByUnexistingName_ReturnsPlanet() {

        when(planetRepository.findByName(anyString())).thenReturn(Optional.empty());
        Optional<Planet> planet = planetService.getByName("Unexisting name");

        Assertions.assertThat(planet).isEmpty();
        Assertions.assertThat(planet).isEqualTo(OPTIONAL_EMPYTY_PLANET);

    }
    @Test
    public void listPlanets_ReturnsAllPlanets() {

        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PLANET.getClimate(), PLANET.getTerrain()));
        when(planetRepository.findAll(query)).thenReturn(List.of(PLANET));
        List<Planet> list = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        Assertions.assertThat(list).isNotEmpty();
        Assertions.assertThat(list).hasSize(1);
        Assertions.assertThat(list.get(0)).isEqualTo(PLANET);

    }
    @Test
    public void listPlanets_ReturnsNoPlanets() {

        // List<Planet> planets = new ArrayList<>(); << poderia usar uma lista vazia
        when(planetRepository.findAll(any())).thenReturn(Collections.emptyList());
        List<Planet> list = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        Assertions.assertThat(list).isEmpty();


    }


}
