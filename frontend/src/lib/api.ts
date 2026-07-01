import type { CreateNotificationRequest, Notification } from './types'

const BASE = '/api/v1/notifications'

// Dev console key; override per environment with VITE_API_KEY. In a public
// deployment the console would sit behind real user auth rather than ship a key.
const API_KEY = import.meta.env.VITE_API_KEY ?? 'dev-local-key'

function authHeaders(extra?: Record<string, string>): Record<string, string> {
  return { 'X-API-Key': API_KEY, ...extra }
}

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
  return fetch(BASE, { headers: authHeaders() }).then((r) => parse<Notification[]>(r))
}

export function createNotification(
  input: CreateNotificationRequest,
): Promise<Notification> {
  return fetch(BASE, {
    method: 'POST',
    headers: authHeaders({ 'Content-Type': 'application/json' }),
    body: JSON.stringify(input),
  }).then((r) => parse<Notification>(r))
}

/**
 * URL for the recipient's live delivery stream (consumed via EventSource).
 * EventSource can't set headers, so the key travels as a query parameter.
 */
export function streamUrl(user: string): string {
  return `${BASE}/stream?user=${encodeURIComponent(user)}&apiKey=${encodeURIComponent(API_KEY)}`
}
