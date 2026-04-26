---
name: task-workflow
description: Spec-driven workflow for non-trivial work. Scaffolds a tasks/<kebab-name>/ folder with overview/analysis/spec/plan/log docs; completion produces an ADR under adr/. Use when the user asks for architectural analysis, multi-step refactor planning, investigations that span sessions, or any work that would benefit from a durable design record. Skip for trivial fixes, typos, or obviously-specified single-file edits — just do those directly.
---

# Task workflow

Spec-driven development for non-trivial work. Every task is a self-contained folder holding the context, analysis, design, plan, and rationale for one piece of work.

## When to use this workflow

**Use it when:**
- The work involves a design decision that will matter in 3 months (why-was-this-done).
- The work spans multiple sessions or agents.
- The user is exploring options, not requesting a pre-specified implementation.
- There's real risk of wasted effort without alignment on approach first.

**Skip it when:**
- The request is a single-file edit with a clear desired outcome.
- The fix is obvious once the bug is located.
- The user explicitly asks to just do the work.

When in doubt, propose the workflow to the user and let them decide — "this looks like a multi-step refactor, want me to scaffold a task folder for it?"

## Folder layout

```
tasks/<kebab-case-topic>/
  00-overview.md    # TL;DR: problem, target, status, index
  01-analysis.md    # current state — facts only, no decisions
  02-spec.md        # target design with decisions baked in
  03-plan.md        # ordered implementation steps, checklists, rollback
  04-log.md         # decision rationale — options, why, consequence per decision
```

Numeric prefixes force readable sort order in directory listings.

**Naming:** kebab-case topic, e.g. `navmodel-encapsulation`, `screenmodel-threading-fix`. Keep it short and descriptive — it's the primary identifier.

## What goes in each file

### 00-overview.md

- **Status** line (see lifecycle below).
- Scope — which files/modules this touches.
- Problem in one sentence.
- Target in one sentence.
- Index of other files in the folder.
- **Follow-ups / next steps** — appended to as deferred items surface during the task. Anything postponed, split into a follow-up task, or noticed-but-out-of-scope goes here. Each entry: short description + pointer (e.g. `04-log.md#Qn`, `03-plan.md` strikeout, or "noticed during Phase 3"). This is the canonical list a fresh agent reads when picking the work back up, and the source the ADR's `Follow-ups` field is distilled from.
- Post-completion note — ADR filename and commit decision.

Every task has this file, even if it's the only one.

### 01-analysis.md

Current state, facts only. No judgments about what *should* happen. No options. Things that belong here:

- Types, interfaces, call graph relevant to the task.
- Current problems / smells — described as observations, not prescriptions.
- Why the current code is shaped the way it is (if discernible — helps avoid undoing intentional choices).
- Call-site map for anything being removed/changed.
- Public API surface impact preview.

This doc should stand alone as a reference even if the task is abandoned.

### 02-spec.md

Target design, decisions baked in. Written as if the design is settled (because it is — this file is updated when decisions change, not as an open debate). Things that belong here:

- Target types with code sketches.
- Key behaviors and invariants.
- Threading/concurrency notes.
- Public API impact.
- Explicit non-goals — what this task does *not* do.

If there are open questions, they go in `04-log.md` as pending decisions, not here.

### 03-plan.md

Ordered steps. Phased so the tree compiles after each phase where possible. Things that belong here:

- Phase-by-phase checklist.
- Known call sites that need updating (pinned with file:line).
- Rollback strategy if a phase can't land.
- Verification steps (tests, manual checks).
- Out-of-scope items explicitly called out.

A fresh agent should be able to implement from this doc alone (with the spec as reference).

**Checklist conventions** — keep the plan in sync with reality as work lands:

- `- [ ]` — todo (default).
- `- [x]` — completed. Tick as soon as the step lands; don't batch at the end.
- `- [-]` — cancelled or superseded. Append an inline reason: `- [-] Step — superseded by phase X` / `- [-] Step — deferred to follow-up task Y`. Use this instead of deleting the line so the audit trail survives. If the cancellation reflects a design change (not just sequencing), also add a `04-log.md` entry capturing *why*.

Note: `- [-]` is not standard CommonMark/GFM (it's an Obsidian/Logseq convention) — it renders as plain text on GitHub. Acceptable here because plan docs are read mostly by agents and locally in IDEs, not on GitHub-rendered pages.

### 04-log.md

Decision rationale. One entry per decision, structured:

```
## Q<n> — <the question>

**Options:** A/B/C with short descriptions.
**Decision:** <which option>.
**Why:** <rationale>.
**Consequence:** <what this implies going forward>.
```

Both accepted and rejected decisions live here. Captures *why*, not *what* — the what is in the spec.

## Status lifecycle

`00-overview.md` carries a **Status** line:

- `exploring` — problem scoped, no target yet.
- `design in progress` — analysis and spec being written; decisions open.
- `design accepted` — spec and plan locked; implementation hasn't started.
- `implementation in progress` — code changes underway.
- `complete` — code merged; ADR written or pending.
- `abandoned` — task dropped; reason captured in `04-log.md` or an ADR.

Update the status line when it changes. Avoid leaving stale statuses — a fresh agent reads it first.

## Commit policy

`tasks/` is **gitignored by default** (see root `.gitignore`). Working docs are local scratch. To commit a specific task, whitelist its subfolder:

```
# in .gitignore
tasks/
!tasks/.gitkeep
!tasks/<task-name>/
```

Default assumption: tasks are local. Agents working on a task need the user to point them to the folder (by path, opening in IDE, or referencing by name).

## Completion → ADR

### Pre-completion review

Before flipping status to `complete` and writing the ADR, run this checklist:

- Every `03-plan.md` item is `[x]` (done) or `[-]` (cancelled with reason). No stale `[ ]` items — if something is genuinely pending but not blocking, move it to `00-overview.md`'s Follow-ups section first.
- Every `[-]` line has an inline reason (`— superseded by X`, `— deferred to follow-up Y`, etc.).
- Every "deferred / postponed / out-of-scope-but-noticed" item that surfaced during implementation is captured in `00-overview.md`'s **Follow-ups / next steps** section, not just buried inline in the log or as a strikeout.
- Status line in `00-overview.md` accurately reflects current state.
- Verification steps (Phase 6 / equivalent) actually ran — not just listed.

If the task was abandoned mid-flight, the same review applies: cancelled items marked `[-]`, follow-ups captured (so resuming is possible), status set to `abandoned`.

### Writing the ADR

When a task reaches `complete`, distill the outcome into an ADR under `adr/`:

```
adr/NNNN-<topic>.md
```

Numbering is zero-padded, monotonically increasing. Grep existing ADRs for the next number.

**ADR format** (short — one page):

```markdown
# <NNNN>. <Title>

## Context
<what problem / why this was decided>

## Decision
<what was decided, concisely>

## Consequences
<implications — positive and negative — going forward>

## Follow-ups
<deferred work, split-out tasks, known limitations to revisit. Distilled from
00-overview.md's Follow-ups section. Omit the section entirely if there are none.>
```

ADRs are **always committed**. They are the durable record of why the codebase looks the way it does. Task folders are scratch; ADRs are canon.

### After the ADR is written

Decide on the task folder:

- **Delete** — ADR captures the outcome; working docs were scratch. Most common.
- **Keep and commit** — analysis or plan is valuable reference material beyond the ADR summary. Whitelist in `.gitignore` and commit.
- **Keep local, uncommitted** — rare; only if the task might resume.

### Abandoned tasks

- If no ADR is warranted (e.g. the direction was rejected without teaching anything new), just delete the folder.
- If the reasoning for abandoning is worth preserving, write a short ADR describing why the direction was rejected.

## Agent access note

`tasks/` being gitignored affects git only. Agents have full filesystem read/write access via their standard tools. Gitignore is about "don't push this"; it is not a permission boundary.

## Typical flow

1. User describes a non-trivial piece of work.
2. Agent proposes the task workflow ("this looks like X, want to scaffold a task?").
3. User confirms → create `tasks/<name>/00-overview.md` with status `exploring` or `design in progress`.
4. Agent populates `01-analysis.md` from code reading.
5. Agent and user converge on design through discussion; `02-spec.md` and `04-log.md` get written together (one captures the settled design, the other captures the why).
6. `03-plan.md` lists phased steps.
7. User agrees → status → `implementation in progress` → code. Tick `03-plan.md` items as each lands (`[x]`); mark cancelled/superseded items `[-]` with a reason. Capture deferred / postponed / out-of-scope-but-noticed items in `00-overview.md`'s Follow-ups section. Update the status line in `00-overview.md` when phases shift.
8. Code merged → run pre-completion review → status → `complete` → write ADR (including `Follow-ups` if any) → delete/commit task folder.
