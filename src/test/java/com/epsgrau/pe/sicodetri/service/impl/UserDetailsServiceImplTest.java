package com.epsgrau.pe.sicodetri.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.Role;
import com.epsgrau.pe.sicodetri.entity.User;
import com.epsgrau.pe.sicodetri.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Unit tests for {@link UserDetailsServiceImpl} and {@link User}.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>{@code loadUserByUsername(String)} returns a {@link org.springframework.security.core.userdetails.UserDetails}
 *       with correct username, password, enabled status, and {@code ROLE_<role>} authorities
 *       when a user exists with roles.</li>
 *   <li>{@code loadUserByUsername(String)} throws {@link org.springframework.security.core.userdetails.UsernameNotFoundException}
 *       when no user is found.</li>
 *   <li>{@code loadUserByUsername(String)} returns UserDetails with no authorities
 *       when the user has no roles.</li>
 *   <li>The {@link User} no-args constructor initializes default values:
 *       enabled = true, empty roles set, and null audit fields.</li>
 *   <li>{@link User#builder()} performs defensive copying of roles to prevent mutations.</li>
 *   <li>{@link User#setRoles(Set)} replaces existing roles and treats a null input
 *       as an empty set.</li>
 *   <li>{@link User#getRoles()} returns an unmodifiable set, throwing
 *       {@link UnsupportedOperationException} on modification attempts.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link UserDetailsServiceImplTest}<br/>
 * <b>Copyright</b>: &Copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau <br/>
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>: <br/>
 *     <ul>
 *     <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:<br/>
 *     <ul>
 *     <li>Jul 29, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {

  private final UserRepository userRepository = mock(UserRepository.class);
  private final UserDetailsServiceImpl service = new UserDetailsServiceImpl(userRepository);

  @Test
  @DisplayName("LoadUserByUsername returns UserDetails when user exists with roles")
  void loadUserByUsernameReturnsUserDetailsWhenUserExistsWithRoles() {
    // Arrange
    User mockUser = mock(User.class);
    when(mockUser.getUsername()).thenReturn("john");
    when(mockUser.getPassword()).thenReturn("secret");
    when(mockUser.isEnabled()).thenReturn(true);
    Role role1 = mock(Role.class);
    when(role1.getName()).thenReturn("ADMIN");
    Role role2 = mock(Role.class);
    when(role2.getName()).thenReturn("USER");
    when(mockUser.getRoles()).thenReturn(Set.of(role1, role2));
    when(userRepository.findByUsername("john"))
        .thenReturn(Optional.of(mockUser));

    // Act
    UserDetails userDetails = service.loadUserByUsername("john");

    // Assert
    assertThat(userDetails.getUsername()).isEqualTo("john");
    assertThat(userDetails.getPassword()).isEqualTo("secret");
    assertThat(userDetails.isEnabled()).isTrue();
    assertThat(userDetails.getAuthorities())
        .extracting(a -> ((SimpleGrantedAuthority) a).getAuthority())
        .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");

    verify(userRepository).findByUsername("john");
  }

  @Test
  @DisplayName("LoadUserByUsername throws when user not found")
  void loadUserByUsernameThrowsWhenUserNotFound() {
    when(userRepository.findByUsername("alice"))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.loadUserByUsername("alice"))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("Usuario no encontrado: alice");

    verify(userRepository).findByUsername("alice");
  }

  @Test
  @DisplayName("LoadUserByUsername returns UserDetails with no authorities when user has no roles")
  void loadUserByUsernameReturnsUserDetailsWithNoAuthoritiesWhenUserHasNoRoles() {
    // Arrange
    User mockUser = mock(User.class);
    when(mockUser.getUsername()).thenReturn("jane");
    when(mockUser.getPassword()).thenReturn("pwd");
    when(mockUser.isEnabled()).thenReturn(false);
    when(mockUser.getRoles()).thenReturn(Set.of());
    when(userRepository.findByUsername("jane"))
        .thenReturn(Optional.of(mockUser));

    // Act
    UserDetails userDetails = service.loadUserByUsername("jane");

    // Assert
    assertThat(userDetails.getUsername()).isEqualTo("jane");
    assertThat(userDetails.getPassword()).isEqualTo("pwd");
    assertThat(userDetails.isEnabled()).isFalse();
    assertThat(userDetails.getAuthorities()).isEmpty();

    verify(userRepository).findByUsername("jane");
  }

  @Test
  @DisplayName("User no-args constructor initializes defaults")
  void userNoArgsConstructorInitializesDefaults() {
    User user = new User();
    assertThat(user.getId()).isNull();
    assertThat(user.getUsername()).isNull();
    assertThat(user.getPassword()).isNull();
    assertThat(user.isEnabled()).isTrue();            // default true
    assertThat(user.getRoles()).isEmpty();             // empty set
    assertThat(user.getAddedBy()).isNull();
    assertThat(user.getAdditionDate()).isNull();
    assertThat(user.getModifiedBy()).isNull();
    assertThat(user.getModificationDate()).isNull();
  }

  @Test
  @DisplayName("UserBuilder copies roles defensively")
  void userBuilderCopiesRolesDefensively() {
    Role r1 = new Role(); r1.setName("R1");
    Role r2 = new Role(); r2.setName("R2");
    Set<Role> original = new HashSet<>(Set.of(r1, r2));

    User built = User.builder()
        .id(10L)
        .username("u")
        .password("p")
        .enabled(false)
        .roles(original)
        .addedBy("creator")
        .additionDate(LocalDateTime.of(2025,1,1,0,0))
        .modifiedBy("mod")
        .modificationDate(LocalDateTime.of(2025,1,2,0,0))
        .build();

    // mutate original and verify built.roles unaffected
    original.clear();
    assertThat(built.getRoles())
        .containsExactlyInAnyOrder(r1, r2);

    assertThat(built.getId()).isEqualTo(10L);
    assertThat(built.getUsername()).isEqualTo("u");
    assertThat(built.getPassword()).isEqualTo("p");
    assertThat(built.isEnabled()).isFalse();
    assertThat(built.getAddedBy()).isEqualTo("creator");
    assertThat(built.getAdditionDate()).isEqualTo(LocalDateTime.of(2025,1,1,0,0));
    assertThat(built.getModifiedBy()).isEqualTo("mod");
    assertThat(built.getModificationDate()).isEqualTo(LocalDateTime.of(2025,1,2,0,0));
  }

  @Test
  @DisplayName("setRoles replaces existing roles and handles null")
  void setRolesReplacesExistingRolesAndHandlesNull() {
    User user = new User();
    Role r = new Role(); r.setName("X");
    user.setRoles(Set.of(r));
    assertThat(user.getRoles()).containsExactly(r);

    user.setRoles(null);
    assertThat(user.getRoles()).isEmpty();

    // also ensure reset to non-null works
    Role r2 = new Role(); r2.setName("Y");
    user.setRoles(Set.of(r2));
    assertThat(user.getRoles()).containsExactly(r2);
  }

  @Test
  @DisplayName("getRoles returns unmodifiable set")
  void getRolesReturnsUnmodifiableSet() {
    User user = new User();
    Role r = new Role(); r.setName("Z");
    user.setRoles(Set.of(r));

    var rolesView = user.getRoles();
    assertThatThrownBy(() -> rolesView.add(new Role()))
        .isInstanceOf(UnsupportedOperationException.class);
  }
}