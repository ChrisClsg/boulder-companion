export interface User {
  id: string
  githubId: string
  name: string
  email: string
  role: 'CLIMBER' | 'GYM_ADMIN' | 'ROUTE_SETTER'
  favoriteGyms: string[]
  gymAdminFor: string[]
  routeSetterFor: string[]
}

export interface Gym {
  id: string
  name: string
  address: string
  description: string
  phone: string
  website: string
  openingHours: OpeningHours
  adminId: string
  createdAt: string
  updatedAt: string
}

export interface OpeningHours {
  monday: string
  tuesday: string
  wednesday: string
  thursday: string
  friday: string
  saturday: string
  sunday: string
}

export interface Route {
  id: string
  gymId: string
  name: string
  difficulty: Difficulty
  holdColor: string
  holdTypes: string[]
  setterId: string
  wall: string
  archived: boolean
  archivedAt: string
  images: Image[]
  createdAt: string
  updatedAt: string
}

export interface Difficulty {
  value: string
  scale: 'v' | 'font' | 'custom'
}

export interface Image {
  url: string
  caption: string
}

export interface ClimbingHistory {
  id: string
  userId: string
  gymId: string
  routeId: string
  topped: boolean
  tries: number
  userRating: number
  difficultyFeedback: DifficultyFeedback
  createdAt: string
}

export type DifficultyFeedback =
  | 'MUCH_HARDER'
  | 'HARDER'
  | 'ABOUT_RIGHT'
  | 'EASIER'
  | 'MUCH_EASIER'
