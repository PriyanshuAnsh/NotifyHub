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
- **Phase 1:** Project Setup & Core Infrastructure 🚧
- **Phase 2:** Notification Intake & Persistence
- **Phase 3:** Asynchronous Delivery Pipeline
- **Phase 4:** Real-Time Notification Streaming
- **Phase 5+:** Reliability Enhancements, Preferences, Observability

---

## How to Run (Coming Soon)
Local setup instructions will be added once Phase 1 infrastructure is complete.

---

## Status
This project is actively under development and focuses on demonstrating backend system design, reliability patterns, and real-world architectural decision-making.

---

## License
Apache 2.0 License
