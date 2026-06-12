package com.securebank.exception;

public class AccountFrozenException extends RuntimeException {
    public AccountFrozenException(String accountNumber) {
        super("Account " + accountNumber + " is frozen. Please contact support.");
    }
}