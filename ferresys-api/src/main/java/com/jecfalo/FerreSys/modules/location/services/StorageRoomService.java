package com.jecfalo.FerreSys.modules.location.services;

import com.jecfalo.FerreSys.modules.location.models.StorageRoom;
import com.jecfalo.FerreSys.modules.location.records.store.ReferenceStorageRoom;
import com.jecfalo.FerreSys.modules.location.records.store.RegisterStorageRoom;
import com.jecfalo.FerreSys.modules.location.records.store.UpdateStorageRoom;
import com.jecfalo.FerreSys.modules.location.repositories.StorageRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageRoomService implements  IStorageRoomService{
    @Autowired
    private StorageRoomRepository repository;
    @Override
    @Transactional
    public List<ReferenceStorageRoom> loadAllActive() {
        List<StorageRoom> storageRoom = repository.findByActiveTrue();
        return storageRoom.stream().map(ReferenceStorageRoom::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReferenceStorageRoom create(RegisterStorageRoom register) {
        StorageRoom storageRoom = new StorageRoom(register);
        var saveSR = repository.save(storageRoom);
        return new ReferenceStorageRoom(saveSR);
    }

    @Override
    @Transactional
    public ReferenceStorageRoom update(Long id, UpdateStorageRoom update) {
        StorageRoom findStorage = repository.findById(id).orElseThrow(()-> new RuntimeException("No se pudo encontrar la tienda"));
        findStorage.setLocation(update.location());
        return new ReferenceStorageRoom(findStorage);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StorageRoom storageRoom = repository.findById(id).orElseThrow(()-> new RuntimeException("No se encontro el id"));
        storageRoom.setActive(false);
        repository.save(storageRoom);
    }
}
