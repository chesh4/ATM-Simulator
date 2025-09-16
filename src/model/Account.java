package model;

public class Account {
	private final String accountNumber;
	private final String holderName;
	private String pin;
	private double balance;

	public Account(String accountNumber, String holderName, String pin, double balance) {
		this.accountNumber = accountNumber;
		this.holderName = holderName;
		this.pin = pin;
		this.balance = balance;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getHolderName() {
		return holderName;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
}


