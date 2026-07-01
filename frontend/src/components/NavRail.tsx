import type { ReactNode } from 'react'
import { NavLink } from 'react-router-dom'

/** Slim brand rail with primary navigation. The monogram echoes the signature. */
export function NavRail() {
  return (
    <aside className="sticky top-0 hidden h-screen w-16 shrink-0 flex-col items-center border-r border-line bg-surface py-5 sm:flex">
      <div className="grid size-9 place-items-center rounded-xl bg-ink">
        <Monogram />
      </div>

      <nav className="mt-8 flex flex-col items-center gap-2">
        <RailLink to="/" label="Console" icon={<PulseIcon />} />
        <RailLink to="/overview" label="Overview" icon={<LanesIcon />} />
      </nav>

      <div className="flex-1" />
      <span
        className="font-mono text-[10px] tracking-[0.3em] text-ink-faint"
        style={{ writingMode: 'vertical-rl' }}
      >
        NOTIFYHUB
      </span>
    </aside>
  )
}

function RailLink({
  to,
  label,
  icon,
}: {
  to: string
  label: string
  icon: ReactNode
}) {
  return (
    <NavLink
      to={to}
      end
      title={label}
      aria-label={label}
      className={({ isActive }) =>
        `grid size-9 place-items-center rounded-lg transition-colors ${
          isActive
            ? 'bg-accent-soft text-accent'
            : 'text-ink-faint hover:bg-raised hover:text-ink-muted'
        }`
      }
    >
      {icon}
    </NavLink>
  )
}

function Monogram() {
  return (
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none" aria-hidden>
      <line x1="2" y1="9" x2="16" y2="9" stroke="white" strokeWidth="1.5" />
      <circle cx="6" cy="9" r="2" fill="#5B5BD6" />
      <circle cx="13" cy="9" r="2.5" fill="white" />
    </svg>
  )
}

function PulseIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none" aria-hidden>
      <path d="M2 9h3l2-5 3 10 2-5h4" stroke="currentColor" strokeWidth="1.5"
        strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  )
}

function LanesIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none" aria-hidden>
      <rect x="2.5" y="3" width="3.4" height="12" rx="1" stroke="currentColor" strokeWidth="1.4" />
      <rect x="7.3" y="3" width="3.4" height="12" rx="1" stroke="currentColor" strokeWidth="1.4" />
      <rect x="12.1" y="3" width="3.4" height="12" rx="1" stroke="currentColor" strokeWidth="1.4" />
    </svg>
  )
}
