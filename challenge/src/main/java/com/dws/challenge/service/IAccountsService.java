package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountIdNotExistException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientBalanceException;

import java.math.BigDecimal;

public interface IAccountsService {

	/**
	 * This function is used for create account in memory database
	 * 
	 * @param account - account information
	 * @return void
	 * @throws DuplicateAccountIdException
	 */
	public void createAccount(Account account);

	/**
	 * This function is used for get account information from in memory database
	 * 
	 * @param accountId - account id
	 * @return Account - Account information
	 * @throws AccountIdNotExistException
	 */
	public Account getAccount(String accountId);

	/**
	 * This function is used for clear accounts from in memory database
	 * 
	 * @param NA
	 * @return void
	 * @throws NA
	 */
	public void clearAccounts();

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

	public boolean transfer(String accountFromId, String accountToId, BigDecimal amount)
			throws AccountIdNotExistException, InsufficientBalanceException;

}
