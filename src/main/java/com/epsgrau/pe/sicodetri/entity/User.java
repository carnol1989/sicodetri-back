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
 * This class entity for tax additions. <br/>
 * <b>Class</b>: {@link User}<br/>
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
    * All-args constructor used by the builder.
    * Defensive copy of 'roles' to avoid saving the external reference.
    *
    * @param id Id
    * @param username Username
    * @param password Password
    * @param enabled Enabled
    * @param roles Roles
    * @param addedBy AddedBy
    * @param additionDate AdditionDate
    * @param modifiedBy ModifiedBy
    * @param modificationDate ModificationDate
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
    * Custom setter: Empty and copy the contents,
    * ensuring that the original reference is not saved.
    *
    * @param roles Roles
    */
    public void setRoles(Set<Role> roles) {
        this.roles.clear();
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    /**
    * Custom getter: Returns an unmodifiable view
    * to prevent external mutations.
    *
    * @return Set Role
    */
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    /**
    * Builder customization: Lombok will see this method and will NOT generate
    * its own conflicting method, but it will generate the rest of the UserBuilder class.
    */
    public static class UserBuilder {
        /**
        * Defensive copy of 'roles' within the builder.
        *
        * @param roles Roles
        * @return UserBuilder
        */
        public UserBuilder roles(Set<Role> roles) {
            this.roles = (roles == null) ? new HashSet<>() : new HashSet<>(roles);
            return this;
        }
    }
}
