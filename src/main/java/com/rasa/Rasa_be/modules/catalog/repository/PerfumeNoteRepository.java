package com.rasa.Rasa_be.modules.catalog.repository;

import com.rasa.Rasa_be.modules.catalog.entity.PerfumeNote;
import com.rasa.Rasa_be.modules.catalog.entity.PerfumeNoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerfumeNoteRepository extends JpaRepository<PerfumeNote, PerfumeNoteId> {
    List<PerfumeNote> findByPerfumeIdIn(List<UUID> perfumeIds);
}
