package com.vagabond.vagabonduserserver.service;

import com.vagabond.vagabonduserserver.dto.AccountDTO;
import com.vagabond.vagabonduserserver.dto.UserDTO;
import com.vagabond.vagabonduserserver.model.Account;
import com.vagabond.vagabonduserserver.model.User;
import com.vagabond.vagabonduserserver.repo.UserRepository;
import com.vagabond.vagabonduserserver.util.DtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO findUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(DtoConverter::createUserDTO).orElse(null);
    }

    @Transactional
    public User createUser(UserDTO userDTO) {
        User user = DtoConverter.createUser(userDTO);
        Objects.requireNonNull(userDTO.accountDTOS());
        userDTO.accountDTOS().forEach(accountDTO -> handelAccountDTO(user, accountDTO));
        return userRepository.save(user);
    }

    private static void handelAccountDTO(User user, AccountDTO accountDTO) {
        Account account = DtoConverter.createAccount(accountDTO);
        user.addAccount(account);
    }

    @Transactional
    public UserDTO modifyUser(UserDTO userDTO) {
        long id = userDTO.userId();
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.map(user -> handleUser(userDTO, user)).orElse(null);
    }

    private static UserDTO handleUser(UserDTO userDTO, User user) {
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setEmail(user.getEmail());
        user.setPassword(userDTO.password());
        return userDTO;
    }
    @Transactional
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }
}