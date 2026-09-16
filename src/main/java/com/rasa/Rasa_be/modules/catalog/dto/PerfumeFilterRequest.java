package com.rasa.Rasa_be.modules.catalog.dto;

import com.rasa.Rasa_be.modules.catalog.entity.enums.*;
import lombok.Data;
import java.util.List;

@Data
public class PerfumeFilterRequest {
    private String brandName;
    private Gender gender;
    private Concentration concentration;
    private List<String> noteNames;
}
