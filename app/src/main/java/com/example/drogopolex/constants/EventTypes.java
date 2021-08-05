package com.example.drogopolex.constants;

import java.util.Arrays;
import java.util.List;

public class EventTypes {
    private static final List<String> eventTypes = Arrays.asList(
            "Wybierz typ", //TODO: there must be a better way to set default value
            "Wypadek",
            "Korek",
            "Patrol Policji",
            "Roboty Drogowe"
    );

    public static List<String> getEventTypes() {
        return eventTypes;
    }
}
