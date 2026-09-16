package com.rasa.Rasa_be.modules.catalog.repository;

import com.rasa.Rasa_be.modules.catalog.dto.AutoSuggestDto;
import com.rasa.Rasa_be.modules.catalog.entity.Perfume;
import com.rasa.Rasa_be.modules.catalog.entity.enums.PriceTier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerfumeRepository extends JpaRepository<Perfume, UUID>, JpaSpecificationExecutor<Perfume> {

    @Query("SELECT new com.rasa.Rasa_be.modules.catalog.dto.AutoSuggestDto(p.id, p.name, b.name) " +
           "FROM Perfume p JOIN p.brand b " +
           "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "   OR LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY p.name ASC")
    List<AutoSuggestDto> findAutoSuggestMatches(@Param("query") String query, Pageable pageable);

    @EntityGraph(attributePaths = {"brand", "notes", "notes.note", "accords", "accords.accord"})
    @Query("SELECT p FROM Perfume p WHERE p.id = :id")
    Optional<Perfume> findByIdWithDetails(@Param("id") UUID id);

    @Query("""
        SELECT p FROM Perfume p
        LEFT JOIN FETCH p.brand
        WHERE (:allowedPriceTiers IS NULL OR p.priceTier IN :allowedPriceTiers)
        AND (:excludedNoteIds IS NULL OR NOT EXISTS (
            SELECT 1 FROM PerfumeNote pn2 WHERE pn2.perfume = p AND pn2.note.id IN :excludedNoteIds
        ))
        AND (:excludedAccordIds IS NULL OR NOT EXISTS (
            SELECT 1 FROM PerfumeAccord pa2 WHERE pa2.perfume = p AND pa2.accord.id IN :excludedAccordIds
        ))
    """)
    List<Perfume> findRecommendationCandidates(
            @Param("allowedPriceTiers") List<PriceTier> allowedPriceTiers,
            @Param("excludedNoteIds") List<UUID> excludedNoteIds,
            @Param("excludedAccordIds") List<UUID> excludedAccordIds
    );
}
