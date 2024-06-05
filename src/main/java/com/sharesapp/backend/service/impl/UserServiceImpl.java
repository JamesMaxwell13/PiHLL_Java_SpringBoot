package com.sharesapp.backend.service.impl;

import com.sharesapp.backend.aspect.annotation.Logging;
import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.dto.user.CreateUser;
import com.sharesapp.backend.dto.user.UserDto;
import com.sharesapp.backend.dto.user.UserShareDto;
import com.sharesapp.backend.exceptions.BadRequestException;
import com.sharesapp.backend.exceptions.NotFoundException;
import com.sharesapp.backend.model.Share;
import com.sharesapp.backend.model.User;
import com.sharesapp.backend.repository.ShareRepository;
import com.sharesapp.backend.repository.UserRepository;
import com.sharesapp.backend.service.UserService;
import com.sharesapp.backend.utils.cache.GenericCache;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {
  private static final String USER_ERROR_MESSAGE = "There is no user with id = ";
  private static final String USER_LIST_ERROR_MESSAGE = "There are no users";
  private final UserRepository userRepository;
  private final ShareRepository shareRepository;
  private final GenericCache<Long, User> cache;
  private final ModelMapper modelMapper;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, ShareRepository shareRepository,
                         GenericCache<Long, User> cache, ModelMapper modelMapper) {
    this.userRepository = userRepository;
    this.shareRepository = shareRepository;
    this.cache = cache;
    this.modelMapper = modelMapper;
  }

  @Logging
  @Override
  public Optional<UserDto> createUser(CreateUser createUser) throws BadRequestException {
    if (createUser.getFirstName().isEmpty() || createUser.getLastName().isEmpty()) {
      throw new BadRequestException("Wrong user name");
    }
    User savedUser = userRepository.save(modelMapper.map(createUser, User.class));
    cache.clear();
    cache.put(savedUser.getId(), savedUser);
    return Optional.of(modelMapper.map(savedUser, UserDto.class));
  }

  @Logging
  @Override
  public Optional<List<UserDto>> createManyUsers(List<CreateUser> createUsers)
      throws BadRequestException {
    if (createUsers.stream().anyMatch(u -> (u.getFirstName()).isEmpty()
        || u.getLastName().isEmpty())) {
      throw new BadRequestException("Wrong users or its name");
    }
    List<User> users =
        createUsers.stream().map(u -> (userRepository.save(modelMapper.map(u, User.class))))
            .toList();
    cache.clear();
    users.forEach(s -> cache.put(s.getId(), s));
    return Optional.of(Arrays.asList(modelMapper.map(users, UserDto[].class)));
  }

  @Logging
  @Override
  public Optional<UserDto> getById(Long id) throws NotFoundException {
    User user = cache.get(id).orElseGet(() -> userRepository.findById(id).orElse(null));
    if (user == null) {
      throw new NotFoundException(USER_ERROR_MESSAGE, id);
    }
    cache.put(id, user);
    return Optional.of(modelMapper.map(user, UserDto.class));
  }

  @Logging
  @Override
  public Optional<List<UserDto>> getAllUsers() throws NotFoundException {
    List<User> users = userRepository.findAll();
    if (users.isEmpty()) {
      throw new NotFoundException(USER_LIST_ERROR_MESSAGE);
    }
    users.sort(Comparator.comparing(User::getId));
    return Optional.of(Arrays.asList(modelMapper.map(users, UserDto[].class)));
  }

  @Logging
  @Override
  public Optional<UserDto> updateUser(Long id, UserDto userDto) throws BadRequestException {
    User user = cache.get(id).orElseGet(() -> userRepository.findById(id).orElse(null));
    if (user == null || userDto.getFirstName().isEmpty() || userDto.getLastName().isEmpty()) {
      throw new BadRequestException("Wrong user name or there is no such user");
    }
    cache.remove(id);
    userDto.setId(id);
    User updatedUser = userRepository.save(modelMapper.map(userDto, User.class));
    cache.put(id, updatedUser);
    return Optional.of(modelMapper.map(updatedUser, UserDto.class));
  }

  @Logging
  @Override
  public Optional<UserDto> deleteUser(Long id) throws NotFoundException {
    User user = cache.get(id).orElseGet(() -> userRepository.findById(id).orElse(null));
    if (user == null) {
      throw new NotFoundException(USER_ERROR_MESSAGE, id);
    }
    userRepository.deleteById(id);
    cache.remove(id);
    return Optional.of(modelMapper.map(user, UserDto.class));
  }

  @Logging
  @Override
  public Optional<ShareDto> buyShare(Long userId, Long shareId) throws NotFoundException {
    User user = cache.get(userId).orElseGet(() -> userRepository.findById(userId).orElse(null));
    Share share = shareRepository.findById(shareId).orElse(null);
    if (user == null) {
      throw new NotFoundException(USER_ERROR_MESSAGE, userId);
    }
    if (share == null) {
      throw new NotFoundException("There is no share with id = ", shareId);
    }
    user.addShare(share);
    userRepository.save(user);
    return Optional.of(modelMapper.map(share, ShareDto.class));
  }

  @Logging
  @Override
  public Optional<List<ShareDto>> getShares(Long id) throws NotFoundException {
    User user = cache.get(id).orElseGet(() -> userRepository.findById(id).orElse(null));
    if (user == null) {
      throw new NotFoundException(USER_ERROR_MESSAGE, id);
    }
    if (user.getShares().isEmpty()) {
      throw new NotFoundException("There are no shares");
    }
    List<Share> shares = new ArrayList<>(user.getShares());
    shares.sort(Comparator.comparing(Share::getId));
    return Optional.of(Arrays.asList(modelMapper.map(shares, ShareDto[].class)));
  }

  @Logging
  @Override
  public Optional<List<ShareDto>> getNotPurchasedShares(Long id) throws NotFoundException {
    User user = cache.get(id).orElseGet(() -> userRepository.findById(id).orElse(null));
    List<Share> shares = shareRepository.findAll();
    if (user == null) {
      throw new NotFoundException(USER_ERROR_MESSAGE, id);
    }
    if (shares.isEmpty()) {
      throw new NotFoundException("There are no shares");
    }
    shares.removeAll(user.getShares());
    shares.sort(Comparator.comparing(Share::getId));
    return Optional.of(Arrays.asList(modelMapper.map(shares, ShareDto[].class)));
  }

  @Logging
  @Override
  public Optional<ShareDto> sellShare(Long userId, Long shareId) throws NotFoundException {
    User user = cache.get(userId).orElseGet(() -> userRepository.findById(userId).orElse(null));
    Share share = shareRepository.findById(shareId).orElse(null);
    if (user == null) {
      throw new NotFoundException(USER_ERROR_MESSAGE, userId);
    }
    if (share == null) {
      throw new NotFoundException("There is no share with id = ", shareId);
    }
    user.removeShare(shareId);
    userRepository.save(user);
    shareRepository.save(share);
    return Optional.of(modelMapper.map(share, ShareDto.class));
  }

  @Logging
  @Override
  public Optional<List<UserShareDto>> getUsersSharesAndCompanies() throws NotFoundException {
    List<User> users = userRepository.findAll();
    if (users.isEmpty()) {
      throw new NotFoundException(USER_LIST_ERROR_MESSAGE);
    }
    return Optional.of(Arrays.asList(modelMapper.map(users, UserShareDto[].class)));
  }

  @Logging
  @Override
  public Optional<List<UserShareDto>> getUsersByCompanyAndSharePriceRange(Long companyId,
                                                                          Float minPrice,
                                                                          Float maxPrice)
      throws NotFoundException {
    List<User> selectUsers =
        userRepository.findUsersByCompanyAndSharePriceRange(companyId, minPrice, maxPrice);
    if (selectUsers.isEmpty()) {
      throw new NotFoundException(USER_LIST_ERROR_MESSAGE);
    }
    return Optional.of(Arrays.asList(modelMapper.map(selectUsers, UserShareDto[].class)));
  }
}