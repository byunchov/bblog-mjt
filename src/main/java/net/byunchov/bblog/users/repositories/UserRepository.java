package net.byunchov.bblog.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.byunchov.bblog.users.models.UserDao;

public interface UserRepository extends JpaRepository<UserDao, Long> {
    Optional<UserDao> findByUsername(String userName);
    Optional<UserDao> findByEmailIgnoreCase(String email);

}
