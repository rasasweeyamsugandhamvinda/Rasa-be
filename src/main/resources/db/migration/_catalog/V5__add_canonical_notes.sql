ALTER TABLE catalog.notes
    ADD COLUMN IF NOT EXISTS is_canonical BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS canonical_id UUID REFERENCES catalog.notes(id);

-- Optional but recommended: Add an index for faster lookups
CREATE INDEX idx_notes_canonical ON catalog.notes(canonical_id);

-- Promote existing parent notes
UPDATE catalog.notes
SET is_canonical = TRUE
WHERE name IN (
               'Orange',
               'Lemon',
               'Grapefruit',
               'Mandarin',
               'Tangerine',
               'Bergamot',
               'Lime',

               'Rose',
               'Jasmine',
               'Lavender',
               'Iris',
               'Violet',
               'Peony',
               'Neroli',
               'Mimosa',
               'Magnolia',
               'Lotus',
               'Lily',
               'Tuberose',
               'Freesia',
               'Gardenia',
               'Geranium',

               'Cedar',
               'Sandalwood',
               'Rosewood',
               'Oud',
               'Vetiver',
               'Patchouli',
               'Pine',
               'Fir',
               'Cypress',
               'Juniper',
               'Ebony',
               'Oak',
               'Birch',
               'Teak',

               'Musk',
               'Leather',
               'Suede',

               'Amber',
               'Ambergris',
               'Benzoin',
               'Myrrh',
               'Labdanum',
               'Balsam',
               'Incense',

               'Vanilla',
               'Tonka',
               'Almond',
               'Caramel',
               'Chocolate',
               'Cacao',
               'Coffee',
               'Honey',
               'Sugar',

               'Apple',
               'Pear',
               'Peach',
               'Apricot',
               'Plum',
               'Cherry',
               'Strawberry',
               'Raspberry',
               'Blackberry',
               'Blueberry',
               'Currant',
               'Fig',
               'Pineapple',
               'Mango',
               'Guava',
               'Lychee',
               'Pomegranate',

               'Ginger',
               'Cardamom',
               'Cinnamon',
               'Nutmeg',
               'Clove',
               'Pepper',
               'Basil',
               'Rosemary',
               'Sage',
               'Mint',
               'Coriander',
               'Anise'
    );

-- Insert missing parent notes safely
INSERT INTO catalog.notes (id, name, is_canonical)
SELECT
    gen_random_uuid(),
    v.name,
    TRUE
FROM (
         VALUES
             ('Orange'),
             ('Lemon'),
             ('Grapefruit'),
             ('Mandarin'),
             ('Tangerine'),
             ('Bergamot'),
             ('Lime'),

             ('Rose'),
             ('Jasmine'),
             ('Lavender'),
             ('Iris'),
             ('Violet'),
             ('Peony'),
             ('Neroli'),
             ('Mimosa'),
             ('Magnolia'),
             ('Lotus'),
             ('Lily'),
             ('Tuberose'),
             ('Freesia'),
             ('Gardenia'),
             ('Geranium'),

             ('Cedar'),
             ('Sandalwood'),
             ('Rosewood'),
             ('Oud'),
             ('Vetiver'),
             ('Patchouli'),
             ('Pine'),
             ('Fir'),
             ('Cypress'),
             ('Juniper'),
             ('Ebony'),
             ('Oak'),
             ('Birch'),
             ('Teak'),

             ('Musk'),
             ('Leather'),
             ('Suede'),

             ('Amber'),
             ('Ambergris'),
             ('Benzoin'),
             ('Myrrh'),
             ('Labdanum'),
             ('Balsam'),
             ('Incense'),

             ('Vanilla'),
             ('Tonka'),
             ('Almond'),
             ('Caramel'),
             ('Chocolate'),
             ('Cacao'),
             ('Coffee'),
             ('Honey'),
             ('Sugar'),

             ('Apple'),
             ('Pear'),
             ('Peach'),
             ('Apricot'),
             ('Plum'),
             ('Cherry'),
             ('Strawberry'),
             ('Raspberry'),
             ('Blackberry'),
             ('Blueberry'),
             ('Currant'),
             ('Fig'),
             ('Pineapple'),
             ('Mango'),
             ('Guava'),
             ('Lychee'),
             ('Pomegranate'),

             ('Ginger'),
             ('Cardamom'),
             ('Cinnamon'),
             ('Nutmeg'),
             ('Clove'),
             ('Pepper'),
             ('Basil'),
             ('Rosemary'),
             ('Sage'),
             ('Mint'),
             ('Coriander'),
             ('Anise')
     ) AS v(name)
WHERE NOT EXISTS (
    SELECT 1
    FROM catalog.notes n
    WHERE n.name = v.name
);

-- =========================================================
-- Oud
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id
    FROM catalog.notes
    WHERE name = 'Oud'
      AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Smoked Oud',
    'Smoky Oud',
    'Smoked Oud Wood',
    'Smoky Oud Wood',
    'Aged Smoky Oud',
    'Rich Oud',
    'Musky Oud',
    'Cambodian Oud',
    'Agarwood (Oud)'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Vanilla
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id
    FROM catalog.notes
    WHERE name = 'Vanilla'
      AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Warm Vanilla',
    'Vanilla Absolute'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Orange
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id
    FROM catalog.notes
    WHERE name = 'Orange'
      AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Blood Orange',
    'Sicilian Orange',
    'Fresh Orange',
    'Orange Oil',
    'Orange Zest',
    'Green Orange'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Rose
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id
    FROM catalog.notes
    WHERE name = 'Rose'
      AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Rose Buds',
    'Woody Rose',
    'Turkish Rose',
    'Turkish Rose Oil',
    'Rose Absolute',
    'Hamanasu or Japanese Rose',
    'Liv Tyler Rose'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Cedar
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id
    FROM catalog.notes
    WHERE name = 'Cedar'
      AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Cedar Essence',
    'Blue Cedar',
    'White Cedar',
    'Light Cedar',
    'Cedar Needles',
    'Chinese Cedar',
    'Himalayan Cedarwood'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Patchouli
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id
    FROM catalog.notes
    WHERE name = 'Patchouli'
      AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Indian Patchouli',
    'Indonesian Patchouli',
    'Patchouli Leaf'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Lavender
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id
    FROM catalog.notes
    WHERE name = 'Lavender'
      AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Wild Lavender',
    'Spiced Lavender',
    'French Lavender',
    'English Lavender',
    'Lavender Oil',
    'Lavandin'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Pine
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id
    FROM catalog.notes
    WHERE name = 'Pine'
      AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Silver Pine'
    )
  AND is_canonical = FALSE;

-- =========================================================
-- Lemon
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Lemon' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Italian Lemon',
    'Iced Lemon',
    'Lemon Leaf',
    'Lemon Blossom',
    'Lemon Zest',
    'Lemon Verbena',
    'Amalfi Lemon',
    'Lemon Peel',
    'Sicilian Lemon',
    'Californian Lemon',
    'Lemon Tree',
    'Lemon Leaf Oil',
    'Hatkora lemon'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Grapefruit
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Grapefruit' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Bitter Grapefruit',
    'Pink Grapefruit',
    'Blood Grapefruit',
    'Grapefruit blossom',
    'White Grapefruit',
    'Florida Grapefruit'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Mandarin
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Mandarin' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Green Mandarin',
    'Sicilian Mandarin',
    'Mandarin Leaf',
    'Mandarin Blossom',
    'Blood Mandarin',
    'Yellow Mandarin',
    'Calabrian Mandarin',
    'Italian Mandarin',
    'Indian Mandarin'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Tangerine
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Tangerine' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Italian Tangerine',
    'Green Tangerine',
    'Tangerine Blossom'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Bergamot
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Bergamot' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Bergamot Leaf',
    'Green Bergamot',
    'Fresh Bergamot',
    'Sicilian Bergamot',
    'White Bergamot'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Lime
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Lime' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Kaffir Lime',
    'Finger Lime',
    'Lime Blossom'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Jasmine
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Jasmine' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Jasmine Tea',
    'Jasmine Petals',
    'Jasmine Sambac',
    'False Jasmine',
    'Egyptian Jasmine',
    'Night Blooming Jasmine',
    'Spanish Jasmine',
    'Italian Jasmine',
    'Star Jasmine',
    'Pink Jasmine',
    'Indian Jasmine',
    'Moroccan Jasmine',
    'Water Jasmine',
    'Jasmine Leaf',
    'Chinese Jasmine'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Iris
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Iris' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Iris Pallida',
    'Italian Iris',
    'Tuscan Iris',
    'White Iris',
    'Iris leaf',
    'Iris Flower'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Violet
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Violet' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Water Violet',
    'Violet Leaves',
    'Violet Root',
    'Black Violet',
    'Violet Leaf',
    'Parma Violet',
    'Italian Violet',
    'White Violet',
    'French Violet'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Peony
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Peony' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Peony Petals',
    'Pink Peony',
    'Red Peony',
    'Chinese Pink Peony',
    'Chinese Peony'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Neroli
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Neroli' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Neroli Essence',
    'Tunisian Neroli'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Mimosa
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Mimosa' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'White Mimosa',
    'Mimosa absolute'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Magnolia
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Magnolia' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Magnolia Petals',
    'White Magnolia'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Lotus
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Lotus' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'White Lotus',
    'Blue Lotus',
    'Japanese Pink Lotus',
    'Green Lotus'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Lily
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Lily' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Lily of the Valley',
    'Lily-of-the-Valley',
    'Casablanca Lily',
    'Pink Lily',
    'Water Lily',
    'White Lily',
    'Green Lily',
    'Arum Lily',
    'White Ginger Lily',
    'Red Lily'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Tuberose
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Tuberose' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Indian Tuberose'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Freesia
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Freesia' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'African Freesia Petals',
    'Pink Freesia',
    'Red Freesia',
    'Yellow Freesia'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Gardenia
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Gardenia' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Californian Gardenia'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Geranium
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Geranium' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Bourbon Geranium',
    'African Geranium'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Sandalwood
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Sandalwood' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'White Sandalwood',
    'Australian Sandalwood',
    'Mysore Sandalwood'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Rosewood
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Rosewood' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Palisander Rosewood',
    'Brazilian Rosewood',
    'Egyptian Rosewood'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Vetiver
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Vetiver' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Vetiver Root',
    'Madagascar Vetiver',
    'Tahitian Vetiver',
    'Bourbon Vetiver',
    'Java vetiver oil',
    'Haitian Vetiver'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Cypress
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Cypress' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Italian Cypress'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Juniper
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Juniper' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'juniper berry',
    'Juniper Berries'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Ebony
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Ebony' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'ebony tree',
    'Ebony Wood'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Oak
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Oak' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Oak Tree'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Birch
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Birch' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Birch Tar',
    'Birch Leaves',
    'Birch Leaf'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Teak
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Teak' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Teak Wood'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Musk
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Musk' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Musky Notes',
    'Fresh Musk',
    'Sensual Musk',
    'Dark Musk',
    'Natural Musk',
    'Sheer Musk',
    'Gray Musk',
    'Black Musk',
    'White Musk',
    'Cashmere Musk',
    'Woody Musk'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Leather
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Leather' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Leathery Accord',
    'Raw Leather',
    'Leatherwood',
    'Saffiano Leather',
    'Black Leather',
    'Russian Leather'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Suede
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Suede' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'White Suede',
    'Suede Leather Accord'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Amber
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Amber' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Golden Amber',
    'Crystal Amber',
    'Amber Xtreme',
    'Rich Amber',
    'White Amber',
    'Warm Amber',
    'Ambermax',
    'Grey Amber',
    'Woody Amber',
    'Dry Amber',
    'Black Amber',
    'Amberwood',
    'Red Amber',
    'Ambertonic',
    'Coral Amber'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Benzoin
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Benzoin' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Siam Benzoin'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Myrrh
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Myrrh' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Namibian Myrrh',
    'Red Myrrh'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Labdanum
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Labdanum' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Spanish Labdanum',
    'French labdanum'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Balsam
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Balsam' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Fir Balsam',
    'Gurjan balsam',
    'Gurjum Balsam',
    'Canada Balsam',
    'Balsam Fir',
    'Tolu Balsam',
    'Peru Balsam',
    'Egyptian balsam'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Incense
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Incense' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Frankincense',
    'Kyara Incense'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Tonka
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Tonka' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Roasted Tonka',
    'Roasted Tonka Beans',
    'Tonka Bean'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Almond
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Almond' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Almond Flower',
    'Green Almond',
    'Bitter Almond',
    'Almond Tree',
    'Almond Milk',
    'Candied Almond',
    'Sweet Almond',
    'Fresh Almond',
    'Almond Blossom'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Caramel
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Caramel' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Melted Caramel',
    'Warm Caramel'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Chocolate
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Chocolate' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Dark Chocolate',
    'White Chocolate',
    'Mexican chocolate',
    'Milk Chocolate'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Cacao
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Cacao' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Cacao Pod'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Coffee
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Coffee' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Coffee Accord',
    'Coffee CO2',
    'Roasted Coffee Beans'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Honey
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Honey' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Wild Honey',
    'Black locust Honey',
    'White Honey',
    'Honeycomb'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Sugar
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Sugar' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Vanilla Sugar',
    'Brown sugar',
    'Sugar Cane',
    'Spun Sugar'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Apple
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Apple' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Crisp Apple',
    'Green Apple Accord',
    'Candy Apple',
    'Red Apple',
    'Green Apple',
    'Granny Smith apple',
    'Frosted Apple',
    'Apple Tree',
    'Apple Leaf',
    'Apple Blossom',
    'Star apple'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Pear
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Pear' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Nashi Pear',
    'Green Pear Accord',
    'Pear Leaf',
    'White Pear',
    'Pear Blossom',
    'prickly pear',
    'Green Pear',
    'Pear Wood'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Peach
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Peach' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Floral Peach',
    'Peach Blossom',
    'Red Peach',
    'White Peach'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Apricot
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Apricot' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Apricot Blossom',
    'Apricot Nectar'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Plum
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Plum' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Plum Blossom',
    'Dried Plum',
    'Damask Plum',
    'Japanese Plum',
    'Mirabelle Plum'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Cherry
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Cherry' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Sour Cherry',
    'Cherry Blossom',
    'Cherry Liqueur',
    'Cherry Milk',
    'Black Cherry',
    'Japanese Cherry Blossom'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Strawberry
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Strawberry' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Wild Strawberry Leaf',
    'Big Strawberry',
    'Wild Strawberry',
    'Strawberry Leaf'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Raspberry
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Raspberry' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Raspberry Leaf',
    'Raspberry Bloom'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Blackberry
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Blackberry' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Blackberry leaf'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Blueberry
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Blueberry' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Wild Blueberry',
    'Blueberry Leaf'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Currant
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Currant' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Blackcurrant Syrup',
    'Currant buds',
    'White Currant',
    'Black Currant Blossom',
    'Red Currant',
    'Red currant leaf',
    'Black currant leaf',
    'Black Currant'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Fig
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Fig' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Fig Nectar',
    'Fig Leaf',
    'black fig',
    'Fig Tree',
    'Fig Wood Bark'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Pineapple
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Pineapple' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Pineapple blossom',
    'Juicy Pineapple',
    'Pineapple Leaf'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Mango
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Mango' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Green Mango'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Guava
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Guava' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Guava blossom'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Ginger
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Ginger' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Ginger Root Oil',
    'Madagascar Ginger Oil',
    'Candied Ginger',
    'Indian Ginger',
    'Ginger flower'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Cardamom
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Cardamom' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Black Cardamom',
    'Guatemalan Cardamom'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Cinnamon
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Cinnamon' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Cinnamon Leaf',
    'Cinnamon Bark',
    'Ceylon Cinnamon'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Nutmeg
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Nutmeg' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Caribbean Nutmeg',
    'Nutmeg Flower',
    'Indonesian Nutmeg'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Clove
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Clove' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Clove Leaf',
    'Cloves'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Pepper
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Pepper' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Chili Pepper',
    'Peruvian Pepper',
    'Red Chilli Pepper',
    'Black Pepper',
    'Red Pepper',
    'Sichuan Pepper',
    'Madagascar Pepper',
    'White Pepper',
    'Green Pepper',
    'Orange Pepper',
    'Bourbon Pepper',
    'Pink Pepper'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Basil
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Basil' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Fresh Basil',
    'Basil Oil',
    'Black Basil'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Rosemary
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Rosemary' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Rosemary Oil'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Sage
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Sage' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Clary Sage',
    'Blue Sage'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Mint
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Mint' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Spearmint',
    'Water Mint',
    'Peppermint'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Coriander
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Coriander' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Green Coriander',
    'Coriander Leaf'
    )
  AND is_canonical = FALSE;


-- =========================================================
-- Anise
-- =========================================================
UPDATE catalog.notes
SET canonical_id = (
    SELECT id FROM catalog.notes
    WHERE name = 'Anise' AND is_canonical = TRUE
    LIMIT 1
    )
WHERE name IN (
    'Star Anise'
    )
  AND is_canonical = FALSE;