import type { CreateNotificationRequest, Notification } from './types'

const BASE = '/api/v1/notifications'

export class ApiError extends Error {
  readonly status: number
  readonly fields?: Record<string, string>

  constructor(message: string, status: number, fields?: Record<string, string>) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fields = fields
  }
}

async function parse<T>(res: Response): Promise<T> {
  if (res.ok) {
    return res.status === 204 ? (undefined as T) : ((await res.json()) as T)
  }
  // Spring returns RFC 7807 ProblemDetail; surface field errors when present.
  let detail = `Request failed (${res.status})`
  let fields: Record<string, string> | undefined
  try {
    const problem = await res.json()
    detail = problem.detail ?? detail
    fields = problem.errors
  } catch {
    /* non-JSON body */
  }
  throw new ApiError(detail, res.status, fields)
}

export function listNotifications(): Promise<Notification[]> {
  return fetch(BASE).then((r) => parse<Notification[]>(r))
}

export function createNotification(
  input: CreateNotificationRequest,
): Promise<Notification> {
  return fetch(BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input),
  }).then((r) => parse<Notification>(r))
}

/** URL for the recipient's live delivery stream (consumed via EventSource). */
export function streamUrl(user: string): string {
  return `${BASE}/stream?user=${encodeURIComponent(user)}`
}
