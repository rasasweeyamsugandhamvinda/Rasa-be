package com.rasa.Rasa_be.modules.catalog.service.impl;

import com.rasa.Rasa_be.modules.catalog.dto.*;
import com.rasa.Rasa_be.modules.catalog.entity.*;
import com.rasa.Rasa_be.modules.catalog.entity.enums.PriceTier;
import com.rasa.Rasa_be.modules.catalog.repository.*;
import com.rasa.Rasa_be.modules.catalog.service.CatalogService;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
    private final PerfumeRepository perfumeRepository;
    private final NoteRepository noteRepository;
    private final AccordRepository accordRepository;
    private final PerfumeNoteRepository perfumeNoteRepository;
    private final PerfumeAccordRepository perfumeAccordRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AutoSuggestDto> getAutoSuggest(String query) {
        log.debug("Executing auto-suggest for query: {}", query);
        return perfumeRepository.findAutoSuggestMatches(query, PageRequest.of(0, 10));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PerfumeCardDto> filterPerfumes(PerfumeFilterRequest request, Pageable pageable) {
        log.debug("Filtering perfumes with request: {}", request);
        Specification<Perfume> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Perfume, Brand> brandJoin = root.join("brand", JoinType.INNER);

            if (request.getBrandName() != null && !request.getBrandName().isBlank()) {
                predicates.add(cb.equal(brandJoin.get("name"), request.getBrandName()));
            }
            if (request.getGender() != null) {
                predicates.add(cb.equal(root.get("gender"), request.getGender()));
            }
            if (request.getConcentration() != null) {
                predicates.add(cb.equal(root.get("concentration"), request.getConcentration()));
            }
            if (request.getNoteNames() != null && !request.getNoteNames().isEmpty()) {
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<PerfumeNote> pnRoot = subquery.from(PerfumeNote.class);
                Join<PerfumeNote, Note> noteJoin = pnRoot.join("note", JoinType.INNER);
                subquery.select(cb.literal(1))
                        .where(
                                cb.equal(pnRoot.get("perfume"), root),
                                noteJoin.get("name").in(request.getNoteNames())
                        );
                predicates.add(cb.exists(subquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return perfumeRepository.findAll(spec, pageable).map(p -> new PerfumeCardDto(
                p.getId(), p.getName(), p.getBrand().getName(),
                p.getGender(), p.getConcentration(), p.getImageUrl(), p.getPriceTier()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public PerfumeDetailDto getPerfumeDetails(UUID id) {
        log.debug("Fetching perfume details for id: {}", id);
        Perfume p = perfumeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Perfume not found with id: " + id));

        Set<NoteDto> notes = p.getNotes().stream()
                .map(pn -> new NoteDto(pn.getNote().getId(), pn.getNote().getName(), pn.getId().getNoteType()))
                .collect(Collectors.toSet());

        Set<AccordDto> accords = p.getAccords().stream()
                .map(pa -> new AccordDto(pa.getAccord().getId(), pa.getAccord().getName(), pa.getWeight()))
                .collect(Collectors.toSet());

        return new PerfumeDetailDto(
                p.getId(), p.getName(), p.getBrand().getName(), p.getBrand().getCountry(),
                p.getGender(), p.getConcentration(), p.getPriceTier(),
                p.getLongevity(), p.getSillage(), p.getImageUrl(),
                notes, accords
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteDto> getAllNotes() {
        return noteRepository.findByIsCanonicalTrueOrderByNameAsc().stream()
                .map(note -> new NoteDto(note.getId(), note.getName(), null)) // Ensure you return a DTO, not the raw entity
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccordDto> getAllAccords() {
        return accordRepository.findAll().stream()
                .map(a -> new AccordDto(a.getId(), a.getName(), null)) // weight can be null for general catalog lists
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerfumeCandidateDto> getRecommendationCandidates(CandidateFilterRequest request) {

        // 1. Compute allowed price tiers using strict ordinal ranking
        List<PriceTier> allowedPriceTiers = request.getMaxPriceTier() == null ? null :
                Arrays.stream(PriceTier.values())
                        .filter(t -> t.ordinal() <= request.getMaxPriceTier().ordinal())
                        .collect(Collectors.toList());

        // 2. Standardize empty lists to null for clean JPQL evaluation
        List<UUID> excludedNotes = (request.getExcludedNoteIds() == null || request.getExcludedNoteIds().isEmpty())
                ? null : request.getExcludedNoteIds();

        List<UUID> excludedAccords = (request.getExcludedAccordIds() == null || request.getExcludedAccordIds().isEmpty())
                ? null : request.getExcludedAccordIds();

        // 3. Fetch candidates (Single flat query, no cartesian product)
        List<Perfume> perfumes = perfumeRepository.findRecommendationCandidates(
                allowedPriceTiers, excludedNotes, excludedAccords
        );

        if (perfumes.isEmpty()) {
            return List.of();
        }

        List<UUID> perfumeIds = perfumes.stream().map(Perfume::getId).toList();

        // 4. Batch fetch plural associations and map them to (EntityID -> EntityName)
        Map<UUID, Map<UUID, String>> noteMapByPerfume = perfumeNoteRepository.findByPerfumeIdIn(perfumeIds).stream()
                .collect(Collectors.groupingBy(
                        pn -> pn.getPerfume().getId(),
                        Collectors.toMap(
                                pn -> pn.getNote().getId(),
                                pn -> pn.getNote().getName(),
                                (existing, replacement) -> existing // safety merge for duplicates
                        )
                ));

        Map<UUID, Map<UUID, String>> accordMapByPerfume = perfumeAccordRepository.findByPerfumeIdIn(perfumeIds).stream()
                .collect(Collectors.groupingBy(
                        pa -> pa.getPerfume().getId(),
                        Collectors.toMap(
                                pa -> pa.getAccord().getId(),
                                pa -> pa.getAccord().getName(),
                                (existing, replacement) -> existing // safety merge for duplicates
                        )
                ));

        // 5. Assemble and return
        return perfumes.stream().map(p -> new PerfumeCandidateDto(
                p.getId(),
                p.getName(),
                p.getBrand().getName(),
                noteMapByPerfume.getOrDefault(p.getId(), Map.of()),
                accordMapByPerfume.getOrDefault(p.getId(), Map.of()),
                p.getLongevity(),
                p.getSillage()
        )).collect(Collectors.toList());
    }
}
