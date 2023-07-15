package com.example.swplanetapi.domain;

import static com.example.swplanetapi.common.PlanetConstants.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

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
        Planet invalidPlanet = new Planet("","","");

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
}
