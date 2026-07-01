import { useState } from 'react'
import { NotificationPipeline } from '../components/NotificationPipeline'
import { NotificationsTable } from '../components/NotificationsTable'
import { ComposeForm } from '../components/ComposeForm'
import { LiveFeed } from '../components/LiveFeed'
import { PageHeader } from '../components/PageHeader'
import { useNotifications } from '../hooks/useNotifications'
import { useNotificationStream } from '../hooks/useNotificationStream'
import type { Notification } from '../lib/types'

export function ConsolePage() {
  const [watching, setWatching] = useState<string | null>(null)
  const [selectedId, setSelectedId] = useState<string | null>(null)
  const notifications = useNotifications()
  const stream = useNotificationStream(watching)

  const data = notifications.data ?? []
  const selected = data.find((n) => n.id === selectedId) ?? mostRecent(data) ?? null

  function handleCreated(n: Notification) {
    setSelectedId(n.id)
    setWatching(n.toEmail)
  }

  return (
    <>
      <PageHeader
        title="NotifyHub"
        subtitle="Notification delivery console — intake, pipeline, live state."
      />

      <NotificationPipeline notification={selected} />

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
          <LiveFeed user={watching} state={stream.state} activity={stream.activity} />
        </div>
      </div>
    </>
  )
}

function mostRecent(list: Notification[]): Notification | undefined {
  if (list.length === 0) return undefined
  return list.reduce((a, b) =>
    new Date(b.createdAt).getTime() > new Date(a.createdAt).getTime() ? b : a,
  )
}
