package com.rasa.Rasa_be.modules.identity.controller;

import com.rasa.Rasa_be.config.security.UserPrincipal;
import com.rasa.Rasa_be.modules.identity.dto.WardrobeReviewRequest;
import com.rasa.Rasa_be.modules.identity.repository.projection.CollectionItemProjection;
import com.rasa.Rasa_be.modules.identity.service.InteractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
@Slf4j
public class CollectionController {

    private final InteractionService interactionService;

    @GetMapping("/library")
    public ResponseEntity<List<CollectionItemProjection>> getLibrary(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UUID userId = userPrincipal.id();
        log.debug("Fetching Library for user: {}", userId);

        return ResponseEntity.ok(interactionService.getUserLibrary(userId));
    }

    @GetMapping("/wardrobe")
    public ResponseEntity<List<CollectionItemProjection>> getWardrobe(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UUID userId = userPrincipal.id();
        log.debug("Fetching Wardrobe for user: {}", userId);

        return ResponseEntity.ok(interactionService.getUserWardrobe(userId));
    }

    @PostMapping("/library/{perfumeId}")
    public ResponseEntity<Void> addToLibrary(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID perfumeId) {

        UUID userId = userPrincipal.id();
        log.debug("Adding perfume {} to Library for user: {}", perfumeId, userId);

        interactionService.addToLibrary(userId, perfumeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/wardrobe/{perfumeId}")
    public ResponseEntity<Void> moveToWardrobe(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID perfumeId,
            @Valid @RequestBody WardrobeReviewRequest reviewRequest) {

        UUID userId = userPrincipal.id();
        log.debug("Moving perfume {} to Wardrobe for user: {}", perfumeId, userId);

        interactionService.moveToWardrobe(
                userId,
                perfumeId,
                reviewRequest.getPerceivedSillage(),
                reviewRequest.getPerceivedLongevity()
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{perfumeId}")
    public ResponseEntity<Void> removeFromCollection(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID perfumeId) {

        UUID userId = userPrincipal.id();
        log.debug("Removing perfume {} from collections for user: {}", perfumeId, userId);

        interactionService.removeInteraction(userId, perfumeId);
        return ResponseEntity.ok().build();
    }
}