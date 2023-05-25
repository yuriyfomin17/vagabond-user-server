package com.vagabond.vagabonduserserver.service;

import com.vagabond.vagabonduserserver.dto.*;
import com.vagabond.vagabonduserserver.model.Account;
import com.vagabond.vagabonduserserver.model.User;
import com.vagabond.vagabonduserserver.repo.AccountRepository;
import com.vagabond.vagabonduserserver.repo.UserRepository;
import com.vagabond.vagabonduserserver.util.DtoConverter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public Account saveNewAccount(Long userId, AccountDTO accountDTO) {
        User userReference = userRepository.getReferenceById(userId);
        Account accountToSave = DtoConverter.createAccount(accountDTO);
        accountToSave.setUser(userReference);
        accountRepository.save(accountToSave);
        return accountToSave;

    }

    public void asyncVerifyAccount(Account account) {
        if (Objects.nonNull(account) && Objects.nonNull(account.getId())) {
            LoginDTO loginDTO = DtoConverter.getLoginDTO(account);
            List<CookieDTO> cookieDTOS = DtoConverter.getCookieDTOS(account.getSerializedCookies());
            CommandDTO commandDTO = new CommandDTO(account.getId(), loginDTO, cookieDTOS);
            rabbitTemplate.convertAndSend(exchangeName, loginRoutingKey, commandDTO);
        }
    }

    @Transactional
    public List<Account> saveMultipleAccounts(Long userId, MultipleAccountsDTO multipleAccountsDTO) {
        List<AccountDTO> accountDTOList = multipleAccountsDTO.accountDTOList();
        User userReference = userRepository.getReferenceById(userId);
        List<Account> accountListToSave = new ArrayList<>();
        accountDTOList.forEach(accountDTO -> handleAccountDTO(userReference, accountListToSave, accountDTO));
        accountRepository.saveAll(accountListToSave);
        return accountListToSave;
    }

    private void handleAccountDTO(User userReference, List<Account> accountListToSave, AccountDTO accountDTO) {
        Account account = DtoConverter.createAccount(accountDTO);
        account.setUser(userReference);
        accountListToSave.add(account);
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