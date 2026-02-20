package com.prestobr.auth.dto.request;

import java.time.LocalDateTime;
import java.util.Set;

public record ApiKeyUpdateRequest(
        String description,
        Set<String> roles,
        LocalDateTime expiresAt) {}