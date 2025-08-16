package com.epsgrau.pe.sicodetri.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing an application user.
 * Stores authentication credentials, enabled status, and assigned roles.
 * Also tracks audit information for creation and modification.
 * <b>Class</b>: {@link User}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
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
 *     <li>Jul 16, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(length = 50)
    private String addedBy;

    @Column
    private LocalDateTime additionDate;

    @Column(length = 50)
    private String modifiedBy;

    @Column
    private LocalDateTime modificationDate;

    /**
     * All-args constructor used by the Lombok builder.
     * Performs a defensive copy of the provided roles set to avoid external mutation.
     *
     * @param id               the user ID
     * @param username         the unique username
     * @param password         the encrypted password
     * @param enabled          whether the account is enabled
     * @param roles            the roles to assign
     * @param addedBy          the creator username
     * @param additionDate     the creation timestamp
     * @param modifiedBy       the last modifier username
     * @param modificationDate the last modification timestamp
    */
    @Builder
    public User(Long id,
                String username,
                String password,
                boolean enabled,
                Set<Role> roles,
                String addedBy,
                LocalDateTime additionDate,
                String modifiedBy,
                LocalDateTime modificationDate) {
        this.id               = id;
        this.username         = username;
        this.password         = password;
        this.enabled          = enabled;
        this.roles            = (roles == null) ? new HashSet<>() : new HashSet<>(roles);
        this.addedBy          = addedBy;
        this.additionDate     = additionDate;
        this.modifiedBy       = modifiedBy;
        this.modificationDate = modificationDate;
    }

    /**
     * Sets the roles for the user.
     * Clears any existing roles and adds all entries from the given set.
     *
     * @param roles the new set of roles to assign
    */
    public void setRoles(Set<Role> roles) {
        this.roles.clear();
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    /**
     * Returns an unmodifiable view of the user's roles to prevent external mutation.
     *
     * @return an unmodifiable set of assigned roles
    */
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    /**
    * Builder customization: ensures a defensive copy of the roles set within the builder.
    */
    public static class UserBuilder {
        /**
         * Sets the roles in the builder with a defensive copy.
         *
         * @param roles the roles to assign
         * @return the builder instance
        */
        public UserBuilder roles(Set<Role> roles) {
            this.roles = (roles == null) ? new HashSet<>() : new HashSet<>(roles);
            return this;
        }
    }
}
