package com.dws.challenge.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferAmount {
	@NotNull
	@NotEmpty
	private final String accountFromId;

	@NotNull
	@NotEmpty
	private final String accountToId;

	@NotNull
	@Min(value = 1, message = "Amount must be natural number.")
	private BigDecimal amount;

	@JsonCreator
	public TransferAmount(@JsonProperty("accountFromId") String accountFromId,
			@JsonProperty("accountToId") String accountToId, @JsonProperty("amount") BigDecimal amount) {
		this.accountFromId = accountFromId;
		this.accountToId = accountToId;
		this.amount = amount;
	}
}
