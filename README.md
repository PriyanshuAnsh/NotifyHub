# NotifyHub

## Overview
NotifyHub is a centralized backend notification platform designed to reliably deliver real-time and asynchronous notifications to users without coupling notification delivery logic to core business services.

Modern applications often embed notification logic directly inside business workflows, leading to performance bottlenecks, duplicated logic, poor fault tolerance, and limited observability. NotifyHub addresses this problem by providing a dedicated, scalable notification infrastructure that handles delivery, retries, and real-time updates independently.

This project focuses on **backend architecture, reliability, and system design**, rather than UI development.

---

## Problem Statement
Applications at scale require timely and reliable notification delivery to users across multiple channels. When notification logic is tightly coupled with business services, failures in delivery (such as provider outages or client disconnections) can negatively impact core workflows and are difficult to diagnose or recover from.

There is a need for a centralized notification platform that:
- Decouples notification delivery from business logic
- Supports real-time updates without blocking workflows
- Provides reliable delivery guarantees and failure handling
- Offers observability into notification state and delivery outcomes

---

## Product Vision
NotifyHub enables product teams to trigger notification intents once and rely on a consistent, fault-tolerant infrastructure to handle delivery, retries, and real-time user updates.

The system prioritizes:
- Reliability over immediacy
- Asynchronous, non-blocking workflows
- Clear separation of concerns
- Production-grade observability

---

## Architecture Overview
NotifyHub is implemented as a modular monolith with clearly defined internal responsibilities.

### Key Architectural Principles
- **Database as the source of truth** — notification and delivery state is persisted explicitly
- **Asynchronous delivery pipeline** — delivery tasks are processed via RabbitMQ
- **Best-effort real-time delivery** — Server-Sent Events (SSE) are used for in-app notifications
- **Failure-aware design** — retries and dead-letter handling are explicit and bounded

### High-Level Flow
1. Producer services submit notification intents via REST APIs
2. Notification intents are validated and persisted
3. Delivery tasks are published to RabbitMQ
4. Delivery workers process tasks asynchronously
5. Real-time notifications are pushed to connected users via SSE when available

> RabbitMQ is used strictly as a transport mechanism; it is not a source of state.

---

## Technology Choices
- **Language & Framework:** Java, Spring Boot
- **Asynchronous Messaging:** RabbitMQ
- **Real-Time Delivery:** Server-Sent Events (SSE)
- **Persistence:** Relational Database (Postgres/MySQL)
- **API Testing:** Postman
- **Architecture Style:** Modular Monolith

---

## Scope & Non-Goals

### In Scope
- Notification intake via REST APIs
- Asynchronous, non-blocking delivery processing
- Real-time in-app notifications via SSE
- Delivery state tracking and reliability mechanisms
- Backend-first, API-driven design

### Out of Scope (Initial Versions)
- Frontend UI or dashboard
- Chat or messaging functionality
- Complex scheduling or campaign systems
- Business workflow orchestration

---

## Development Approach
This project follows an **Agile, design-first approach**:
1. Problem definition and product vision
2. Stakeholder and persona analysis
3. Architecture and system contracts
4. Incremental, phase-based implementation

Each phase is tracked via GitHub Issues with explicit acceptance criteria.

---

## Project Phases
- **Phase 0:** Architecture & System Design ✅
- **Phase 1:** Project Setup & Core Infrastructure ✅
- **Phase 2:** Notification Intake & Persistence ✅
- **Phase 3:** Asynchronous Delivery Pipeline ✅
- **Phase 4:** Real-Time Notification Streaming ✅
- **Phase 5+:** Reliability Enhancements, Preferences, Observability

---

## Repository Structure
```
NotifyHub/
├── backend/     Spring Boot service (REST intake, outbox, RabbitMQ workers, SSE)
├── frontend/    React web client (planned)
├── infra/       Docker Compose (Postgres, RabbitMQ, backend)
├── README.md
└── LICENSE
```

---

## How to Run

The backend stack (Postgres, RabbitMQ, application) runs via Docker Compose.

```bash
cd infra
docker compose up --build
```

Services:
- App: http://localhost:8080
- RabbitMQ management UI: http://localhost:15672 (guest / guest)
- Postgres: localhost:5432 (notifyhub / notifyhub)

Health check: `curl http://localhost:8080/actuator/health`

### API

Create a notification (persisted, then delivered asynchronously):

```bash
curl -X POST http://localhost:8080/api/v1/notifications \
  -H 'Content-Type: application/json' \
  -d '{"toEmail":"user@example.com","subject":"Welcome","body":"Hello from NotifyHub"}'
```

Fetch a notification / list all:

```bash
curl http://localhost:8080/api/v1/notifications/{id}
curl http://localhost:8080/api/v1/notifications
```

Subscribe to real-time delivery updates (SSE) for a recipient:

```bash
curl -N http://localhost:8080/api/v1/notifications/stream?user=user@example.com
```

> Delivery is **simulated**: the worker logs the send and marks the notification `SENT`.
> A subject containing the word `fail` forces the failure path — the message is retried,
> then dead-lettered, and the notification is marked `FAILED`.

### Delivery flow

1. `POST /notifications` persists a `Notification` (`PENDING`) **and** an `outbox_events` row in one transaction.
2. `OutboxRelay` polls the outbox and publishes pending events to RabbitMQ, marking them `PUBLISHED`.
3. `NotificationDeliveryWorker` consumes the delivery task, performs the (simulated) send, and marks the notification `SENT`.
4. Failed deliveries retry, then dead-letter to the DLQ where `DeadLetterWorker` marks the notification `FAILED`.
5. Outcomes are pushed to connected SSE clients (best-effort).

> Note: the `contextLoads` integration test requires Postgres and RabbitMQ to be running
> (`docker compose up` the infra first, or run tests inside the compose network).

---

## Status
This project is actively under development and focuses on demonstrating backend system design, reliability patterns, and real-world architectural decision-making.

---

## License
Apache 2.0 License
