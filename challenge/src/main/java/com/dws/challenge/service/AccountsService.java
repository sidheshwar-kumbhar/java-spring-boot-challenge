package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountIdNotExistException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.repository.AccountsRepository;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountsService")
public class AccountsService implements IAccountsService {

	@Autowired
	private AccountsRepository accountsRepository;

	/**
	 * This function is used for create account in memory database
	 * 
	 * @param account - account information
	 * @return void
	 * @throws DuplicateAccountIdException
	 */
	@Override
	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	/**
	 * This function is used for get account information from in memory database
	 * 
	 * @param accountId - account id
	 * @return Account - Account information
	 * @throws AccountIdNotExistException
	 */
	@Override
	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	/**
	 * This function is used for clear accounts from in memory database
	 * 
	 * @param NA
	 * @return void
	 * @throws NA
	 */
	@Override
	public void clearAccounts() {
		accountsRepository.clearAccounts();
	}

	/**
	 * This function is used for transfer amount from to account
	 * 
	 * @param accountFromId - account id need to be withdraw amount
	 * @param accountToId   - account id need to be deposit amount
	 * @param amount        - amount need to be withdraw and deposit in account
	 * @return true / false
	 * @throws AccountIdNotExistException
	 * @throws InsufficientBalanceException
	 */
	@Override
	public boolean transfer(String accountFromId, String accountToId, BigDecimal amount)
			throws AccountIdNotExistException, InsufficientBalanceException {

		// Used a lock per account to avoid deadlock I have used acquire locks in
		// the same order always.

		Object accountFromIdLock = accountFromId;
		Object accountToIdLlock = accountToId;
		// First lock for account from id
		synchronized (accountFromIdLock) {
			// Second lock for account to id
			synchronized (accountToIdLlock) {
				// withdraw amount from account
				// If error occurred at withdraw time will not affect amount
				if (accountsRepository.withdraw(accountFromId, amount)) {
					try {
						// After successful withdraw amount need to be deposit in to account
						accountsRepository.deposit(accountToId, amount);
					} catch (AccountIdNotExistException | InsufficientBalanceException e) {

						// While depositing any error occurred need to revert the withdraw transaction
						accountsRepository.deposit(accountFromId, amount);
						throw e;
					} catch (Exception e) {
						// While depositing any error occurred need to revert the withdraw transaction
						accountsRepository.deposit(accountFromId, amount);
						throw e;
					}
				}
			}
		}

		// return true after successful transfer amount from to account
		return true;
	}

}
