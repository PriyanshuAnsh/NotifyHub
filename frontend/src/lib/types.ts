export type NotificationStatus = 'PENDING' | 'SENT' | 'FAILED'

export interface Notification {
  id: string
  toEmail: string
  subject: string
  body: string
  status: NotificationStatus
  createdAt: string
  updatedAt: string | null
}

export interface CreateNotificationRequest {
  toEmail: string
  subject: string
  body: string
}

/** Payload pushed over SSE when a notification's delivery state changes. */
export interface NotificationEvent {
  notificationId: string
  subject: string
  status: NotificationStatus
}
