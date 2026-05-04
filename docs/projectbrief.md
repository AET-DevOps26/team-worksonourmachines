# Project Brief

The web-app **TUtorMatch** is a tutor-student matching platform for TUM courses, where learners can quickly find the best tutor 
for their goals and budget. It combines smart matching with practical filters like cost, language, location, and tutor capacity 
with knowledge about the most common struggles of the students in each module for students best learning experience.

## Motivation

The current options for finding tutoring at TUM are fragmented: students search through WhatsApp groups, ask around informally, or use commercial platforms that charge significant fees and lack TUM-specific knowledge. There is no central, accessible platform focused on the unique requirements of TUM’s courses, student needs, and learning challenges. TUtorMatch fills this gap by providing a dedicated, community-driven place that seamlessly connects students and tutors while integrating actual knowledge of TUM’s module expectations to make support more effective and tailored. This will enhance the learning experience for everyone involved and foster a stronger academic community at TUM.

## Main Functionality

- **Discovery and matching**: Search and filter tutors by course/module, price, language, location (e.g. campus vs online), and available capacity.
- **Trust and fit**: Ratings and structured signals so students can compare tutors for a given module.
- **Content awareness**: For each module, big content topics are presented with information about them and which ones are especially hard/easy to guide tutors and students where to put the most ressources.

Additionally refer to [GenAI](#genai).

## Intended Users

- **Students at TUM** who want paid tutoring, are on a budget, started late, need a target grade, or want help in a specific course.
- **Tutors** (typically students or others in the ecosystem) who want to teach and/or earn side income.
- **Contributors** (optional path) who enrich the platform with vetted or rated learning materials tied to courses.

## GenAI

Use cases, which highlight the use of GenAI as part of our main features:

1. **Late start, exam pressure**: A student is behind in *Diskrete Strukturen* before the exam. They enter their module, exam date, and weekly budget. The app suggests tutors matching language and location, and GenAI proposes a **short catch-up plan** (topics ordered by impact) with optional slots linked to those tutors’ capacity.
2. **Structured semester planning**: A student wants a well-organized approach to their semester with tutoring support at the right moments but isn’t sure how to set this up. They input their modules, exam dates, target grades, and budget. GenAI generates a **cost-optimized semester plan**: this includes when to schedule sessions with tutors (and for which topics), suggestions to mix live and async learning (to save both tutor capacity and student budget), and a timeline aligning study intensity to module difficulty and exam schedule. The plan connects to specific tutors’ availability and the content-awareness within the system, making it actionable and budget-friendly.

## Example scenarios

1. **Earning side income**: Anna, a computer science student, wants to earn extra money by tutoring others in modules she excels at. She signs up as a tutor for *Diskrete Strukturen* and *GDB*, sets her availability and rate. Students who need tutoring in those areas can easily find and book Anna through the platform.
2. **Finding subject-specific help**: Lukas, who recently switched majors, is struggling with *DWT* but is confident in *THEO*. He looks for a tutor for *DWT* and notices the tutor matching engine can also suggest swapping help: Lukas offers sessions in *THEO* while getting help in *DWT*.
3. **Language-specific tutoring**: Maria does not speak German fluently but needs assistance in *DWT*. She filters tutors by language and finds an English-speaking tutor experienced in helping non-German speakers with this module.
4. **Budget-friendly learning**: Jonas needs at least a 1.7 in *GDB* to qualify for his program but is on a tight budget. The platform helps him find affordable tutors, filter for async or group sessions, and suggests swapping tutoring as lower-cost options.
5. **Contributing learning materials**: Sarah enjoys creating structured summaries and video tutorials for *lineare Algebra*. She uploads her vetted content to the platform, where tutors can rate its helpfulness and students can use it to supplement their live sessions.

## Goals

These goals should be reached over the project. They are defined as milestones, that will be put into the GitHub Project.

1. **Foundation**: Monorepo or agreed repo layout, local setup, shared API contracts, basic logging, and baseline CI so client, server, and GenAI can integrate against stable interfaces.
2. **Identity and access**: End-to-end authentication and authorization (e.g. Keycloak) wired into the web client and backend services, with roles that distinguish students, tutors, and admins as needed for the first release.
3. **Core marketplace**: Tutor profiles, module/topic coverage, capacity, pricing signals, and student-side **discovery** (search, filters for language, location, budget) backed by the server and Postgres logical separation.
4. **Trust and module context**: Ratings or comparable signals for tutors, plus **content awareness** for modules (topics and difficulty hints) so discovery is not only “who” but “where to focus.”
5. **GenAI in product**: Have GenAI being integrated into the app and produce result from the **live platform data** (e.g. catch-up plan or cost-aware semester plan tied to selected modules, dates, budget, and tutor availability).
6. **Release-ready**: Documentation for setup and architecture, basic observability, and a coherent demo path that exercises student → find tutor → plan/book.

## Non-Goals

The following non-goals define what will not be part of the project to define the scope from the beginning. Those aspects were discussed and might be valuable in the future to extend the platform, but will not be implemented during the course.

- Payments will not be processed via the platform
- A full university timetable optimizer (“when to attend which lecture”)
- We won't implement help exchange (tutor in one subject then student in another with the same partner) for the MVP

## Responsibilities

The project is organized into three main module areas, each with a Product Owner (PO) who takes the leading role, but with everyone still being encouraged to help in every area. Each area may include several artifacts.

Product Owners (POs) for modules:

- Client: _Julian Wilke_
    - Web client (React Router)
- Server: _Amritanshu Sikdar_
    - At least three services using Spring Boot
    - One Postgres DB split logically for each service
- GenAI: _Hristina Ivanova_
    - Python service

There are also shared/common services, coordinated by a primary responsible person for (initial) structure/operation, but these are mainly used and touched by all team members.

- user-facing reverse proxy _Julian Wilke_
- API specs: _Julian Wilke_
- Local setup: _Julian Wilke_
- Monorepo tooling: _Julian Wilke_
- Keycloak: _Amritanshu Sikdar_
- Docs: _Hristina Ivanova_
- Observability: _Amritanshu Sikdar_
- tbd after architecture decisions (CI/CD, Kubernetes)