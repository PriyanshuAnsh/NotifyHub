import { useState } from 'react'
import { NavRail } from './components/NavRail'
import { DeliveryLane } from './components/DeliveryLane'
import { NotificationsTable } from './components/NotificationsTable'
import { ComposeForm } from './components/ComposeForm'
import { LiveFeed } from './components/LiveFeed'
import { useNotifications } from './hooks/useNotifications'
import { useNotificationStream } from './hooks/useNotificationStream'

export default function App() {
  const [watching, setWatching] = useState<string | null>(null)
  const notifications = useNotifications()
  const stream = useNotificationStream(watching)

  const data = notifications.data ?? []

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

        <DeliveryLane notifications={data} pulseKey={stream.activity.length} />

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_360px]">
          <NotificationsTable
            notifications={data}
            isLoading={notifications.isLoading}
            isError={notifications.isError}
            error={notifications.error}
          />
          <div className="flex flex-col gap-6">
            <ComposeForm onSent={setWatching} />
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
