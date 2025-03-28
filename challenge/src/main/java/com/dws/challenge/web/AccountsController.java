package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferAmount;
import com.dws.challenge.exception.AccountIdNotExistException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.service.EmailNotificationService;
import com.dws.challenge.service.IAccountsService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

	@Autowired
	private IAccountsService accountsService;

	@Autowired
	private EmailNotificationService emailNotificationService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
		log.info("Creating account {}", account);
		this.accountsService.createAccount(account);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(path = "/{accountId}")
	public Account getAccount(@PathVariable("accountId") String accountId) {
		log.info("Retrieving account for id {}", accountId);
		return this.accountsService.getAccount(accountId);
	}

	/**
	 * This function is used to transfer amount form account to account. The amount
	 * to transfer should always be a positive number. All exception handling with
	 * spring global exception handler. Validate request by using @Valid
	 * 
	 * @param accountFromId - account id need to be withdraw amount
	 * @param accountToId   - account id need to be deposit amount
	 * @param amount        - amount need to be withdraw and deposit in account
	 * @return true / false
	 * @throws AccountIdNotExistException
	 * @throws InsufficientBalanceException
	 */

	@PostMapping(path = "/amount/transfer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> transferAmount(@RequestBody @Valid TransferAmount transferAmount) {
		log.info("Transfer amount for {} account to {} account", transferAmount.getAccountFromId(),
				transferAmount.getAccountToId());

		// Transfer amount form account to account. All exception handling with spring
		// global exception handler.
		accountsService.transfer(transferAmount.getAccountFromId(), transferAmount.getAccountToId(),
				transferAmount.getAmount());

		// Message created after successful transfer amount
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		String message = String.format(
				"Amount transfer of %1s from %2s to %3s account has been successfully completed on %4s",
				transferAmount.getAmount(), transferAmount.getAccountFromId(), transferAmount.getAccountToId(), date);

		// Notification sent to account holders, with a message containing id of
		// the other account and amount transferred
		emailNotificationService.notifyAboutTransfer(this.accountsService.getAccount(transferAmount.getAccountFromId()),
				message);

		// Notification sent from account holders, with a message containing id of
		// the other account and amount transferred
		emailNotificationService.notifyAboutTransfer(this.accountsService.getAccount(transferAmount.getAccountToId()),
				message);
		// Send response back to client
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

}
