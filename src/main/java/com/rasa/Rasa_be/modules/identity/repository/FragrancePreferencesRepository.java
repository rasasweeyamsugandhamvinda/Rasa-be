package com.rasa.Rasa_be.modules.identity.repository;

import com.rasa.Rasa_be.modules.identity.entity.FragrancePreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FragrancePreferencesRepository extends JpaRepository<FragrancePreferences, UUID> {

    @Query(value = "SELECT * FROM identity.fragrance_preferences WHERE :noteId = ANY(liked_note_ids)", nativeQuery = true)
    List<FragrancePreferences> findUsersWhoLikeNote(@Param("noteId") UUID noteId);
}