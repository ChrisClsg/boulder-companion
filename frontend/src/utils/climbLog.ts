import type { ClimbLog } from 'src/types'

export const logLabel = (log: ClimbLog): string => {
  if (log.flashed) return 'Flash'
  if (log.topped) return 'Topped'
  return 'Tried'
}

export const logColor = (log: ClimbLog): string => {
  if (log.flashed) return 'purple'
  if (log.topped) return 'positive'
  return 'orange'
}

export const logIcon = (log: ClimbLog): string => {
  if (log.flashed) return 'bolt'
  if (log.topped) return 'check_circle'
  return 'hourglass_bottom'
}

export const formatClimbedAt = (value: string): string =>
  new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
