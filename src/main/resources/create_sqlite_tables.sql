CREATE TABLE IF NOT EXISTS title
(
    name        TEXT NOT NULL PRIMARY KEY,
    color       TEXT NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS player
(
    uuid          TEXT    NOT NULL PRIMARY KEY,
    username      TEXT    NOT NULL,
    points        INTEGER NOT NULL DEFAULT 0,
    current_title TEXT    REFERENCES title (name) ON UPDATE CASCADE ON DELETE SET NULL,
    role          TEXT    NOT NULL CHECK (role IN ('ADMIN', 'BUILD_JUDGE', 'BUILD_SIZER', 'PLAYER')),
    show_rank     INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS player_title
(
    player_uuid TEXT NOT NULL REFERENCES player (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
    title       TEXT NOT NULL REFERENCES title (name) ON UPDATE CASCADE ON DELETE CASCADE,
    dateEarned  TEXT,
    PRIMARY KEY (player_uuid, title)
);

CREATE TABLE IF NOT EXISTS location
(
    id           INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    x_coordinate INTEGER NOT NULL,
    y_coordinate INTEGER NOT NULL,
    z_coordinate INTEGER NOT NULL,
    dimension    TEXT    NOT NULL,
    yaw          REAL    NOT NULL,
    pitch        REAL    NOT NULL
);

CREATE TABLE IF NOT EXISTS civ
(
    id           INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name         TEXT    NOT NULL UNIQUE,
    points       INTEGER NOT NULL,
    status       INTEGER NOT NULL DEFAULT 1,
    has_border   INTEGER          DEFAULT 0,
    location_id  INTEGER NOT NULL REFERENCES location (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    founded_date TEXT
);

CREATE TABLE IF NOT EXISTS civ_player
(
    civ_id      INTEGER NOT NULL REFERENCES civ (id) ON UPDATE CASCADE ON DELETE CASCADE,
    player_uuid TEXT    NOT NULL REFERENCES player (name) ON UPDATE CASCADE ON DELETE CASCADE,
    status      TEXT    NOT NULL CHECK (status IN ('FOUNDER', 'OWNER', 'LEADER', 'MEMBER', 'CONTRIBUTOR')),
    PRIMARY KEY (civ_id, player_uuid, status)
);

CREATE TABLE IF NOT EXISTS new_civ_request
(
    id                INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    request_date      TEXT    NOT NULL,
    requesting_player TEXT    NOT NULL REFERENCES player (name) ON UPDATE CASCADE ON DELETE CASCADE,
    name              TEXT    NOT NULL,
    location_id       INTEGER NOT NULL REFERENCES location (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS join_civ_request
(
    id                INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    request_date      TEXT    NOT NULL,
    requesting_player TEXT    NOT NULL REFERENCES player (name) ON UPDATE CASCADE ON DELETE CASCADE,
    civ_id            INTEGER NOT NULL REFERENCES civ (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS build
(
    id             INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    civ_id         INTEGER REFERENCES civ (id) ON UPDATE CASCADE ON DELETE SET NULL,
    name           TEXT    NOT NULL,
    submitted_date TEXT    NOT NULL,
    comments       TEXT,
    size           INTEGER,
    points         INTEGER,
    status         TEXT    NOT NULL CHECK (status IN ('JUDGED', 'ACTIVE', 'PENDING')),
    location_id    INTEGER NOT NULL REFERENCES location (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS build_player
(
    build_id    INTEGER NOT NULL REFERENCES build (id) ON UPDATE CASCADE ON DELETE CASCADE,
    player_uuid TEXT    NOT NULL REFERENCES player (name) ON UPDATE CASCADE ON DELETE CASCADE,
    status      TEXT    NOT NULL CHECK (status IN ('SUBMITTER', 'BUILDER')),
    PRIMARY KEY (build_id, player_uuid, status)
);

CREATE TABLE IF NOT EXISTS build_score
(
    id               INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    build_id         INTEGER NOT NULL REFERENCES build (id) ON UPDATE CASCADE ON DELETE CASCADE,
    judge            TEXT    NOT NULL REFERENCES player (name) ON UPDATE CASCADE ON DELETE SET NULL,
    judge_date       TEXT    NOT NULL,
    comments         TEXT    NOT NULL,
    point_total      INTEGER NOT NULL,
    functionality    INTEGER NOT NULL,
    technical        INTEGER NOT NULL,
    texture          INTEGER NOT NULL,
    storytelling     INTEGER NOT NULL,
    thematic         INTEGER NOT NULL,
    terraforming     INTEGER NOT NULL,
    detailing        INTEGER NOT NULL,
    lighting         INTEGER NOT NULL,
    layout           INTEGER NOT NULL,
    judge_discretion INTEGER NOT NULL
);