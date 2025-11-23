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
 * Utility methods specifically for titles
 */
public class TitleUtilities {
    private static final HashMap<UUID, Title> title_cache = new HashMap<>();

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

    private static void insertCache(UUID id, Title title) {
        synchronized (title_cache) {
            title_cache.put(id, title);
        }
    }

    public static void removeCache(UUID id) {
        synchronized (title_cache) {
            title_cache.remove(id);
        }
    }

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

    @Nullable
    public static Title getCache(UUID id) {
        synchronized (title_cache) {
            return title_cache.get(id);
        }
    }

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

    public static Collection<String> getAllUsableTitles(UUID player) throws DataAccessException {
        DataAccess access = CivsAndTitles.getDataAccess();

        List<String> usable = new ArrayList<>(access.getUnlockedTitleDAO().getAll(player).stream().map(UnlockedTitle::title).toList());
        usable.addAll(access.getTitleDAO().getAllDefault().stream().map(Title::title).toList());
        return usable;
    }

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
