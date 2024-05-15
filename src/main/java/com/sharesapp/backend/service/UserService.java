package com.sharesapp.backend.service;

import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.dto.user.CreateUser;
import com.sharesapp.backend.dto.user.UserDto;
import com.sharesapp.backend.dto.user.UserShareDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDto> createUser(CreateUser crateUser);
    Optional<UserDto> getById(Long id);
    Optional<List<UserDto>> getAllUsers();
    Optional<UserDto> updateUser(Long id, UserDto userDto);
    Optional<UserDto> deleteUser(Long id);
    Optional<ShareDto> buyShare(Long userId, Long shareId);
    Optional<ShareDto> sellShare(Long userId, Long shareId);
    Optional<List<ShareDto>> getShares(Long id);
    Optional<List<UserShareDto>> getUsersSharesAndCompanies();
    Optional<List<UserShareDto>> getUsersByCompanyAndSharePriceRange(Long companyId, Float minPrice, Float maxPrice);
}
