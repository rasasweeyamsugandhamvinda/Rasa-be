package com.rasa.Rasa_be.modules.catalog.repository;

import com.rasa.Rasa_be.modules.catalog.entity.Accord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AccordRepository extends JpaRepository<Accord, UUID> {

}
