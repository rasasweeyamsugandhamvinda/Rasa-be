package com.rasa.Rasa_be.modules.catalog.service;

import com.rasa.Rasa_be.modules.catalog.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface CatalogService {
    List<AutoSuggestDto> getAutoSuggest(String query);
    Page<PerfumeCardDto> filterPerfumes(PerfumeFilterRequest request, Pageable pageable);
    PerfumeDetailDto getPerfumeDetails(UUID id);
    List<NoteDto> getAllNotes();
    List<AccordDto> getAllAccords();
    List<PerfumeCandidateDto> getRecommendationCandidates(CandidateFilterRequest request);
}
