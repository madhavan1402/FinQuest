package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Simple {@code { "message": "..." }} response used for generic
 * one-line-success responses (verify email, forgot password, logout, reset).
 */
@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;
}
