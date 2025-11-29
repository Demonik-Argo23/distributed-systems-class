// MongoDB initialization script
db = db.getSiblingDB('zelda_characters');

// Create characters collection
db.createCollection('characters');

// Create indexes
db.characters.createIndex({ email: 1 }, { unique: true });
db.characters.createIndex({ game: 1 });
db.characters.createIndex({ "weapons": 1 });

// Insert sample characters
db.characters.insertMany([
  {
    name: "Link",
    email: "link@hyrule.com",
    game: "Breath of the Wild",
    race: "Hylian",
    health: 150,
    stamina: 120,
    attack: 80,
    defense: 70,
    weapons: ["1", "5", "10"], // Master Sword, Hylian Shield, Bow
    created_at: new Date(),
    updated_at: new Date()
  },
  {
    name: "Zelda",
    email: "zelda@hyrule.com",
    game: "Breath of the Wild",
    race: "Hylian",
    health: 100,
    stamina: 100,
    attack: 60,
    defense: 90,
    weapons: ["8"], // Bow of Light
    created_at: new Date(),
    updated_at: new Date()
  },
  {
    name: "Daruk",
    email: "daruk@goron.com",
    game: "Breath of the Wild",
    race: "Goron",
    health: 200,
    stamina: 80,
    attack: 100,
    defense: 120,
    weapons: ["15"], // Boulder Breaker
    created_at: new Date(),
    updated_at: new Date()
  },
  {
    name: "Mipha",
    email: "mipha@zora.com",
    game: "Breath of the Wild",
    race: "Zora",
    health: 120,
    stamina: 110,
    attack: 70,
    defense: 85,
    weapons: ["12"], // Lightscale Trident
    created_at: new Date(),
    updated_at: new Date()
  },
  {
    name: "Revali",
    email: "revali@rito.com",
    game: "Breath of the Wild",
    race: "Rito",
    health: 90,
    stamina: 150,
    attack: 95,
    defense: 60,
    weapons: ["20"], // Great Eagle Bow
    created_at: new Date(),
    updated_at: new Date()
  },
  {
    name: "Urbosa",
    email: "urbosa@gerudo.com",
    game: "Breath of the Wild",
    race: "Gerudo",
    health: 130,
    stamina: 100,
    attack: 110,
    defense: 95,
    weapons: ["18"], // Scimitar of the Seven
    created_at: new Date(),
    updated_at: new Date()
  }
]);

print("✅ Database initialized with sample characters");
