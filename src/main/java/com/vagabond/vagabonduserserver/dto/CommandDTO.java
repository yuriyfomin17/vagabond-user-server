package com.vagabond.vagabonduserserver.dto;

public record CommandDTO(long accountId, LoginDTO loginDTO, CookiesDTO cookiesDTO) {
}
