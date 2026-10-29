package com.securebank.util;

import com.securebank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final AccountRepository accountRepository;
    private final Random random = new Random();

    public String generate() {
        String accountNumber;
        do {
            long number = Math.abs(random.nextLong() % 9000000000000000L) + 1000000000000000L; // 16 digits
            accountNumber = String.format("%016d", number);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}