package com.vagabond.vagabonduserserver.repo;

import com.vagabond.vagabonduserserver.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
