package com.vagabond.vagabonduserserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CommandDTO(@JsonProperty("account_id") long accountId, LoginDTO credentials, List<CookieDTO> cookies) {
}
