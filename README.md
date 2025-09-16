ATM Simulator CLI (Java)

Overview
This is a simple ATM simulator written in plain Java (no external dependencies). It supports login with account number and PIN, balance inquiry, withdraw, deposit, transfer, PIN change, and session transaction history. Account data and a transaction log are stored as CSV files under the data/ directory. Currency is Indian Rupee (₹).

Project Structure
- src/ — Java source files
  - model/ — data models (Account, Transaction)
  - service/ — business logic and persistence (ATMService)
  - ui/ — console-driven UI (ConsoleUI)
  - Main.java — entry point
- data/ — CSV files
  - accounts.csv — account seed data
  - transactions.csv — transaction log (created on first run)

How to Run
1) Ensure you have JDK 8+ installed and available on PATH.
2) From the project root, compile:
   javac -d out src/model/*.java src/service/*.java src/ui/*.java src/Main.java
3) Run:
   java -cp out Main

Default Accounts (from data/accounts.csv) — amounts in ₹
- 2001 / PIN 1234 — Aarav Sharma — ₹45,250.00
- 2002 / PIN 2345 — Diya Patel — ₹30,500.75
- 2003 / PIN 3456 — Vikram Singh — ₹1,29,500.00
- 2004 / PIN 4567 — Ananya Iyer — ₹7,850.25
- 2005 / PIN 5678 — Rohit Verma — ₹2,23,400.00

Notes
- The app writes updated balances back to data/accounts.csv after each transaction.
- The app appends to data/transactions.csv for every transaction; session history displayed in the UI only shows actions performed during the current run.
- PINs are stored in plain text for simplicity (not for production use).


