package com.vagabond.vagabonduserserver.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AccountDTO(long accountId, String email, String password, String proxy, List<CookieDTO> cookies) {
}
