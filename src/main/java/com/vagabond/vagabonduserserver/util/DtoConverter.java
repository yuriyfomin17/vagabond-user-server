package com.vagabond.vagabonduserserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vagabond.vagabonduserserver.dto.AccountDTO;
import com.vagabond.vagabonduserserver.dto.CookieDTO;
import com.vagabond.vagabonduserserver.dto.LoginDTO;
import com.vagabond.vagabonduserserver.dto.UserDTO;
import com.vagabond.vagabonduserserver.model.Account;
import com.vagabond.vagabonduserserver.model.User;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@UtilityClass
@Slf4j
public class DtoConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Account createAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setEmail(accountDTO.email());
        account.setPassword(accountDTO.password());
        account.setProxy(accountDTO.proxy());
        account.setActive(false);
        List<CookieDTO> cookieDTOS = accountDTO.cookies();
        String serializedCookies = getSerializedCookies(cookieDTOS);
        account.setSerializedCookies(serializedCookies);
        return account;
    }

    public static String getSerializedCookies(List<CookieDTO> cookieDTOS) {
        String serializedCookies;
        try {
            serializedCookies = objectMapper.writeValueAsString(cookieDTOS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return serializedCookies;

    }

    public UserDTO createUserDTO(User user) {
        List<AccountDTO> accounts = user.getAccounts().stream().map(DtoConverter::createAccountDTO).toList();
        return UserDTO.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .email(user.getEmail())
                .accountDTOS(accounts)
                .build();
    }

    public AccountDTO createAccountDTO(Account account) {
        String serializedCookies = account.getSerializedCookies();
        List<CookieDTO> cookieDTOS = getCookieDTOS(serializedCookies);
        return AccountDTO.builder()
                .accountId(account.getId())
                .email(account.getEmail())
                .password(account.getPassword())
                .proxy(account.getProxy())
                .active(account.getActive())
                .eaabToken(account.getEaabToken())
                .cookies(cookieDTOS)
                .build();
    }

    public static List<CookieDTO> getCookieDTOS(String serializedCookies) {
        List<CookieDTO> cookieDTOS;
        try {
            cookieDTOS = objectMapper.readValue(serializedCookies, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return cookieDTOS;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setEmail(userDTO.email());
        user.setPassword(userDTO.password());
        return user;
    }

    public LoginDTO getLoginDTO(Account account) {
        String email = account.getEmail();
        String password = account.getPassword();
        return new LoginDTO(email, password);
    }
}