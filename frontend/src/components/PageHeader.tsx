export function PageHeader({ title, subtitle }: { title: string; subtitle: string }) {
  return (
    <header className="flex flex-wrap items-end justify-between gap-3">
      <div>
        <h1 className="font-display text-2xl font-semibold tracking-tight text-ink">
          {title}
        </h1>
        <p className="text-sm text-ink-muted">{subtitle}</p>
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
  )
}
