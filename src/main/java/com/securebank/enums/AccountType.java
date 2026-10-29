package com.securebank.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AccountType {
    SAVINGS, CURRENT;

    @JsonCreator
    public static AccountType fromString(String value) {
        if (value != null) {
            for (AccountType type : AccountType.values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException("Invalid AccountType: " + value);
    }
}