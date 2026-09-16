package com.rasa.Rasa_be.modules.identity.service.impl;

import com.rasa.Rasa_be.modules.identity.entity.Interaction;
import com.rasa.Rasa_be.modules.identity.entity.enums.InteractionSource;
import com.rasa.Rasa_be.modules.identity.entity.enums.SignalType;
import com.rasa.Rasa_be.modules.identity.repository.InteractionRepository;
import com.rasa.Rasa_be.modules.identity.repository.projection.CollectionItemProjection;
import com.rasa.Rasa_be.modules.identity.service.InteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractionServiceImpl implements InteractionService {

    private final InteractionRepository interactionRepository;

    @Override
    @Transactional
    public void addToLibrary(UUID userId, UUID perfumeId) {
        Interaction interaction = interactionRepository
                .findByUserIdAndPerfumeIdAndSource(userId, perfumeId, InteractionSource.WISHLIST)
                .orElseGet(() -> {
                    Interaction i = new Interaction();
                    i.setUserId(userId);
                    i.setPerfumeId(perfumeId);
                    i.setSource(InteractionSource.WISHLIST);
                    return i;
                });

        interactionRepository.save(interaction);
    }

    @Override
    @Transactional
    public void moveToWardrobe(UUID userId, UUID perfumeId, int perceivedSillage, int perceivedLongevity) {
        Interaction interaction = interactionRepository
                .findByUserIdAndPerfumeIdAndSource(userId, perfumeId, InteractionSource.WISHLIST)
                .orElseGet(() -> {
                    Interaction i = new Interaction();
                    i.setUserId(userId);
                    i.setPerfumeId(perfumeId);
                    return i;
                });

        interaction.setSource(InteractionSource.POST_USE_REVIEW);
        interaction.setSignalType(SignalType.POSITIVE);
        interaction.setPerceivedSillage(perceivedSillage);
        interaction.setPerceivedLongevity(perceivedLongevity);

        interactionRepository.save(interaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionItemProjection> getUserLibrary(UUID userId) {
        return interactionRepository.getUserCollectionBySource(
                userId,
                InteractionSource.WISHLIST.name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionItemProjection> getUserWardrobe(UUID userId) {
        return interactionRepository.getUserCollectionBySource(
                userId,
                InteractionSource.POST_USE_REVIEW.name()
        );
    }

    @Override
    @Transactional
    public void removeInteraction(UUID userId, UUID perfumeId) {
        interactionRepository.findByUserIdAndPerfumeIdAndSource(userId, perfumeId, InteractionSource.WISHLIST)
                .ifPresent(interactionRepository::delete);

        interactionRepository.findByUserIdAndPerfumeIdAndSource(userId, perfumeId, InteractionSource.POST_USE_REVIEW)
                .ifPresent(interactionRepository::delete);

        log.debug("Successfully removed perfume {} from collections for user {}", perfumeId, userId);
    }
}