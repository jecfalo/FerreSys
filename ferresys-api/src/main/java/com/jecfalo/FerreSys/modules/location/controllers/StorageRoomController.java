package com.jecfalo.FerreSys.modules.location.controllers;

import com.jecfalo.FerreSys.modules.location.records.store.ReferenceStorageRoom;
import com.jecfalo.FerreSys.modules.location.records.store.RegisterStorageRoom;
import com.jecfalo.FerreSys.modules.location.records.store.UpdateStorageRoom;
import com.jecfalo.FerreSys.modules.location.services.IStorageRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/storages-room")
public class StorageRoomController {
    @Autowired
    private IStorageRoomService service;
    @GetMapping
    public ResponseEntity<List<ReferenceStorageRoom>> getStorages(){
        List<ReferenceStorageRoom> getAll = service.loadAllActive();
        return ResponseEntity.ok(getAll);
    }
    @PostMapping
    public ResponseEntity<ReferenceStorageRoom> create(@RequestBody RegisterStorageRoom register){
        ReferenceStorageRoom reference = service.create(register);
        return ResponseEntity.status(HttpStatus.CREATED).body(reference);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ReferenceStorageRoom> update(@PathVariable Long id, @RequestBody UpdateStorageRoom update){
        ReferenceStorageRoom ref = service.update(id, update);
        return ResponseEntity.ok(ref);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
