export type NotificationStatus = 'PENDING' | 'SENT' | 'FAILED'

export interface Branding {
  logoUrl: string | null
  bannerUrl: string | null
  heading: string | null
  ctaText: string | null
  ctaUrl: string | null
}

export interface Notification {
  id: string
  toEmail: string
  subject: string
  body: string
  status: NotificationStatus
  createdAt: string
  updatedAt: string | null
  branding?: Branding
}

export interface CreateNotificationRequest {
  toEmail: string
  subject: string
  body: string
  // Optional branding — any present renders a branded HTML email.
  logoUrl?: string
  bannerUrl?: string
  heading?: string
  ctaText?: string
  ctaUrl?: string
}

/** Payload pushed over SSE when a notification's delivery state changes. */
export interface NotificationEvent {
  notificationId: string
  subject: string
  status: NotificationStatus
}
