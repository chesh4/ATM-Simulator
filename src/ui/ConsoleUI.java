package ui;

import model.Account;
import model.Transaction;
import service.ATMService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ConsoleUI {
	private final ATMService service;
	private final Scanner scanner = new Scanner(System.in);

	public ConsoleUI(ATMService service) {
		this.service = service;
	}

	public void start() {
		System.out.println("=== Welcome to the ATM Simulator ===");
		boolean running = true;
		while (running) {
			Account current = loginFlow();
			if (current == null) {
				System.out.println("Exiting. Goodbye!");
				return;
			}
			sessionMenu(current);
			System.out.println("You have been logged out.\n");
		}
	}

	private Account loginFlow() {
		int attemptsLeft = 3;
		while (attemptsLeft > 0) {
			System.out.print("Enter account number (or 0 to exit): ");
			String acc = scanner.nextLine().trim();
			if ("0".equals(acc)) return null;
			System.out.print("Enter PIN: ");
			String pin = scanner.nextLine().trim();
			Account a = service.authenticate(acc, pin);
			if (a != null) {
				System.out.println("Login successful. Welcome, " + a.getHolderName() + "!\n");
				return a;
			}
			attemptsLeft--;
			System.out.println("Invalid account number or PIN. Attempts left: " + attemptsLeft);
		}
		return null;
	}

	private void sessionMenu(Account account) {
		boolean loggedIn = true;
		while (loggedIn) {
			printMenu();
			int choice = readInt("Choose an option: ");
			switch (choice) {
				case 1:
					System.out.printf(Locale.ROOT, "Current balance: ₹%.2f\n", service.getBalance(account));
					break;
				case 2:
					performWithdraw(account);
					break;
				case 3:
					performDeposit(account);
					break;
				case 4:
					performTransfer(account);
					break;
				case 5:
					performChangePin(account);
					break;
				case 6:
					showHistory();
					break;
				case 7:
					loggedIn = false;
					break;
				case 0:
					System.out.println("Exiting. Goodbye!");
					System.exit(0);
					break;
				default:
					System.out.println("Invalid choice. Try again.");
			}
			System.out.println();
		}
	}

	private void printMenu() {
		System.out.println("--- Main Menu ---");
		System.out.println("1. Show Balance");
		System.out.println("2. Withdraw");
		System.out.println("3. Deposit");
		System.out.println("4. Transfer");
		System.out.println("5. Change PIN");
		System.out.println("6. Transaction History (this session)");
		System.out.println("7. Logout");
		System.out.println("0. Exit");
	}

	private int readInt(String prompt) {
		while (true) {
			System.out.print(prompt);
			try {
				String line = scanner.nextLine().trim();
				return Integer.parseInt(line);
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number.");
			}
		}
	}

	private double readDouble(String prompt) {
		while (true) {
			System.out.print(prompt);
			try {
				String line = scanner.nextLine().trim();
				return Double.parseDouble(line);
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid amount.");
			}
		}
	}

	private void performWithdraw(Account account) {
		double amount = readDouble("Enter amount to withdraw: ₹");
		boolean ok = service.withdraw(account, amount);
		if (ok) {
			System.out.println("Withdrawal successful.");
		} else {
			System.out.println("Withdrawal failed. Check amount and balance.");
		}
	}

	private void performDeposit(Account account) {
		double amount = readDouble("Enter amount to deposit: ₹");
		boolean ok = service.deposit(account, amount);
		if (ok) {
			System.out.println("Deposit successful.");
		} else {
			System.out.println("Deposit failed. Amount must be positive.");
		}
	}

	private void performTransfer(Account account) {
		System.out.print("Enter target account number: ");
		String target = scanner.nextLine().trim();
		double amount = readDouble("Enter amount to transfer: ₹");
		boolean ok = service.transfer(account, target, amount);
		if (ok) {
			System.out.println("Transfer successful.");
		} else {
			System.out.println("Transfer failed. Check account and amount.");
		}
	}

	private void performChangePin(Account account) {
		System.out.print("Enter current PIN: ");
		String oldPin = scanner.nextLine().trim();
		System.out.print("Enter new PIN (min 4 chars): ");
		String newPin = scanner.nextLine().trim();
		boolean ok = service.changePin(account, oldPin, newPin);
		if (ok) {
			System.out.println("PIN changed successfully.");
		} else {
			System.out.println("PIN change failed. Check current PIN or new PIN length.");
		}
	}

	private void showHistory() {
		List<Transaction> txs = service.getSessionTransactions();
		if (txs.isEmpty()) {
			System.out.println("No transactions this session.");
			return;
		}
		System.out.println("--- Session Transactions ---");
		for (Transaction t : txs) {
			System.out.printf(Locale.ROOT, "%s | %s | ₹%.2f | Balance: ₹%.2f %s\n",
				t.getTimestamp(), t.getType(), t.getAmount(), t.getBalanceAfter(),
				t.getTargetAccount() == null || t.getTargetAccount().isEmpty() ? "" : ("| Counterparty: " + t.getTargetAccount()));
		}
	}
}


