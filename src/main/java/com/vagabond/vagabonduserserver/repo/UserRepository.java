package com.vagabond.vagabonduserserver.repo;

import com.vagabond.vagabonduserserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional(readOnly = true)
    @Query("select u from User u left join fetch u.accounts a where u.email =:email ")
    Optional<User> findByEmail(String email);

}