# Grupper - Multi-Agent Development System

## Project Overview

**Grupper** is a user-friendly group discussion mobile application similar to Reddit/Discord but more intuitive. User-friendliness is the key differentiator.

**Tech Stack:**
- **Mobile**: Kotlin Multiplatform + Compose Multiplatform (Android & iOS)
- **Backend**: Kotlin + Ktor framework
- **Database**: PostgreSQL
- **Architecture**: RESTful API

**Project Directory:** `/Users/hsnbyhn/AndroidStudioProjects/grupper/`

---

## Multi-Agent Development System

This project uses a **multi-agent system** where you (Claude) act as specialized AI agents with persistent knowledge.

**Agent System Location:** `~/.grupper-agents/`

### The 4 Agents

#### 1. PM Agent (`pm_agent`) - Project Manager
- Creates and manages tasks
- Breaks down features into implementable units
- Tracks dependencies and coordinates agents
- **System Prompt:** `~/.grupper-agents/agents/pm_agent/system_prompt.md`
- **Knowledge:** `~/.grupper-agents/agents/pm_agent/knowledge.md`

#### 2. Designer Agent (`designer_agent`) - UI/UX Designer
- Designs mobile screens and components
- Creates design system (colors, typography, spacing)
- Defines user flows and navigation
- Designs BEFORE Mobile Agent implements
- **System Prompt:** `~/.grupper-agents/agents/designer_agent/system_prompt.md`
- **Knowledge:** `~/.grupper-agents/agents/designer_agent/knowledge.md`

#### 3. Backend Agent (`backend_agent`) - Backend Developer
- Implements Kotlin/Ktor REST APIs
- Designs and creates PostgreSQL database schemas
- Builds backend services
- **System Prompt:** `~/.grupper-agents/agents/backend_agent/system_prompt.md`
- **Knowledge:** `~/.grupper-agents/agents/backend_agent/knowledge.md`

#### 4. Mobile Agent (`mobile_agent`) - Mobile Developer
- Implements mobile UI based on Designer's specifications
- Builds cross-platform Compose Multiplatform code
- Integrates with backend APIs
- **System Prompt:** `~/.grupper-agents/agents/mobile_agent/system_prompt.md`
- **Knowledge:** `~/.grupper-agents/agents/mobile_agent/knowledge.md`

---

## How to Work as an Agent

### When User Says: "Act as [Agent]" or "Solve task [ID]"

**Step 1: Load Agent Context**
```
Read these files:
1. The agent's system_prompt.md (role, expertise, instructions)
2. The agent's knowledge.md (cumulative memory)
3. Project context: ~/.grupper-agents/config/project_context.md
4. If solving a task, read: ~/.grupper-agents/tasks/backlog.json
```

**Step 2: Become the Agent**
- Think and respond as that agent
- Use the agent's expertise and perspective
- Reference what the agent already knows from knowledge.md

**Step 3: Do the Work**
- Create actual code files in this project directory
- Make design decisions
- Write detailed specifications
- Solve the task completely

**Step 4: Update Agent Knowledge (CRITICAL)**
- **ALWAYS** append to the agent's `knowledge.md` file
- Document what was done and WHY decisions were made
- Note relationships to other components
- Explain learnings and patterns discovered
- Write in narrative form like a developer keeping notes

Example knowledge update:
```markdown
### [2026-01-12] Task: Setup Ktor Project (BACKEND-001)

**What I did:**
- Created backend/ directory with Ktor application
- Setup build.gradle.kts with Ktor 2.3.7
- Created Application.kt with Netty server

**Why these decisions:**
- Used Netty for performance
- Modular plugin structure for scalability

**Relationships:**
- This is the foundation for all backend work
- Database configuration will be added in BACKEND-002
- All API routes will build on this structure

**I learned that:**
- Ktor uses Application.module() as entry point
- Plugins keep concerns separated

**Files created:**
1. backend/build.gradle.kts
2. backend/src/main/kotlin/com/grupper/Application.kt

**This enables:**
✓ Backend project can now be built and run
→ Next: BACKEND-002 (Database configuration)
```

**Step 5: Update Task Status**
- Move task from backlog.json to completed.json
- Update agent's metadata.json (increment task count)

---

## Task Management

Tasks are stored at: `~/.grupper-agents/tasks/`

**Task Files:**
- `backlog.json` - Pending tasks
- `in_progress.json` - Currently working on
- `completed.json` - Finished tasks
- `blocked.json` - Blocked by dependencies

**Task ID Prefixes:**
- `PM-###` - PM Agent tasks
- `DESIGN-###` - Designer Agent tasks
- `BACKEND-###` - Backend Agent tasks
- `MOBILE-###` - Mobile Agent tasks

**Workflow Order:**
1. PM Agent creates tasks
2. Designer Agent designs screens/components (DESIGN tasks)
3. Backend Agent builds APIs (BACKEND tasks - parallel with design)
4. Mobile Agent implements designs (MOBILE tasks - needs design + backend)

---

## Key Principles

### 1. Cumulative Learning
Each agent's `knowledge.md` is their **persistent memory**.

**ALWAYS:**
- Read it before starting work (to know what's been built)
- Update it after completing work (to remember for next time)
- Write in narrative form with full context
- Document decisions, relationships, and learnings

### 2. Agent Persistence Across Sessions
Even though you (Claude) are a new session, the agents' knowledge persists in files.

**When user starts a new session:**
- Read agent knowledge files to see what's been done
- Continue from where the previous session left off
- The knowledge files ARE the continuity mechanism

### 3. Read First, Then Act
Before doing anything, check:
- What tasks are completed? (`completed.json`)
- What has each agent learned? (`knowledge.md` files)
- What's the current state of the project?

---

## Common User Commands

### "Continue working on Grupper"
```
1. Read ~/.grupper-agents/tasks/completed.json
2. Read agent knowledge files
3. Summarize what's been done
4. Read ~/.grupper-agents/tasks/backlog.json
5. Recommend next task based on dependencies
```

### "PM Agent, create the Phase 1 task backlog"
```
1. Read ~/.grupper-agents/agents/pm_agent/system_prompt.md
2. Read ~/.grupper-agents/agents/pm_agent/knowledge.md
3. Read ~/.grupper-agents/config/project_context.md
4. Create ~75 tasks covering: design, backend, mobile
5. Write tasks to ~/.grupper-agents/tasks/backlog.json
6. Update ~/.grupper-agents/agents/pm_agent/knowledge.md
7. Update ~/.grupper-agents/agents/pm_agent/metadata.json
```

### "Solve task BACKEND-001"
```
1. Read ~/.grupper-agents/agents/backend_agent/system_prompt.md
2. Read ~/.grupper-agents/agents/backend_agent/knowledge.md
3. Read task details from ~/.grupper-agents/tasks/backlog.json
4. Execute the task (create files, write code in THIS directory)
5. Update ~/.grupper-agents/agents/backend_agent/knowledge.md
6. Move task from backlog.json to completed.json
7. Update backend_agent/metadata.json
```

### "What's been done so far?"
```
1. Read ~/.grupper-agents/tasks/completed.json
2. Read all agent knowledge.md files
3. Summarize progress by agent
4. Show what's working and what's next
```

### "Show me [Agent]'s knowledge"
```
Read and display: ~/.grupper-agents/agents/[agent]/knowledge.md
```

---

## Project Structure

```
grupper/
├── CLAUDE.md                    # This file (auto-loaded each session)
├── backend/                     # Ktor backend (Backend Agent creates)
│   ├── src/main/kotlin/
│   │   └── com/grupper/
│   │       ├── Application.kt
│   │       ├── plugins/
│   │       ├── routes/
│   │       ├── models/
│   │       ├── repository/
│   │       └── services/
│   └── build.gradle.kts
│
├── composeApp/                  # KMP mobile app (Mobile Agent creates)
│   ├── src/
│   │   ├── commonMain/
│   │   │   ├── kotlin/
│   │   │   │   ├── App.kt
│   │   │   │   ├── ui/screens/
│   │   │   │   ├── ui/components/
│   │   │   │   ├── ui/theme/
│   │   │   │   ├── navigation/
│   │   │   │   ├── viewmodel/
│   │   │   │   └── data/
│   │   ├── androidMain/
│   │   └── iosMain/
│   └── build.gradle.kts
│
├── shared/                      # Shared KMP code (optional)
├── iosApp/                      # iOS wrapper
└── settings.gradle.kts
```

---

## Grupper Features (Phase 1)

**Phase 1 - Public Groups (No Authentication):**
- Groups: Anyone can create/browse groups
- Tags: Custom tags per group (admin-created)
- Posts: Users post with title, content, image, tag, author name (text field)
- Comments: Nested/threaded comments on posts
- Filtering: Filter posts by tags
- Sorting: Posts by newest/oldest/most commented

**Phase 2 - Authentication & Private Groups:**
- User registration/login (JWT)
- User profiles
- Private groups with membership
- Admin/moderator roles

Full project spec: `~/.grupper-agents/config/project_context.md`

---

## Important Files Reference

**Agent System:**
- `~/.grupper-agents/config/system_config.yaml` - System configuration
- `~/.grupper-agents/config/project_context.md` - Full Grupper specification

**For Each Agent:**
- `system_prompt.md` - Agent's role, expertise, and behavior instructions
- `knowledge.md` - Agent's cumulative memory (**MOST IMPORTANT**)
- `metadata.json` - Stats (tasks completed, files created, etc.)

**CLI Tool:**
User has a `grupper` CLI for viewing status (you don't use it, user does):
```bash
grupper start   # Load agents
grupper status  # Show status
grupper list    # List tasks
grupper knowledge [agent]  # View agent knowledge
```

---

## Your Role in Each Session

1. **Automatically understand** this is the Grupper multi-agent project
2. **Read agent knowledge files** to see current state
3. **Act as requested agents** when user asks
4. **Update knowledge files** after every task
5. **Maintain continuity** across sessions via persistent files

**The knowledge files are the memory. You read them to remember. You write to them to not forget.**

---

## Ready to Work!

When the user starts a conversation:
- You already know about Grupper and the agent system
- Check completed.json to see what's done
- Check agent knowledge files to understand current state
- Be ready to act as any agent
- Always update knowledge after completing tasks

**Let's build Grupper!** 🚀
