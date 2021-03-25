INSERT INTO COURSE_UNIT(UNIT_CODE) VALUES ('COMP0'), ('COMP1'), ('COMP2');

INSERT INTO COURSE_UNIT$LAB_TIMES(ID, START, END) VALUES
    (
        0,
        DATEADD('ww', -1, DATEADD('hh', 14, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7), today))),
        DATEADD('ww', -1, DATEADD('hh', 16, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7), today)))
    ),
    (
        1,
        DATEADD('ww', 0, DATEADD('hh', 14, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7), today))),
        DATEADD('ww', 0, DATEADD('hh', 16, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7), today)))
    ),
    (
        2,
        DATEADD('ww', 1, DATEADD('hh', 14, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7), today))),
        DATEADD('ww', 1, DATEADD('hh', 16, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7), today)))
    ),
    (
        3,
        DATEADD('ww', -1, DATEADD('hh', 10, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 2, today))),
        DATEADD('ww', -1, DATEADD('hh', 12, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 2, today)))
    ),
    (
        4,
        DATEADD('ww', 0, DATEADD('hh', 10, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 2, today))),
        DATEADD('ww', 0, DATEADD('hh', 12, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 2, today)))
    ),
    (
        5,
        DATEADD('ww', 1, DATEADD('hh', 10, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 2, today))),
        DATEADD('ww', 1, DATEADD('hh', 12, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 2, today)))
    ),
    (
        6,
        DATEADD('ww', -1, DATEADD('hh', 15, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 3, today))),
        DATEADD('ww', -1, DATEADD('hh', 16, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 3, today)))
    ),
    (
        7,
        DATEADD('ww', 0, DATEADD('hh', 15, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 3, today))),
        DATEADD('ww', 0, DATEADD('hh', 16, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 3, today)))
    ),
    (
        8,
        DATEADD('ww', 1, DATEADD('hh', 15, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 3, today))),
        DATEADD('ww', 1, DATEADD('hh', 16, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 3, today)))
    );

INSERT INTO COURSE_UNIT_LAB_TIMES(COURSE_UNIT_UNIT_CODE, LAB_TIMES_ID) VALUES
    ('COMP0', 0),
    ('COMP0', 1),
    ('COMP0', 2),
    ('COMP1', 3),
    ('COMP1', 4),
    ('COMP1', 5),
    ('COMP2', 6),
    ('COMP2', 7),
    ('COMP2', 8);

INSERT INTO LAB_FORMAT(REPO_NAMING_SCHEMA, COURSE_UNIT_UNIT_CODE) VALUES
    ('COMP0_Lab', 'COMP0'), ('COMP1_Lab', 'COMP1'), ('COMP2_Lab', 'COMP2');

INSERT INTO LAB_EXERCISE(EXERCISE_ID, DEADLINE, EXERCISE_NAME, LAB_FORMAT_REPO_NAMING_SCHEMA) VALUES
    (0, DATEADD('ww', -1, DATEADD('hh', 18, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 4, today))), 'ex_1', 'COMP0_Lab'),
    (1, DATEADD('ww', 2, DATEADD('hh', 18, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 4, today))), 'ex_2', 'COMP0_Lab'),
    (2, DATEADD('ww', 4, DATEADD('hh', 18, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 4, today))), 'ex_3', 'COMP0_Lab'),
    (3, DATEADD('ww', 3, DATEADD('hh', 14, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 2, today))), 'ex_1', 'COMP1_Lab'),
    (4, DATEADD('ww', 5, DATEADD('hh', 14, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 3, today))), 'ex_2', 'COMP1_Lab'),
    (5, DATEADD('ww', 9, DATEADD('hh', 14, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7), today))), 'ex_3', 'COMP1_Lab'),
    (6, DATEADD('ww', 4, DATEADD('hh', 12, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 2, today))), 'ex_1', 'COMP2_Lab'),
    (7, DATEADD('ww', 5, DATEADD('hh', 12, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 2, today))), 'ex_2', 'COMP2_Lab'),
    (8, DATEADD('ww', 6, DATEADD('hh', 12, DATEADD('dd', -((DAY_OF_WEEK(today) - 2) % 7) + 2, today))), 'ex_3', 'COMP2_Lab');
