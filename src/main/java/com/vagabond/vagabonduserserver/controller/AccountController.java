package com.vagabond.vagabonduserserver.controller;

import com.vagabond.vagabonduserserver.dto.AccountDTO;
import com.vagabond.vagabonduserserver.dto.MultipleAccountsDTO;
import com.vagabond.vagabonduserserver.model.Account;
import com.vagabond.vagabonduserserver.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vagabond/account")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<HttpStatus> createNewAccount(@PathVariable Long userId, @RequestBody AccountDTO accountDTO) {
        Account savedAccount = accountService.saveNewAccount(userId, accountDTO);
        accountService.asyncVerifyAccount(savedAccount);
        if (Objects.isNull(savedAccount)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping("/create/multiple/{userId}")
    public ResponseEntity<HttpStatus> createMultipleAccounts(@PathVariable Long userId, @RequestBody MultipleAccountsDTO accountsDTO){
        List<Account> accountList = accountService.saveMultipleAccounts(userId, accountsDTO);
        accountList.forEach(accountService::asyncVerifyAccount);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/modify")
    public ResponseEntity<AccountDTO> modifyAccount(@RequestBody AccountDTO accountDTO) {
        accountService.modifyAccount(accountDTO);
        return ResponseEntity.ok(accountDTO);
    }

    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<HttpStatus> deleteAccountById(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }
}