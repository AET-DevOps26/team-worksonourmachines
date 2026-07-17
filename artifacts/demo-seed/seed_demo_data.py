#!/usr/bin/env python3
"""Seed demo user data after keycloak-config-cli using live Keycloak user IDs.

Resolves demo accounts by email via the Keycloak Admin API, then upserts
student / marketplace / communication rows. Idempotent: re-running remaps
user_id / student_id / participant columns when Keycloak IDs change.

Shared by Docker Compose and Helm (see artifacts/demo-seed/).
"""

from __future__ import annotations

import json
import os
import time
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path
from typing import Any


DEMO_DATA_PATH = Path(os.environ.get("DEMO_DATA_PATH", "/demo-seed/demo-data.json"))
KEYCLOAK_URL = os.environ.get("KEYCLOAK_URL", "http://keycloak:8080").rstrip("/")
KEYCLOAK_REALM = os.environ.get("KEYCLOAK_REALM", "tutormatch")
KEYCLOAK_USER = os.environ.get("KEYCLOAK_USER", "admin")
KEYCLOAK_PASSWORD = os.environ.get("KEYCLOAK_PASSWORD", "admin")

PGHOST = os.environ.get("PGHOST", "postgres")
PGPORT = os.environ.get("PGPORT", "5432")

STUDENT_DB = os.environ.get("STUDENT_DB", "student")
STUDENT_USER = os.environ.get("STUDENT_USER", "student")
STUDENT_PASSWORD = os.environ.get("STUDENT_PASSWORD", "student")

MARKETPLACE_DB = os.environ.get("MARKETPLACE_DB", "marketplace")
MARKETPLACE_USER = os.environ.get("MARKETPLACE_USER", "marketplace")
MARKETPLACE_PASSWORD = os.environ.get("MARKETPLACE_PASSWORD", "marketplace")

COMMUNICATION_DB = os.environ.get("COMMUNICATION_DB", "communication")
COMMUNICATION_USER = os.environ.get("COMMUNICATION_USER", "communication")
COMMUNICATION_PASSWORD = os.environ.get("COMMUNICATION_PASSWORD", "communication")


def log(msg: str) -> None:
    print(msg, flush=True)


def http_json(method: str, url: str, *, data: bytes | None = None, headers: dict[str, str] | None = None) -> Any:
    req = urllib.request.Request(url, data=data, headers=headers or {}, method=method)
    with urllib.request.urlopen(req, timeout=60) as resp:
        body = resp.read()
        if not body:
            return None
        return json.loads(body.decode())


def keycloak_token() -> str:
    form = urllib.parse.urlencode(
        {
            "grant_type": "password",
            "client_id": "admin-cli",
            "username": KEYCLOAK_USER,
            "password": KEYCLOAK_PASSWORD,
        }
    ).encode()
    for attempt in range(1, 31):
        try:
            payload = http_json(
                "POST",
                f"{KEYCLOAK_URL}/realms/master/protocol/openid-connect/token",
                data=form,
                headers={"Content-Type": "application/x-www-form-urlencoded"},
            )
            token = payload.get("access_token")
            if token:
                return token
        except (urllib.error.URLError, urllib.error.HTTPError, TimeoutError, json.JSONDecodeError) as exc:
            log(f"waiting for Keycloak admin token ({attempt}/30): {exc}")
            time.sleep(2)
    raise RuntimeError("Could not obtain Keycloak admin token")


def resolve_users(emails: set[str]) -> dict[str, str]:
    token = keycloak_token()
    resolved: dict[str, str] = {}
    for email in sorted(emails):
        query = urllib.parse.urlencode({"email": email, "exact": "true"})
        users = http_json(
            "GET",
            f"{KEYCLOAK_URL}/admin/realms/{KEYCLOAK_REALM}/users?{query}",
            headers={"Authorization": f"Bearer {token}"},
        )
        if not users:
            raise RuntimeError(f"Keycloak user not found for email={email}")
        user_id = users[0].get("id")
        if not user_id:
            raise RuntimeError(f"Keycloak user missing id for email={email}")
        resolved[email] = user_id
        log(f"resolved {email} -> {user_id}")
    return resolved


def connect_ready(dbname: str, user: str, password: str, schema: str):
    import psycopg

    last_exc: Exception | None = None
    for attempt in range(1, 91):
        try:
            conn = psycopg.connect(
                host=PGHOST,
                port=PGPORT,
                dbname=dbname,
                user=user,
                password=password,
                autocommit=False,
            )
            with conn.cursor() as cur:
                cur.execute(
                    "SELECT 1 FROM information_schema.schemata WHERE schema_name = %s",
                    (schema,),
                )
                if cur.fetchone():
                    return conn
            conn.close()
        except Exception as exc:  # noqa: BLE001 - retry until DBs exist
            last_exc = exc
            log(f"waiting for {dbname}/{schema} ({attempt}/90): {exc}")
            time.sleep(2)
    raise RuntimeError(f"Could not connect to {dbname}/{schema}: {last_exc}")


def ensure_map_table(conn, schema: str) -> None:
    with conn.cursor() as cur:
        cur.execute(
            f"""
            CREATE TABLE IF NOT EXISTS {schema}.demo_seed_user_map (
                email text PRIMARY KEY,
                user_id uuid NOT NULL
            )
            """
        )


def get_mapped_id(conn, schema: str, email: str) -> str | None:
    with conn.cursor() as cur:
        cur.execute(
            f"SELECT user_id::text FROM {schema}.demo_seed_user_map WHERE email = %s",
            (email,),
        )
        row = cur.fetchone()
        return row[0] if row else None


def set_mapped_id(conn, schema: str, email: str, user_id: str) -> None:
    with conn.cursor() as cur:
        cur.execute(
            f"""
            INSERT INTO {schema}.demo_seed_user_map (email, user_id)
            VALUES (%s, %s::uuid)
            ON CONFLICT (email) DO UPDATE SET user_id = EXCLUDED.user_id
            """,
            (email, user_id),
        )


def remap_student_id(conn, old_id: str, new_id: str) -> None:
    with conn.cursor() as cur:
        cur.execute(
            "SELECT 1 FROM student.student_profiles WHERE student_id = %s::uuid",
            (new_id,),
        )
        new_exists = cur.fetchone() is not None
        if new_exists:
            cur.execute(
                "UPDATE student.learning_goals SET student_id = %s::uuid WHERE student_id = %s::uuid",
                (new_id, old_id),
            )
            cur.execute(
                "DELETE FROM student.student_profiles WHERE student_id = %s::uuid",
                (old_id,),
            )
            return

        cur.execute(
            "UPDATE student.learning_goals SET student_id = %s::uuid WHERE student_id = %s::uuid",
            (new_id, old_id),
        )
        cur.execute(
            "UPDATE student.student_profile_languages SET student_id = %s::uuid WHERE student_id = %s::uuid",
            (new_id, old_id),
        )
        cur.execute(
            "UPDATE student.student_profiles SET student_id = %s::uuid WHERE student_id = %s::uuid",
            (new_id, old_id),
        )


def seed_students(conn, data: dict[str, Any], ids: dict[str, str]) -> None:
    ensure_map_table(conn, "student")
    for student in data["students"]:
        email = student["email"]
        new_id = ids[email]
        old_id = get_mapped_id(conn, "student", email)

        if old_id and old_id != new_id:
            log(f"remapping student {email}: {old_id} -> {new_id}")
            remap_student_id(conn, old_id, new_id)

        with conn.cursor() as cur:
            cur.execute(
                """
                INSERT INTO student.student_profiles (
                    student_id, display_name, bio, memorization, formal_reasoning,
                    conceptual_understanding, problem_solving
                ) VALUES (
                    %s::uuid, %s, %s, %s, %s, %s, %s
                )
                ON CONFLICT (student_id) DO UPDATE SET
                    display_name = EXCLUDED.display_name,
                    bio = EXCLUDED.bio,
                    memorization = EXCLUDED.memorization,
                    formal_reasoning = EXCLUDED.formal_reasoning,
                    conceptual_understanding = EXCLUDED.conceptual_understanding,
                    problem_solving = EXCLUDED.problem_solving
                """,
                (
                    new_id,
                    student["displayName"],
                    student["bio"],
                    student["memorization"],
                    student["formalReasoning"],
                    student["conceptualUnderstanding"],
                    student["problemSolving"],
                ),
            )

            cur.execute(
                "DELETE FROM student.student_profile_languages WHERE student_id = %s::uuid",
                (new_id,),
            )
            for position, language in enumerate(student.get("languages", [])):
                cur.execute(
                    """
                    INSERT INTO student.student_profile_languages (student_id, position, language)
                    VALUES (%s::uuid, %s, %s)
                    """,
                    (new_id, position, language),
                )

            goal = student.get("learningGoal")
            if goal:
                cur.execute(
                    """
                    INSERT INTO student.learning_goals (
                        id, student_id, module_id, description, target_date,
                        self_assessed_level, budget_eur
                    ) VALUES (
                        %s::uuid, %s::uuid, %s, %s, %s::timestamptz, %s, %s
                    )
                    ON CONFLICT (id) DO UPDATE SET
                        student_id = EXCLUDED.student_id,
                        module_id = EXCLUDED.module_id,
                        description = EXCLUDED.description,
                        target_date = EXCLUDED.target_date,
                        self_assessed_level = EXCLUDED.self_assessed_level,
                        budget_eur = EXCLUDED.budget_eur
                    """,
                    (
                        goal["id"],
                        new_id,
                        goal["moduleId"],
                        goal["description"],
                        goal["targetDate"],
                        goal["selfAssessedLevel"],
                        goal["budgetEur"],
                    ),
                )
                cur.execute(
                    "DELETE FROM student.learning_goal_locations WHERE goal_id = %s::uuid",
                    (goal["id"],),
                )
                for position, location in enumerate(goal.get("locations", [])):
                    cur.execute(
                        """
                        INSERT INTO student.learning_goal_locations (goal_id, position, location)
                        VALUES (%s::uuid, %s, %s)
                        """,
                        (goal["id"], position, location),
                    )

        set_mapped_id(conn, "student", email, new_id)

    with conn.cursor() as cur:
        demo_names = [s["displayName"] for s in data["students"]]
        current_ids = [ids[s["email"]] for s in data["students"]]
        cur.execute(
            """
            DELETE FROM student.student_profiles
            WHERE display_name = ANY(%s)
              AND NOT (student_id = ANY(%s::uuid[]))
            """,
            (demo_names, current_ids),
        )
        if cur.rowcount:
            log(f"removed {cur.rowcount} stale demo student profile(s)")

    conn.commit()
    log(f"seeded {len(data['students'])} students")


def seed_marketplace(conn, data: dict[str, Any], ids: dict[str, str]) -> None:
    ensure_map_table(conn, "marketplace")

    for tutor in data["tutors"]:
        email = tutor["email"]
        new_id = ids[email]
        old_id = get_mapped_id(conn, "marketplace", email)
        profile_id = tutor["profileId"]

        with conn.cursor() as cur:
            if old_id and old_id != new_id:
                log(f"remapping tutor {email}: {old_id} -> {new_id}")
                cur.execute(
                    "UPDATE marketplace.tutor_applications SET user_id = %s::uuid WHERE user_id = %s::uuid",
                    (new_id, old_id),
                )

            cur.execute(
                """
                INSERT INTO marketplace.tutor_profiles (
                    id, user_id, display_name, bio, hourly_rate, published
                ) VALUES (
                    %s::uuid, %s::uuid, %s, %s, %s, %s
                )
                ON CONFLICT (id) DO UPDATE SET
                    user_id = EXCLUDED.user_id,
                    display_name = EXCLUDED.display_name,
                    bio = EXCLUDED.bio,
                    hourly_rate = EXCLUDED.hourly_rate,
                    published = EXCLUDED.published
                """,
                (
                    profile_id,
                    new_id,
                    tutor["displayName"],
                    tutor["bio"],
                    tutor["hourlyRate"],
                    tutor["published"],
                ),
            )

            cur.execute(
                "DELETE FROM marketplace.tutor_profile_languages WHERE profile_id = %s::uuid",
                (profile_id,),
            )
            for position, language in enumerate(tutor.get("languages", [])):
                cur.execute(
                    """
                    INSERT INTO marketplace.tutor_profile_languages (profile_id, position, language)
                    VALUES (%s::uuid, %s, %s)
                    """,
                    (profile_id, position, language),
                )

            cur.execute(
                "DELETE FROM marketplace.tutor_profile_locations WHERE profile_id = %s::uuid",
                (profile_id,),
            )
            for position, location in enumerate(tutor.get("locations", [])):
                cur.execute(
                    """
                    INSERT INTO marketplace.tutor_profile_locations (profile_id, position, location)
                    VALUES (%s::uuid, %s, %s)
                    """,
                    (profile_id, position, location),
                )

            cur.execute(
                "DELETE FROM marketplace.tutor_profile_availability WHERE profile_id = %s::uuid",
                (profile_id,),
            )
            for position, slot in enumerate(tutor.get("availability", [])):
                cur.execute(
                    """
                    INSERT INTO marketplace.tutor_profile_availability (
                        profile_id, position, weekday, available, note
                    ) VALUES (%s::uuid, %s, %s, %s, %s)
                    """,
                    (
                        profile_id,
                        position,
                        slot["weekday"],
                        slot["available"],
                        slot["note"],
                    ),
                )

        set_mapped_id(conn, "marketplace", email, new_id)

    with conn.cursor() as cur:
        for app in data["applications"]:
            cur.execute(
                """
                INSERT INTO marketplace.tutor_applications (
                    id, user_id, module_id, status, certificate_ref, submitted_at, rejection_reason
                ) VALUES (
                    %s::uuid, %s::uuid, %s::uuid, %s, %s, %s::timestamptz, NULL
                )
                ON CONFLICT (id) DO UPDATE SET
                    user_id = EXCLUDED.user_id,
                    module_id = EXCLUDED.module_id,
                    status = EXCLUDED.status,
                    certificate_ref = EXCLUDED.certificate_ref,
                    submitted_at = EXCLUDED.submitted_at,
                    rejection_reason = EXCLUDED.rejection_reason
                """,
                (
                    app["id"],
                    ids[app["email"]],
                    app["moduleId"],
                    app["status"],
                    app["certificateRef"],
                    app["submittedAt"],
                ),
            )

        for coverage in data["coverages"]:
            cur.execute(
                """
                INSERT INTO marketplace.tutor_coverages (
                    id, profile_id, module_id, proficiency_level
                ) VALUES (
                    %s::uuid, %s::uuid, %s::uuid, %s
                )
                ON CONFLICT (id) DO UPDATE SET
                    profile_id = EXCLUDED.profile_id,
                    module_id = EXCLUDED.module_id,
                    proficiency_level = EXCLUDED.proficiency_level
                """,
                (
                    coverage["id"],
                    coverage["profileId"],
                    coverage["moduleId"],
                    coverage["proficiencyLevel"],
                ),
            )

    conn.commit()
    log(f"seeded {len(data['tutors'])} tutors, {len(data['applications'])} applications")


def seed_communication(conn, data: dict[str, Any], ids: dict[str, str]) -> None:
    conversation = data.get("conversation")
    if not conversation:
        return

    ensure_map_table(conn, "communication")
    student_email = conversation["studentEmail"]
    tutor_email = conversation["tutorEmail"]
    student_id = ids[student_email]
    tutor_id = ids[tutor_email]
    participant_a, participant_b = sorted([student_id, tutor_id])
    if participant_a == student_id:
        name_a, name_b = conversation["studentDisplayName"], conversation["tutorDisplayName"]
    else:
        name_a, name_b = conversation["tutorDisplayName"], conversation["studentDisplayName"]

    tutor_profile_id = next(t["profileId"] for t in data["tutors"] if t["email"] == tutor_email)
    tutor_id_on_a = tutor_profile_id if participant_a == tutor_id else None
    tutor_id_on_b = tutor_profile_id if participant_b == tutor_id else None

    for email, old_id, new_id in (
        (student_email, get_mapped_id(conn, "communication", student_email), student_id),
        (tutor_email, get_mapped_id(conn, "communication", tutor_email), tutor_id),
    ):
        if old_id and old_id != new_id:
            log(f"remapping communication participant {email}: {old_id} -> {new_id}")
            with conn.cursor() as cur:
                cur.execute(
                    """
                    UPDATE communication.conversations
                    SET participant_a_id = %s::uuid WHERE participant_a_id = %s::uuid
                    """,
                    (new_id, old_id),
                )
                cur.execute(
                    """
                    UPDATE communication.conversations
                    SET participant_b_id = %s::uuid WHERE participant_b_id = %s::uuid
                    """,
                    (new_id, old_id),
                )
                cur.execute(
                    """
                    UPDATE communication.messages
                    SET sender_id = %s::uuid WHERE sender_id = %s::uuid
                    """,
                    (new_id, old_id),
                )

    with conn.cursor() as cur:
        cur.execute(
            """
            INSERT INTO communication.conversations (
                id, participant_a_id, participant_b_id,
                participant_a_display_name, participant_b_display_name,
                participant_a_tutor_id, participant_b_tutor_id,
                created_at, updated_at
            ) VALUES (
                %s::uuid, %s::uuid, %s::uuid, %s, %s,
                %s, %s,
                '2026-07-15T10:00:00Z'::timestamptz,
                '2026-07-15T10:05:00Z'::timestamptz
            )
            ON CONFLICT (id) DO UPDATE SET
                participant_a_id = EXCLUDED.participant_a_id,
                participant_b_id = EXCLUDED.participant_b_id,
                participant_a_display_name = EXCLUDED.participant_a_display_name,
                participant_b_display_name = EXCLUDED.participant_b_display_name,
                participant_a_tutor_id = EXCLUDED.participant_a_tutor_id,
                participant_b_tutor_id = EXCLUDED.participant_b_tutor_id,
                updated_at = EXCLUDED.updated_at
            """,
            (
                conversation["id"],
                participant_a,
                participant_b,
                name_a,
                name_b,
                tutor_id_on_a,
                tutor_id_on_b,
            ),
        )

        for message in conversation.get("messages", []):
            cur.execute(
                """
                INSERT INTO communication.messages (
                    id, conversation_id, sender_id, content, sent_at
                ) VALUES (
                    %s::uuid, %s::uuid, %s::uuid, %s, %s::timestamptz
                )
                ON CONFLICT (id) DO UPDATE SET
                    conversation_id = EXCLUDED.conversation_id,
                    sender_id = EXCLUDED.sender_id,
                    content = EXCLUDED.content,
                    sent_at = EXCLUDED.sent_at
                """,
                (
                    message["id"],
                    conversation["id"],
                    ids[message["senderEmail"]],
                    message["content"],
                    message["sentAt"],
                ),
            )

    set_mapped_id(conn, "communication", student_email, student_id)
    set_mapped_id(conn, "communication", tutor_email, tutor_id)
    conn.commit()
    log("seeded demo conversation")


def main() -> int:
    if not DEMO_DATA_PATH.is_file():
        log(f"demo data not found: {DEMO_DATA_PATH}")
        return 1

    data = json.loads(DEMO_DATA_PATH.read_text(encoding="utf-8"))
    emails: set[str] = {s["email"] for s in data["students"]}
    emails.update(t["email"] for t in data["tutors"])
    if data.get("conversation"):
        emails.add(data["conversation"]["studentEmail"])
        emails.add(data["conversation"]["tutorEmail"])
        for message in data["conversation"].get("messages", []):
            emails.add(message["senderEmail"])

    ids = resolve_users(emails)

    student_conn = connect_ready(STUDENT_DB, STUDENT_USER, STUDENT_PASSWORD, "student")
    marketplace_conn = connect_ready(MARKETPLACE_DB, MARKETPLACE_USER, MARKETPLACE_PASSWORD, "marketplace")
    communication_conn = connect_ready(
        COMMUNICATION_DB, COMMUNICATION_USER, COMMUNICATION_PASSWORD, "communication"
    )

    try:
        seed_students(student_conn, data, ids)
        seed_marketplace(marketplace_conn, data, ids)
        seed_communication(communication_conn, data, ids)
    finally:
        student_conn.close()
        marketplace_conn.close()
        communication_conn.close()

    log("demo seed completed")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
