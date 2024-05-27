package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.JoinRequest;

import java.util.Collection;

public interface JoinRequestDAO extends SimpleDAO<JoinRequest, Integer> {
    Collection<JoinRequest> getForCiv(int civID) throws DataAccessException;
}
