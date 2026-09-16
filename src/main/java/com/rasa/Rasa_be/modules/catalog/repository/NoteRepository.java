package com.rasa.Rasa_be.modules.catalog.repository;

import com.rasa.Rasa_be.modules.catalog.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<Note> findByIsCanonicalTrueOrderByNameAsc();
}
