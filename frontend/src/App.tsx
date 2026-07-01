import { useState } from 'react'
import { NavRail } from './components/NavRail'
import { NotificationPipeline } from './components/NotificationPipeline'
import { DeliveryLane } from './components/DeliveryLane'
import { NotificationsTable } from './components/NotificationsTable'
import { ComposeForm } from './components/ComposeForm'
import { LiveFeed } from './components/LiveFeed'
import { useNotifications } from './hooks/useNotifications'
import { useNotificationStream } from './hooks/useNotificationStream'
import type { Notification } from './lib/types'

export default function App() {
  const [watching, setWatching] = useState<string | null>(null)
  const [selectedId, setSelectedId] = useState<string | null>(null)
  const notifications = useNotifications()
  const stream = useNotificationStream(watching)

  const data = notifications.data ?? []
  // Trace the explicitly selected notification, else the most recent one.
  const selected =
    data.find((n) => n.id === selectedId) ?? mostRecent(data) ?? null

  function handleCreated(n: Notification) {
    setSelectedId(n.id)
    setWatching(n.toEmail)
  }

  return (
    <div className="flex min-h-screen">
      <NavRail />

      <div className="mx-auto flex w-full max-w-6xl flex-col gap-6 px-4 py-8 sm:px-8">
        <header className="flex flex-wrap items-end justify-between gap-3">
          <div>
            <h1 className="font-display text-2xl font-semibold tracking-tight text-ink">
              NotifyHub
            </h1>
            <p className="text-sm text-ink-muted">
              Notification delivery console — intake, pipeline, live state.
            </p>
          </div>
          <a
            href="http://localhost:8080/actuator/health"
            target="_blank"
            rel="noreferrer"
            className="font-mono text-xs text-ink-faint underline-offset-4 hover:text-ink-muted hover:underline"
          >
            backend&nbsp;health &#8599;
          </a>
        </header>

        <NotificationPipeline notification={selected} />

        <DeliveryLane notifications={data} pulseKey={stream.activity.length} />

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_360px]">
          <NotificationsTable
            notifications={data}
            isLoading={notifications.isLoading}
            isError={notifications.isError}
            error={notifications.error}
            selectedId={selected?.id ?? null}
            onSelect={(n) => setSelectedId(n.id)}
          />
          <div className="flex flex-col gap-6">
            <ComposeForm onCreated={handleCreated} />
            <LiveFeed
              user={watching}
              state={stream.state}
              activity={stream.activity}
            />
          </div>
        </div>
      </div>
    </div>
  )
}

function mostRecent(list: Notification[]): Notification | undefined {
  if (list.length === 0) return undefined
  return list.reduce((a, b) =>
    new Date(b.createdAt).getTime() > new Date(a.createdAt).getTime() ? b : a,
  )
}
