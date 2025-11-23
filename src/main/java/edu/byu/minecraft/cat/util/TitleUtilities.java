package edu.byu.minecraft.cat.util;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.dataaccess.*;
import edu.byu.minecraft.cat.model.Player;
import edu.byu.minecraft.cat.model.Title;
import edu.byu.minecraft.cat.model.UnlockedTitle;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * Utility methods specifically for titles.
 * Most methods return a Boolean Supplier to be used with CommandUtilities, but can be used with AsyncUtilities or
 * directly ran if needed. These methods usually run database queries and so should ideally be run off the server
 * thread. They all return true if an error is encountered, which error is specific to the method being called (Such as
 * a title already existing for addTitle).
 */
public class TitleUtilities {
    private static final HashMap<UUID, Title> title_cache = new HashMap<>();

    /**
     * Updates the cache for a player. <br>
     *
     * This method does data access synchronously, so it might cause a lag spike if called on the server thread!<br>
     * Most methods that update a player's title information should also keep the cache updated if the player is
     * online. This should only be called when the player joins or when player title data is modified manually
     * (i.e. Not through a method in TitleUtils).
     * @param id The id of the player whose cache data needs to be updated
     */
    public static void updateCache(UUID id) {
        synchronized (title_cache) {
            try {
                Player player = CivsAndTitles.getDataAccess().getPlayerDAO().get(id);
                if (player == null || player.title() == null) {
                    return;
                }
                Title title = CivsAndTitles.getDataAccess().getTitleDAO().get(player.title());
                title_cache.put(id, title);
            } catch (DataAccessException ignored) {

            }
        }
    }

    /**
     * Inserts or updates the cache for a player
     *
     * @param id The id of the player whose cache data needs to be updated
     * @param title The title that they have applied
     */
    private static void insertCache(UUID id, Title title) {
        synchronized (title_cache) {
            title_cache.put(id, title);
        }
    }

    /**
     * Removes a player's title cache.
     * Called when the player leaves the server or when they have removed their title
     *
     * @param id The id of the player whose cache data needs to be removed
     */
    public static void removeCache(UUID id) {
        synchronized (title_cache) {
            title_cache.remove(id);
        }
    }

    /**
     * Clears the cache for every player who has a title applied.
     * Called when the title has been deleted or is a world title that must be removed
     *
     * @param title The name of the title that needs to be cleared
     */
    private static void removeCache(String title) {
        synchronized (title_cache) {
            for (UUID id : title_cache.keySet()) {
                Title t = title_cache.get(id);
                if (t != null && t.title().equals(title)) {
                    title_cache.remove(id);
                }
            }
        }
    }

    /**
     * Gets the cached title of a player
     * Titles are cached to avoid queries to the database, which can take a while and will freeze the server if run on
     * the server thread. The title is needed every time the player's name is displayed, like when they chat or when the
     * player list is updated.
     * @param id The uuid of the player whose title is needed
     * @return The player's title
     */
    @Nullable
    public static Title getCache(UUID id) {
        synchronized (title_cache) {
            return title_cache.get(id);
        }
    }

    /**
     * Creates a new title in the database
     * @param title The title to be inserted in the database
     * @return A Supplier that returns true if the title already exists
     */
    public static Supplier<Boolean> addTitle(Title title) {
        return () -> {try {
            TitleDAO dao = CivsAndTitles.getDataAccess().getTitleDAO();
            Title t = dao.get(title.title());
            if (t != null) {
                return true;
            } else {
                dao.insert(title);
                return false;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }};
    }

    /**
     * Edits a title in the database
     * @param title The title to be modified in the database
     * @return A Supplier that returns true if the title does not exist
     */
    public static Supplier<Boolean> editTitle(Title title) {
        return () -> {try {
            TitleDAO dao = CivsAndTitles.getDataAccess().getTitleDAO();
            Title t = dao.get(title.title());
            if (t == null) {
                return true;
            } else {
                dao.update(title);
                return false;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }};
    }

    /**
     * Removes a title in the database
     * @param title The name of the title to be removed from the database
     * @return A Supplier that returns true if the title does not exist
     */
    public static Supplier<Boolean> deleteTitle(String title) {
        return () -> {try {
            TitleDAO dao = CivsAndTitles.getDataAccess().getTitleDAO();
            Title t = dao.get(title);
            if (t == null) {
                return true;
            } else {
                dao.delete(title);
                CivsAndTitles.getDataAccess().getPlayerDAO().removeAllTitles(title);
                CivsAndTitles.getDataAccess().getUnlockedTitleDAO().deleteAll(title);
                removeCache(title);
                return false;
            }
        } catch (DataAccessException e) {
                throw new RuntimeException(e);
        }};
    }

    /**
     * Grants a title to a player.<br>
     * Note: If the player already has the title, this <i>should</i> do nothing and return false
     * @param player The name of the player that should receive the title
     * @param title The name of the title to be given to the player
     * @return A Supplier that returns true if the title does not exist
     */
    public static Supplier<Boolean> awardTitle(String player, String title) {
        return () -> {try {
            UnlockedTitleDAO access = CivsAndTitles.getDataAccess().getUnlockedTitleDAO();
            UUID id = CivsAndTitles.getDataAccess().getPlayerDAO().getPlayerUUID(player);
            UnlockedTitle t = access.get(id, title);
            if (t != null) {
                return true;
            } else {
                access.insert(new UnlockedTitle(id, title, Utilities.getTime()));
                return false;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }};
    }

    /**
     * Removes a title to a player.<br>
     * Note: If the player doesn't have the title, this <i>should</i> do nothing and return false
     * @param player The name of the player who should lose the title
     * @param title The title to be removed from the player
     * @return A Supplier that returns true if the title does not exist
     */
    public static Supplier<Boolean> revokeTitle(String player, String title) {
        return () -> {try {
            UnlockedTitleDAO access = CivsAndTitles.getDataAccess().getUnlockedTitleDAO();
            PlayerDAO playerDAO = CivsAndTitles.getDataAccess().getPlayerDAO();
            UUID id = playerDAO.getPlayerUUID(player);
            UnlockedTitle t = access.get(id, title);
            if (t == null) {
                return true;
            } else {
                access.delete(id, title);
                Player p = playerDAO.get(id);
                if (title.equals(p.title())) {
                    playerDAO.update(p.setTitle(null));
                    removeCache(p.uuid());
                }
                return false;
            }
        } catch (DataAccessException e ){
            throw new RuntimeException(e);
        }};
    }

    /**
     * Removes all "world" titles from players.<br>
     * Titles of type WORLD should only exist until player progress is reset. For example, titles awarded through
     * advancements should not be usable on a new world where those players haven't received an advancement but did on a
     * previous world
     * @return A Supplier that always returns false (cannot encounter an error)
     */
    public static Supplier<Boolean> clearWorldTitles() {
        return () -> {try {
            PlayerDAO playerDAO = CivsAndTitles.getDataAccess().getPlayerDAO();
            UnlockedTitleDAO unlockedTitleDAO = CivsAndTitles.getDataAccess().getUnlockedTitleDAO();
            for (Title title : CivsAndTitles.getDataAccess().getTitleDAO().getAllWorld()) {
                playerDAO.removeAllTitles(title.title());
                unlockedTitleDAO.deleteAll(title.title());
                removeCache(title.title());
            }
            return false;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }};
    }

    /**
     * Gets all titles that a player can use<br>
     * Note: This method does not run database queries asynchronously since it must return the result. This shouldn't be
     * an issue for a SuggestionProvider since those are processed asynchronously anyways
     * @param player The player whose titles should be fetched
     * @return A collection of the names of the player's usable titles
     * @throws DataAccessException Database error
     */
    public static Collection<String> getAllUsableTitles(UUID player) throws DataAccessException {
        DataAccess access = CivsAndTitles.getDataAccess();

        List<String> usable = new ArrayList<>(access.getUnlockedTitleDAO().getAll(player).stream().map(UnlockedTitle::title).toList());
        usable.addAll(access.getTitleDAO().getAllDefault().stream().map(Title::title).toList());
        return usable;
    }

    /**
     * Applies a title to a player
     *
     * @param player The player to apply the title to
     * @param title The title to be applied
     * @return A Supplier that returns true if the title doesn't exist or the player cannot apply it
     */
    public static Supplier<Boolean> applyTitle(UUID player, String title) {
        return () -> {try {
            PlayerDAO playerDAO = CivsAndTitles.getDataAccess().getPlayerDAO();
            Title t = CivsAndTitles.getDataAccess().getTitleDAO().get(title);
            if (
                    t == null ||
                    !t.type().equals(Title.Type.DEFAULT) &&
                    CivsAndTitles.getDataAccess().getUnlockedTitleDAO().get(player, title) == null
            ) {
                return true;
            } else {
                playerDAO.update(playerDAO.get(player).setTitle(title));
                insertCache(player, t);
                return false;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }};
    }

    /**
     * Removes any applied title from a player
     *
     * @param player The player from whom to clear the title
     * @return A Supplier that always returns false (Cannot encounter an error)
     */
    public static Supplier<Boolean> clearTitle(UUID player) {
        return () -> {try {
            PlayerDAO playerDAO = CivsAndTitles.getDataAccess().getPlayerDAO();
            playerDAO.update(playerDAO.get(player).setTitle(null));
            removeCache(player);
            return false;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }};
    }
}
