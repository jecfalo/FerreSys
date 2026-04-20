package com.jecfalo.FerreSys.modules.users.records;

public record RegisterUser(
        String username,
        String password,
        String document,
        String names,
        String surnames,
        String email
){
}
