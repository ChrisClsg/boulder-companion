// Seed script — runs once on first container start (when the volume is empty).
// To reset: docker compose down -v && docker compose up

db = db.getSiblingDB('boulder_companion');

// ─── Gyms ────────────────────────────────────────────────────────────────────

db.gyms.insertMany([
  {
    _id: ObjectId("6a05c44e567d80cf2b7f95fb"),
    name: "Granite Bloc",
    address: "Musterstraße 12, 10115 Berlin, Germany",
    description: "Modern bouldering gym with beginner-friendly routes, training boards, and a small café.",
    phone: "+49 30 12345678",
    website: "https://granite-bloc.example.com",
    openingHours: {
      monday: "10:00-23:00", tuesday: "10:00-23:00", wednesday: "10:00-23:00",
      thursday: "10:00-23:00", friday: "10:00-22:00", saturday: "09:00-21:00", sunday: "09:00-21:00"
    },
    adminId: "replace-with-admin-id",
    createdAt: new Date("2026-05-14T10:00:00.000Z"),
    updatedAt: new Date("2026-05-14T10:00:00.000Z")
  },
  {
    _id: ObjectId("6a05c44e567d80cf2b7f95fc"),
    name: "Chalk District",
    address: "Kletterweg 5, 80331 Munich, Germany",
    description: "Spacious indoor bouldering facility with weekly route setting and dedicated kids area.",
    phone: "+49 89 23456789",
    website: "https://chalk-district.example.com",
    openingHours: {
      monday: "08:00-22:00", tuesday: "08:00-22:00", wednesday: "08:00-22:00",
      thursday: "08:00-22:00", friday: "08:00-22:00", saturday: "10:00-20:00", sunday: "10:00-20:00"
    },
    adminId: "replace-with-admin-id",
    createdAt: new Date("2026-05-14T10:00:00.000Z"),
    updatedAt: new Date("2026-05-14T10:00:00.000Z")
  },
  {
    _id: ObjectId("6a05c44e567d80cf2b7f95fd"),
    name: "Overhang Hall",
    address: "Industriestraße 44, 22761 Hamburg, Germany",
    description: "Large bouldering hall featuring steep overhangs, competition-style problems, and a training zone.",
    phone: "+49 40 34567890",
    website: "https://overhang-hall.example.com",
    openingHours: {
      monday: "12:00-23:00", tuesday: "12:00-23:00", wednesday: "12:00-23:00",
      thursday: "12:00-23:00", friday: "12:00-23:00", saturday: "09:00-22:00", sunday: "09:00-22:00"
    },
    adminId: "replace-with-admin-id",
    createdAt: new Date("2026-05-14T10:00:00.000Z"),
    updatedAt: new Date("2026-05-14T10:00:00.000Z")
  },
  {
    _id: ObjectId("6a05c44e567d80cf2b7f95fe"),
    name: "Beta Factory",
    address: "Routenallee 18, 50667 Cologne, Germany",
    description: "Community-focused bouldering gym with technique classes, rental shoes, and regular social events.",
    phone: "+49 221 45678901",
    website: "https://beta-factory.example.com",
    openingHours: {
      monday: "10:00-22:30", tuesday: "10:00-22:30", wednesday: "10:00-22:30",
      thursday: "10:00-22:30", friday: "10:00-22:30", saturday: "09:00-20:00", sunday: "09:00-20:00"
    },
    adminId: "replace-with-admin-id",
    createdAt: new Date("2026-05-14T10:00:00.000Z"),
    updatedAt: new Date("2026-05-14T10:00:00.000Z")
  },
  {
    _id: ObjectId("6a05c44e567d80cf2b7f95ff"),
    name: "Crux Studio",
    address: "Boulderplatz 9, 70173 Stuttgart, Germany",
    description: "Compact bouldering studio with creative routes, spray wall, campus board, and personal coaching.",
    phone: "+49 711 56789012",
    website: "https://crux-studio.example.com",
    openingHours: {
      monday: "07:00-22:00", tuesday: "07:00-22:00", wednesday: "07:00-22:00",
      thursday: "07:00-22:00", friday: "07:00-22:00", saturday: "10:00-19:00", sunday: "10:00-19:00"
    },
    adminId: "replace-with-admin-id",
    createdAt: new Date("2026-05-14T10:00:00.000Z"),
    updatedAt: new Date("2026-05-14T10:00:00.000Z")
  }
]);

// ─── Routes ──────────────────────────────────────────────────────────────────

db.routes.insertMany([

  // Granite Bloc (Berlin)
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9640"), gymId: "6a05c44e567d80cf2b7f95fb",
    name: "Slab Symphony", difficulty: { value: "V0", scale: "V_SCALE" },
    holdColor: "Yellow", holdTypes: ["jug", "foothold"], setterId: "replace-with-setter-id",
    wall: "Slab Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Slab+Symphony+1", caption: "Slab Symphony photo 1" }],
    createdAt: new Date("2026-05-14T10:30:00.000Z"), updatedAt: new Date("2026-05-14T10:30:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9641"), gymId: "6a05c44e567d80cf2b7f95fb",
    name: "Granite Groove", difficulty: { value: "V1", scale: "V_SCALE" },
    holdColor: "Blue", holdTypes: ["jug", "edge"], setterId: "replace-with-setter-id",
    wall: "Main Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Granite+Groove+1", caption: "Granite Groove photo 1" }],
    createdAt: new Date("2026-05-14T10:31:00.000Z"), updatedAt: new Date("2026-05-14T10:31:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9642"), gymId: "6a05c44e567d80cf2b7f95fb",
    name: "Chalky Business", difficulty: { value: "V2", scale: "V_SCALE" },
    holdColor: "Green", holdTypes: ["sloper", "jug"], setterId: "replace-with-setter-id",
    wall: "Topout Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Chalky+Business+1", caption: "Chalky Business photo 1" },
      { url: "https://placehold.co/800x1000?text=Chalky+Business+2", caption: "Chalky Business photo 2" },
      { url: "https://placehold.co/800x1000?text=Chalky+Business+3", caption: "Chalky Business photo 3" }
    ],
    createdAt: new Date("2026-05-14T10:32:00.000Z"), updatedAt: new Date("2026-05-14T10:32:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9643"), gymId: "6a05c44e567d80cf2b7f95fb",
    name: "Pocket Rocket", difficulty: { value: "V3", scale: "V_SCALE" },
    holdColor: "Red", holdTypes: ["pocket", "edge"], setterId: "replace-with-setter-id",
    wall: "Slab Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Pocket+Rocket+1", caption: "Pocket Rocket photo 1" },
      { url: "https://placehold.co/800x1000?text=Pocket+Rocket+2", caption: "Pocket Rocket photo 2" }
    ],
    createdAt: new Date("2026-05-14T10:33:00.000Z"), updatedAt: new Date("2026-05-14T10:33:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9644"), gymId: "6a05c44e567d80cf2b7f95fb",
    name: "Berlin Mantle", difficulty: { value: "V4", scale: "V_SCALE" },
    holdColor: "Purple", holdTypes: ["sloper", "volume"], setterId: "replace-with-setter-id",
    wall: "Main Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Berlin+Mantle+1", caption: "Berlin Mantle photo 1" },
      { url: "https://placehold.co/800x1000?text=Berlin+Mantle+2", caption: "Berlin Mantle photo 2" }
    ],
    createdAt: new Date("2026-05-14T10:34:00.000Z"), updatedAt: new Date("2026-05-14T10:34:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9645"), gymId: "6a05c44e567d80cf2b7f95fb",
    name: "Compression Session", difficulty: { value: "V5", scale: "V_SCALE" },
    holdColor: "Orange", holdTypes: ["pinch", "volume"], setterId: "replace-with-setter-id",
    wall: "Topout Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Compression+Session+1", caption: "Compression Session photo 1" },
      { url: "https://placehold.co/800x1000?text=Compression+Session+2", caption: "Compression Session photo 2" }
    ],
    createdAt: new Date("2026-05-14T10:35:00.000Z"), updatedAt: new Date("2026-05-14T10:35:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9646"), gymId: "6a05c44e567d80cf2b7f95fb",
    name: "Tiny Edges", difficulty: { value: "V6", scale: "V_SCALE" },
    holdColor: "White", holdTypes: ["crimp", "edge"], setterId: "replace-with-setter-id",
    wall: "Slab Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Tiny+Edges+1", caption: "Tiny Edges photo 1" }],
    createdAt: new Date("2026-05-14T10:36:00.000Z"), updatedAt: new Date("2026-05-14T10:36:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9647"), gymId: "6a05c44e567d80cf2b7f95fb",
    name: "Dyno Picnic", difficulty: { value: "V3", scale: "V_SCALE" },
    holdColor: "Pink", holdTypes: ["jug", "volume"], setterId: "replace-with-setter-id",
    wall: "Main Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Dyno+Picnic+1", caption: "Dyno Picnic photo 1" },
      { url: "https://placehold.co/800x1000?text=Dyno+Picnic+2", caption: "Dyno Picnic photo 2" },
      { url: "https://placehold.co/800x1000?text=Dyno+Picnic+3", caption: "Dyno Picnic photo 3" },
      { url: "https://placehold.co/800x1000?text=Dyno+Picnic+4", caption: "Dyno Picnic photo 4" },
      { url: "https://placehold.co/800x1000?text=Dyno+Picnic+5", caption: "Dyno Picnic photo 5" }
    ],
    createdAt: new Date("2026-05-14T10:37:00.000Z"), updatedAt: new Date("2026-05-14T10:37:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9648"), gymId: "6a05c44e567d80cf2b7f95fb",
    name: "Moonboard Lite", difficulty: { value: "V4", scale: "V_SCALE" },
    holdColor: "Black", holdTypes: ["crimp", "pinch"], setterId: "replace-with-setter-id",
    wall: "Topout Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Moonboard+Lite+1", caption: "Moonboard Lite photo 1" }],
    createdAt: new Date("2026-05-14T10:38:00.000Z"), updatedAt: new Date("2026-05-14T10:38:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9649"), gymId: "6a05c44e567d80cf2b7f95fb",
    name: "Final Pebble", difficulty: { value: "V7", scale: "V_SCALE" },
    holdColor: "Grey", holdTypes: ["crimp", "sloper"], setterId: "replace-with-setter-id",
    wall: "Slab Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Final+Pebble+1", caption: "Final Pebble photo 1" },
      { url: "https://placehold.co/800x1000?text=Final+Pebble+2", caption: "Final Pebble photo 2" },
      { url: "https://placehold.co/800x1000?text=Final+Pebble+3", caption: "Final Pebble photo 3" },
      { url: "https://placehold.co/800x1000?text=Final+Pebble+4", caption: "Final Pebble photo 4" },
      { url: "https://placehold.co/800x1000?text=Final+Pebble+5", caption: "Final Pebble photo 5" }
    ],
    createdAt: new Date("2026-05-14T10:39:00.000Z"), updatedAt: new Date("2026-05-14T10:39:00.000Z")
  },

  // Chalk District (Munich)
  {
    _id: ObjectId("6a05cae7567d80cf2b7f964a"), gymId: "6a05c44e567d80cf2b7f95fc",
    name: "Munich Warmup", difficulty: { value: "V0", scale: "V_SCALE" },
    holdColor: "Yellow", holdTypes: ["jug", "foothold"], setterId: "replace-with-setter-id",
    wall: "Beginner Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Munich+Warmup+1", caption: "Munich Warmup photo 1" },
      { url: "https://placehold.co/800x1000?text=Munich+Warmup+2", caption: "Munich Warmup photo 2" },
      { url: "https://placehold.co/800x1000?text=Munich+Warmup+3", caption: "Munich Warmup photo 3" },
      { url: "https://placehold.co/800x1000?text=Munich+Warmup+4", caption: "Munich Warmup photo 4" }
    ],
    createdAt: new Date("2026-05-14T10:40:00.000Z"), updatedAt: new Date("2026-05-14T10:40:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f964b"), gymId: "6a05c44e567d80cf2b7f95fc",
    name: "Chalk Ladder", difficulty: { value: "V1", scale: "V_SCALE" },
    holdColor: "Green", holdTypes: ["jug", "edge"], setterId: "replace-with-setter-id",
    wall: "Main Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Chalk+Ladder+1", caption: "Chalk Ladder photo 1" }],
    createdAt: new Date("2026-05-14T10:41:00.000Z"), updatedAt: new Date("2026-05-14T10:41:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f964c"), gymId: "6a05c44e567d80cf2b7f95fc",
    name: "Pretzel Problem", difficulty: { value: "V2", scale: "V_SCALE" },
    holdColor: "Blue", holdTypes: ["pinch", "jug"], setterId: "replace-with-setter-id",
    wall: "Training Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Pretzel+Problem+1", caption: "Pretzel Problem photo 1" }],
    createdAt: new Date("2026-05-14T10:42:00.000Z"), updatedAt: new Date("2026-05-14T10:42:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f964d"), gymId: "6a05c44e567d80cf2b7f95fc",
    name: "Silent Slopers", difficulty: { value: "V3", scale: "V_SCALE" },
    holdColor: "Purple", holdTypes: ["sloper", "volume"], setterId: "replace-with-setter-id",
    wall: "Competition Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Silent+Slopers+1", caption: "Silent Slopers photo 1" },
      { url: "https://placehold.co/800x1000?text=Silent+Slopers+2", caption: "Silent Slopers photo 2" }
    ],
    createdAt: new Date("2026-05-14T10:43:00.000Z"), updatedAt: new Date("2026-05-14T10:43:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f964e"), gymId: "6a05c44e567d80cf2b7f95fc",
    name: "Campus Casual", difficulty: { value: "V4", scale: "V_SCALE" },
    holdColor: "Orange", holdTypes: ["crimp", "edge"], setterId: "replace-with-setter-id",
    wall: "Beginner Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Campus+Casual+1", caption: "Campus Casual photo 1" },
      { url: "https://placehold.co/800x1000?text=Campus+Casual+2", caption: "Campus Casual photo 2" }
    ],
    createdAt: new Date("2026-05-14T10:44:00.000Z"), updatedAt: new Date("2026-05-14T10:44:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f964f"), gymId: "6a05c44e567d80cf2b7f95fc",
    name: "Crimp Carnival", difficulty: { value: "V5", scale: "V_SCALE" },
    holdColor: "Red", holdTypes: ["crimp", "pocket"], setterId: "replace-with-setter-id",
    wall: "Main Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Crimp+Carnival+1", caption: "Crimp Carnival photo 1" },
      { url: "https://placehold.co/800x1000?text=Crimp+Carnival+2", caption: "Crimp Carnival photo 2" },
      { url: "https://placehold.co/800x1000?text=Crimp+Carnival+3", caption: "Crimp Carnival photo 3" },
      { url: "https://placehold.co/800x1000?text=Crimp+Carnival+4", caption: "Crimp Carnival photo 4" },
      { url: "https://placehold.co/800x1000?text=Crimp+Carnival+5", caption: "Crimp Carnival photo 5" }
    ],
    createdAt: new Date("2026-05-14T10:45:00.000Z"), updatedAt: new Date("2026-05-14T10:45:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9650"), gymId: "6a05c44e567d80cf2b7f95fc",
    name: "Volume Voyage", difficulty: { value: "V6", scale: "V_SCALE" },
    holdColor: "White", holdTypes: ["volume", "sloper"], setterId: "replace-with-setter-id",
    wall: "Training Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Volume+Voyage+1", caption: "Volume Voyage photo 1" },
      { url: "https://placehold.co/800x1000?text=Volume+Voyage+2", caption: "Volume Voyage photo 2" },
      { url: "https://placehold.co/800x1000?text=Volume+Voyage+3", caption: "Volume Voyage photo 3" },
      { url: "https://placehold.co/800x1000?text=Volume+Voyage+4", caption: "Volume Voyage photo 4" },
      { url: "https://placehold.co/800x1000?text=Volume+Voyage+5", caption: "Volume Voyage photo 5" }
    ],
    createdAt: new Date("2026-05-14T10:46:00.000Z"), updatedAt: new Date("2026-05-14T10:46:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9651"), gymId: "6a05c44e567d80cf2b7f95fc",
    name: "Kids Area Crusher", difficulty: { value: "V1", scale: "V_SCALE" },
    holdColor: "Pink", holdTypes: ["jug", "foothold"], setterId: "replace-with-setter-id",
    wall: "Competition Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Kids+Area+Crusher+1", caption: "Kids Area Crusher photo 1" }],
    createdAt: new Date("2026-05-14T10:47:00.000Z"), updatedAt: new Date("2026-05-14T10:47:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9652"), gymId: "6a05c44e567d80cf2b7f95fc",
    name: "Board Meeting", difficulty: { value: "V5", scale: "V_SCALE" },
    holdColor: "Black", holdTypes: ["crimp", "pinch"], setterId: "replace-with-setter-id",
    wall: "Beginner Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Board+Meeting+1", caption: "Board Meeting photo 1" },
      { url: "https://placehold.co/800x1000?text=Board+Meeting+2", caption: "Board Meeting photo 2" },
      { url: "https://placehold.co/800x1000?text=Board+Meeting+3", caption: "Board Meeting photo 3" },
      { url: "https://placehold.co/800x1000?text=Board+Meeting+4", caption: "Board Meeting photo 4" },
      { url: "https://placehold.co/800x1000?text=Board+Meeting+5", caption: "Board Meeting photo 5" }
    ],
    createdAt: new Date("2026-05-14T10:48:00.000Z"), updatedAt: new Date("2026-05-14T10:48:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9653"), gymId: "6a05c44e567d80cf2b7f95fc",
    name: "District Project", difficulty: { value: "V7", scale: "V_SCALE" },
    holdColor: "Grey", holdTypes: ["sloper", "crimp", "volume"], setterId: "replace-with-setter-id",
    wall: "Main Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=District+Project+1", caption: "District Project photo 1" },
      { url: "https://placehold.co/800x1000?text=District+Project+2", caption: "District Project photo 2" },
      { url: "https://placehold.co/800x1000?text=District+Project+3", caption: "District Project photo 3" },
      { url: "https://placehold.co/800x1000?text=District+Project+4", caption: "District Project photo 4" }
    ],
    createdAt: new Date("2026-05-14T10:49:00.000Z"), updatedAt: new Date("2026-05-14T10:49:00.000Z")
  },

  // Overhang Hall (Hamburg)
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9654"), gymId: "6a05c44e567d80cf2b7f95fd",
    name: "Harbor Warmup", difficulty: { value: "V0", scale: "V_SCALE" },
    holdColor: "Yellow", holdTypes: ["jug", "foothold"], setterId: "replace-with-setter-id",
    wall: "Beginner Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Harbor+Warmup+1", caption: "Harbor Warmup photo 1" },
      { url: "https://placehold.co/800x1000?text=Harbor+Warmup+2", caption: "Harbor Warmup photo 2" }
    ],
    createdAt: new Date("2026-05-14T10:50:00.000Z"), updatedAt: new Date("2026-05-14T10:50:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9655"), gymId: "6a05c44e567d80cf2b7f95fd",
    name: "Overhang Express", difficulty: { value: "V2", scale: "V_SCALE" },
    holdColor: "Blue", holdTypes: ["jug", "pinch"], setterId: "replace-with-setter-id",
    wall: "Overhang Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Overhang+Express+1", caption: "Overhang Express photo 1" },
      { url: "https://placehold.co/800x1000?text=Overhang+Express+2", caption: "Overhang Express photo 2" },
      { url: "https://placehold.co/800x1000?text=Overhang+Express+3", caption: "Overhang Express photo 3" },
      { url: "https://placehold.co/800x1000?text=Overhang+Express+4", caption: "Overhang Express photo 4" }
    ],
    createdAt: new Date("2026-05-14T10:51:00.000Z"), updatedAt: new Date("2026-05-14T10:51:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9656"), gymId: "6a05c44e567d80cf2b7f95fd",
    name: "Steep Tea", difficulty: { value: "V3", scale: "V_SCALE" },
    holdColor: "Green", holdTypes: ["jug", "sloper"], setterId: "replace-with-setter-id",
    wall: "Roof Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Steep+Tea+1", caption: "Steep Tea photo 1" },
      { url: "https://placehold.co/800x1000?text=Steep+Tea+2", caption: "Steep Tea photo 2" },
      { url: "https://placehold.co/800x1000?text=Steep+Tea+3", caption: "Steep Tea photo 3" },
      { url: "https://placehold.co/800x1000?text=Steep+Tea+4", caption: "Steep Tea photo 4" },
      { url: "https://placehold.co/800x1000?text=Steep+Tea+5", caption: "Steep Tea photo 5" }
    ],
    createdAt: new Date("2026-05-14T10:52:00.000Z"), updatedAt: new Date("2026-05-14T10:52:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9657"), gymId: "6a05c44e567d80cf2b7f95fd",
    name: "Swing State", difficulty: { value: "V4", scale: "V_SCALE" },
    holdColor: "Red", holdTypes: ["pinch", "volume"], setterId: "replace-with-setter-id",
    wall: "Competition Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Swing+State+1", caption: "Swing State photo 1" },
      { url: "https://placehold.co/800x1000?text=Swing+State+2", caption: "Swing State photo 2" },
      { url: "https://placehold.co/800x1000?text=Swing+State+3", caption: "Swing State photo 3" }
    ],
    createdAt: new Date("2026-05-14T10:53:00.000Z"), updatedAt: new Date("2026-05-14T10:53:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9658"), gymId: "6a05c44e567d80cf2b7f95fd",
    name: "Hamburg Hug", difficulty: { value: "V5", scale: "V_SCALE" },
    holdColor: "Purple", holdTypes: ["pinch", "sloper", "volume"], setterId: "replace-with-setter-id",
    wall: "Beginner Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Hamburg+Hug+1", caption: "Hamburg Hug photo 1" }],
    createdAt: new Date("2026-05-14T10:54:00.000Z"), updatedAt: new Date("2026-05-14T10:54:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9659"), gymId: "6a05c44e567d80cf2b7f95fd",
    name: "Roof Runner", difficulty: { value: "V6", scale: "V_SCALE" },
    holdColor: "Orange", holdTypes: ["jug", "heel hook", "toe hook"], setterId: "replace-with-setter-id",
    wall: "Overhang Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Roof+Runner+1", caption: "Roof Runner photo 1" },
      { url: "https://placehold.co/800x1000?text=Roof+Runner+2", caption: "Roof Runner photo 2" }
    ],
    createdAt: new Date("2026-05-14T10:55:00.000Z"), updatedAt: new Date("2026-05-14T10:55:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f965a"), gymId: "6a05c44e567d80cf2b7f95fd",
    name: "Industrial Crimps", difficulty: { value: "V5", scale: "V_SCALE" },
    holdColor: "White", holdTypes: ["crimp", "edge"], setterId: "replace-with-setter-id",
    wall: "Roof Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Industrial+Crimps+1", caption: "Industrial Crimps photo 1" },
      { url: "https://placehold.co/800x1000?text=Industrial+Crimps+2", caption: "Industrial Crimps photo 2" },
      { url: "https://placehold.co/800x1000?text=Industrial+Crimps+3", caption: "Industrial Crimps photo 3" }
    ],
    createdAt: new Date("2026-05-14T10:56:00.000Z"), updatedAt: new Date("2026-05-14T10:56:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f965b"), gymId: "6a05c44e567d80cf2b7f95fd",
    name: "Dyno Dock", difficulty: { value: "V3", scale: "V_SCALE" },
    holdColor: "Pink", holdTypes: ["jug", "volume"], setterId: "replace-with-setter-id",
    wall: "Competition Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Dyno+Dock+1", caption: "Dyno Dock photo 1" },
      { url: "https://placehold.co/800x1000?text=Dyno+Dock+2", caption: "Dyno Dock photo 2" }
    ],
    createdAt: new Date("2026-05-14T10:57:00.000Z"), updatedAt: new Date("2026-05-14T10:57:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f965c"), gymId: "6a05c44e567d80cf2b7f95fd",
    name: "Power Hour", difficulty: { value: "V7", scale: "V_SCALE" },
    holdColor: "Black", holdTypes: ["crimp", "pinch", "pocket"], setterId: "replace-with-setter-id",
    wall: "Beginner Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Power+Hour+1", caption: "Power Hour photo 1" },
      { url: "https://placehold.co/800x1000?text=Power+Hour+2", caption: "Power Hour photo 2" }
    ],
    createdAt: new Date("2026-05-14T10:58:00.000Z"), updatedAt: new Date("2026-05-14T10:58:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f965d"), gymId: "6a05c44e567d80cf2b7f95fd",
    name: "Final Overhang", difficulty: { value: "V8", scale: "V_SCALE" },
    holdColor: "Grey", holdTypes: ["sloper", "crimp", "toe hook"], setterId: "replace-with-setter-id",
    wall: "Overhang Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Final+Overhang+1", caption: "Final Overhang photo 1" }],
    createdAt: new Date("2026-05-14T10:59:00.000Z"), updatedAt: new Date("2026-05-14T10:59:00.000Z")
  },

  // Beta Factory (Cologne)
  {
    _id: ObjectId("6a05cae7567d80cf2b7f965e"), gymId: "6a05c44e567d80cf2b7f95fe",
    name: "Cologne Cruise", difficulty: { value: "V0", scale: "V_SCALE" },
    holdColor: "Yellow", holdTypes: ["jug", "foothold"], setterId: "replace-with-setter-id",
    wall: "Beginner Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Cologne+Cruise+1", caption: "Cologne Cruise photo 1" },
      { url: "https://placehold.co/800x1000?text=Cologne+Cruise+2", caption: "Cologne Cruise photo 2" },
      { url: "https://placehold.co/800x1000?text=Cologne+Cruise+3", caption: "Cologne Cruise photo 3" },
      { url: "https://placehold.co/800x1000?text=Cologne+Cruise+4", caption: "Cologne Cruise photo 4" }
    ],
    createdAt: new Date("2026-05-14T11:00:00.000Z"), updatedAt: new Date("2026-05-14T11:00:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f965f"), gymId: "6a05c44e567d80cf2b7f95fe",
    name: "Beta Basics", difficulty: { value: "V1", scale: "V_SCALE" },
    holdColor: "Green", holdTypes: ["jug", "edge"], setterId: "replace-with-setter-id",
    wall: "Main Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Beta+Basics+1", caption: "Beta Basics photo 1" }],
    createdAt: new Date("2026-05-14T11:01:00.000Z"), updatedAt: new Date("2026-05-14T11:01:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9660"), gymId: "6a05c44e567d80cf2b7f95fe",
    name: "Social Send", difficulty: { value: "V2", scale: "V_SCALE" },
    holdColor: "Blue", holdTypes: ["jug", "sloper"], setterId: "replace-with-setter-id",
    wall: "Technique Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Social+Send+1", caption: "Social Send photo 1" },
      { url: "https://placehold.co/800x1000?text=Social+Send+2", caption: "Social Send photo 2" },
      { url: "https://placehold.co/800x1000?text=Social+Send+3", caption: "Social Send photo 3" }
    ],
    createdAt: new Date("2026-05-14T11:02:00.000Z"), updatedAt: new Date("2026-05-14T11:02:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9661"), gymId: "6a05c44e567d80cf2b7f95fe",
    name: "Rental Shoe Shuffle", difficulty: { value: "V2", scale: "V_SCALE" },
    holdColor: "Pink", holdTypes: ["edge", "foothold"], setterId: "replace-with-setter-id",
    wall: "Competition Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Rental+Shoe+Shuffle+1", caption: "Rental Shoe Shuffle photo 1" }],
    createdAt: new Date("2026-05-14T11:03:00.000Z"), updatedAt: new Date("2026-05-14T11:03:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9662"), gymId: "6a05c44e567d80cf2b7f95fe",
    name: "Technique Tuesday", difficulty: { value: "V3", scale: "V_SCALE" },
    holdColor: "Red", holdTypes: ["sloper", "edge"], setterId: "replace-with-setter-id",
    wall: "Beginner Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Technique+Tuesday+1", caption: "Technique Tuesday photo 1" },
      { url: "https://placehold.co/800x1000?text=Technique+Tuesday+2", caption: "Technique Tuesday photo 2" },
      { url: "https://placehold.co/800x1000?text=Technique+Tuesday+3", caption: "Technique Tuesday photo 3" },
      { url: "https://placehold.co/800x1000?text=Technique+Tuesday+4", caption: "Technique Tuesday photo 4" }
    ],
    createdAt: new Date("2026-05-14T11:04:00.000Z"), updatedAt: new Date("2026-05-14T11:04:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9663"), gymId: "6a05c44e567d80cf2b7f95fe",
    name: "Factory Reset", difficulty: { value: "V4", scale: "V_SCALE" },
    holdColor: "Orange", holdTypes: ["pinch", "volume"], setterId: "replace-with-setter-id",
    wall: "Main Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Factory+Reset+1", caption: "Factory Reset photo 1" }],
    createdAt: new Date("2026-05-14T11:05:00.000Z"), updatedAt: new Date("2026-05-14T11:05:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9664"), gymId: "6a05c44e567d80cf2b7f95fe",
    name: "Community Crux", difficulty: { value: "V5", scale: "V_SCALE" },
    holdColor: "Purple", holdTypes: ["crimp", "sloper"], setterId: "replace-with-setter-id",
    wall: "Technique Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Community+Crux+1", caption: "Community Crux photo 1" },
      { url: "https://placehold.co/800x1000?text=Community+Crux+2", caption: "Community Crux photo 2" },
      { url: "https://placehold.co/800x1000?text=Community+Crux+3", caption: "Community Crux photo 3" }
    ],
    createdAt: new Date("2026-05-14T11:06:00.000Z"), updatedAt: new Date("2026-05-14T11:06:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9665"), gymId: "6a05c44e567d80cf2b7f95fe",
    name: "Afterwork Pump", difficulty: { value: "V4", scale: "V_SCALE" },
    holdColor: "White", holdTypes: ["jug", "pinch"], setterId: "replace-with-setter-id",
    wall: "Competition Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Afterwork+Pump+1", caption: "Afterwork Pump photo 1" },
      { url: "https://placehold.co/800x1000?text=Afterwork+Pump+2", caption: "Afterwork Pump photo 2" },
      { url: "https://placehold.co/800x1000?text=Afterwork+Pump+3", caption: "Afterwork Pump photo 3" }
    ],
    createdAt: new Date("2026-05-14T11:07:00.000Z"), updatedAt: new Date("2026-05-14T11:07:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9666"), gymId: "6a05c44e567d80cf2b7f95fe",
    name: "Beta Breaker", difficulty: { value: "V6", scale: "V_SCALE" },
    holdColor: "Black", holdTypes: ["crimp", "pocket"], setterId: "replace-with-setter-id",
    wall: "Beginner Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Beta+Breaker+1", caption: "Beta Breaker photo 1" },
      { url: "https://placehold.co/800x1000?text=Beta+Breaker+2", caption: "Beta Breaker photo 2" },
      { url: "https://placehold.co/800x1000?text=Beta+Breaker+3", caption: "Beta Breaker photo 3" },
      { url: "https://placehold.co/800x1000?text=Beta+Breaker+4", caption: "Beta Breaker photo 4" },
      { url: "https://placehold.co/800x1000?text=Beta+Breaker+5", caption: "Beta Breaker photo 5" }
    ],
    createdAt: new Date("2026-05-14T11:08:00.000Z"), updatedAt: new Date("2026-05-14T11:08:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9667"), gymId: "6a05c44e567d80cf2b7f95fe",
    name: "Cologne Crusher", difficulty: { value: "V7", scale: "V_SCALE" },
    holdColor: "Grey", holdTypes: ["sloper", "pinch", "volume"], setterId: "replace-with-setter-id",
    wall: "Main Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Cologne+Crusher+1", caption: "Cologne Crusher photo 1" },
      { url: "https://placehold.co/800x1000?text=Cologne+Crusher+2", caption: "Cologne Crusher photo 2" },
      { url: "https://placehold.co/800x1000?text=Cologne+Crusher+3", caption: "Cologne Crusher photo 3" }
    ],
    createdAt: new Date("2026-05-14T11:09:00.000Z"), updatedAt: new Date("2026-05-14T11:09:00.000Z")
  },

  // Crux Studio (Stuttgart)
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9668"), gymId: "6a05c44e567d80cf2b7f95ff",
    name: "Studio Starter", difficulty: { value: "V0", scale: "V_SCALE" },
    holdColor: "Yellow", holdTypes: ["jug", "foothold"], setterId: "replace-with-setter-id",
    wall: "Spray Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Studio+Starter+1", caption: "Studio Starter photo 1" },
      { url: "https://placehold.co/800x1000?text=Studio+Starter+2", caption: "Studio Starter photo 2" }
    ],
    createdAt: new Date("2026-05-14T11:10:00.000Z"), updatedAt: new Date("2026-05-14T11:10:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9669"), gymId: "6a05c44e567d80cf2b7f95ff",
    name: "Spray Wall Softie", difficulty: { value: "V1", scale: "V_SCALE" },
    holdColor: "Green", holdTypes: ["jug", "edge"], setterId: "replace-with-setter-id",
    wall: "Technique Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Spray+Wall+Softie+1", caption: "Spray Wall Softie photo 1" }],
    createdAt: new Date("2026-05-14T11:11:00.000Z"), updatedAt: new Date("2026-05-14T11:11:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f966a"), gymId: "6a05c44e567d80cf2b7f95ff",
    name: "Coaching Corner", difficulty: { value: "V2", scale: "V_SCALE" },
    holdColor: "Blue", holdTypes: ["edge", "sloper"], setterId: "replace-with-setter-id",
    wall: "Training Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Coaching+Corner+1", caption: "Coaching Corner photo 1" },
      { url: "https://placehold.co/800x1000?text=Coaching+Corner+2", caption: "Coaching Corner photo 2" },
      { url: "https://placehold.co/800x1000?text=Coaching+Corner+3", caption: "Coaching Corner photo 3" },
      { url: "https://placehold.co/800x1000?text=Coaching+Corner+4", caption: "Coaching Corner photo 4" }
    ],
    createdAt: new Date("2026-05-14T11:12:00.000Z"), updatedAt: new Date("2026-05-14T11:12:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f966b"), gymId: "6a05c44e567d80cf2b7f95ff",
    name: "Campus Coffee", difficulty: { value: "V3", scale: "V_SCALE" },
    holdColor: "Red", holdTypes: ["crimp", "jug"], setterId: "replace-with-setter-id",
    wall: "Competition Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Campus+Coffee+1", caption: "Campus Coffee photo 1" }],
    createdAt: new Date("2026-05-14T11:13:00.000Z"), updatedAt: new Date("2026-05-14T11:13:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f966c"), gymId: "6a05c44e567d80cf2b7f95ff",
    name: "Tiny Studio Big Move", difficulty: { value: "V4", scale: "V_SCALE" },
    holdColor: "Orange", holdTypes: ["volume", "jug"], setterId: "replace-with-setter-id",
    wall: "Spray Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Tiny+Studio+Big+Move+1", caption: "Tiny Studio Big Move photo 1" },
      { url: "https://placehold.co/800x1000?text=Tiny+Studio+Big+Move+2", caption: "Tiny Studio Big Move photo 2" },
      { url: "https://placehold.co/800x1000?text=Tiny+Studio+Big+Move+3", caption: "Tiny Studio Big Move photo 3" },
      { url: "https://placehold.co/800x1000?text=Tiny+Studio+Big+Move+4", caption: "Tiny Studio Big Move photo 4" },
      { url: "https://placehold.co/800x1000?text=Tiny+Studio+Big+Move+5", caption: "Tiny Studio Big Move photo 5" }
    ],
    createdAt: new Date("2026-05-14T11:14:00.000Z"), updatedAt: new Date("2026-05-14T11:14:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f966d"), gymId: "6a05c44e567d80cf2b7f95ff",
    name: "Personal Project", difficulty: { value: "V5", scale: "V_SCALE" },
    holdColor: "Purple", holdTypes: ["pinch", "crimp"], setterId: "replace-with-setter-id",
    wall: "Technique Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Personal+Project+1", caption: "Personal Project photo 1" },
      { url: "https://placehold.co/800x1000?text=Personal+Project+2", caption: "Personal Project photo 2" },
      { url: "https://placehold.co/800x1000?text=Personal+Project+3", caption: "Personal Project photo 3" }
    ],
    createdAt: new Date("2026-05-14T11:15:00.000Z"), updatedAt: new Date("2026-05-14T11:15:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f966e"), gymId: "6a05c44e567d80cf2b7f95ff",
    name: "Spray Lord", difficulty: { value: "V6", scale: "V_SCALE" },
    holdColor: "Black", holdTypes: ["crimp", "pocket", "pinch"], setterId: "replace-with-setter-id",
    wall: "Training Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Spray+Lord+1", caption: "Spray Lord photo 1" },
      { url: "https://placehold.co/800x1000?text=Spray+Lord+2", caption: "Spray Lord photo 2" },
      { url: "https://placehold.co/800x1000?text=Spray+Lord+3", caption: "Spray Lord photo 3" }
    ],
    createdAt: new Date("2026-05-14T11:16:00.000Z"), updatedAt: new Date("2026-05-14T11:16:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f966f"), gymId: "6a05c44e567d80cf2b7f95ff",
    name: "Stuttgart Slab", difficulty: { value: "V3", scale: "V_SCALE" },
    holdColor: "White", holdTypes: ["sloper", "edge"], setterId: "replace-with-setter-id",
    wall: "Competition Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Stuttgart+Slab+1", caption: "Stuttgart Slab photo 1" },
      { url: "https://placehold.co/800x1000?text=Stuttgart+Slab+2", caption: "Stuttgart Slab photo 2" }
    ],
    createdAt: new Date("2026-05-14T11:17:00.000Z"), updatedAt: new Date("2026-05-14T11:17:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9670"), gymId: "6a05c44e567d80cf2b7f95ff",
    name: "Crux Control", difficulty: { value: "V7", scale: "V_SCALE" },
    holdColor: "Grey", holdTypes: ["sloper", "volume", "crimp"], setterId: "replace-with-setter-id",
    wall: "Spray Wall", archived: false, archivedAt: null,
    images: [
      { url: "https://placehold.co/800x1000?text=Crux+Control+1", caption: "Crux Control photo 1" },
      { url: "https://placehold.co/800x1000?text=Crux+Control+2", caption: "Crux Control photo 2" },
      { url: "https://placehold.co/800x1000?text=Crux+Control+3", caption: "Crux Control photo 3" }
    ],
    createdAt: new Date("2026-05-14T11:18:00.000Z"), updatedAt: new Date("2026-05-14T11:18:00.000Z")
  },
  {
    _id: ObjectId("6a05cae7567d80cf2b7f9671"), gymId: "6a05c44e567d80cf2b7f95ff",
    name: "Last Move Best Move", difficulty: { value: "V8", scale: "V_SCALE" },
    holdColor: "Pink", holdTypes: ["crimp", "sloper", "toe hook"], setterId: "replace-with-setter-id",
    wall: "Technique Wall", archived: false, archivedAt: null,
    images: [{ url: "https://placehold.co/800x1000?text=Last+Move+Best+Move+1", caption: "Last Move Best Move photo 1" }],
    createdAt: new Date("2026-05-14T11:19:00.000Z"), updatedAt: new Date("2026-05-14T11:19:00.000Z")
  }
]);

print('✓ Seeded ' + db.gyms.countDocuments() + ' gyms and ' + db.routes.countDocuments() + ' routes into boulder_companion');
