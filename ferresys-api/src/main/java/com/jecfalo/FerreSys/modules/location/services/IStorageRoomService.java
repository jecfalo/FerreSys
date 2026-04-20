package com.jecfalo.FerreSys.modules.location.services;

import com.jecfalo.FerreSys.modules.location.records.store.ReferenceStorageRoom;
import com.jecfalo.FerreSys.modules.location.records.store.RegisterStorageRoom;
import com.jecfalo.FerreSys.modules.location.records.store.UpdateStorageRoom;

import java.util.List;

public interface IStorageRoomService {
    List<ReferenceStorageRoom> loadAllActive();
    ReferenceStorageRoom create(RegisterStorageRoom register);
    ReferenceStorageRoom update(Long id, UpdateStorageRoom update);
    void delete(Long id);
}
