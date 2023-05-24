package com.vagabond.vagabonduserserver.dto;

public record CookieDTO(
        String name,
        String value,
        String domain,
        String path,
        long expiry,
        boolean isSecure,
        boolean isHttpOnly,
        String sameSite
) {
}
