# NotifyHub Frontend

React web client for NotifyHub. Consumes the backend REST API and subscribes to
the live delivery stream (SSE) to reflect notification state in real time.

> **Status:** not yet scaffolded. Planned stack below.

## Planned stack
- **Vite + React + TypeScript** — fast dev server, typed components
- **TanStack Query** — server-state, caching, request lifecycle
- **Tailwind CSS** — utility styling with a deliberate design system (not default templates)
- **EventSource** — subscribe to `GET /api/v1/notifications/stream?user=` for live updates

## Backend API
Base URL: `http://localhost:8080`

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/v1/notifications` | Create a notification |
| GET | `/api/v1/notifications` | List notifications |
| GET | `/api/v1/notifications/{id}` | Fetch one |
| GET | `/api/v1/notifications/stream?user=` | SSE delivery stream |

## Local development (once scaffolded)
```bash
cd frontend
npm install
npm run dev
```
