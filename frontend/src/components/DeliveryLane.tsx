import { useEffect, useRef, useState } from 'react'
import type { Notification, NotificationStatus } from '../lib/types'
import { STATUS_META, shortId } from '../lib/format'

const LANES: NotificationStatus[] = ['PENDING', 'SENT', 'FAILED']

/**
 * The signature element: notifications shown as nodes in their delivery stage.
 * A transmission pulse sweeps the track whenever a live event lands, so the
 * page reads as state in transit rather than a static table.
 */
export function DeliveryLane({
  notifications,
  pulseKey,
}: {
  notifications: Notification[]
  pulseKey: number
}) {
  const byStatus = (s: NotificationStatus) =>
    notifications.filter((n) => n.status === s)

  return (
    <section className="rounded-2xl border border-line bg-surface">
      <header className="flex items-center justify-between px-5 pt-4">
        <div>
          <h2 className="font-display text-sm font-semibold tracking-tight text-ink">
            Delivery lane
          </h2>
          <p className="text-xs text-ink-muted">
            Intake &rarr; queue &rarr; delivery, live
          </p>
        </div>
        <span className="font-mono text-xs tabular-nums text-ink-faint">
          {notifications.length} tracked
        </span>
      </header>

      <PulseTrack pulseKey={pulseKey} />

      <div className="grid grid-cols-1 gap-px overflow-hidden rounded-b-2xl bg-line sm:grid-cols-3">
        {LANES.map((status) => {
          const items = byStatus(status)
          const m = STATUS_META[status]
          return (
            <div key={status} className="bg-surface p-4">
              <div className="mb-3 flex items-center justify-between">
                <span className="inline-flex items-center gap-1.5 text-xs font-medium text-ink">
                  <span className={`size-1.5 rounded-full ${m.dot}`} />
                  {m.label}
                </span>
                <span
                  className={`font-display text-lg font-semibold tabular-nums ${m.text}`}
                >
                  {items.length}
                </span>
              </div>
              <div className="flex flex-col gap-1.5">
                {items.slice(0, 6).map((n) => (
                  <Node key={n.id} n={n} />
                ))}
                {items.length === 0 && (
                  <p className="py-2 text-xs text-ink-faint">Nothing here.</p>
                )}
              </div>
            </div>
          )
        })}
      </div>
    </section>
  )
}

function Node({ n }: { n: Notification }) {
  const m = STATUS_META[n.status]
  return (
    <div
      className={`animate-rise flex items-center gap-2 rounded-lg border ${m.border} ${m.soft} px-2.5 py-1.5`}
    >
      <span className="font-mono text-[11px] tabular-nums text-ink-muted">
        {shortId(n.id)}
      </span>
      <span className="truncate text-xs text-ink">{n.subject}</span>
    </div>
  )
}

/** A thin accent bar that sweeps once each time pulseKey changes. */
function PulseTrack({ pulseKey }: { pulseKey: number }) {
  const [visible, setVisible] = useState(false)
  const first = useRef(true)

  useEffect(() => {
    if (first.current) {
      first.current = false
      return
    }
    setVisible(true)
    const t = setTimeout(() => setVisible(false), 700)
    return () => clearTimeout(t)
  }, [pulseKey])

  return (
    <div className="relative mx-5 mt-3 mb-4 h-px bg-line">
      {visible && (
        <div
          className="absolute inset-0 origin-left bg-accent"
          style={{ animation: 'nh-pulse 0.7s ease-out both' }}
        />
      )}
    </div>
  )
}
