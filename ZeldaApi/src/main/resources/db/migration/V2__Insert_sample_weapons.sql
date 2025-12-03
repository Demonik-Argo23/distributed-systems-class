-- Insertar datos iniciales de armas de Zelda
INSERT INTO weapons (id, name, weapon_type, damage, durability, element) VALUES 
-- Armas emblemáticas de Zelda
('550e8400-e29b-41d4-a716-446655440001', 'Master Sword', 'ONE_HANDED_SWORD', 30, 200, 'NONE'),
('550e8400-e29b-41d4-a716-446655440002', 'Royal Claymore', 'TWO_HANDED_SWORD', 52, 40, 'NONE'),
('550e8400-e29b-41d4-a716-446655440003', 'Ancient Spear', 'SPEAR', 30, 25, 'NONE'),
('550e8400-e29b-41d4-a716-446655440004', 'Traveler''s Sword', 'ONE_HANDED_SWORD', 5, 22, 'NONE'),
('550e8400-e29b-41d4-a716-446655440005', 'Great Eagle Bow', 'BOW', 28, 60, 'NONE'),

-- Armas con elementos
('550e8400-e29b-41d4-a716-446655440006', 'Flameblade', 'ONE_HANDED_SWORD', 24, 25, 'FIRE'),
('550e8400-e29b-41d4-a716-446655440007', 'Frostblade', 'ONE_HANDED_SWORD', 20, 25, 'ICE'),
('550e8400-e29b-41d4-a716-446655440008', 'Thunderblade', 'ONE_HANDED_SWORD', 22, 25, 'LIGHTNING'),
('550e8400-e29b-41d4-a716-446655440009', 'Great Flameblade', 'TWO_HANDED_SWORD', 34, 25, 'FIRE'),
('550e8400-e29b-41d4-a716-446655440010', 'Great Frostblade', 'TWO_HANDED_SWORD', 30, 25, 'ICE'),

-- Lanzas especiales
('550e8400-e29b-41d4-a716-446655440011', 'Savage Lynel Spear', 'SPEAR', 30, 25, 'NONE'),
('550e8400-e29b-41d4-a716-446655440012', 'Royal Guard''s Spear', 'SPEAR', 32, 15, 'NONE'),
('550e8400-e29b-41d4-a716-446655440013', 'Silver Lynel Spear', 'SPEAR', 26, 25, 'NONE'),
('550e8400-e29b-41d4-a716-446655440014', 'Flamespear', 'SPEAR', 24, 25, 'FIRE'),
('550e8400-e29b-41d4-a716-446655440015', 'Frostspear', 'SPEAR', 20, 25, 'ICE'),

-- Arcos variados
('550e8400-e29b-41d4-a716-446655440016', 'Royal Bow', 'BOW', 38, 60, 'NONE'),
('550e8400-e29b-41d4-a716-446655440017', 'Ancient Bow', 'BOW', 44, 120, 'NONE'),
('550e8400-e29b-41d4-a716-446655440018', 'Savage Lynel Bow', 'BOW', 32, 45, 'NONE'),
('550e8400-e29b-41d4-a716-446655440019', 'Bow of Light', 'BOW', 100, 100, 'LIGHTNING'),
('550e8400-e29b-41d4-a716-446655440020', 'Duplex Bow', 'BOW', 14, 18, 'NONE'),

-- Escudos
('550e8400-e29b-41d4-a716-446655440021', 'Hylian Shield', 'SHIELD', 90, 800, 'NONE'),
('550e8400-e29b-41d4-a716-446655440022', 'Ancient Shield', 'SHIELD', 70, 32, 'NONE'),
('550e8400-e29b-41d4-a716-446655440023', 'Royal Shield', 'SHIELD', 55, 29, 'NONE'),
('550e8400-e29b-41d4-a716-446655440024', 'Kite Shield', 'SHIELD', 14, 17, 'NONE'),
('550e8400-e29b-41d4-a716-446655440025', 'Traveler''s Shield', 'SHIELD', 4, 12, 'NONE'),

-- Más espadas de una mano
('550e8400-e29b-41d4-a716-446655440026', 'Royal Broadsword', 'ONE_HANDED_SWORD', 36, 36, 'NONE'),
('550e8400-e29b-41d4-a716-446655440027', 'Knight''s Broadsword', 'ONE_HANDED_SWORD', 26, 40, 'NONE'),
('550e8400-e29b-41d4-a716-446655440028', 'Soldier''s Broadsword', 'ONE_HANDED_SWORD', 14, 27, 'NONE'),
('550e8400-e29b-41d4-a716-446655440029', 'Ancient Short Sword', 'ONE_HANDED_SWORD', 40, 50, 'NONE'),
('550e8400-e29b-41d4-a716-446655440030', 'Guardian Sword', 'ONE_HANDED_SWORD', 40, 20, 'NONE'),

-- Más espadas de dos manos
('550e8400-e29b-41d4-a716-446655440031', 'Royal Guard''s Claymore', 'TWO_HANDED_SWORD', 72, 15, 'NONE'),
('550e8400-e29b-41d4-a716-446655440032', 'Knight''s Claymore', 'TWO_HANDED_SWORD', 38, 40, 'NONE'),
('550e8400-e29b-41d4-a716-446655440033', 'Soldier''s Claymore', 'TWO_HANDED_SWORD', 20, 36, 'NONE'),
('550e8400-e29b-41d4-a716-446655440034', 'Ancient Battle Axe', 'TWO_HANDED_SWORD', 60, 25, 'NONE'),
('550e8400-e29b-41d4-a716-446655440035', 'Savage Lynel Crusher', 'TWO_HANDED_SWORD', 78, 35, 'NONE'),

-- Armas especiales y únicas
('550e8400-e29b-41d4-a716-446655440036', 'Biggoron''s Sword', 'TWO_HANDED_SWORD', 60, 60, 'NONE'),
('550e8400-e29b-41d4-a716-446655440037', 'Fairy Bow', 'BOW', 15, 30, 'NONE'),
('550e8400-e29b-41d4-a716-446655440038', 'Hero''s Bow', 'BOW', 28, 48, 'NONE'),
('550e8400-e29b-41d4-a716-446655440039', 'Silver Bow', 'BOW', 15, 40, 'NONE'),
('550e8400-e29b-41d4-a716-446655440040', 'Wooden Shield', 'SHIELD', 2, 8, 'NONE'),

-- Completar hasta 50 armas
('550e8400-e29b-41d4-a716-446655440041', 'Lizal Boomerang', 'ONE_HANDED_SWORD', 24, 25, 'NONE'),
('550e8400-e29b-41d4-a716-446655440042', 'Moblin Spear', 'SPEAR', 18, 25, 'NONE'),
('550e8400-e29b-41d4-a716-446655440043', 'Bokoblin Arm', 'ONE_HANDED_SWORD', 12, 8, 'NONE'),
('550e8400-e29b-41d4-a716-446655440044', 'Spiked Moblin Spear', 'SPEAR', 18, 36, 'NONE'),
('550e8400-e29b-41d4-a716-446655440045', 'Dragonbone Moblin Club', 'TWO_HANDED_SWORD', 45, 25, 'NONE'),
('550e8400-e29b-41d4-a716-446655440046', 'Lynel Bow', 'BOW', 20, 30, 'NONE'),
('550e8400-e29b-41d4-a716-446655440047', 'Boko Shield', 'SHIELD', 3, 12, 'NONE'),
('550e8400-e29b-41d4-a716-446655440048', 'Spiked Boko Shield', 'SHIELD', 8, 14, 'NONE'),
('550e8400-e29b-41d4-a716-446655440049', 'Steel Lizal Shield', 'SHIELD', 35, 35, 'NONE'),
('550e8400-e29b-41d4-a716-446655440050', 'Guardian Sword++', 'ONE_HANDED_SWORD', 60, 22, 'LIGHTNING');