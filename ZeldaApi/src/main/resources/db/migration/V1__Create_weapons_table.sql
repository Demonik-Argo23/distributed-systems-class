CREATE TABLE weapons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    weapon_type VARCHAR(50) NOT NULL,
    damage INTEGER NOT NULL CHECK (damage > 0),
    durability INTEGER NOT NULL CHECK (durability > 0),
    element VARCHAR(20)
);