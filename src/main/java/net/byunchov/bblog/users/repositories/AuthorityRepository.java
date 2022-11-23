package net.byunchov.bblog.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import net.byunchov.bblog.users.models.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String> {}
