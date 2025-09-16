package model;

import java.time.LocalDateTime;

public class Transaction {
	private final LocalDateTime timestamp;
	private final String accountNumber;
	private final String type; // WITHDRAW, DEPOSIT, TRANSFER_OUT, TRANSFER_IN
	private final double amount;
	private final double balanceAfter;
	private final String targetAccount; // optional for transfers

	public Transaction(LocalDateTime timestamp, String accountNumber, String type, double amount, double balanceAfter, String targetAccount) {
		this.timestamp = timestamp;
		this.accountNumber = accountNumber;
		this.type = type;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.targetAccount = targetAccount;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getType() {
		return type;
	}

	public double getAmount() {
		return amount;
	}

	public double getBalanceAfter() {
		return balanceAfter;
	}

	public String getTargetAccount() {
		return targetAccount;
	}
}


