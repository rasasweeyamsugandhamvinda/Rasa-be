-- ============================================================================
-- V4: Create Identity Schema for User Profiles, Lifestyle, Preferences & Interactions
-- ============================================================================

-- 1. Schema
CREATE SCHEMA IF NOT EXISTS identity;

-- 2. Automatic Updated-At Trigger Function
CREATE OR REPLACE FUNCTION identity.update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3. User Profiles
CREATE TABLE identity.user_profiles (
    user_id             UUID PRIMARY KEY,
    experience_level    VARCHAR(50),
    onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,

    -- Audit
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_experience_level CHECK (experience_level IN ('NOVICE', 'CONNOISSEUR'))
);

CREATE TRIGGER trg_user_profiles_updated_at
    BEFORE UPDATE ON identity.user_profiles
    FOR EACH ROW EXECUTE FUNCTION identity.update_timestamp();

-- 4. Lifestyle Profiles
CREATE TABLE identity.lifestyle_profiles (
    user_id             UUID PRIMARY KEY REFERENCES identity.user_profiles(user_id) ON DELETE CASCADE,
    city                VARCHAR(255),
    state               VARCHAR(255),
    primary_environment VARCHAR(50),
    sweat_level         VARCHAR(50),
    vibe_preference     VARCHAR(50),
    budget_preference   VARCHAR(50),

    -- Audit
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_primary_environment CHECK (primary_environment IN ('AC_OFFICE', 'OUTDOORS', 'ACTIVE_GYM')),
    CONSTRAINT chk_sweat_level CHECK (sweat_level IN ('LOW', 'MODERATE', 'HIGH')),
    CONSTRAINT chk_vibe_preference CHECK (vibe_preference IN ('PROFESSIONAL', 'SEDUCTIVE', 'FRESH_CASUAL', 'LOUD_ATTENTION_GRABBING')),
    CONSTRAINT chk_budget_preference CHECK (budget_preference IN ('BUDGET', 'MID_RANGE', 'DESIGNER', 'NICHE', 'LUXURY'))
);

CREATE TRIGGER trg_lifestyle_profiles_updated_at
    BEFORE UPDATE ON identity.lifestyle_profiles
    FOR EACH ROW EXECUTE FUNCTION identity.update_timestamp();

-- 5. Fragrance Preferences
CREATE TABLE identity.fragrance_preferences (
    user_id              UUID PRIMARY KEY REFERENCES identity.user_profiles(user_id) ON DELETE CASCADE,
    liked_note_ids       UUID[],
    disliked_note_ids    UUID[],
    preferred_accord_ids UUID[],
    disliked_accord_ids  UUID[],

    -- Audit
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP,
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255),
    version              BIGINT NOT NULL DEFAULT 0
);

CREATE TRIGGER trg_fragrance_preferences_updated_at
    BEFORE UPDATE ON identity.fragrance_preferences
    FOR EACH ROW EXECUTE FUNCTION identity.update_timestamp();

-- GIN indexes on UUID array columns for containment queries (@>, <@, &&)
CREATE INDEX idx_fp_liked_note_ids ON identity.fragrance_preferences USING gin (liked_note_ids);
CREATE INDEX idx_fp_disliked_note_ids ON identity.fragrance_preferences USING gin (disliked_note_ids);
CREATE INDEX idx_fp_preferred_accord_ids ON identity.fragrance_preferences USING gin (preferred_accord_ids);
CREATE INDEX idx_fp_disliked_accord_ids ON identity.fragrance_preferences USING gin (disliked_accord_ids);

-- 6. Interactions
CREATE TABLE identity.interactions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES identity.user_profiles(user_id) ON DELETE CASCADE,
    perfume_id          UUID NOT NULL,
    source              VARCHAR(50) NOT NULL,
    signal_type         VARCHAR(50) NOT NULL,
    rating              INTEGER,

    -- Audit
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_user_perfume_source UNIQUE (user_id, perfume_id, source),
    CONSTRAINT chk_source CHECK (source IN ('ONBOARDING', 'POST_USE_REVIEW', 'WISHLIST')),
    CONSTRAINT chk_signal_type CHECK (signal_type IN ('POSITIVE', 'NEGATIVE', 'NEUTRAL'))
);

CREATE INDEX idx_interactions_perfume_id ON identity.interactions(perfume_id);

CREATE TRIGGER trg_interactions_updated_at
    BEFORE UPDATE ON identity.interactions
    FOR EACH ROW EXECUTE FUNCTION identity.update_timestamp();
