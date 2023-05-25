package com.vagabond.vagabonduserserver.dto;

public record CookieDTO(
        String domain,
        double expirationDate,
        boolean hostOnly,
        String name,
        String path,
        String sameSite,
        boolean secure,
        boolean session,
        String storeId,
        String value,
        String id
) {
}
