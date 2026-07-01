import type { NotificationStatus } from './types'

export const STATUS_META: Record<
  NotificationStatus,
  { label: string; dot: string; text: string; soft: string; border: string }
> = {
  PENDING: {
    label: 'Pending',
    dot: 'bg-pending',
    text: 'text-pending',
    soft: 'bg-pending-soft',
    border: 'border-pending/25',
  },
  SENT: {
    label: 'Sent',
    dot: 'bg-sent',
    text: 'text-sent',
    soft: 'bg-sent-soft',
    border: 'border-sent/25',
  },
  FAILED: {
    label: 'Failed',
    dot: 'bg-failed',
    text: 'text-failed',
    soft: 'bg-failed-soft',
    border: 'border-failed/25',
  },
}

export function shortId(id: string): string {
  return id.slice(0, 8)
}

export function relativeTime(iso: string | number): string {
  const then = typeof iso === 'number' ? iso : new Date(iso).getTime()
  const secs = Math.round((Date.now() - then) / 1000)
  if (secs < 5) return 'just now'
  if (secs < 60) return `${secs}s ago`
  const mins = Math.round(secs / 60)
  if (mins < 60) return `${mins}m ago`
  const hrs = Math.round(mins / 60)
  if (hrs < 24) return `${hrs}h ago`
  return `${Math.round(hrs / 24)}d ago`
}
