-- Feature 5: farmer farm profiles and the crops currently grown there.

CREATE TABLE farms (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    farmer_id           BIGINT       NOT NULL,
    farm_name           VARCHAR(120) NOT NULL,
    location            VARCHAR(200) NOT NULL,
    total_area_in_acres DOUBLE       NOT NULL,
    created_at          TIMESTAMP(6) NOT NULL,
    updated_at          TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_farms_farmer FOREIGN KEY (farmer_id) REFERENCES farmers (id)
);

CREATE TABLE farm_crops (
    farm_id   BIGINT NOT NULL,
    crop_id   BIGINT NOT NULL,
    PRIMARY KEY (farm_id, crop_id),
    CONSTRAINT fk_farm_crops_farm FOREIGN KEY (farm_id) REFERENCES farms (id),
    CONSTRAINT fk_farm_crops_crop FOREIGN KEY (crop_id) REFERENCES crops (id)
);

CREATE INDEX idx_farms_farmer ON farms (farmer_id);
