package com.sharesapp.backend.controller;

import com.sharesapp.backend.dto.share.ShareDto;
import com.sharesapp.backend.dto.user.CreateUser;
import com.sharesapp.backend.dto.user.UserDto;
import com.sharesapp.backend.dto.user.UserShareDto;
import com.sharesapp.backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUser createUser) {
        return ResponseEntity.of(userService.createUser(createUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.of(userService.getById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.of(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.of(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable("id") Long id) {
        return ResponseEntity.of(userService.deleteUser(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<ShareDto> buyShare(@PathVariable("id") Long userId, @RequestParam(value = "share_id") Long shareId) {
        return ResponseEntity.of(userService.buyShare(userId, shareId));
    }

    @GetMapping("/{id}/shares")
    public ResponseEntity<List<ShareDto>> getShares(@PathVariable("id") Long id) {
        return ResponseEntity.of(userService.getShares(id));
    }

    @DeleteMapping("/{id}/shares")
    public ResponseEntity<ShareDto> sellShare(@PathVariable("id") Long userId, @RequestParam(value = "share_id") Long shareId) {
        return ResponseEntity.of(userService.sellShare(userId, shareId));
    }

    @GetMapping("/all/all")
    public ResponseEntity<List<UserShareDto>> getAllUsersSharesAndCompanies() {
        return ResponseEntity.of(userService.getUsersSharesAndCompanies());
    }
}
