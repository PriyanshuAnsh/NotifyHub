import { useState, type FormEvent, type ReactNode } from 'react'
import { useCreateNotification } from '../hooks/useNotifications'
import { ApiError } from '../lib/api'
import type { Notification } from '../lib/types'

/**
 * Submits a notification intent. Mirrors the backend contract; a subject
 * containing "fail" intentionally drives the retry / dead-letter path.
 */
export function ComposeForm({ onCreated }: { onCreated: (n: Notification) => void }) {
  const [toEmail, setToEmail] = useState('')
  const [subject, setSubject] = useState('')
  const [body, setBody] = useState('')
  const [showBranding, setShowBranding] = useState(false)
  const [logoUrl, setLogoUrl] = useState('')
  const [bannerUrl, setBannerUrl] = useState('')
  const [heading, setHeading] = useState('')
  const [ctaText, setCtaText] = useState('')
  const [ctaUrl, setCtaUrl] = useState('')
  const create = useCreateNotification()

  const fieldErrors =
    create.error instanceof ApiError ? create.error.fields : undefined

  function handleSubmit(e: FormEvent) {
    e.preventDefault()
    create.mutate(
      { toEmail, subject, body, logoUrl, bannerUrl, heading, ctaText, ctaUrl },
      {
        onSuccess: (created) => {
          onCreated(created)
          setSubject('')
          setBody('')
        },
      },
    )
  }

  return (
    <section className="rounded-2xl border border-line bg-surface p-5">
      <h2 className="font-display text-sm font-semibold tracking-tight text-ink">
        Send a notification
      </h2>
      <p className="mb-4 text-xs text-ink-muted">
        Persisted, then delivered through the pipeline.
      </p>

      <form onSubmit={handleSubmit} className="flex flex-col gap-3">
        <Field label="Recipient" error={fieldErrors?.toEmail}>
          <input
            type="email"
            required
            value={toEmail}
            onChange={(e) => setToEmail(e.target.value)}
            placeholder="user@example.com"
            className="input"
          />
        </Field>
        <Field label="Subject" error={fieldErrors?.subject}>
          <input
            type="text"
            required
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
            placeholder="Welcome to NotifyHub"
            className="input"
          />
        </Field>
        <Field label="Body" error={fieldErrors?.body}>
          <textarea
            required
            rows={3}
            value={body}
            onChange={(e) => setBody(e.target.value)}
            placeholder="Your account is ready."
            className="input resize-none"
          />
        </Field>

        <div className="border-t border-line pt-2">
          <button
            type="button"
            onClick={() => setShowBranding((v) => !v)}
            className="flex w-full items-center justify-between text-xs font-medium text-ink-muted hover:text-ink"
          >
            Branding &amp; rich email
            <span className="text-ink-faint">{showBranding ? '–' : '+'}</span>
          </button>
          {showBranding && (
            <div className="mt-3 flex flex-col gap-3">
              <p className="text-[11px] text-ink-faint">
                Any field here renders a branded HTML email. Images are referenced by URL.
              </p>
              <Field label="Logo URL">
                <input type="url" value={logoUrl} onChange={(e) => setLogoUrl(e.target.value)}
                  placeholder="https://acme.com/logo.png" className="input" />
              </Field>
              <Field label="Banner URL">
                <input type="url" value={bannerUrl} onChange={(e) => setBannerUrl(e.target.value)}
                  placeholder="https://acme.com/banner.png" className="input" />
              </Field>
              <Field label="Heading">
                <input type="text" value={heading} onChange={(e) => setHeading(e.target.value)}
                  placeholder="Welcome to Acme" className="input" />
              </Field>
              <div className="grid grid-cols-2 gap-3">
                <Field label="Button text">
                  <input type="text" value={ctaText} onChange={(e) => setCtaText(e.target.value)}
                    placeholder="Get started" className="input" />
                </Field>
                <Field label="Button URL">
                  <input type="url" value={ctaUrl} onChange={(e) => setCtaUrl(e.target.value)}
                    placeholder="https://acme.com" className="input" />
                </Field>
              </div>
            </div>
          )}
        </div>

        {create.error && !fieldErrors && (
          <p className="text-xs text-failed">{create.error.message}</p>
        )}

        <button
          type="submit"
          disabled={create.isPending}
          className="mt-1 inline-flex items-center justify-center rounded-lg bg-accent px-3.5 py-2 text-sm font-medium text-white transition-colors hover:bg-accent-ink focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent disabled:opacity-60"
        >
          {create.isPending ? 'Sending…' : 'Send notification'}
        </button>
        <p className="text-[11px] text-ink-faint">
          Tip: a subject containing{' '}
          <code className="rounded bg-raised px-1 font-mono text-failed">
            fail
          </code>{' '}
          exercises the retry &amp; dead-letter path.
        </p>
      </form>
    </section>
  )
}

function Field({
  label,
  error,
  children,
}: {
  label: string
  error?: string
  children: ReactNode
}) {
  return (
    <label className="flex flex-col gap-1">
      <span className="text-xs font-medium text-ink-muted">{label}</span>
      {children}
      {error && <span className="text-xs text-failed">{error}</span>}
    </label>
  )
}
