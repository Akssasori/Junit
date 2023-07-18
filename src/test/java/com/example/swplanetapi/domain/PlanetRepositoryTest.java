package com.example.swplanetapi.domain;

import static com.example.swplanetapi.common.PlanetConstants.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        PLANET.setId(null);  //depois de cada teste zero o id
        // e necessario pq usamos a conatnte PLANET em todos os testes sendo assim ele sempre tem id no banco
        // e quando usar mais de um persistAndFlush o segunda não vai cosneguir salvar
        //pq o primeiro persistaAndFlush já o salvou e já recebeu id
    }

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        Planet planet = planetRepository.save(PLANET);
        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut.getName()).isEqualTo(PLANET.getName());
        Assertions.assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
        Assertions.assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());
    }

    @Test
    public void createPlanet_WithInValidData_ThrowsException() {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        Assertions.assertThatThrownBy(() -> planetRepository.save(emptyPlanet)).isInstanceOf(RuntimeException.class);
        Assertions.assertThatThrownBy(() -> planetRepository.save(invalidPlanet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void createPlanet_WithExistingName_ThrowsException() {

        Planet planet = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.detach(planet);
        planet.setId(null);

        Assertions.assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);

    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanets() {

        Planet sut = testEntityManager.persistFlushFind(PLANET);
        Optional<Planet> planet = planetRepository.findById(sut.getId());

        Assertions.assertThat(planet.get()).isNotNull();
        Assertions.assertThat(sut.getName()).isEqualTo(PLANET.getName());
        Assertions.assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());

        Assertions.assertThat(planet).isNotEmpty();
        Assertions.assertThat(planet.get()).isEqualTo(sut);


    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsNotFound() {

        Optional<Planet> planetOpt = planetRepository.findById(1L);
        Assertions.assertThat(planetOpt).isEmpty();


    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() {

        Planet sut = testEntityManager.persistFlushFind(PLANET);
        Optional<Planet> planet = planetRepository.findByName(sut.getName());

        Assertions.assertThat(planet.get()).isNotNull();
        Assertions.assertThat(sut.getName()).isEqualTo(PLANET.getName());
        Assertions.assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());

        Assertions.assertThat(planet).isNotEmpty();
        Assertions.assertThat(planet.get()).isEqualTo(sut);

    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsPlanet() {

        Optional<Planet> planetOpt = planetRepository.findByName("exemplo");
        Assertions.assertThat(planetOpt).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    public void listPlanets_ReturnsFilteredPlanets() {

        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(TATOOINE.getClimate(), TATOOINE.getTerrain()));

        List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);
        List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);

        Assertions.assertThat(responseWithoutFilters).isNotEmpty();
        Assertions.assertThat(responseWithoutFilters).hasSize(3);
        Assertions.assertThat(responseWithFilters).isNotEmpty();
        Assertions.assertThat(responseWithFilters).hasSize(1);
        Assertions.assertThat(responseWithoutFilters.get(0)).isEqualTo(TATOOINE);

    }

    @Test
    public void listPlanets_ReturnsNoPlanets() {

        Example<Planet> query = QueryBuilder.makeQuery(new Planet());
        List<Planet> response = planetRepository.findAll(query);

        Assertions.assertThat(response).isNotEmpty();

    }
    @Test
    public void removePlanet_WithExistingId_RemovesPlanetFromDatabase() {

        Planet planet = testEntityManager.persistFlushFind(PLANET);

        planetRepository.deleteById(planet.getId());

        Planet removedPlanet = testEntityManager.find(Planet.class, planet.getId());
        Assertions.assertThat(removedPlanet).isNull();


    }

    @Test
    public void removePlanet_WithUneExistingId_ThrowsException() {
        Assertions.assertThatThrownBy(() -> planetRepository.deleteById(1L)).isInstanceOf(EmptyResultDataAccessException.class);

    }


}
