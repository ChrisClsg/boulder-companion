import axios from 'axios'

type ErrorResponseData = {
  message?: unknown
  error?: unknown
}

function getStringValue(value: unknown): string | undefined {
  return typeof value === 'string' && value.trim().length > 0
    ? value
    : undefined
}

export function getErrorMessage(error: unknown, fallback = 'Something went wrong'): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as ErrorResponseData | undefined

    const message =
      getStringValue(data?.message) ||
      getStringValue(data?.error) ||
      getStringValue(error.message)

    return message || fallback
  }

  if (error instanceof Error) {
    return error.message || fallback
  }

  return fallback
}
