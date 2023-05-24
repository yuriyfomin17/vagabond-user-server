package com.vagabond.vagabonduserserver.controller;

import com.vagabond.vagabonduserserver.dto.AccountDTO;
import com.vagabond.vagabonduserserver.dto.MultipleAccountsDTO;
import com.vagabond.vagabonduserserver.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vagabond/account")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<AccountDTO> createNewAccount(@PathVariable Long userId, @RequestBody AccountDTO accountDTO) {
        AccountDTO newAccountDTO = accountService.saveNewAccount(userId, accountDTO);
        return ResponseEntity.ok(newAccountDTO);
    }
    @PostMapping("/create/multiple/{userId}")
    public ResponseEntity<HttpStatus> createMultipleAccounts(@PathVariable Long userId, @RequestBody MultipleAccountsDTO accountsDTO){
        accountService.saveMultipleAccounts(userId, accountsDTO);
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