-- 1. Extensions
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 2. Domain Schema Separation
CREATE SCHEMA IF NOT EXISTS catalog;

-- 3. Automatic Updated-At Trigger Function
CREATE OR REPLACE FUNCTION catalog.update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 4. Brands Table
CREATE TABLE catalog.brands (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                name VARCHAR(255) NOT NULL UNIQUE,
                                country VARCHAR(100),

    -- Audit columns
                                created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                                updated_at TIMESTAMP WITH TIME ZONE,
                                created_by VARCHAR(255),
                                updated_by VARCHAR(255),
                                version BIGINT DEFAULT 0
);

CREATE TRIGGER trg_brands_updated_at
    BEFORE UPDATE ON catalog.brands
    FOR EACH ROW EXECUTE FUNCTION catalog.update_timestamp();

-- 5. Perfumes Table
CREATE TABLE catalog.perfumes (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  brand_id UUID NOT NULL REFERENCES catalog.brands(id) ON DELETE RESTRICT,
                                  name VARCHAR(255) NOT NULL,
                                  gender VARCHAR(50) NOT NULL,
                                  concentration VARCHAR(50) NOT NULL,
                                  price_tier VARCHAR(50) NOT NULL,
                                  longevity INTEGER,
                                  sillage INTEGER,
                                  image_url VARCHAR(1024),

    -- Data Lineage
                                  source VARCHAR(100),
                                  source_url VARCHAR(1024),
                                  is_verified BOOLEAN DEFAULT FALSE,

    -- Vector Column (384 dimensions for sentence-transformers all-MiniLM-L6-v2)
                                  embedding vector(384),

    -- Audit columns
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                                  updated_at TIMESTAMP WITH TIME ZONE,
                                  created_by VARCHAR(255),
                                  updated_by VARCHAR(255),
                                  version BIGINT DEFAULT 0,

    -- Domain Integrity Constraints
                                  CONSTRAINT uk_brand_name_concentration UNIQUE (brand_id, name, concentration),
                                  CONSTRAINT chk_longevity_range CHECK (longevity BETWEEN 1 AND 100),
                                  CONSTRAINT chk_sillage_range CHECK (sillage BETWEEN 1 AND 100),
                                  CONSTRAINT chk_gender_enum CHECK (gender IN ('MALE', 'FEMALE', 'UNISEX')),
                                  CONSTRAINT chk_concentration_enum CHECK (concentration IN ('PARFUM', 'EDP', 'EDT', 'EDC', 'EXTRAIT', 'COLOGNE')),
                                  CONSTRAINT chk_price_tier_enum CHECK (price_tier IN ('BUDGET', 'MID_RANGE', 'DESIGNER', 'NICHE', 'LUXURY'))
);

CREATE TRIGGER trg_perfumes_updated_at
    BEFORE UPDATE ON catalog.perfumes
    FOR EACH ROW EXECUTE FUNCTION catalog.update_timestamp();

-- 6. Notes Table (Raw Ingredients)
CREATE TABLE catalog.notes (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               name VARCHAR(255) NOT NULL UNIQUE
);

-- 7. Accords Table (Scent Families)
CREATE TABLE catalog.accords (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 name VARCHAR(255) NOT NULL UNIQUE
);

-- 8. Perfume Notes Junction
CREATE TABLE catalog.perfume_notes (
                                       perfume_id UUID NOT NULL REFERENCES catalog.perfumes(id) ON DELETE CASCADE,
                                       note_id UUID NOT NULL REFERENCES catalog.notes(id) ON DELETE CASCADE,
                                       note_type VARCHAR(20) NOT NULL,

                                       PRIMARY KEY (perfume_id, note_id, note_type),
                                       CONSTRAINT chk_note_type CHECK (note_type IN ('TOP', 'HEART', 'BASE'))
);

-- 9. Perfume Accords Junction
CREATE TABLE catalog.perfume_accords (
                                         perfume_id UUID NOT NULL REFERENCES catalog.perfumes(id) ON DELETE CASCADE,
                                         accord_id UUID NOT NULL REFERENCES catalog.accords(id) ON DELETE CASCADE,
                                         weight NUMERIC(5,2) NOT NULL,

                                         PRIMARY KEY (perfume_id, accord_id),
                                         CONSTRAINT chk_weight_range CHECK (weight >= 0 AND weight <= 100)
);

-- 10. Relational and Search Indexes
CREATE INDEX idx_perfumes_brand_id ON catalog.perfumes(brand_id);
CREATE INDEX idx_perfume_notes_perfume_id ON catalog.perfume_notes(perfume_id);
CREATE INDEX idx_perfume_accords_perfume_id ON catalog.perfume_accords(perfume_id);

-- Trigram indexes for fast fuzzy search ("tom frd" -> "Tom Ford")
CREATE INDEX idx_perfumes_name_trgm ON catalog.perfumes USING gin (name gin_trgm_ops);
CREATE INDEX idx_brands_name_trgm ON catalog.brands USING gin (name gin_trgm_ops);

-- Cosine distance HNSW Vector index for fast nearest-neighbor search
CREATE INDEX idx_perfumes_embedding_hnsw ON catalog.perfumes USING hnsw (embedding vector_cosine_ops);