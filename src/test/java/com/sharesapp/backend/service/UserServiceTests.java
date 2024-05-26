package com.sharesapp.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.dto.user.CreateUser;
import com.sharesapp.backend.dto.user.UserDto;
import com.sharesapp.backend.dto.user.UserShareDto;
import com.sharesapp.backend.model.Company;
import com.sharesapp.backend.model.Share;
import com.sharesapp.backend.model.User;
import com.sharesapp.backend.repository.CompanyRepository;
import com.sharesapp.backend.repository.ShareRepository;
import com.sharesapp.backend.repository.UserRepository;
import com.sharesapp.backend.service.impl.UserServiceImpl;
import com.sharesapp.backend.utils.cache.GenericCache;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTests {

  private User user;
  private Share share;
  private CreateUser createUser = new CreateUser();
  @Mock
  private UserRepository userRepository;
  @Mock
  private ShareRepository shareRepository;
  @Mock
  private CompanyRepository companyRepository;
  @Mock
  private GenericCache<Long, User> cache;
  @InjectMocks
  private UserServiceImpl userService;
  @Spy
  private ModelMapper modelMapper = new ModelMapper();

  @BeforeEach
  public void setUp() {
    share = new Share(1L, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
        Instant.parse("2007-12-03T10:15:30.00Z"), "Symbol", new HashSet<>(), null);
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    user = new User(1L, "First Name", "Last Name", "Email",
        "Phone Number", "Password", new HashSet<>());
    createUser = modelMapper.map(user, CreateUser.class);
  }

  @Test
  void testCreateUser() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    Optional<UserDto> result = userService.createUser(createUser);

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(user, UserDto.class), result.get());
    verify(userRepository, times(1)).save(any(User.class));
    verify(cache, times(1)).put(1L, user);

    Optional<User> cacheUser = cache.get(user.getId());
    assertTrue(cacheUser.isPresent());
    assertEquals(user, cacheUser.get());
    verify(cache, times(1)).clear();
  }

  @Test
  void testCreateManyUsers() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    List<User> users = List.of(user, user, user);
    Optional<List<UserDto>>
        result = userService.createManyUsers(
        users.stream().map(u -> modelMapper.map(u, CreateUser.class)).toList());

    assertTrue(result.isPresent());
    assertEquals(users.stream().map(u -> modelMapper.map(u, UserDto.class)).toList(), result.get());
    verify(userRepository, times(3)).save(any(User.class));
    verify(cache, times(3)).put(1L, user);

    Optional<User> cacheUser = cache.get(user.getId());
    assertTrue(cacheUser.isPresent());
    assertEquals(user, cacheUser.get());
    verify(cache, times(1)).clear();
  }

  @Test
  void testGetUserById() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(user));
    when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

    Optional<UserDto> result = userService.getById(1L);

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(user, UserDto.class), result.get());
    verify(cache, times(1)).get(1L);
    verify(cache, times(1)).put(1L, user);
  }

  @Test
  void testGetAllUsers() {
    when(userRepository.findAll()).thenReturn(List.of(user));

    Optional<List<UserDto>> result = userService.getAllUsers();

    assertTrue(result.isPresent());
    assertEquals(List.of(modelMapper.map(user, UserDto.class)), result.get());
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void testUpdateUser() {
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(cache.get(1L)).thenReturn(Optional.ofNullable(user));
    when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

    Optional<UserDto> result = userService.updateUser(1L, modelMapper.map(user, UserDto.class));

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(user, UserDto.class), result.get());
    verify(userRepository, times(1)).deleteById(1L);
    verify(userRepository, times(1)).save(any(User.class));
    verify(cache, times(1)).remove(1L);
    verify(cache, times(1)).put(1L, user);
  }

  @Test
  void testDeleteUser() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(user));
    when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
    when(userRepository.save(any(User.class))).thenReturn(user);
    doNothing().when(userRepository).deleteById(anyLong());
    doNothing().when(cache).remove(anyLong());

    userService.createUser(createUser);
    Optional<UserDto> result = userService.deleteUser(1L);

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(user, UserDto.class), result.get());
    verify(userRepository, times(1)).deleteById(1L);
    verify(cache, times(1)).remove(1L);
  }

  @Test
  void testSellShare() {
    when(cache.get(1L)).thenReturn(Optional.ofNullable(user));
    when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(shareRepository.findById(1L)).thenReturn(Optional.ofNullable(share));

    user.addShare(share);
    shareRepository.save(share);
    userService.createUser(createUser);

    Optional<ShareDto> result = userService.sellShare(1L, 1L);

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(share, ShareDto.class), result.get());
    verify(userRepository, times(2)).save(any(User.class));
  }

  @Test
  void testBuyShare() {
    when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(shareRepository.findById(1L)).thenReturn(Optional.ofNullable(share));

    user.addShare(share);
    shareRepository.save(share);
    userService.createUser(createUser);
    Optional<ShareDto> result = userService.buyShare(1L, 1L);

    assertTrue(result.isPresent());
    assertEquals(modelMapper.map(share, ShareDto.class), result.get());
    verify(userRepository, times(2)).save(any(User.class));
  }

  @Test
  void testGetShares() {
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

    user.addShare(share);
    shareRepository.save(share);
    userService.createUser(createUser);

    Optional<List<ShareDto>> result = userService.getShares(1L);

    assertTrue(result.isPresent());
    assertEquals(Collections.singletonList(modelMapper.map(share, ShareDto.class)), result.get());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testGetUsersSharesAndCompanies() {
    when(userRepository.findAll()).thenReturn(List.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    user.addShare(share);
    shareRepository.save(share);
    userService.createUser(createUser);

    Optional<List<UserShareDto>> result = userService.getUsersSharesAndCompanies();

    assertTrue(result.isPresent());
    assertEquals(Collections.singletonList(modelMapper.map(user, UserShareDto.class)),
        result.get());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testGetUsersByCompanyAndSharePriceRange() {
    when(userRepository.findUsersByCompanyAndSharePriceRange(anyLong(), anyFloat(), anyFloat())).thenReturn(List.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);

    Company company = new Company(1L, "Company Name", 1D, "Adress", "Website", new HashSet<>());
    company.addShare(share);
    companyRepository.save(company);
    user.addShare(share);
    shareRepository.save(share);
    userService.createUser(createUser);

    Optional<List<UserShareDto>> result =
        userService.getUsersByCompanyAndSharePriceRange(1L, 1.0f, 1.0f);

    assertTrue(result.isPresent());
    assertEquals(Collections.singletonList(modelMapper.map(user, UserShareDto.class)),
        result.get());
    verify(userRepository, times(1)).save(any(User.class));
  }
}
