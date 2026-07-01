import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createNotification, listNotifications } from '../lib/api'
import type { CreateNotificationRequest, Notification } from '../lib/types'

const KEY = ['notifications'] as const

export function useNotifications() {
  return useQuery<Notification[]>({
    queryKey: KEY,
    queryFn: listNotifications,
    // SSE is per-recipient, so poll as a floor for the global view.
    refetchInterval: 5000,
  })
}

export function useCreateNotification() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (input: CreateNotificationRequest) => createNotification(input),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEY }),
  })
}

export function invalidateNotifications(qc: ReturnType<typeof useQueryClient>) {
  return qc.invalidateQueries({ queryKey: KEY })
}
