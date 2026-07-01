import type { ReactNode } from 'react'
import type { Notification } from '../lib/types'
import { relativeTime, shortId } from '../lib/format'
import { StatusPill } from './StatusPill'

export function NotificationsTable({
  notifications,
  isLoading,
  isError,
  error,
  selectedId,
  onSelect,
}: {
  notifications: Notification[]
  isLoading: boolean
  isError: boolean
  error?: Error | null
  selectedId?: string | null
  onSelect?: (n: Notification) => void
}) {
  return (
    <section className="overflow-hidden rounded-2xl border border-line bg-surface">
      <header className="flex items-center justify-between border-b border-line px-5 py-3.5">
        <h2 className="font-display text-sm font-semibold tracking-tight text-ink">
          Notifications
        </h2>
        <span className="font-mono text-xs tabular-nums text-ink-faint">
          {notifications.length}
        </span>
      </header>

      {isError ? (
        <Message
          title="Can’t reach the backend"
          detail={
            error?.message ??
            'Start it with docker compose up in the infra directory.'
          }
        />
      ) : isLoading ? (
        <SkeletonRows />
      ) : notifications.length === 0 ? (
        <Message
          title="No notifications yet"
          detail="Send one from the panel on the right to watch it move through the lane."
        />
      ) : (
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="text-xs text-ink-faint">
              <Th>ID</Th>
              <Th>Recipient</Th>
              <Th>Subject</Th>
              <Th>Status</Th>
              <Th className="text-right">Updated</Th>
            </tr>
          </thead>
          <tbody>
            {notifications.map((n) => (
              <tr
                key={n.id}
                onClick={() => onSelect?.(n)}
                aria-selected={selectedId === n.id}
                className={`cursor-pointer border-t border-line transition-colors hover:bg-raised ${
                  selectedId === n.id ? 'bg-accent-soft/60' : ''
                }`}
              >
                <Td className="font-mono text-xs tabular-nums text-ink-muted">
                  {shortId(n.id)}
                </Td>
                <Td className="font-mono text-xs text-ink">{n.toEmail}</Td>
                <Td className="max-w-[22ch] truncate text-ink">{n.subject}</Td>
                <Td>
                  <StatusPill status={n.status} />
                </Td>
                <Td className="text-right text-xs tabular-nums text-ink-muted">
                  {relativeTime(n.updatedAt ?? n.createdAt)}
                </Td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}

function Th({
  children,
  className = '',
}: {
  children: ReactNode
  className?: string
}) {
  return (
    <th className={`px-5 py-2.5 font-medium ${className}`}>{children}</th>
  )
}

function Td({
  children,
  className = '',
}: {
  children: ReactNode
  className?: string
}) {
  return <td className={`px-5 py-3 ${className}`}>{children}</td>
}

function Message({ title, detail }: { title: string; detail: string }) {
  return (
    <div className="px-5 py-12 text-center">
      <p className="text-sm font-medium text-ink">{title}</p>
      <p className="mx-auto mt-1 max-w-sm text-xs text-ink-muted">{detail}</p>
    </div>
  )
}

function SkeletonRows() {
  return (
    <div className="divide-y divide-line">
      {Array.from({ length: 4 }).map((_, i) => (
        <div key={i} className="flex items-center gap-4 px-5 py-3.5">
          <div className="h-3 w-16 animate-pulse rounded bg-raised" />
          <div className="h-3 w-40 animate-pulse rounded bg-raised" />
          <div className="ml-auto h-4 w-16 animate-pulse rounded-full bg-raised" />
        </div>
      ))}
    </div>
  )
}
