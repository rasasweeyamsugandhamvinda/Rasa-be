package com.rasa.Rasa_be.modules.identity;

import com.rasa.Rasa_be.modules.identity.entity.FragrancePreferences;
import com.rasa.Rasa_be.modules.identity.entity.UserProfile;
import com.rasa.Rasa_be.modules.identity.repository.FragrancePreferencesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FragrancePreferencesArrayTest {

    @Autowired
    private FragrancePreferencesRepository preferencesRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Rollback
    public void testUuidArrayMappingAndQuery() {
        // 1. Setup mock auth UserProfile
        UUID mockAuthId = UUID.randomUUID();
        UserProfile profile = new UserProfile();
        profile.setUserId(mockAuthId);
        profile.setOnboardingCompleted(false);
        entityManager.persist(profile);

        // 2. Setup mock Catalog Note UUIDs
        UUID bergamotId = UUID.randomUUID();
        UUID vetiverId = UUID.randomUUID();

        // 3. Save Preferences with UUID arrays
        FragrancePreferences prefs = new FragrancePreferences();
        prefs.setUserId(mockAuthId);
        prefs.setLikedNoteIds(new UUID[]{bergamotId, vetiverId});
        preferencesRepository.saveAndFlush(prefs);

        entityManager.clear(); // Force Hibernate to detach and fetch fresh from DB

        // 4. Test Read-back formatting
        FragrancePreferences retrieved = preferencesRepository.findById(mockAuthId).orElseThrow();
        assertThat(retrieved.getLikedNoteIds()).containsExactlyInAnyOrder(bergamotId, vetiverId);

        // 5. Test PostgreSQL ANY() Native Query execution
        List<FragrancePreferences> matchers = preferencesRepository.findUsersWhoLikeNote(bergamotId);
        assertThat(matchers).hasSize(1);
        assertThat(matchers.getFirst().getUserId()).isEqualTo(mockAuthId);
    }
}