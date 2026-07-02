-- Create the isolated authentication schema
CREATE SCHEMA IF NOT EXISTS auth;

-- 1. Core User Table
CREATE TABLE auth.users (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            email VARCHAR(255) UNIQUE NOT NULL,
                            password_hash VARCHAR(255),
                            is_verified BOOLEAN DEFAULT FALSE,
                            auth_provider VARCHAR(50) DEFAULT 'LOCAL',

    -- Audit Columns
                            version BIGINT NOT NULL DEFAULT 0,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP,
                            created_by VARCHAR(255),
                            updated_by VARCHAR(255)
);

-- 2. OAuth Identities (For linking Google/Apple to a User)
CREATE TABLE auth.oauth_identities (
                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
                                       provider VARCHAR(50) NOT NULL,
                                       provider_subject_id VARCHAR(255) NOT NULL,

    -- Audit Columns
                                       version BIGINT NOT NULL DEFAULT 0,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP,
                                       created_by VARCHAR(255),
                                       updated_by VARCHAR(255),

                                       UNIQUE(provider, provider_subject_id)
);

-- Index for fast user lookups
CREATE INDEX idx_oauth_identities_user_id ON auth.oauth_identities(user_id);

-- 3. Session Tracking (For multi-device logout and tracking)
CREATE TABLE auth.user_sessions (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
                                    refresh_token_hash VARCHAR(255) UNIQUE NOT NULL,
                                    device_info VARCHAR(255),
                                    ip_address VARCHAR(45),
                                    is_revoked BOOLEAN DEFAULT FALSE,
                                    expires_at TIMESTAMP NOT NULL,

    -- Audit Columns
                                    version BIGINT NOT NULL DEFAULT 0,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP,
                                    created_by VARCHAR(255),
                                    updated_by VARCHAR(255)
);

-- Index for querying active sessions by user
CREATE INDEX idx_user_sessions_user_id ON auth.user_sessions(user_id);