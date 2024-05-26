package com.sharesapp.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.dto.user.CreateUser;
import com.sharesapp.backend.dto.user.UserDto;
import com.sharesapp.backend.dto.user.UserShareDto;
import com.sharesapp.backend.service.impl.UserServiceImpl;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {
  private final UserDto user = new UserDto();
  private final CreateUser createUser = new CreateUser();
  private final ShareDto share = new ShareDto();
  @Mock
  private UserServiceImpl userService;
  @InjectMocks
  private UserController userController;

  @BeforeEach
  public void setUp() {
    user.setId(1L);
    user.setFirstName("Test First Name");
    user.setLastName("Test Last Name");

    share.setId(1L);
    share.setPrevClosePrice(1.0f);
    share.setHighPrice(1.0f);
    share.setLowPrice(1.0f);
    share.setOpenPrice(1.0f);
    share.setLastSalePrice(1.0f);
    share.setLastTimeUpdated(Instant.parse("2021-01-01T00:00:00Z"));
    share.setSymbol("Test Symbol");
  }

  @Test
  void testGetAllUsers() {
    when(userService.getAllUsers()).thenReturn(Optional.of(Arrays.asList(user, user, user)));

    ResponseEntity<List<UserDto>> result = userController.getAllUsers();

    assertEquals(3, Objects.requireNonNull(result.getBody()).size());
    assertEquals(user, result.getBody().get(1));
  }

  @Test
  void testGetUserById() {
    when(userService.getById(1L)).thenReturn(Optional.of(user));

    ResponseEntity<UserDto> result = userController.getUser(1L);

    assertEquals(user, result.getBody());
  }

  @Test
  void testCreateUser() {
    createUser.setId(1L);
    createUser.setFirstName("Test First Name");
    createUser.setLastName("Test Last Name");
    when(userService.createUser(createUser)).thenReturn(Optional.of(user));

    ResponseEntity<UserDto> result = userController.createUser(createUser);

    assertEquals(user, result.getBody());
  }

  @Test
  void testCreateManyUsers() {
    when(userService.createManyUsers(Arrays.asList(createUser, createUser, createUser)))
        .thenReturn(Optional.of(Arrays.asList(user, user, user)));

    ResponseEntity<List<UserDto>> result =
        userController.createManyUsers(Arrays.asList(createUser, createUser, createUser));

    assertEquals(3, Objects.requireNonNull(result.getBody()).size());
    assertEquals(user, result.getBody().get(1));
  }

  @Test
  void testUpdateUser() {
    when(userService.updateUser(1L, user)).thenReturn(Optional.of(user));

    ResponseEntity<UserDto> result = userController.updateUser(1L, user);

    assertEquals(user, result.getBody());
  }

  @Test
  void testDeleteUser() {
    when(userService.deleteUser(1L)).thenReturn(Optional.of(user));

    ResponseEntity<UserDto> result = userController.deleteUser(1L);

    assertEquals(user, result.getBody());
  }

  @Test
  void testBuyShare() {
    when(userService.buyShare(1L, 1L)).thenReturn(Optional.of(share));

    ResponseEntity<ShareDto> result = userController.buyShare(1L, 1L);

    assertEquals(share, result.getBody());
  }

  @Test
  void testGetShares() {
    when(userService.getShares(1L)).thenReturn(Optional.of(Arrays.asList(share, share, share)));

    ResponseEntity<List<ShareDto>> result = userController.getShares(1L);

    assertEquals(3, Objects.requireNonNull(result.getBody()).size());
    assertEquals(share, result.getBody().get(1));
  }

  @Test
  void testSellShare() {
    when(userService.sellShare(1L, 1L)).thenReturn(Optional.of(share));

    ResponseEntity<ShareDto> result = userController.sellShare(1L, 1L);

    assertEquals(share, result.getBody());
  }

  @Test
  void testGetUsersSharesAndCompanies() {
    when(userService.getUsersSharesAndCompanies())
        .thenReturn(
            Optional.of(Arrays.asList(new UserShareDto(), new UserShareDto(), new UserShareDto())));

    ResponseEntity<List<UserShareDto>> result = userController.getAllUsersSharesAndCompanies();

    assertEquals(3, Objects.requireNonNull(result.getBody()).size());
  }

  @Test
  void testGetUsersByCompanyAndSharePriceRange() {
    when(userService.getUsersByCompanyAndSharePriceRange(1L, 1.0f, 1.0f))
        .thenReturn(
            Optional.of(Arrays.asList(new UserShareDto(), new UserShareDto(), new UserShareDto())));

    ResponseEntity<List<UserShareDto>> result =
        userController.getUsersByCompanyAndSharePriceRange(1L, 1.0f, 1.0f);

    assertEquals(3, Objects.requireNonNull(result.getBody()).size());
  }
}