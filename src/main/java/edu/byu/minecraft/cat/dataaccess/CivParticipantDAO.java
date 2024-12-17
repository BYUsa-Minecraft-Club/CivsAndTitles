package edu.byu.minecraft.cat.dataaccess;

import edu.byu.minecraft.cat.model.CivParticipantPlayer;

import java.util.Collection;
import java.util.UUID;

public interface CivParticipantDAO {
    void insert(CivParticipantPlayer player) throws DataAccessException;

    void delete(CivParticipantPlayer player) throws DataAccessException;

    Collection<CivParticipantPlayer> getAll() throws DataAccessException;

    Collection<CivParticipantPlayer> getAllForPlayer(UUID uuid) throws DataAccessException;

    Collection<CivParticipantPlayer> getAllForPlayerStatus(UUID uuid, CivParticipantPlayer.Status status) throws DataAccessException;

    Collection<CivParticipantPlayer> getAllForCiv(int civID) throws DataAccessException;

    Collection<CivParticipantPlayer> getAllForCivStatus(int civID, CivParticipantPlayer.Status status) throws DataAccessException;
}
