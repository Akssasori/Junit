package com.example.swplanetapi.common;

import com.example.swplanetapi.domain.Planet;

import java.util.Optional;

public class PlanetConstants {
    public static final Planet PLANET = new Planet("name","climate","terrain");
    public static final Planet INVALID_PLANET = new Planet("","","");
    public static final Optional<Planet> OPTIONAL_EMPYTY_PLANET = Optional.empty();
}
