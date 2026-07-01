import type { Notification, NotificationStatus } from '../lib/types'
import { shortId } from '../lib/format'
import { StatusPill } from './StatusPill'

type NodeState = 'done' | 'active' | 'upcoming' | 'delivered' | 'failed'

const STAGES = ['Accepted', 'Queued', 'Dispatched', 'Delivered'] as const

/**
 * Traces one notification through the delivery pipeline. The connecting line and
 * the stage nodes animate as the status advances (PENDING → SENT / FAILED).
 */
export function NotificationPipeline({ notification }: { notification: Notification | null }) {
  if (!notification) {
    return (
      <section className="rounded-2xl border border-line bg-surface p-5">
        <Header />
        <div className="grid h-28 place-items-center">
          <p className="text-xs text-ink-muted">
            Select a notification to trace its journey through the pipeline.
          </p>
        </div>
      </section>
    )
  }

  const status = notification.status
  const reached = reachedIndex(status)
  const fillPct = (reached / (STAGES.length - 1)) * 100
  const fillColor =
    status === 'SENT' ? 'bg-sent' : status === 'FAILED' ? 'bg-failed' : 'bg-accent'

  return (
    <section className="rounded-2xl border border-line bg-surface p-5">
      <Header notification={notification} />

      <div className="relative mt-7 mb-2 px-1">
        {/* track + animated fill */}
        <div className="absolute left-[15px] right-[15px] top-[14px] h-0.5 rounded bg-line">
          <div
            className={`h-full rounded ${fillColor} transition-all duration-700 ease-out`}
            style={{ width: `${fillPct}%` }}
          />
        </div>

        {/* stage nodes */}
        <div className="relative flex justify-between">
          {STAGES.map((stage, i) => (
            <Node key={stage} label={labelFor(stage, i, status)} state={nodeState(i, status)} />
          ))}
        </div>
      </div>
    </section>
  )
}

function Header({ notification }: { notification?: Notification }) {
  return (
    <div className="flex items-start justify-between gap-3">
      <div>
        <h2 className="font-display text-sm font-semibold tracking-tight text-ink">
          Delivery pipeline
        </h2>
        {notification ? (
          <p className="mt-0.5 flex items-center gap-2 text-xs text-ink-muted">
            <span className="font-mono text-ink-faint">{shortId(notification.id)}</span>
            <span className="max-w-[26ch] truncate text-ink">{notification.subject}</span>
          </p>
        ) : (
          <p className="mt-0.5 text-xs text-ink-muted">Live stage of a single notification</p>
        )}
      </div>
      {notification && <StatusPill status={notification.status} />}
    </div>
  )
}

function Node({ label, state }: { label: string; state: NodeState }) {
  return (
    <div className="flex flex-col items-center gap-2" style={{ width: 64 }}>
      <div className="relative grid size-7 place-items-center">
        {state === 'active' && (
          <span className="absolute inline-flex size-7 animate-ping rounded-full bg-accent/30" />
        )}
        <span
          className={`relative grid size-7 place-items-center rounded-full border-2 transition-all duration-500 ${dotClass(
            state,
          )}`}
        >
          {(state === 'done' || state === 'delivered') && <Check />}
          {state === 'failed' && <Cross />}
          {state === 'active' && <span className="size-2 rounded-full bg-accent" />}
        </span>
      </div>
      <span
        className={`text-center text-[11px] leading-tight transition-colors duration-500 ${labelClass(
          state,
        )}`}
      >
        {label}
      </span>
    </div>
  )
}

function dotClass(state: NodeState): string {
  switch (state) {
    case 'done':
      return 'border-accent bg-accent text-white'
    case 'delivered':
      return 'border-sent bg-sent text-white'
    case 'failed':
      return 'border-failed bg-failed text-white'
    case 'active':
      return 'border-accent bg-surface'
    default:
      return 'border-line-strong bg-surface'
  }
}

function labelClass(state: NodeState): string {
  if (state === 'upcoming') return 'text-ink-faint'
  if (state === 'failed') return 'text-failed font-medium'
  if (state === 'delivered') return 'text-sent font-medium'
  if (state === 'active') return 'text-accent font-medium'
  return 'text-ink-muted'
}

/** How far the pipeline has progressed for a given status. */
function reachedIndex(status: NotificationStatus): number {
  if (status === 'SENT' || status === 'FAILED') return STAGES.length - 1
  return 2 // PENDING: accepted + queued done, dispatching now
}

function nodeState(i: number, status: NotificationStatus): NodeState {
  const last = STAGES.length - 1
  if (i === last) {
    if (status === 'SENT') return 'delivered'
    if (status === 'FAILED') return 'failed'
    return 'upcoming'
  }
  if (status === 'PENDING') {
    if (i < 2) return 'done'
    if (i === 2) return 'active'
    return 'upcoming'
  }
  return 'done' // SENT / FAILED: all prior stages complete
}

function labelFor(stage: string, i: number, status: NotificationStatus): string {
  if (i === STAGES.length - 1 && status === 'FAILED') return 'Failed'
  return stage
}

function Check() {
  return (
    <svg width="12" height="12" viewBox="0 0 12 12" fill="none" aria-hidden>
      <path d="M2.5 6.2 5 8.5 9.5 3.5" stroke="currentColor" strokeWidth="1.6"
        strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  )
}

function Cross() {
  return (
    <svg width="11" height="11" viewBox="0 0 12 12" fill="none" aria-hidden>
      <path d="M3.5 3.5 8.5 8.5M8.5 3.5 3.5 8.5" stroke="currentColor" strokeWidth="1.6"
        strokeLinecap="round" />
    </svg>
  )
}
