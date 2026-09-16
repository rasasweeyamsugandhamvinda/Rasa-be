package com.rasa.Rasa_be.modules.identity.repository;

import com.rasa.Rasa_be.modules.identity.entity.LifestyleProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LifestyleProfileRepository extends JpaRepository<LifestyleProfile, UUID> {
}
