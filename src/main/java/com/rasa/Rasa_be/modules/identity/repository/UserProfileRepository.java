package com.rasa.Rasa_be.modules.identity.repository;

import com.rasa.Rasa_be.modules.identity.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}
