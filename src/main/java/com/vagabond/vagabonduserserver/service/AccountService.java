package com.vagabond.vagabonduserserver.service;

import com.vagabond.vagabonduserserver.dto.*;
import com.vagabond.vagabonduserserver.model.Account;
import com.vagabond.vagabonduserserver.model.User;
import com.vagabond.vagabonduserserver.repo.AccountRepository;
import com.vagabond.vagabonduserserver.repo.UserRepository;
import com.vagabond.vagabonduserserver.util.DtoConverter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbit.exchange.name}")
    private String exchangeName;

    @Value("${rabbit.key.login}")
    private String loginRoutingKey;

    @Transactional
    public AccountDTO saveNewAccount(Long userId, AccountDTO accountDTO) {
        User userReference = userRepository.getReferenceById(userId);
        Account account = DtoConverter.createAccount(accountDTO);
        account.setUser(userReference);
        Account savedAccount = accountRepository.save(account);
        asyncVerifyAccount(savedAccount);
        return DtoConverter.createAccountDTO(savedAccount);
    }

    private void asyncVerifyAccount(Account account) {
        LoginDTO loginDTO = DtoConverter.getLoginDTO(account);
        List<CookieDTO> cookieDTOS = DtoConverter.getCookieDTOS(account.getSerializedCookies());
        CookiesDTO cookiesDTO = new CookiesDTO(cookieDTOS);
        CommandDTO commandDTO = new CommandDTO(account.getId(), loginDTO, cookiesDTO);
        rabbitTemplate.convertAndSend(exchangeName, loginRoutingKey, commandDTO);
    }

    @Transactional
    public void saveMultipleAccounts(Long userId, MultipleAccountsDTO multipleAccountsDTO) {
        List<AccountDTO> accountDTOList = multipleAccountsDTO.accountDTOList();
        User userReference = userRepository.getReferenceById(userId);
        List<Account> accountListToSave = new ArrayList<>();
        accountDTOList.forEach(accountDTO -> handleAccountDTO(userReference, accountListToSave, accountDTO));
        accountRepository.saveAll(accountListToSave);
    }
    private void handleAccountDTO(User userReference, List<Account> accountListToSave, AccountDTO accountDTO) {
        Account account = DtoConverter.createAccount(accountDTO);
        account.setUser(userReference);
        accountListToSave.add(account);
        asyncVerifyAccount(account);
    }

    @Transactional
    public void modifyAccount(AccountDTO accountDTO) {
        String serializedCookies = DtoConverter.getSerializedCookies(accountDTO.cookies());
        entityManager.createQuery("update Account a " +
                        "set a.email =:email, " +
                        "a.password =:password, " +
                        "a.proxy =:proxy, " +
                        "a.serializedCookies =:serializedCookies, " +
                        "a.active = false " +
                        "where a.id =:id")
                .setParameter("email", accountDTO.email())
                .setParameter("password", accountDTO.password())
                .setParameter("proxy", accountDTO.proxy())
                .setParameter("serializedCookies", serializedCookies)
                .setParameter("id", accountDTO.accountId())
                .executeUpdate();
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }
}