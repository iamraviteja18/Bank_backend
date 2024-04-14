package com.bank.service;
import  com.bank.entities.Account;
import com.bank.entities.AccountType;
import com.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;


    public List<Account> listAllAccounts() {
        return accountRepository.findAll();
    }
    public List<Account> listAccountsByUserId(String userId) {
        return accountRepository.findByUserId(userId);
    }

    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return Optional.ofNullable(accountRepository.findByAccountNumber(accountNumber));
    }

//    public void createAccount(Account account) {
//        if(account.getAccountType().equals(AccountType.CHECKING)){
//            long sequence = sequenceGeneratorService.generateSequence(Account.SEQUENCE_NAME);
//            String accountNumber = "CA" + String.format("%08d", sequence);
//            account.setAccountNumber(accountNumber);
//        }else {
//            long sequence = sequenceGeneratorService.generateSequence(Account.SEQUENCE_NAME);
//            String accountNumber = "SA" + String.format("%08d", sequence);
//            account.setAccountNumber(accountNumber);
//        }
//        //account.setAccountNumber(String.format("%010d", sequenceGeneratorService.generateSequence(Account.SEQUENCE_NAME)));
//
//        accountRepository.save(account);
//    }
    public void createAccount(Account account) {
        long sequence = sequenceGeneratorService.generateSequence(Account.SEQUENCE_NAME);
        String accountPrefix = switch (account.getAccountType()) {
            case CHECKING -> "CA";
            case SAVINGS -> "SA";
            case MERCHANT -> "MA";
        };
        String accountNumber = accountPrefix + String.format("%08d", sequence);
        account.setAccountNumber(accountNumber);
        accountRepository.save(account);
    }


    public void deleteAccount(String accountNumber) {
        accountRepository.deleteByAccountNumber(accountNumber);
    }

    public void updateAccount(Account oldAccount, Account updatedAccountDetails) {
        if (updatedAccountDetails.getPhoneNumber() != null) {
            oldAccount.setPhoneNumber(updatedAccountDetails.getPhoneNumber());
        } else if (updatedAccountDetails.getFirstName() != null) {
            oldAccount.setFirstName(updatedAccountDetails.getFirstName());
        } else if (updatedAccountDetails.getLastName() != null) {
            oldAccount.setLastName(updatedAccountDetails.getLastName());
        }
        accountRepository.save(oldAccount);
    }



    // Methods for creating, updating, and deleting accounts
}
