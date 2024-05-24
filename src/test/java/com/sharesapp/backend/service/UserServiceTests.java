package com.sharesapp.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.dto.user.CreateUser;
import com.sharesapp.backend.dto.user.UserDto;
import com.sharesapp.backend.dto.user.UserShareDto;
import com.sharesapp.backend.model.Share;
import com.sharesapp.backend.model.User;
import com.sharesapp.backend.repository.ShareRepository;
import com.sharesapp.backend.repository.UserRepository;
import com.sharesapp.backend.service.impl.UserServiceImpl;
import com.sharesapp.backend.utils.cache.GenericCache;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

  private final Share share = new Share();
  private final User user = new User();
  @Mock
  private UserRepository userRepository;
  @Mock
  private ShareRepository shareRepository;
  @Mock
  private GenericCache<Long, User> cache;
  @InjectMocks
  private UserServiceImpl userService;
  @Spy
  private ModelMapper modelMapper = new ModelMapper();

  @BeforeEach
  public void setUp() {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

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
  void testCreateUser() {
    CreateUser createUser = modelMapper.map(user, CreateUser.class);

    when(userRepository.save(any(User.class))).thenReturn(user);

    Optional<UserDto> result = userService.createUser(createUser);

    assertTrue(result.isPresent());
    assertEquals(user.getId(), result.get().getId());
    assertEquals(user.getFirstName(), result.get().getFirstName());
    assertEquals(user.getLastName(), result.get().getLastName());
    verify(cache, times(1)).put(1L, user);
  }

  @Test
  void testCreateManyUsers() {
    CreateUser createUser = modelMapper.map(user, CreateUser.class);

    when(userRepository.save(any(User.class))).thenReturn(user);

    List<CreateUser> createUsers = Arrays.asList(createUser, createUser, createUser);

    Optional<List<UserDto>> result = userService.createManyUsers(createUsers);

    assertTrue(result.isPresent());
    assertFalse(result.get().isEmpty());
    assertEquals(modelMapper.map(user, UserDto.class), result.get().get(1));
    verify(userRepository, times(3)).save(any(User.class));
  }

  @Test
  void testGetById() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    Optional<UserDto> result = userService.getById(1L);

    assertTrue(result.isPresent());
    assertEquals(user.getId(), result.get().getId());
    assertEquals(user.getFirstName(), result.get().getFirstName());
    assertEquals(user.getLastName(), result.get().getLastName());
    verify(cache, times(1)).put(1L, user);
  }

  @Test
  void testGetAllUsers() {
    when(userRepository.findAll()).thenReturn(Arrays.asList(user, user, user));

    Optional<List<UserDto>> result = userService.getAllUsers();

    assertTrue(result.isPresent());
    assertEquals(3, result.get().size());
    assertEquals(user.getId(), result.get().get(1).getId());
    assertEquals(user.getFirstName(), result.get().get(1).getFirstName());
    assertEquals(user.getLastName(), result.get().get(1).getLastName());
  }

//  @Test
//  void testUpdateUser() {
//    UserDto userDto = modelMapper.map(user, UserDto.class);
//
//    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//    Optional<UserDto> result = userService.updateUser(1L, modelMapper.map(userDto, UserDto.class));
//
//    assertTrue(result.isPresent());
//    assertEquals(user.getId(), result.get().getId());
//    assertEquals(user.getFirstName(), result.get().getFirstName());
//    assertEquals(user.getLastName(), result.get().getLastName());
//    verify(cache, times(1)).remove(1L);
//    verify(cache, times(1)).remove(1L);
//  }

  @Test
  void testDeleteUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    Optional<UserDto> result = userService.deleteUser(1L);

    assertTrue(result.isPresent());
    assertEquals(user.getId(), result.get().getId());
    assertEquals(user.getFirstName(), result.get().getFirstName());
    assertEquals(user.getLastName(), result.get().getLastName());
    verify(userRepository, times(1)).deleteById(1L);
    verify(cache, times(1)).remove(1L);
  }

  @Test
  void testBuyShare() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(shareRepository.findById(1L)).thenReturn(Optional.of(share));

    Optional<ShareDto> result = userService.buyShare(1L, 1L);

    assertTrue(result.isPresent());
  }

  @Test
  void testGetShares() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    Optional<List<ShareDto>> result = userService.getShares(1L);

    assertTrue(result.isPresent());
    assertEquals(3, result.get().size());
    assertEquals(share.getId(), result.get().get(1).getId());
    assertEquals(share.getSymbol(), result.get().get(1).getSymbol());
  }

  @Test
  void testSellShare() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(shareRepository.findById(1L)).thenReturn(Optional.of(share));

    Optional<ShareDto> result = userService.sellShare(1L, 1L);

    assertTrue(result.isPresent());
  }

//  @Test
//  void testGetUsersSharesAndCompany() {
//    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//    when(shareRepository.findById(1L)).thenReturn(Optional.of(share));
//
//    Optional<List<UserShareDto>> result = userService.getUsersSharesAndCompanies();
//
//    assertTrue(result.isPresent());
//    assertEquals(3, result.get().size());
//    assertEquals(user.getId(), result.get().get(1).getId());
//    assertEquals(user.getFirstName(), result.get().get(1).getFirstName());
//    assertEquals(user.getLastName(), result.get().get(1).getLastName());
//    verify(userRepository, times(3)).save(any(User.class));
//  }
//
//  @Test
//  void testGetUsersByCompanyAndSharePriceRange() {
//    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//    when(shareRepository.findById(1L)).thenReturn(Optional.of(share));
//
//    Optional<List<UserShareDto>> result =
//        userService.getUsersByCompanyAndSharePriceRange(1L, 1.0f, 1.0f);
//
//    assertTrue(result.isPresent());
//    assertEquals(3, result.get().size());
//    assertEquals(user.getId(), result.get().get(1).getId());
//    assertEquals(user.getFirstName(), result.get().get(1).getFirstName());
//    assertEquals(user.getLastName(), result.get().get(1).getLastName());
//    verify(userRepository, times(3)).save(any(User.class));
//  }
}
