export interface User {
  id: string
  githubId: string
  name: string
  email: string
  role: 'CLIMBER' | 'GYM_ADMIN' | 'ROUTE_SETTER'
  favoriteGyms: string[]
  favoriteRoutes: string[]
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
  wall: string
  difficulty: {
    value: string
    scale: string
  }
  holdColor: string
  holdTypes: string[]
  setterId: string
  images?: {
    url: string
    caption?: string
  }[]
}

export interface Difficulty {
  value: string
  scale: 'v' | 'font' | 'custom'
}

export interface Image {
  url: string
  caption: string
}

export type DifficultyFeedback =
  | 'MUCH_HARDER'
  | 'HARDER'
  | 'ABOUT_RIGHT'
  | 'EASIER'
  | 'MUCH_EASIER'

export type ClimbLog = {
  id: string
  userId: string
  gymId: string
  routeId: string
  sessionId: string | null
  topped: boolean
  flashed: boolean
  attempts: number
  climbedAt: string
  createdAt: string
}

export type CreateClimbLogPayload = {
  gymId: string
  routeId: string
  sessionId?: string | null
  topped: boolean
  attempts: number
  climbedAt: string
}

export type UpdateClimbLogPayload = {
  sessionId?: string | null
  topped?: boolean
  flashed?: boolean
  attempts?: number
  climbedAt?: string
}

export type RouteFeedback = {
  id: string
  userId: string
  routeId: string
  gymId: string
  userRating: number
  difficultyFeedback: DifficultyFeedback
  createdAt: string
  updatedAt: string
}

export type SaveRouteFeedbackPayload = {
  userRating: number
  difficultyFeedback: DifficultyFeedback
}

export type RoutePersonalSummary = {
  totalLogs: number
  totalAttempts: number
  topped: boolean
  flashed: boolean
  lastLog: ClimbLog | null
}

export type ClimbStatsSummary = {
  toppedCount: number
  toppedPercentage: number
  flashedPercentage: number
  averageAttemptsPerTop: number
}
