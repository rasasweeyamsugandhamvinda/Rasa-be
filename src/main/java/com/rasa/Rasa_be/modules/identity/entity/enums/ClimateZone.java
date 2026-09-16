package com.rasa.Rasa_be.modules.identity.entity.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ClimateZone {
    @JsonProperty("tropical_humid") TROPICAL_HUMID,
    @JsonProperty("hot_humid") HOT_HUMID,
    @JsonProperty("hot_semi_arid") HOT_SEMI_ARID,
    @JsonProperty("hot_arid") HOT_ARID,
    @JsonProperty("subtropical") SUBTROPICAL,
    @JsonProperty("moderate_temperate") MODERATE_TEMPERATE,
    @JsonProperty("cool_temperate") COOL_TEMPERATE,
    @JsonProperty("mountain_cool") MOUNTAIN_COOL
}