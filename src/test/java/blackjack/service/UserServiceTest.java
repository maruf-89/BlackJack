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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, transactionRepository, passwordEncoder);
    }

    @Test
    @DisplayName("register() hashes the password and assigns the USER role with a 1000 starting balance")
    void registerCreatesUserWithDefaults() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newplayer");
        request.setEmail("newplayer@example.com");
        request.setPassword("plaintext");

        Role userRole = new Role();
        userRole.setName("USER");

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("plaintext")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(request);

        assertEquals("newplayer", result.getUsername());
        assertEquals("newplayer@example.com", result.getEmail());
        assertEquals("hashed", result.getPassword());
        assertEquals(userRole, result.getRole());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
    }

    @Test
    @DisplayName("register() throws when the USER role is missing")
    void registerThrowsWhenRoleMissing() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newplayer");
        request.setEmail("newplayer@example.com");
        request.setPassword("plaintext");

        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("findByUsername() throws when the user does not exist")
    void findByUsernameThrowsWhenMissing() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findByUsername("ghost"));
    }

    @Test
    @DisplayName("updateBalance() rejects a negative balance")
    void updateBalanceRejectsNegative() {
        assertThrows(RuntimeException.class, () -> userService.updateBalance(1L, new BigDecimal("-5")));
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("updateBalance() records the delta as an ADMIN_ADJUSTMENT transaction")
    void updateBalanceRecordsTransactionDelta() {
        User user = new User();
        user.setId(1L);
        user.setUsername("player");
        user.setBalance(new BigDecimal("100.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updateBalance(1L, new BigDecimal("250.00"));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        assertEquals(new BigDecimal("150.00"), captor.getValue().getAmount());
        assertEquals("ADMIN_ADJUSTMENT", captor.getValue().getType());
        assertEquals(new BigDecimal("250.00"), user.getBalance());
    }

    @Test
    @DisplayName("changePassword() throws when the current password does not match")
    void changePasswordThrowsOnWrongCurrentPassword() {
        User user = new User();
        user.setUsername("player");
        user.setPassword("hashedOld");

        when(userRepository.findByUsername("player")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashedOld")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.changePassword("player", "wrong", "newpass"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("changePassword() re-encodes and saves the new password when the current one matches")
    void changePasswordSucceedsWhenCurrentMatches() {
        User user = new User();
        user.setUsername("player");
        user.setPassword("hashedOld");

        when(userRepository.findByUsername("player")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correct", "hashedOld")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("hashedNew");

        userService.changePassword("player", "correct", "newpass");

        assertEquals("hashedNew", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("setEnabled() prevents an admin from banning their own account")
    void setEnabledPreventsSelfBan() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.setEnabled("admin", 1L, false));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("setEnabled() allows banning a different account")
    void setEnabledAllowsBanningOtherAccount() {
        User user = new User();
        user.setId(2L);
        user.setUsername("someoneElse");
        user.setEnabled(true);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.setEnabled("admin", 2L, false);

        assertFalse(result.isEnabled());
    }

    @Test
    @DisplayName("getHighscores() maps top users to HighscoreEntry with username and balance")
    void getHighscoresMapsTopUsers() {
        User first = new User();
        first.setUsername("richguy");
        first.setBalance(new BigDecimal("5000.00"));

        User second = new User();
        second.setUsername("poorguy");
        second.setBalance(new BigDecimal("100.00"));

        when(userRepository.findTop10ByOrderByBalanceDesc()).thenReturn(List.of(first, second));

        List<HighscoreEntry> result = userService.getHighscores();

        assertEquals(2, result.size());
        assertEquals("richguy", result.get(0).getUsername());
        assertEquals(new BigDecimal("5000.00"), result.get(0).getBalance());
        assertEquals("poorguy", result.get(1).getUsername());
    }

    @Test
    @DisplayName("getTransactions() maps a user's transactions to TransactionResponse")
    void getTransactionsMapsUserTransactions() {
        User user = new User();
        user.setUsername("player");

        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setUser(user);
        tx.setAmount(new BigDecimal("-10.00"));
        tx.setType("BET");
        tx.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByUsername("player")).thenReturn(Optional.of(user));
        when(transactionRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(tx));

        List<TransactionResponse> result = userService.getTransactions("player");

        assertEquals(1, result.size());
        assertEquals("BET", result.get(0).getType());
        assertEquals(new BigDecimal("-10.00"), result.get(0).getAmount());
    }
}