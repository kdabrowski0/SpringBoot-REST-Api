package com.ug.projekt1blog.repositories;

import com.ug.projekt1blog.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    List<Account> findAllByEmailNot(String email);

    Boolean existsByEmail(String email);

    List<Account> findAllByEmailIn(List<String> emails);
}
