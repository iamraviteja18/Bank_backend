package com.bank.controller;

import com.bank.entities.SendUser;
import com.bank.entities.User;
import com.bank.repository.UserRepository;
import com.bank.service.JwtService;
import com.bank.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    @PreAuthorize("authentication.name == #username or hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserProfile(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("authentication.name == #user.username or hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> updateUserProfile(@PathVariable String id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/")
    public ResponseEntity<?> listUserDetails(HttpServletRequest request) {
        String token = getBearerToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No bearer token provided");
        }
        String username = jwtService.extractUserName(token);
        logger.debug("username :::::::::::::::::::{}",username);
        Optional<User> user = userRepository.findByEmail(username);
        if(user.isPresent()) {
            SendUser sendUser = new SendUser();
            sendUser.setUserId(user.get().getUserId());
            sendUser.setEmail(user.get().getEmail());
            sendUser.setFirstName(user.get().getFirstName());
            sendUser.setLastName(user.get().getLastName());
            sendUser.setPhoneNumber(user.get().getPhoneNumber());
            logger.debug("UserID : {}",user.get().getUserId());
            return ResponseEntity.ok(sendUser);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized or user not found");
    }

    private String getBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
