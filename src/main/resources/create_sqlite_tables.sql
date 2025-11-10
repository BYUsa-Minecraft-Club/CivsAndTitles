CREATE TABLE IF NOT EXISTS title
(
    name        TEXT NOT NULL PRIMARY KEY,
    format      TEXT NOT NULL,
    description TEXT NOT NULL,
    type        TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS player
(
    uuid          TEXT    NOT NULL PRIMARY KEY,
    username      TEXT    NOT NULL,
    current_title TEXT    REFERENCES title (name) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS player_title
(
    player_uuid TEXT NOT NULL REFERENCES player (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
    title       TEXT NOT NULL REFERENCES title (name) ON UPDATE CASCADE ON DELETE CASCADE,
    date_earned  TEXT,
    PRIMARY KEY (player_uuid, title)
);