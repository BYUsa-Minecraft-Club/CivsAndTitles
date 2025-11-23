CREATE TABLE IF NOT EXISTS title
(
    name        TEXT NOT NULL PRIMARY KEY,
    format      TEXT NOT NULL,
    description TEXT NOT NULL,
    type        TEXT NOT NULL,
    advancement TEXT
);

CREATE TABLE IF NOT EXISTS player
(
    uuid          TEXT NOT NULL PRIMARY KEY,
    username      TEXT NOT NULL,
    current_title TEXT,
        FOREIGN KEY (current_title)
        REFERENCES title (name)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS player_title
(
    player_uuid TEXT,
    title       TEXT,
    date_earned  TEXT,
        PRIMARY KEY (player_uuid, title),
        FOREIGN KEY (player_uuid)
        REFERENCES player (uuid)
        ON DELETE CASCADE,
        FOREIGN KEY (title)
        REFERENCES title (name)
        ON DELETE CASCADE
);