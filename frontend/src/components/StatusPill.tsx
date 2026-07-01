import type { NotificationStatus } from '../lib/types'
import { STATUS_META } from '../lib/format'

export function StatusPill({ status }: { status: NotificationStatus }) {
  const m = STATUS_META[status]
  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full border ${m.border} ${m.soft} px-2 py-0.5 text-xs font-medium ${m.text}`}
    >
      <span className={`size-1.5 rounded-full ${m.dot}`} />
      {m.label}
    </span>
  )
}
