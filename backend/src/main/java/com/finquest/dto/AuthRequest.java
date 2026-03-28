package com.finquest.dto;

import lombok.Data;

// DTO (Data Transfer Object) — represents the JSON body sent by the client.
// Using a DTO instead of the User entity directly keeps the API contract
// separate from the DB schema (e.g. client never sends id, xp, level).
@Data
public class AuthRequest {
    private String name;     // only used during registration
    private String email;
    private String password; // plain text — hashed in the service before storing
}
