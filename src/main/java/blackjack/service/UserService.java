package blackjack.service;

import blackjack.dto.RegisterRequest;
import blackjack.entity.Role;
import blackjack.entity.User;
import blackjack.repository.RoleRepository;
import blackjack.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public List<User> getAllUsers() {

        return userRepository.findAll();

    }

    public User register(RegisterRequest request) {

        Role userRole = roleRepository
                .findByName("USER")
                .orElseThrow(
                        () -> new RuntimeException("USER role missing")
                );

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(userRole);
        user.setBalance(new BigDecimal("1000.00"));

        return userRepository.save(user);

    }

    public User makeAdmin(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role missing"));

        user.setRole(adminRole);

        return userRepository.save(user);

    }

}