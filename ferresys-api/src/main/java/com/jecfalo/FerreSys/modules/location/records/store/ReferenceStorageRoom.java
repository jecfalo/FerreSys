package com.jecfalo.FerreSys.modules.location.records.store;

import com.jecfalo.FerreSys.modules.location.models.StorageRoom;

public record ReferenceStorageRoom(
        Long id,
        String name,
        String location,
        Boolean active
)
{
    public ReferenceStorageRoom(StorageRoom room){
        this(
                room.getId(),
                room.getName(),
                room.getLocation(),
                room.getActive()
        );
    }
}
