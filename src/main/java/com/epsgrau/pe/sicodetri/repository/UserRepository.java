package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for performing CRUD operations on {@link User} entities.
 * Extends {@link JpaRepository} to provide basic data access methods, and
 * declares a custom finder to retrieve a user by their username.
 * <b>Class</b>: {@link UserRepository}<br/>
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
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Retrieves a {@link User} entity by its unique username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the User if found, or empty if not found
     */
    Optional<User> findByUsername(String username);
}