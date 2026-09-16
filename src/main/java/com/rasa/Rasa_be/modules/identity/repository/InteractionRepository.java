package com.rasa.Rasa_be.modules.identity.repository;

import com.rasa.Rasa_be.modules.identity.entity.Interaction;
import com.rasa.Rasa_be.modules.identity.entity.enums.InteractionSource;
import com.rasa.Rasa_be.modules.identity.entity.enums.SignalType;
import com.rasa.Rasa_be.modules.identity.repository.projection.AccordAffinity;
import com.rasa.Rasa_be.modules.identity.repository.projection.CollectionItemProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface InteractionRepository extends JpaRepository<Interaction, UUID> {

    @Query("SELECT i.perfumeId FROM Interaction i WHERE i.userProfile.userId = :userId AND i.source = :source")
    Set<UUID> findPerfumeIdsByUserIdAndSource(@Param("userId") UUID userId, @Param("source") InteractionSource source);

    Optional<Interaction> findByUserIdAndPerfumeIdAndSource(UUID userId, UUID perfumeId, InteractionSource source);

    long countByUserIdAndSourceAndSignalType(UUID userId, InteractionSource source, SignalType signalType);

    @Query(value = """
        SELECT pa.accord_id as accordId, SUM(pa.weight) as totalWeight
        FROM identity.interactions i
        JOIN catalog.perfume_accords pa ON i.perfume_id = pa.perfume_id
        WHERE i.user_id = :userId
          AND i.source = 'POST_USE_REVIEW'
          AND i.signal_type = 'POSITIVE'
        GROUP BY pa.accord_id
        ORDER BY totalWeight DESC
        """, nativeQuery = true)
    List<AccordAffinity> findImplicitAccordAffinities(@Param("userId") UUID userId);

    @Query(value = """
        SELECT 
            i.id as interactionId,
            i.perfume_id as perfumeId,
            p.name as perfumeName,
            p.brand_name as brandName,
            i.source as source,
            i.perceived_sillage as perceivedSillage,
            i.perceived_longevity as perceivedLongevity,
            i.created_at as createdAt
        FROM identity.interactions i
        JOIN catalog.perfumes p ON i.perfume_id = p.id
        WHERE i.user_id = :userId 
          AND i.source = :source
        ORDER BY i.created_at DESC
        """, nativeQuery = true)
    List<CollectionItemProjection> getUserCollectionBySource(
            @Param("userId") UUID userId,
            @Param("source") String source);
}
