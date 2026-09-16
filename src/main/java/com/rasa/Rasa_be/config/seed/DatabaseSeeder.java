package com.rasa.Rasa_be.config.seed;

import com.rasa.Rasa_be.modules.auth.domain.AuthProvider;
import com.rasa.Rasa_be.modules.auth.entity.User;
import com.rasa.Rasa_be.modules.auth.repository.UserRepository;
import com.rasa.Rasa_be.modules.catalog.entity.Accord;
import com.rasa.Rasa_be.modules.catalog.entity.Note;
import com.rasa.Rasa_be.modules.catalog.repository.AccordRepository;
import com.rasa.Rasa_be.modules.catalog.repository.NoteRepository;
import com.rasa.Rasa_be.modules.identity.dto.LifestyleDto;
import com.rasa.Rasa_be.modules.identity.dto.OnboardingRequestDto;
import com.rasa.Rasa_be.modules.identity.dto.PreferencesDto;
import com.rasa.Rasa_be.modules.identity.entity.enums.*;
import com.rasa.Rasa_be.modules.identity.service.IdentityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Profile("!prod") // Safety guardrail: Never run in production
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdentityService identityService;
    private final NoteRepository noteRepository;
    private final AccordRepository accordRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains users. Skipping seed execution.");
            return;
        }

        log.info("Seeding 8 Test Personas into the database...");

        // Fetch catalog UUIDs to use for explicit preferences (if catalog is populated)
        List<UUID> allNoteIds = noteRepository.findAll().stream().map(Note::getId).collect(Collectors.toList());
        List<UUID> allAccordIds = accordRepository.findAll().stream().map(Accord::getId).collect(Collectors.toList());

        List<UUID> someNotes = allNoteIds.size() >= 2 ? allNoteIds.subList(0, 2) : new ArrayList<>();
        List<UUID> someAccords = allAccordIds.size() >= 2 ? allAccordIds.subList(0, 2) : new ArrayList<>();

        // Persona 1: The Office Novice (Safe, clean, low projection needs)
        seedUser("novice.office@rasa.com", "Password123!",
                ExperienceLevel.NOVICE, PrimaryEnvironment.AC_OFFICE, SweatLevel.LOW,
                VibePreference.PROFESSIONAL, BudgetPreference.MID_RANGE,
                null, null);

        // Persona 2: The Picky Connoisseur (High sweat, outdoors, strict explicit preferences)
        seedUser("connoisseur.picky@rasa.com", "Password123!",
                ExperienceLevel.CONNOISSEUR, PrimaryEnvironment.OUTDOORS, SweatLevel.HIGH,
                VibePreference.FRESH_CASUAL, BudgetPreference.NICHE,
                someNotes, someAccords);

        // Persona 3: The Humid Commuter (Budget conscious, heavy sweater outdoors)
        seedUser("commuter.humid@rasa.com", "Password123!",
                ExperienceLevel.NOVICE, PrimaryEnvironment.OUTDOORS, SweatLevel.HIGH,
                VibePreference.FRESH_CASUAL, BudgetPreference.BUDGET,
                null, null);

        // Persona 4: The Romantic Connoisseur (Low sweat, wants seductive luxury)
        seedUser("romantic.luxury@rasa.com", "Password123!",
                ExperienceLevel.CONNOISSEUR, PrimaryEnvironment.AC_OFFICE, SweatLevel.LOW,
                VibePreference.SEDUCTIVE, BudgetPreference.LUXURY,
                someNotes, null);

        // Persona 5: The Blank Slate (Bare minimum onboarding, engine must guess)
        seedUser("blank.slate@rasa.com", "Password123!",
                ExperienceLevel.NOVICE, null, null, null, null,
                null, null);

        // Persona 6: The Gym Bro (Active, loud, designer)
        seedUser("active.gym@rasa.com", "Password123!",
                ExperienceLevel.NOVICE, PrimaryEnvironment.ACTIVE_GYM, SweatLevel.HIGH,
                VibePreference.LOUD_ATTENTION_GRABBING, BudgetPreference.DESIGNER,
                null, null);

        // Persona 7: The Balanced Professional (Moderate everything)
        seedUser("balanced.pro@rasa.com", "Password123!",
                ExperienceLevel.CONNOISSEUR, PrimaryEnvironment.AC_OFFICE, SweatLevel.MODERATE,
                VibePreference.PROFESSIONAL, BudgetPreference.DESIGNER,
                null, someAccords);

        // Persona 8: The Niche Seductive (High budget, looking for attention)
        seedUser("niche.seductive@rasa.com", "Password123!",
                ExperienceLevel.CONNOISSEUR, PrimaryEnvironment.OUTDOORS, SweatLevel.LOW,
                VibePreference.LOUD_ATTENTION_GRABBING, BudgetPreference.NICHE,
                someNotes, someAccords);

        log.info("Database seeding completed successfully. 8 Personas injected.");
    }

    private void seedUser(String email, String rawPassword,
                          ExperienceLevel experience, PrimaryEnvironment env, SweatLevel sweat,
                          VibePreference vibe, BudgetPreference budget,
                          List<UUID> likedNotes, List<UUID> preferredAccords) {

        // 1. Create Auth User
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setVerified(true);
        user.setAuthProvider(AuthProvider.LOCAL);
        user = userRepository.save(user);

        // 2. Build Identity Onboarding Request
        OnboardingRequestDto request = new OnboardingRequestDto();
        request.setExperienceLevel(experience);

        if (env != null) {
            LifestyleDto lifestyle = new LifestyleDto();
            lifestyle.setCity("Raipur");
            lifestyle.setState("Chhattisgarh");
            lifestyle.setPrimaryEnvironment(env);
            lifestyle.setSweatLevel(sweat);
            lifestyle.setVibePreference(vibe);
            lifestyle.setBudgetPreference(budget);
            request.setLifestyle(lifestyle);
        }

        if (likedNotes != null || preferredAccords != null) {
            PreferencesDto prefs = new PreferencesDto();
            prefs.setLikedNoteIds(likedNotes != null ? likedNotes : new ArrayList<>());
            prefs.setDislikedNoteIds(new ArrayList<>());
            prefs.setPreferredAccordIds(preferredAccords != null ? preferredAccords : new ArrayList<>());
            prefs.setDislikedAccordIds(new ArrayList<>());
            request.setPreferences(prefs);
        }

        // 3. Trigger Just-In-Time Provisioning
        identityService.processOnboarding(user.getId(), request);
    }
}