# NotifyHub Frontend

React console for NotifyHub. Sends notification intents, shows them moving through
the delivery pipeline, and reflects live state from the backend's SSE stream.

## Stack
- **Vite + React 19 + TypeScript**
- **Tailwind CSS v4** (`@tailwindcss/vite`) — design tokens in `src/index.css`
- **TanStack Query** — server state, polling, cache invalidation
- **EventSource** — live delivery stream (`useNotificationStream`)
- Self-hosted fonts: Space Grotesk (display), IBM Plex Sans (UI), IBM Plex Mono (data)

## Design
Direction: **"The Line"** — a delivery-pipeline console. The signature element is the
live delivery lane (`DeliveryLane`): notifications appear as nodes in their stage and a
transmission pulse sweeps the track on each live event. The UI stays neutral so the
status trio (emerald SENT / amber PENDING / red FAILED) carries all the color.

## Structure
```
src/
├── lib/          types, API client, formatters + status metadata
├── hooks/        useNotifications (Query), useNotificationStream (SSE)
├── components/   NavRail, DeliveryLane, NotificationsTable, ComposeForm, LiveFeed, StatusPill
├── App.tsx       layout
└── main.tsx      providers + fonts
```

## Develop
```bash
cd frontend
pnpm install
pnpm dev            # http://localhost:5173
```
`/api` is proxied to the backend at `http://localhost:8080` (see `vite.config.ts`), so
start the backend (`cd infra && docker compose up`) for live data. Without it, the console
shows its empty / disconnected states.

## Build
```bash
pnpm build          # tsc + vite build → dist/
pnpm preview
```
