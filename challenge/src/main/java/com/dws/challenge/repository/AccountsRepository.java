package com.dws.challenge.repository;

import java.math.BigDecimal;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountIdNotExistException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientBalanceException;

public interface AccountsRepository {

	/**
	 * This function is used for create account in memory database
	 * 
	 * @param account - account information
	 * @return void
	 * @throws DuplicateAccountIdException
	 */
	public void createAccount(Account account) throws DuplicateAccountIdException;

	/**
	 * This function is used for get account information from in memory database
	 * 
	 * @param accountId - account id
	 * @return Account - Account information
	 * @throws AccountIdNotExistException
	 */
	public Account getAccount(String accountId) throws AccountIdNotExistException;

	/**
	 * This function is used for clear accounts from in memory database
	 * 
	 * @param NA
	 * @return void
	 * @throws NA
	 */
	public void clearAccounts();

	/**
	 * This function is used for withdraw amount from account
	 * 
	 * @param accountId - account id need to be withdraw amount
	 * @param amount    - amount need to be withdraw from account
	 * @return true / false
	 * @throws AccountIdNotExistException
	 * @throws InsufficientBalanceException
	 */
	public boolean withdraw(String accountId, BigDecimal amount)
			throws AccountIdNotExistException, InsufficientBalanceException;

	/**
	 * This function is used for deposit amount in account
	 * 
	 * @param accountId - account id need to be deposit amount
	 * @param amount    - amount need to be deposit in account
	 * @return true / false
	 * @throws AccountIdNotExistException
	 */
	public boolean deposit(String accountId, BigDecimal amount) throws AccountIdNotExistException;

}
