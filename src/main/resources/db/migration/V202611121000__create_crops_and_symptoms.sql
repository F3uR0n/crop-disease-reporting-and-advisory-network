-- Master data used by the case submission form (Feature 1): crops and the symptom checklist.

CREATE TABLE crops (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name_en     VARCHAR(60)  NOT NULL,
    name_bn     VARCHAR(60)  NOT NULL,
    created_at  TIMESTAMP(6) NOT NULL,
    updated_at  TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE symptoms (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name_en     VARCHAR(100) NOT NULL,
    name_bn     VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP(6) NOT NULL,
    updated_at  TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO crops (name_en, name_bn, created_at, updated_at) VALUES
    ('Rice',    'ধান',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Potato',  'আলু',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Tomato',  'টমেটো', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Jute',    'পাট',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Wheat',   'গম',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Brinjal', 'বেগুন', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO symptoms (name_en, name_bn, created_at, updated_at) VALUES
    ('Yellowing leaves',                'পাতা হলুদ হয়ে যাওয়া',        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Brown or black spots on leaves',  'পাতায় বাদামি বা কালো দাগ',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Wilting',                         'গাছ নেতিয়ে পড়া',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Leaf curling',                    'পাতা কুঁকড়ে যাওয়া',          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('White powdery coating',           'সাদা পাউডারের মতো আস্তরণ',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Rotting stem or roots',           'কাণ্ড বা শিকড় পচে যাওয়া',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Holes in leaves',                 'পাতায় ছিদ্র',                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Stunted growth',                  'গাছের বৃদ্ধি কমে যাওয়া',      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
