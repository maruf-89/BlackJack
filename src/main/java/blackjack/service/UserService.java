package blackjack.service;

import blackjack.dto.HighscoreEntry;
import blackjack.dto.RegisterRequest;
import blackjack.dto.TransactionResponse;
import blackjack.entity.Role;
import blackjack.entity.Transaction;
import blackjack.entity.User;
import blackjack.repository.RoleRepository;
import blackjack.repository.TransactionRepository;
import blackjack.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            TransactionRepository transactionRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<HighscoreEntry> getHighscores() {
        return userRepository.findTop10ByOrderByBalanceDesc()
                .stream()
                .map(u -> new HighscoreEntry(u.getUsername(), u.getBalance()))
                .toList();
    }

    public List<TransactionResponse> getTransactions(String username) {
        User user = findByUsername(username);

        return transactionRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(t -> new TransactionResponse(t.getId(), t.getAmount(), t.getType(), t.getCreatedAt()))
                .toList();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public User register(RegisterRequest request) {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role missing"));

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

    public User updateBalance(Long userId, BigDecimal newBalance) {
        if (newBalance == null || newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Balance cannot be negative");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal delta = newBalance.subtract(user.getBalance());

        user.setBalance(newBalance);
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(delta);
        transaction.setType("ADMIN_ADJUSTMENT");
        transactionRepository.save(transaction);

        return user;
    }

    public User setEnabled(String actingAdminUsername, Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!enabled && user.getUsername().equals(actingAdminUsername)) {
            throw new RuntimeException("You cannot ban your own account");
        }

        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 4) {
            throw new RuntimeException("New password must be at least 4 characters");
        }

        User user = findByUsername(username);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}