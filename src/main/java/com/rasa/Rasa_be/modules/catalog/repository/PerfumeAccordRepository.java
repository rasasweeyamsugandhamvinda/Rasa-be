package com.rasa.Rasa_be.modules.catalog.repository;

import com.rasa.Rasa_be.modules.catalog.entity.PerfumeAccord;
import com.rasa.Rasa_be.modules.catalog.entity.PerfumeAccordId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerfumeAccordRepository extends JpaRepository<PerfumeAccord, PerfumeAccordId> {
    List<PerfumeAccord> findByPerfumeIdIn(List<UUID> perfumeIds);
}