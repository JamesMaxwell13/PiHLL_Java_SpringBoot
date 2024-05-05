package com.sharesapp.backend.service.impl;

import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.dto.user.CreateUser;
import com.sharesapp.backend.dto.user.UserDto;
import com.sharesapp.backend.model.Share;
import com.sharesapp.backend.model.User;
import com.sharesapp.backend.repository.ShareRepository;
import com.sharesapp.backend.repository.UserRepository;
import com.sharesapp.backend.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ShareRepository shareRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ShareRepository shareRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.shareRepository = shareRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<UserDto> createUser(CreateUser createUser) {
        User savedUser = userRepository.save(modelMapper.map(createUser, User.class));
        return Optional.of(modelMapper.map(savedUser, UserDto.class));
    }

    @Override
    public Optional<UserDto> getById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return Optional.empty();
        }
        return Optional.of(modelMapper.map(user, UserDto.class));
    }

    @Override
    public Optional<List<UserDto>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Arrays.asList(modelMapper.map(users, UserDto[].class)));
    }

    @Override
    public Optional<UserDto> updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return Optional.empty();
        }
        userDto.setId(id);
        User updatedUser = userRepository.save(modelMapper.map(userDto, User.class));
        return Optional.of(modelMapper.map(updatedUser, UserDto.class));
    }

    @Override
    public Optional<UserDto> deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return Optional.empty();
        }
        userRepository.deleteById(id);
        return Optional.of(modelMapper.map(user, UserDto.class));
    }

    @Override
    public Optional<ShareDto> buyShare(Long userId, Long shareId) {
        User user = userRepository.findById(userId).orElse(null);
        Share share = shareRepository.findById(shareId).orElse(null);
        if(user == null || share == null) {
            return Optional.empty();
        }
        user.addShare(share);
        userRepository.save(user);
        return Optional.of(modelMapper.map(share, ShareDto.class));
    }

    @Override
    public Optional<List<ShareDto>> getShares(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            return Optional.empty();
        }
        return Optional.of(Arrays.asList(modelMapper.map(user.getShares(), ShareDto[].class)));
    }

    public Optional<ShareDto> sellShare(Long userId, Long shareId) {
        User user = userRepository.findById(userId).orElse(null);
        Share share = shareRepository.findById(shareId).orElse(null);
        if(user == null || share == null) {
            return Optional.empty();
        }
        user.removeShare(shareId);
        userRepository.save(user);
        return Optional.of(modelMapper.map(share, ShareDto.class));
    }
}