package com.jecfalo.FerreSys.modules.users.controllers;

import com.jecfalo.FerreSys.modules.users.records.ReferenceUser;
import com.jecfalo.FerreSys.modules.users.records.RegisterUser;
import com.jecfalo.FerreSys.modules.users.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private IUserService service;
    @PostMapping
    @Transactional
    public ResponseEntity<ReferenceUser> createUser(@RequestBody RegisterUser register, UriComponentsBuilder uriBuilder){
        ReferenceUser user = service.create(register);
        //buenas practicas: devolver un 201 y la uri
        URI uri = uriBuilder.path("/api/user/{id}").buildAndExpand(user.id()).toUri();
        return ResponseEntity.created(uri).body(user);
    }
}
