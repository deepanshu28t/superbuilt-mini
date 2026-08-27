# SuperBuilt Mini — Autonomous Project Coordination Agent

## 1. Project Overview

SuperBuilt Mini is a proof-of-concept inspired by the problem SuperBuilt is solving: reducing the operational coordination work that consumes an architect's time.

The system acts as a small autonomous project coordination agent. It ingests project documents, RFIs, and project communications, understands the available project context, identifies potential issues, prioritizes risks, recommends actions, and produces a concise "Morning Brief" showing the architect only the items that require attention.

### Core Product Idea

> The architect should not have to search through the project to discover what needs attention. The system should continuously analyze project information and surface the important issues.

This is a portfolio/POC project, not an attempt to reproduce SuperBuilt's production system.

---

# 2. Project Goal

Build a working vertical slice of an autonomous construction-project coordination system.

The final POC should demonstrate this flow:

```text
Project Data
    |
    +-- Architectural Drawings
    +-- Structural Drawings
    +-- MEP Drawings
    +-- Specifications
    +-- RFIs
    +-- Communications
    |
    v
Document / Communication Ingestion
    |
    v
AI Understanding + Retrieval
    |
    +-- RFI Analysis
    +-- Cross-document Context
    +-- Compliance Checks
    +-- Issue Detection
    |
    v
Issue Engine
    |
    v
Risk / Priority Engine
    |
    v
Recommended Actions
    |
    v
Morning Brief
    |
    v
Architect / Human Decision
```

---

# 3. Target User

Primary user:

- Architect
- Project architect
- Design coordinator
- Project coordination team

The user should receive a concise view of what requires attention rather than manually reviewing every project communication and document.

---

# 4. Example Project

For our POC we will use a fictional construction project.

### Project

**Name:** Tower A

**Type:** Commercial Building

**Floors:** 12

**Location:** Bangalore, India

**Phase:** Construction

### Disciplines

- Architecture
- Structural
- MEP
- Fire Safety

### Example project information

We will create realistic sample data containing:

- Architectural drawings
- Structural drawings
- MEP drawings
- Fire-safety/project specifications
- RFIs
- Project communications

Target sample dataset:

- 10–15 documents
- 8–10 RFIs
- 10–15 communications
- 5–8 compliance rules

The dataset is intentionally small so that the POC can be completed within approximately 10 days.

---

# 5. MVP Features

## Feature 1 — Project Management

The system will support basic project creation and retrieval.

Example:

```text
Tower A
12 floors
Construction phase
```

---

## Feature 2 — Document Ingestion

The user can upload project PDF documents.

Initial supported format:

```text
PDF
```

The system will:

```text
PDF
 |
 v
Text Extraction
 |
 v
Chunking
 |
 v
Embeddings
 |
 v
Vector Storage
```

Apache PDFBox will initially be used for PDF text extraction.

---

## Feature 3 — RFI Intelligence

The system will analyze an RFI and extract structured information such as:

```json
{
  "issueType": "CLASH",
  "severity": "HIGH",
  "disciplines": [
    "MEP",
    "STRUCTURAL"
  ],
  "level": "8",
  "requiresDecision": true,
  "summary": "Potential HVAC duct and structural beam conflict",
  "recommendedAction": "Coordinate revised MEP routing"
}
```

The extracted information will be used to create or update project issues.

---

## Feature 4 — Project-Aware Retrieval / RAG

The system should not blindly send every document to the LLM.

Instead:

```text
RFI
 |
 v
Query / Embedding
 |
 v
Vector Search
 |
 v
Relevant Project Documents
 |
 v
LLM
```

This gives the AI relevant project context.

Example:

An RFI about an HVAC issue on Level 8 may retrieve:

```text
M-812.pdf
S-208.pdf
A-104.pdf
```

The retrieved content is then provided to the AI for analysis.

---

## Feature 5 — Compliance Engine

The POC will combine:

```text
LLM
+
Deterministic Rules
```

The LLM extracts facts from project documents.

A deterministic rule evaluates the actual requirement.

Example:

```text
Requirement:
Minimum exit door width = 1500 mm

Drawing:
Door D18 = 1200 mm

Result:
COMPLIANCE ISSUE
```

This approach is intentional because important compliance decisions should not rely entirely on probabilistic LLM output.

---

## Feature 6 — Issue Management

Different findings will be normalized into an `Issue`.

Examples:

```text
CLASH
COMPLIANCE
RFI
DELAY
COORDINATION
```

Each issue will contain information such as:

```text
Title
Description
Type
Severity
Status
Risk Score
Source
Requires Human Decision
```

---

## Feature 7 — Risk / Priority Engine

Issues will be prioritized using factors such as:

```text
Severity
Deadline
Dependency / impact
AI confidence
```

The result will be a risk score and priority.

Example:

```text
HVAC / Structural Clash
Risk: 94/100
Priority: CRITICAL

Fire Compliance
Risk: 87/100
Priority: HIGH

Overdue RFI
Risk: 81/100
Priority: HIGH
```

The exact scoring formula will be kept simple for the POC and documented as an explicit design decision.

---

## Feature 8 — Recommended Actions

For detected issues, the system can recommend actions.

Examples:

```text
Request revised MEP drawing
Escalate overdue RFI
Review compliance issue
Coordinate with structural team
```

Important:

The AI will recommend actions but high-impact actions will remain subject to human approval.

---

## Feature 9 — AI-Generated RFI Response

For appropriate RFIs, the system will generate a draft response using relevant project context.

Example:

```text
Please coordinate with the structural and MEP teams
to revise the proposed HVAC duct routing at Level 8.

The current routing conflicts with the structural beam
shown in the relevant structural drawing.

Please submit the revised MEP drawing for coordination review.
```

The architect can review, edit, approve, or reject the draft.

---

## Feature 10 — Morning Brief

This is the primary product experience.

The system should summarize:

```text
Critical Issues
High-Priority Issues
Overdue RFIs
Recommended Actions
AI-Handled Items
```

Example:

```text
GOOD MORNING

3 issues need your decision.

1 Critical
2 High

AI handled 8 other items.

Critical:
HVAC / Structural Clash
Level 8
Risk: 94

High:
Fire Door Compliance
Risk: 87

High:
Overdue RFI
Risk: 81
```

The key product idea is:

> Humans own the creative and strategic decisions. The system handles the operational coordination workload.

---

# 6. What "Autonomous" Means in This POC

The system is not intended to be a generic chatbot.

A simple chatbot flow is:

```text
User
 |
 v
Question
 |
 v
LLM
 |
 v
Answer
```

Our agent flow is:

```text
Project Data Changes
 |
 v
Observe
 |
 v
Understand
 |
 v
Retrieve Relevant Context
 |
 v
Analyze
 |
 v
Identify Issue
 |
 v
Evaluate Risk
 |
 v
Recommend Action
 |
 v
Update Project State
 |
 v
Morning Brief
 |
 v
Human Decision
```

Example:

```text
New RFI
 |
 v
RFI Analysis
 |
 v
Relevant Documents Retrieved
 |
 v
Potential Clash Identified
 |
 v
Issue Created
 |
 v
Risk Calculated
 |
 v
Response Drafted
 |
 v
Architect Sees Issue in Morning Brief
```

---

# 7. Technology Stack

## Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring AI
- Maven
- Bean Validation
- Lombok

## Database

- PostgreSQL
- pgvector

PostgreSQL will store both application data and vector embeddings for the POC.

## Frontend

- React
- TypeScript
- Vite

The frontend will provide a simple project dashboard.

## AI

- LLM API
- Spring AI
- Embeddings
- Structured LLM output
- Retrieval-Augmented Generation (RAG)

The exact model/provider will be configured during the AI implementation phase.

## Document Processing

- Apache PDFBox

## Development / Infrastructure

- Git
- Docker
- Docker Compose

---

# 8. High-Level Architecture

```text
                         +----------------------+
                         |        React         |
                         |      Dashboard       |
                         +----------+-----------+
                                    |
                                   REST
                                    |
                         +----------v-----------+
                         |     Spring Boot      |
                         |                      |
                         | Project Service      |
                         | Document Service     |
                         | RFI Service          |
                         | Issue Service        |
                         | Communication Service|
                         | Compliance Engine     |
                         | Risk Engine           |
                         | Action Service        |
                         | AI Service            |
                         +-----+-----------+----+
                               |           |
                               |           |
                    +----------v--+     +--v----------+
                    | PostgreSQL  |     |   LLM API   |
                    | + pgvector  |     +-------------+
                    +------+------+
                           ^
                           |
                    +------+- -----+
                    | PDF Processor |
                    |   PDFBox      |
                    +--------------+
```

---

# 9. Architectural Approach

For this POC we will use a:

## Modular Monolith

We will NOT build a microservices architecture.

The Spring Boot application will contain logical modules:

```text
project
document
rfi
issue
communication
compliance
risk
action
ai
```

Reason:

- Faster development
- Easier local setup
- Easier debugging
- Appropriate for a 10-day POC
- Still demonstrates clean separation of responsibilities
- Can later be split into services if scale requires it

---

# 10. Planned Project Structure

Final repository structure:

```text
superbuilt-mini/
|
+-- backend/
|   |
|   +-- src/
|   |   +-- main/
|   |       +-- java/
|   |       |   +-- com/superbuilt/mini/
|   |       |       +-- project/
|   |       |       +-- document/
|   |       |       +-- rfi/
|   |       |       +-- issue/
|   |       |       +-- communication/
|   |       |       +-- compliance/
|   |       |       +-- risk/
|   |       |       +-- action/
|   |       |       +-- ai/
|   |       |
|   |       +-- resources/
|   |
|   +-- pom.xml
|
+-- frontend/
|   |
|   +-- src/
|   |
|   +-- package.json
|
+-- sample-data/
|   |
|   +-- drawings/
|   +-- rfis/
|   +-- specifications/
|   +-- communications/
|
+-- docs/
|   |
|   +-- product.md
|   +-- architecture.md
|   +-- decisions.md
|
+-- docker-compose.yml
+-- README.md
```

The actual package structure may evolve slightly as implementation progresses.

---

# 11. Core Domain Model

Initial entities:

```text
Project
Document
DocumentChunk
RFI
Communication
Issue
Action
```

Relationship overview:

```text
Project
 |
 +-- Documents
 |     |
 |     +-- DocumentChunks
 |
 +-- RFIs
 |
 +-- Communications
 |
 +-- Issues
       |
       +-- Actions
```

---

# 12. Initial Database Model

## Project

```text
id
name
description
location
phase
createdAt
```

## Document

```text
id
projectId
name
type
discipline
filePath
status
createdAt
```

## DocumentChunk

```text
id
documentId
content
pageNumber
embedding
```

## RFI

```text
id
projectId
rfiNumber
title
description
discipline
status
priority
createdAt
dueDate
```

## Communication

```text
id
projectId
type
sender
subject
content
timestamp
status
```

## Issue

```text
id
projectId
title
description
type
severity
status
riskScore
requiresDecision
source
createdAt
```

## Action

```text
id
issueId
description
type
status
assignedTo
dueDate
```

---

# 13. AI / RAG Pipeline

Document processing:

```text
PDF
 |
 v
PDFBox
 |
 v
Extract Text
 |
 v
Chunk Text
 |
 v
Generate Embeddings
 |
 v
Store in pgvector
```

Query processing:

```text
RFI / Issue
 |
 v
Generate Query Embedding
 |
 v
Vector Similarity Search
 |
 v
Retrieve Relevant Chunks
 |
 v
Build AI Context
 |
 v
LLM
 |
 v
Structured Output
 |
 v
Application Logic
```

---

# 14. Important AI Design Principle

We should not allow the LLM to control the entire application.

The application owns:

```text
Database state
Risk calculation
Compliance rules
Permissions
Workflow state
Human approval
```

The LLM assists with:

```text
Understanding
Extraction
Classification
Summarization
Reasoning over retrieved context
Draft generation
```

This separation improves reliability and makes the system easier to explain.

---

# 15. Human-in-the-Loop Design

High-impact decisions should not be automatically executed.

Example:

```text
AI detects issue
       |
       v
AI recommends action
       |
       v
Human reviews
       |
   +---+---+
   |       |
Approve   Reject/Edit
   |
   v
Action
```

This is especially important for construction-related workflows where incorrect automated decisions could cause real-world consequences.

---

# 16. Non-Goals for the 10-Day POC

We intentionally will NOT build:

- Real BIM geometry processing
- Revit integration
- AutoCAD integration
- Advanced CAD clash detection
- Real WhatsApp integration
- Real Gmail integration
- Production authentication
- Multi-tenancy
- Kubernetes deployment
- Production-grade distributed architecture
- Full computer vision for drawings
- Automatic sending of critical project decisions

These may be future extensions.

---

# 17. Future Scope

Potential production extensions:

```text
BIM / IFC integration
Revit integration
AutoCAD integration
Real Gmail integration
WhatsApp integration
Slack / Teams integration
Computer vision for drawings
Geometric clash detection
Real-time project monitoring
Calendar integration
Advanced workflow automation
Multi-project portfolio intelligence
Multi-tenant architecture
Role-based access control
Audit logs
Human feedback learning
```

---

# 18. 10-Day Development Plan

## Day 1 — Foundation

- Product definition
- Architecture
- Repository setup
- Spring Boot setup
- React setup
- PostgreSQL setup
- Docker setup
- Health endpoint
- Initial documentation

Deliverable:

```text
Running project skeleton
```

---

## Day 2 — Domain + Database

Build:

- Project
- Document
- DocumentChunk
- RFI
- Communication
- Issue
- Action

Build repositories, services, controllers, and database relationships.

Deliverable:

```text
Working backend domain model
```

---

## Day 3 — Document Processing

Build:

```text
PDF upload
PDF text extraction
Text chunking
Document processing pipeline
```

Deliverable:

```text
PDF -> Extracted Text -> Stored Chunks
```

---

## Day 4 — Embeddings + RAG

Build:

```text
Chunk
 -> Embedding
 -> pgvector

Query
 -> Similarity Search
 -> Relevant Project Context
```

Deliverable:

```text
Project-aware semantic search
```

---

## Day 5 — RFI Intelligence

Build:

```text
RFI
 -> LLM
 -> Structured Analysis
 -> Issue
```

Deliverable:

```text
RFI automatically converted into structured issue information
```

---

## Day 6 — Compliance Engine

Build:

```text
Document facts
+
Compliance rules
 ->
Compliance Issues
```

Deliverable:

```text
Basic deterministic compliance checking
```

---

## Day 7 — Risk Engine

Build:

```text
Issue
 -> Severity
 -> Deadline
 -> Impact
 -> Confidence
 -> Risk Score
```

Deliverable:

```text
Prioritized project issues
```

---

## Day 8 — Actions + Morning Brief

Build:

```text
Issue
 -> Recommended Action
 -> Draft Response
 -> Morning Brief
```

Deliverable:

```text
Core autonomous coordination workflow
```

---

## Day 9 — Frontend

Build:

- Dashboard
- Project page
- Issues
- RFIs
- Documents
- Issue detail
- Morning Brief

Deliverable:

```text
Usable product interface
```

---

## Day 10 — Polish + Demo

Complete:

- End-to-end testing
- Sample data
- README
- Architecture documentation
- Screenshots
- Demo flow
- Error handling
- UI polish
- Demo video

Deliverable:

```text
Portfolio-ready POC
```

---

# 19. Final Demo Flow

Our final demonstration should take approximately 2–3 minutes.

### Step 1

Open:

```text
Tower A
```

### Step 2

Show uploaded project documents.

### Step 3

Show RFIs.

### Step 4

Trigger/process an RFI.

### Step 5

System identifies:

```text
Potential MEP / Structural clash
```

### Step 6

System retrieves relevant drawings.

### Step 7

System creates an issue.

### Step 8

Risk engine calculates:

```text
94/100 — CRITICAL
```

### Step 9

System generates recommended action and draft response.

### Step 10

Open Morning Brief.

Show:

```text
3 issues need your decision.
8 items handled automatically.
```

The final message of the demo should be:

> The architect doesn't need to search through the project. The system surfaces what actually needs their attention.

---

# 20. Success Criteria

The POC is successful if we can demonstrate:

- A PDF can be uploaded.
- PDF text can be extracted.
- Project context can be searched semantically.
- An RFI can be analyzed by AI.
- Relevant documents can be retrieved.
- An issue can be automatically created.
- Compliance rules can identify a sample violation.
- Issues can be prioritized.
- Recommended actions can be generated.
- An RFI response can be drafted.
- A Morning Brief can summarize the most important work.
- A human remains in control of important decisions.

---

# 21. Engineering Principles

During development we will follow:

### Keep business logic outside controllers

```text
Controller
    ↓
Service
    ↓
Repository
```

### Keep AI separate from core business logic

```text
AI Service
    ↓
Structured Result
    ↓
Business Service
```

### Prefer deterministic logic where possible

For example:

```text
Compliance Rule
Risk Calculation
Workflow State
```

should not unnecessarily depend on an LLM.

### Use structured AI output

Prefer:

```json
{
  "severity": "HIGH",
  "issueType": "CLASH"
}
```

over parsing arbitrary natural-language responses.

### Human approval for important actions

AI recommends; the user decides.

---

# 22. Current Status

## Day 1

- [ ] Product definition
- [ ] Architecture definition
- [ ] Git repository
- [ ] Spring Boot backend
- [ ] React frontend
- [ ] PostgreSQL
- [ ] Docker Compose
- [ ] Health API
- [ ] Initial project documentation

## Day 2

Not started.

## Day 3

Not started.

## Day 4

Not started.

## Day 5

Not started.

## Day 6

Not started.

## Day 7

Not started.

## Day 8

Not started.

## Day 9

Not started.

## Day 10

Not started.

---

# 23. Important Scope Rule

For this 10-day project:

> If a feature does not help demonstrate autonomous project coordination, we do not build it unless the core MVP is already complete.

The priority is:

```text
Working Core Product
        >
Fancy UI
        >
Extra Integrations
```

---

# 24. Working Philosophy

We will build this project step-by-step.

For every implementation step, we will cover:

1. What we are building
2. Why we are building it
3. Where the code belongs
4. Complete code
5. How to run it
6. How to test it
7. Expected output
8. What the code is doing
9. Engineering decisions
10. Interview questions that can come from it

We will not move to the next major component until the current component is working.

