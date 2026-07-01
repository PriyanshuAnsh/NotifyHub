/** Slim brand rail. The monogram echoes the signature: a line with a node. */
export function NavRail() {
  return (
    <aside className="sticky top-0 hidden h-screen w-16 shrink-0 flex-col items-center border-r border-line bg-surface py-5 sm:flex">
      <div className="grid size-9 place-items-center rounded-xl bg-ink">
        <Monogram />
      </div>
      <div className="mt-4 flex-1" />
      <span
        className="font-mono text-[10px] tracking-[0.3em] text-ink-faint"
        style={{ writingMode: 'vertical-rl' }}
      >
        NOTIFYHUB
      </span>
    </aside>
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
