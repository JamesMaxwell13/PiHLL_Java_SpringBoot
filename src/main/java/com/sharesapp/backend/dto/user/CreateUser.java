package com.sharesapp.backend.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class CreateUser extends UserDto {
    private String password;
}
