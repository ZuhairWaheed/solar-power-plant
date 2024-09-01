
-- changeset ZuhairWaheed:1719351983-1
CREATE TABLE power_plant(
    id Int NOT NULL PRIMARY KEY auto_increment,
    name VARCHAR(200) NOT NULL COMMENT 'Name of Power Plant',
    age BigInt NOT NULL COMMENT 'Age of Power Plant',
    created_at timestamp DEFAULT NOW() NOT NULL,
    updated_at timestamp DEFAULT NOW() NOT NULL
);