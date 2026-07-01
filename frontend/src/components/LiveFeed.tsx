import type { ReactNode } from 'react'
import type { StreamActivity, StreamState } from '../hooks/useNotificationStream'
import { STATUS_META, relativeTime, shortId } from '../lib/format'

const CONN: Record<StreamState, { label: string; dot: string }> = {
  idle: { label: 'Not watching', dot: 'bg-ink-faint' },
  connecting: { label: 'Connecting', dot: 'bg-pending' },
  open: { label: 'Live', dot: 'bg-sent' },
  error: { label: 'Disconnected', dot: 'bg-failed' },
}

export function LiveFeed({
  user,
  state,
  activity,
}: {
  user: string | null
  state: StreamState
  activity: StreamActivity[]
}) {
  const conn = CONN[state]
  return (
    <section className="flex min-h-0 flex-col rounded-2xl border border-line bg-surface">
      <header className="flex items-center justify-between border-b border-line px-5 py-3.5">
        <div>
          <h2 className="font-display text-sm font-semibold tracking-tight text-ink">
            Live feed
          </h2>
          {user && (
            <p className="font-mono text-[11px] text-ink-faint">{user}</p>
          )}
        </div>
        <span className="inline-flex items-center gap-1.5 text-xs text-ink-muted">
          <span className={`size-1.5 rounded-full ${conn.dot}`} />
          {conn.label}
        </span>
      </header>

      <div className="flex-1 overflow-y-auto p-3">
        {!user ? (
          <Empty>Send a notification to start watching its recipient.</Empty>
        ) : activity.length === 0 ? (
          <Empty>Connected. Delivery events will appear here.</Empty>
        ) : (
          <ul className="flex flex-col gap-1">
            {activity.map((a) => {
              const m = STATUS_META[a.status]
              return (
                <li
                  key={a.key}
                  className="animate-rise flex items-center gap-2.5 rounded-lg px-2.5 py-2 hover:bg-raised"
                >
                  <span className={`size-1.5 shrink-0 rounded-full ${m.dot}`} />
                  <span className="font-mono text-[11px] tabular-nums text-ink-muted">
                    {shortId(a.notificationId)}
                  </span>
                  <span className="truncate text-xs text-ink">{a.subject}</span>
                  <span className={`ml-auto shrink-0 text-xs font-medium ${m.text}`}>
                    {m.label}
                  </span>
                  <span className="shrink-0 text-[11px] tabular-nums text-ink-faint">
                    {relativeTime(a.at)}
                  </span>
                </li>
              )
            })}
          </ul>
        )}
      </div>
    </section>
  )
}

function Empty({ children }: { children: ReactNode }) {
  return (
    <div className="grid h-full min-h-32 place-items-center px-4 text-center">
      <p className="max-w-xs text-xs text-ink-muted">{children}</p>
    </div>
  )
}
