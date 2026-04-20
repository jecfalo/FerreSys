package com.jecfalo.FerreSys.modules.users.controllers;

import com.jecfalo.FerreSys.modules.users.records.profile.ReferenceProfile;
import com.jecfalo.FerreSys.modules.users.services.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para la información de perfiles de usuario y roles (Profiles).
 * Permite buscar clientes o trabajadores mediante varios identificadores únicos.
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    @Autowired
    private IProfileService service;
    /**
     * Obtiene el DTO de referencia de un perfil usando su ID de base de datos primaria.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReferenceProfile> getProfileId(@PathVariable Long id){
        ReferenceProfile reference = service.getProfileById(id);
        return ResponseEntity.ok(reference);
    }

    /**
     * Búsqueda específica de un cliente o usuario por su número de documento de identidad (DNI, Cédula).
     * Se usa frecuentemente en la caja para asignar una venta a un cliente regular.
     */
    @GetMapping("/document/{document}")
    public ResponseEntity<ReferenceProfile> getProfileByDocument(@PathVariable String document){
        ReferenceProfile reference = service.getProfileByDocument(document);
        return ResponseEntity.ok(reference);
    }

    /**
     * Consulta el perfil de la persona partiendo de su username (Ej: login del vendedor actual).
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ReferenceProfile> getProfileByUsername(@PathVariable String username){
        ReferenceProfile reference = service.getProfileByUsername(username);
        return ResponseEntity.ok(reference);
    }
}
