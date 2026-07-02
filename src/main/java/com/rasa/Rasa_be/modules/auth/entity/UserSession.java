package com.rasa.Rasa_be.modules.auth.entity;

import com.rasa.Rasa_be.modules.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_sessions", schema = "auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token_hash", nullable = false, unique = true)
    private String refreshTokenHash;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "ip_address", length = 45) // Length 45 handles long IPv6 addresses
    private String ipAddress;

    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked = false;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public void revoke() {
        this.isRevoked = true;
    }
}
