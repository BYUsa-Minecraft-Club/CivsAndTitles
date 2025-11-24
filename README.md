# Civs And Titles
Now with 100% less civs! So I guess it's the AndTitles mod now

## Usage

The Titles mod adds unlockable titles that players can use to show off what they've achieved!

### Creating a new title

With all the parts needed for a title, it takes multiple commands to store all of it on the server. `/titles admin create start` starts a new title creation session.
To create a title, you will need:
- A unique name for the title, which will be used in commands to give or apply the title
- A display for the title, which is the formatted text that will be displayed before a player's name when the title is applied
- A description
- A type, which is one of:
  - DEFAULT for titles that any player can apply without needing it to be given
  - WORLD for titles that only apply in one world and should be cleared when a new one is created
  - PERMANENT for titles that always apply
- And an optional advancement, allowing the title to be awarded automatically without any admin intervention

When you run `/titles admin create start`, it will make a screen in the chat that looks like this:
```
Title Creation
Name: UNSET (SET)
Description: UNSET (SET)
Format: UNSET (SET)
Type: UNSET (SET)
Advancement: UNSET (SET)
INCOMPLETE
```

You can click on where it says `(SET)` to autofill a command to set that value. Once you have filled out all the values (Advancement is optional), it will look something like this:
```
Title Creation
Name: example (SET)
Description: An example title (SET)
Format: Example (SET)
Type: WORLD (SET)
Advancement: minecraft:story/mine_diamond (SET)
(SUBMIT)
```
Clicking on submit will finalize the title and save it to the database.

### Using titles

From there, you can give the title to a player with `/titles admin giveTitle <player> <title>`.
Once a player has a title they can use it with `/titles change <title>`

### Other commands

Other useful commands include:
- `/titles admin edit start <title>` starts a new edit title session, which works much like the title creation
- `/titles admin deleteTitle <title>` deletes a title and removes it from every player currently using it
- `/titles admin revokeTitle <title>` takes a title from a player
- `/titles admin clearWorldTitles` removes every `WORLD` title from every player, to be used when a new world is created and advancement titles need to be reset
- `/titles clear` removes any title you have equipped
- `/titles display list` shows a list of every title and whether you can use them
- `/titles display detail <title>` shows details about a title, such as the description and necessary advancement, as well as giving an option to apply it if possible

## Configuration
All CivsAndTitles config is stored in the civsandtitles folder in your server's config folder. You'll find three files there:
- config.json is generic configuration
- postgres.json is configuration for the postgres storage option when it is enabled
- database.db is all the titles data when sqlite is the selected storage

Actually displaying titles is done either by modifying the player's display name directly or through the placeholder api in mods like styled chat. The placeholder to use is `byu:title`.
Make sure to configure your server correctly, or titles won't show!

#### config.json
```javascript
{
    // The storage option to be used. Options include:
    // - sqlite to use an SQLite3 file at config/civsandtitles/database.db
    // - postgres to use Postgresql server. Make sure to configure in postgres.json!
    // - none to store all titles data in memory. Does not persist between resets and is mostly used for testing
  "database": "sqlite",
    // Enables titles with advancements to be automatically given when a player receives an advancement.
    // Toggle off when the server is linked to others through postgresql and you want advancements to give titles on a different server
  "enable_advancement_rewards": true,
    // Enable the mixin that changes a player's display name manually.
    // Use when you aren't using placeholder api to display titles
  "modify_display_name": false
}
```

#### postgres.json
```javascript
{
  "host": "localhost",
  "port": 5432,
  "database": "titles",
  "username": "postgres",
  "password": "postgres"
}
```
