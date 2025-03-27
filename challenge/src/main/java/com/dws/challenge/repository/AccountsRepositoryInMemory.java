package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountIdNotExistException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientBalanceException;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository("accountsRepository")
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) throws AccountIdNotExistException {
		// Get account from in memory data
		Optional<Account> optAccount = Optional.ofNullable(accounts.get(accountId));
		// Check account is present or not. If not present throw the exception
		if (!optAccount.isPresent()) {
			throw new AccountIdNotExistException("Account id " + accountId + " not exists!");
		}
		// Return account information from in memory data
		return optAccount.get();
	}

	@Override
	public void clearAccounts() {
		// Clear all accounts
		accounts.clear();
	}

	@Override
	public boolean withdraw(String accountId, BigDecimal amount)
			throws AccountIdNotExistException, InsufficientBalanceException {
		// Get withdraw account from in memory data
		Optional<Account> optAccount = Optional.ofNullable(accounts.get(accountId));
		// Check account is present or not. If not present throw the exception
		if (!optAccount.isPresent()) {
			throw new AccountIdNotExistException("Account id " + accountId + " not exists!");
		}
		// Get withdraw account
		Account account = optAccount.get();
		// Check account has sufficient balance
		// If not throw the exception
		if (account.getBalance().compareTo(amount) == -1) {
			throw new InsufficientBalanceException("Account id " + accountId + " has insufficient balance!");
		}
		// withdraw amount from account balance
		account.setBalance(account.getBalance().subtract(amount));

		// After successful withdraw return true
		return true;
	}

	@Override
	public boolean deposit(String accountId, BigDecimal amount) throws AccountIdNotExistException {
		// Get deposit account from in memory data
		Optional<Account> optAccount = Optional.ofNullable(accounts.get(accountId));
		// Check account is present or not. If not present throw the exception
		if (!optAccount.isPresent()) {
			throw new AccountIdNotExistException("Account id " + accountId + " not exists!");
		}
		// Get deposit account
		Account account = optAccount.get();
		// deposit amount in account
		account.setBalance(account.getBalance().add(amount));
		// After successful deposit return true
		return true;
	}

}
