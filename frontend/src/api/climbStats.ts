import { api } from 'src/boot/axios'
import type { ClimbStatsSummary } from 'src/types'

export const climbStatsApi = {
  async getSummary(): Promise<ClimbStatsSummary> {
    const response = await api.get<ClimbStatsSummary>('/me/stats/summary')
    return response.data
  },
}
