import { useEffect, useRef, useState } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { streamUrl } from '../lib/api'
import type { NotificationEvent } from '../lib/types'

export type StreamState = 'idle' | 'connecting' | 'open' | 'error'

export interface StreamActivity extends NotificationEvent {
  /** client-side receive time, for the live feed ordering */
  at: number
  key: string
}

/**
 * Subscribes to a recipient's SSE delivery stream. Emits recent activity and
 * refreshes the notifications query whenever the server reports a state change.
 */
export function useNotificationStream(user: string | null) {
  const qc = useQueryClient()
  const [state, setState] = useState<StreamState>('idle')
  const [activity, setActivity] = useState<StreamActivity[]>([])
  const seq = useRef(0)

  useEffect(() => {
    if (!user) {
      setState('idle')
      return
    }
    setState('connecting')
    const source = new EventSource(streamUrl(user))

    source.onopen = () => setState('open')
    source.onerror = () => setState('error')

    source.addEventListener('notification', (e) => {
      try {
        const data = JSON.parse((e as MessageEvent).data) as NotificationEvent
        setActivity((prev) =>
          [{ ...data, at: Date.now(), key: `${seq.current++}` }, ...prev].slice(0, 40),
        )
        qc.invalidateQueries({ queryKey: ['notifications'] })
      } catch {
        /* ignore malformed frames */
      }
    })

    return () => source.close()
  }, [user, qc])

  return { state, activity }
}
