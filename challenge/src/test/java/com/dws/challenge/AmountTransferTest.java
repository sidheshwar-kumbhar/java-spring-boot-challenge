package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountIdNotExistException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.service.AccountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@WebAppConfiguration
class AmountTransferTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

		// Reset the existing accounts before each test.
		accountsService.clearAccounts();
	}

	/**
	 * This function is used for test amount transfer from to to account
	 */
	@Test
	void transferAmountTest() throws Exception {

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1001\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1002\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(post("/v1/accounts/amount/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\": \"1001\",\"accountToId\": \"1002\",\"amount\": 10}"))
				.andExpect(status().isOk());

		Account fromAccount = accountsService.getAccount("1001");
		Account toAccount = accountsService.getAccount("1002");
		assertThat(fromAccount.getBalance()).isEqualByComparingTo("990");
		assertThat(toAccount.getBalance()).isEqualByComparingTo("1010");
	}

	/**
	 * This function is used for test amount should be positive number
	 */
	@Test
	void transferAmountShouldPositiveNumberTest() throws Exception {

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1001\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1002\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(post("/v1/accounts/amount/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\": \"1001\",\"accountToId\": \"1002\",\"amount\": -10}"))
				.andExpect(status().isBadRequest());

		Account fromAccount = accountsService.getAccount("1001");
		Account toAccount = accountsService.getAccount("1002");
		assertThat(fromAccount.getBalance()).isEqualByComparingTo("1000");
		assertThat(toAccount.getBalance()).isEqualByComparingTo("1000");
	}

	/**
	 * This function is used for test transfer amount to non existing account id.
	 * After exception received debited amount to from account id
	 */
	@Test
	void transferAmountNonExistingAccountTest() throws Exception {

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1001\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(post("/v1/accounts/amount/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\": \"1001\",\"accountToId\": \"1002\",\"amount\": 10}"))
				.andExpect(status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccountIdNotExistException));

		Account fromAccount = accountsService.getAccount("1001");
		assertThat(fromAccount.getBalance()).isEqualByComparingTo("1000");
	}

	/**
	 * This function is used for test Insufficient Balance transfer from account
	 */
	@Test
	void transferInsufficientBalanceTest() throws Exception {

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1001\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1002\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(post("/v1/accounts/amount/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\": \"1001\",\"accountToId\": \"1002\",\"amount\": 2000}"))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InsufficientBalanceException));

		Account fromAccount = accountsService.getAccount("1001");
		Account toAccount = accountsService.getAccount("1002");
		assertThat(fromAccount.getBalance()).isEqualByComparingTo("1000");
		assertThat(toAccount.getBalance()).isEqualByComparingTo("1000");
	}

	/**
	 * This function is used for test amount should be non zero number
	 */
	@Test
	void transferAmountShouldNonZeroNumberTest() throws Exception {

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1001\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1002\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(post("/v1/accounts/amount/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\": \"1001\",\"accountToId\": \"1002\",\"amount\": 0}"))
				.andExpect(status().isBadRequest());

		Account fromAccount = accountsService.getAccount("1001");
		Account toAccount = accountsService.getAccount("1002");
		assertThat(fromAccount.getBalance()).isEqualByComparingTo("1000");
		assertThat(toAccount.getBalance()).isEqualByComparingTo("1000");
	}

	/**
	 * This function is used for test without accountFromId
	 */
	@Test
	void transferAmountWithoutAccountFromIdTest() throws Exception {

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1001\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1002\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts/amount/transfer").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountToId\": \"1002\",\"amount\": 10}")).andExpect(status().isBadRequest());

		Account fromAccount = accountsService.getAccount("1001");
		Account toAccount = accountsService.getAccount("1002");
		assertThat(fromAccount.getBalance()).isEqualByComparingTo("1000");
		assertThat(toAccount.getBalance()).isEqualByComparingTo("1000");
	}

	/**
	 * This function is used for test without accountToId
	 */
	@Test
	void transferAmountWithoutAccountToIdTest() throws Exception {

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1001\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1002\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts/amount/transfer").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountFromId\": \"1001\",\"amount\": 10}")).andExpect(status().isBadRequest());

		Account fromAccount = accountsService.getAccount("1001");
		Account toAccount = accountsService.getAccount("1002");
		assertThat(fromAccount.getBalance()).isEqualByComparingTo("1000");
		assertThat(toAccount.getBalance()).isEqualByComparingTo("1000");
	}

	/**
	 * This function is used for test without amount
	 */
	@Test
	void transferAmountWithoutAmountTest() throws Exception {

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1001\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"1002\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc
				.perform(post("/v1/accounts/amount/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\": \"1001\",\"accountToId\": \"1002\"}"))
				.andExpect(status().isBadRequest());

		Account fromAccount = accountsService.getAccount("1001");
		Account toAccount = accountsService.getAccount("1002");
		assertThat(fromAccount.getBalance()).isEqualByComparingTo("1000");
		assertThat(toAccount.getBalance()).isEqualByComparingTo("1000");
	}

}
