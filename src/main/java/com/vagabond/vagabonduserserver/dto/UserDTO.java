package com.vagabond.vagabonduserserver.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDTO(long userId, String firstName, String lastName, String email, String password, List<AccountDTO> accountDTOS) {
}
