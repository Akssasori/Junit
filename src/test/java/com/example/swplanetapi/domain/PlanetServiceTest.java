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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static com.example.swplanetapi.common.PlanetConstants.*;
import static org.mockito.ArgumentMatchers.anyLong;
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
        Optional<Planet> planet = planetService.get(anyLong());
        Assertions.assertThat(planet.get()).isEqualTo(PLANET);

    }
    @Test
    public void getPlanet_ByUnexistingId_ReturnsPlanet() {
        when(planetRepository.findById(anyLong())).thenReturn(Optional.empty());
        Optional<Planet> planet = planetService.get(anyLong());
        Assertions.assertThat(planet).isEqualTo(OPTIONAL_EMPYTY_PLANET);
    }

}
