package com.rasa.Rasa_be.modules.auth.entity;

import com.rasa.Rasa_be.modules.auth.domain.AuthProvider;
import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    // Orphan removal ensures if we remove a session from the list, it gets deleted in the DB.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OAuthIdentity> oauthIdentities = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserSession> sessions = new ArrayList<>();

    public void addSession(UserSession session) {
        sessions.add(session);
        session.setUser(this);
    }

    public void addOAuthIdentity(OAuthIdentity identity) {
        oauthIdentities.add(identity);
        identity.setUser(this);
    }
}
