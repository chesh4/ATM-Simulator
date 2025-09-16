package service;

import model.Account;
import model.Transaction;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ATMService {
	private final Map<String, Account> accountNumberToAccount = new HashMap<>();
	private final List<Transaction> sessionTransactions = new ArrayList<>();
	private final File accountsFile = new File("data" + File.separator + "accounts.csv");
	private final File transactionsFile = new File("data" + File.separator + "transactions.csv");
	private final DateTimeFormatter timestampFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	public ATMService() {
		ensureDataDirectory();
		loadAccounts();
	}

	private void ensureDataDirectory() {
		File dataDir = new File("data");
		if (!dataDir.exists()) {
			//noinspection ResultOfMethodCallIgnored
			dataDir.mkdirs();
		}
	}

	public void loadAccounts() {
		accountNumberToAccount.clear();
		if (!accountsFile.exists()) {
			return;
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(accountsFile))) {
			String line;
			boolean isFirst = true;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				if (isFirst && line.toLowerCase(Locale.ROOT).startsWith("accountnumber,")) {
					isFirst = false;
					continue; // skip header
				}
				isFirst = false;
				String[] parts = line.split(",");
				if (parts.length < 4) continue;
				String acc = parts[0].trim();
				String name = parts[1].trim();
				String pin = parts[2].trim();
				double bal;
				try {
					bal = Double.parseDouble(parts[3].trim());
				} catch (NumberFormatException e) {
					bal = 0.0;
				}
				accountNumberToAccount.put(acc, new Account(acc, name, pin, bal));
			}
		} catch (IOException e) {
			System.out.println("Error loading accounts: " + e.getMessage());
		}
	}

	public synchronized void saveAccounts() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountsFile))) {
			writer.write("AccountNumber,HolderName,Pin,Balance");
			writer.newLine();
			for (Account a : accountNumberToAccount.values()) {
				writer.write(a.getAccountNumber() + "," + escapeCsv(a.getHolderName()) + "," + a.getPin() + "," + String.format(Locale.ROOT, "%.2f", a.getBalance()));
				writer.newLine();
			}
		} catch (IOException e) {
			System.out.println("Error saving accounts: " + e.getMessage());
		}
	}

	private String escapeCsv(String value) {
		if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
			return '"' + value.replace("\"", "\"\"") + '"';
		}
		return value;
	}

	private void logTransaction(Transaction t) {
		sessionTransactions.add(t);
		boolean fileExists = transactionsFile.exists();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(transactionsFile, true))) {
			if (!fileExists) {
				writer.write("Timestamp,AccountNumber,Type,Amount,BalanceAfter,TargetAccount");
				writer.newLine();
			}
			writer.write(timestampFormatter.format(t.getTimestamp()) + "," + t.getAccountNumber() + "," + t.getType() + "," + String.format(Locale.ROOT, "%.2f", t.getAmount()) + "," + String.format(Locale.ROOT, "%.2f", t.getBalanceAfter()) + "," + (t.getTargetAccount() == null ? "" : t.getTargetAccount()));
			writer.newLine();
		} catch (IOException e) {
			System.out.println("Error logging transaction: " + e.getMessage());
		}
	}

	public Account authenticate(String accountNumber, String pin) {
		Account a = accountNumberToAccount.get(accountNumber);
		if (a != null && a.getPin().equals(pin)) {
			return a;
		}
		return null;
	}

	public double getBalance(Account account) {
		return account.getBalance();
	}

	public boolean changePin(Account account, String oldPin, String newPin) {
		if (!account.getPin().equals(oldPin)) return false;
		if (newPin == null || newPin.length() < 4) return false;
		account.setPin(newPin);
		saveAccounts();
		return true;
	}

	public boolean withdraw(Account account, double amount) {
		if (amount <= 0) return false;
		double current = account.getBalance();
		if (amount > current) return false;
		account.setBalance(current - amount);
		saveAccounts();
		logTransaction(new Transaction(LocalDateTime.now(), account.getAccountNumber(), "WITHDRAW", amount, account.getBalance(), null));
		return true;
	}

	public boolean deposit(Account account, double amount) {
		if (amount <= 0) return false;
		account.setBalance(account.getBalance() + amount);
		saveAccounts();
		logTransaction(new Transaction(LocalDateTime.now(), account.getAccountNumber(), "DEPOSIT", amount, account.getBalance(), null));
		return true;
	}

	public boolean transfer(Account from, String toAccountNumber, double amount) {
		if (amount <= 0) return false;
		Account to = accountNumberToAccount.get(toAccountNumber);
		if (to == null) return false;
		if (from.getAccountNumber().equals(to.getAccountNumber())) return false;
		if (from.getBalance() < amount) return false;
		from.setBalance(from.getBalance() - amount);
		to.setBalance(to.getBalance() + amount);
		saveAccounts();
		logTransaction(new Transaction(LocalDateTime.now(), from.getAccountNumber(), "TRANSFER_OUT", amount, from.getBalance(), to.getAccountNumber()));
		logTransaction(new Transaction(LocalDateTime.now(), to.getAccountNumber(), "TRANSFER_IN", amount, to.getBalance(), from.getAccountNumber()));
		return true;
	}

	public List<Transaction> getSessionTransactions() {
		return new ArrayList<>(sessionTransactions);
	}

	public Set<String> getAllAccountNumbers() {
		return new HashSet<>(accountNumberToAccount.keySet());
	}
}


