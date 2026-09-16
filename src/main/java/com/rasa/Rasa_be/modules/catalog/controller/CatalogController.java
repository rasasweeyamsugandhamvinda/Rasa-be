package com.rasa.Rasa_be.modules.catalog.controller;

import com.rasa.Rasa_be.modules.catalog.dto.*;
import com.rasa.Rasa_be.modules.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalogService;

    @GetMapping("/suggest")
    public ResponseEntity<List<AutoSuggestDto>> suggest(@RequestParam String q) {
        return ResponseEntity.ok(catalogService.getAutoSuggest(q));
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<PerfumeCardDto>> filter(@RequestBody PerfumeFilterRequest request, Pageable pageable) {
        return ResponseEntity.ok(catalogService.filterPerfumes(request, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfumeDetailDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogService.getPerfumeDetails(id));
    }

    @GetMapping("/notes")
    public ResponseEntity<List<NoteDto>> getAllNotes() {
        return ResponseEntity.ok(catalogService.getAllNotes());
    }

    @GetMapping("/accords")
    public ResponseEntity<List<AccordDto>> getAllAccords() {
        return ResponseEntity.ok(catalogService.getAllAccords());
    }
}
