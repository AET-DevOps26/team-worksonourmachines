-- Local demo catalogue (modules and topics). User-keyed demo rows are applied by artifacts/demo-seed after Keycloak import.

INSERT INTO marketplace.modules (id, code, title, description, difficulty_hint)
VALUES
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001',
        'DWT',
        'Diskrete Wahrscheinlichkeitstheorie',
        'Probability theory for computer science students.',
        'Medium'
    ),
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0002',
        'GDB',
        'Grundlagen der Informatik',
        'Foundational computer science concepts.',
        'Easy'
    ),
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0003',
        'DS',
        'Diskrete Strukturen',
        'Discrete structures for CS.',
        'Hard'
    ),
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0004',
        'LA',
        'Lineare Algebra',
        'Linear algebra for engineers.',
        'Medium'
    )
ON CONFLICT (id) DO UPDATE
    SET
        code = EXCLUDED.code,
        title = EXCLUDED.title,
        description = EXCLUDED.description,
        difficulty_hint = EXCLUDED.difficulty_hint;

INSERT INTO marketplace.module_topics (
    id,
    module_id,
    position,
    name,
    description,
    difficulty_hint,
    memorization,
    formal_reasoning,
    conceptual_understanding,
    problem_solving
)
VALUES
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0001',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001',
        0,
        'Probability spaces',
        'Sigma algebras and measures.',
        'Hard',
        2,
        5,
        4,
        4
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0002',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001',
        1,
        'Random variables',
        'Expectation and variance.',
        'Medium',
        3,
        4,
        4,
        3
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0003',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001',
        2,
        'Limit theorems',
        'LLN and CLT.',
        'Hard',
        2,
        5,
        5,
        4
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0004',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0002',
        0,
        'Logic',
        'Propositional and predicate logic.',
        'Easy',
        3,
        3,
        3,
        3
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0005',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0002',
        1,
        'Automata',
        'Finite automata and regular languages.',
        'Medium',
        3,
        3,
        3,
        3
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0006',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0003',
        0,
        'Graphs',
        'Basic graph theory.',
        'Medium',
        3,
        3,
        3,
        3
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0007',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0003',
        1,
        'Combinatorics',
        'Counting principles.',
        'Hard',
        3,
        3,
        3,
        3
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0008',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0004',
        0,
        'Vector spaces',
        'Bases and dimension.',
        'Medium',
        3,
        3,
        3,
        3
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0009',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0004',
        1,
        'Eigenvalues',
        'Diagonalization.',
        'Hard',
        3,
        3,
        3,
        3
    )
ON CONFLICT (id) DO UPDATE
    SET
        module_id = EXCLUDED.module_id,
        position = EXCLUDED.position,
        name = EXCLUDED.name,
        description = EXCLUDED.description,
        difficulty_hint = EXCLUDED.difficulty_hint,
        memorization = EXCLUDED.memorization,
        formal_reasoning = EXCLUDED.formal_reasoning,
        conceptual_understanding = EXCLUDED.conceptual_understanding,
        problem_solving = EXCLUDED.problem_solving;
