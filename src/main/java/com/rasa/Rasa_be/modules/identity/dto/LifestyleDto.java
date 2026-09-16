package com.rasa.Rasa_be.modules.identity.dto;

import com.rasa.Rasa_be.modules.identity.entity.enums.BudgetPreference;
import com.rasa.Rasa_be.modules.identity.entity.enums.PrimaryEnvironment;
import com.rasa.Rasa_be.modules.identity.entity.enums.SweatLevel;
import com.rasa.Rasa_be.modules.identity.entity.enums.VibePreference;
import lombok.Data;

@Data
public class LifestyleDto {

    private String city;
    private String state;
    private PrimaryEnvironment primaryEnvironment;
    private SweatLevel sweatLevel;
    private VibePreference vibePreference;
    private BudgetPreference budgetPreference;
}
