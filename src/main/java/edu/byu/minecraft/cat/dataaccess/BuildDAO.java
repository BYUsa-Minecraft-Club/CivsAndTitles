package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.Build;

import java.util.Collection;
import java.util.UUID;

public interface BuildDAO extends SimpleDAO<Build, Integer> {
    Collection<Build> getAllForCiv(int civID) throws DataAccessException;
    Collection<Build> getAllForSubmitter(UUID uuid) throws DataAccessException;
    Collection<Build> getAllForBuilder(UUID uuid) throws DataAccessException;
    Collection<Build> getAllForStatus(Build.JudgeStatus status) throws DataAccessException;
    Build getForName(String name) throws DataAccessException;
}
