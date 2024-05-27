package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Civ;

import java.util.Collection;
import java.util.UUID;

public interface CivDAO extends SimpleDAO<Civ, Integer> {
    Collection<Civ> getForPlayer(UUID uuid) throws DataAccessException;
    Collection<Civ> getForActivity(boolean active) throws DataAccessException;
    Collection<Civ> getForIncorporation(boolean incorporated) throws DataAccessException;
    Collection<Civ> getForPoints(int minPoints) throws DataAccessException;
    Collection<Civ> getForPoints(int minPoints, int maxPoints) throws DataAccessException;
}
