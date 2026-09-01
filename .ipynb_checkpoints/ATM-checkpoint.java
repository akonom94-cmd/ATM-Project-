import java.util.Scanner;

/**
 * Abstract base class representing any bank account.
  * Demonstrates ABSTRACTION and ENCAPSULATION.
   */
   abstract class Account {
        private final String accountNumber;   // encapsulated: no outside class can read/change this directly
            private final String pin;
                protected double balance;             // protected so subclasses can adjust it directly

                    public Account(String accountNumber, String pin, double initialBalance) {
                                this.accountNumber = accountNumber;
                                        this.pin = pin;
                                                this.balance = initialBalance;
                    }

                        public boolean authenticate(String enteredPin) {
                                    return pin.equals(enteredPin);
                        }

                            public double getBalance() {
                                        return balance;
                            }

                                public String getAccountNumber() {
                                            return accountNumber;
                                }

                                    public boolean deposit(double amount) {
                                                if (amount <= 0) return false;
                                                        balance += amount;
                                                                return true;
                                    }

                                        // Abstract methods: every account type MUST implement its own version.
                                            // This is the basis for polymorphism further down.
                                                public abstract boolean withdraw(double amount);
                                                    public abstract String getAccountType();
   }

   /**
    * SavingsAccount: withdrawal is blocked if it would drop balance below a minimum.
     * Demonstrates INHERITANCE (extends Account) and POLYMORPHISM (its own withdraw()).
      */
      class SavingsAccount extends Account {
            private static final double MIN_BALANCE = 100.0;

                public SavingsAccount(String accountNumber, String pin, double initialBalance) {
                            super(accountNumber, pin, initialBalance); // calls parent constructor
                }

                    @Override
                        public boolean withdraw(double amount) {
                                    if (amount <= 0) return false;
                                            if (balance - amount < MIN_BALANCE) return false;
                                                    balance -= amount;
                                                            return true;
                        }

                            @Override
                                public String getAccountType() {
                                            return String.format("Savings Account (minimum balance $%.2f)", MIN_BALANCE);
                                }
      }

      /**
       * CheckingAccount: allows withdrawing into a fixed overdraft limit.
        * Same parent, DIFFERENT withdraw() behavior -> polymorphism in action.
         */
         class CheckingAccount extends Account {
                private static final double OVERDRAFT_LIMIT = 200.0;

                    public CheckingAccount(String accountNumber, String pin, double initialBalance) {
                                super(accountNumber, pin, initialBalance);
                    }

                        @Override
                            public boolean withdraw(double amount) {
                                        if (amount <= 0) return false;
                                                if (balance - amount < -OVERDRAFT_LIMIT) return false;
                                                        balance -= amount;
                                                                return true;
                            }

                                @Override
                                    public String getAccountType() {
                                                return String.format("Checking Account (overdraft limit $%.2f)", OVERDRAFT_LIMIT);
                                    }
         }

         /**
          * ATM: the console controller. It HAS-A Account (composition) and drives
           * the menu loop, delegating all money logic to whichever Account subtype
            * it was given -- it never needs to know which subtype that is.
             */
             public class ATM {
                    private static final int EOF_SENTINEL = Integer.MIN_VALUE;

                        private final Account account;   // composition: ATM "has-a" Account
                            private final Scanner scanner;

                                public ATM(Account account, Scanner scanner) {
                                            this.account = account;
                                                    this.scanner = scanner;
                                }

                                    public static void main(String[] args) {
                                                // ONE Scanner shared for the entire program's lifetime. Creating a
                                                        // second Scanner(System.in) later (e.g. inside the ATM constructor)
                                                                // would fight over the same buffered input and silently drop data.
                                                                        Scanner scanner = new Scanner(System.in);

                                                                                System.out.println("=====================================");
                                                                                        System.out.println("      WELCOME TO JAVA CONSOLE ATM     ");
                                                                                                System.out.println("=====================================");
                                                                                                        System.out.println("Select account type:");
                                                                                                                System.out.println("1. Savings Account");
                                                                                                                        System.out.println("2. Checking Account");
                                                                                                                                System.out.print("Choice: ");
                                                                                                                                        String typeChoice = scanner.hasNextLine() ? scanner.nextLine().trim() : "1";

                                                                                                                                                // Polymorphism starts here: 'account' is declared as the parent type,
                                                                                                                                                        // but points to whichever concrete subtype the user picked.
                                                                                                                                                                Account account = typeChoice.equals("2")
                                                                                                                                                                                ? new CheckingAccount("CHK-1001", "1234", 1000.00)
                                                                                                                                                                                                : new SavingsAccount("SAV-1001", "1234", 1000.00);

                                                                                                                                                                                                        ATM atm = new ATM(account, scanner);
                                                                                                                                                                                                                atm.run();
                                    }

                                        public void run() {
                                                    if (!authenticate()) {
                                                                    System.out.println("Too many incorrect attempts (or input ended). Exiting...");
                                                                                scanner.close();
                                                                                            return;
                                                    }

                                                            System.out.println("Logged into: " + account.getAccountType());

                                                                    boolean running = true;
                                                                            while (running) {
                                                                                            printMenu();
                                                                                                        int choice = readInt("Choose an option: ");

                                                                                                                    if (choice == EOF_SENTINEL) {
                                                                                                                                        System.out.println("Input ended. Exiting...");
                                                                                                                                                        break;
                                                                                                                    }

                                                                                                                                switch (choice) {
                                                                                                                                                    case 1:
                                                                                                                                                                        checkBalance();
                                                                                                                                                                                            break;
                                                                                                                                                                                                            case 2:
                                                                                                                                                                                                                                deposit();
                                                                                                                                                                                                                                                    break;
                                                                                                                                                                                                                                                                    case 3:
                                                                                                                                                                                                                                                                                        withdraw();
                                                                                                                                                                                                                                                                                                            break;
                                                                                                                                                                                                                                                                                                                            case 4:
                                                                                                                                                                                                                                                                                                                                                System.out.println("Account type: " + account.getAccountType());
                                                                                                                                                                                                                                                                                                                                                                    break;
                                                                                                                                                                                                                                                                                                                                                                                    case 5:
                                                                                                                                                                                                                                                                                                                                                                                                        running = false;
                                                                                                                                                                                                                                                                                                                                                                                                                            System.out.println("Thank you for using Java ATM. Goodbye!");
                                                                                                                                                                                                                                                                                                                                                                                                                                                break;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                default:
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    System.out.println("Invalid option. Please try again.");
                                                                                                                                }
                                                                                                                                            System.out.println();
                                                                            }

                                                                                    scanner.close();
                                        }

                                            private boolean authenticate() {
                                                        int attempts = 3;
                                                                while (attempts > 0) {
                                                                                System.out.print("Enter your 4-digit PIN: ");
                                                                                            if (!scanner.hasNextLine()) return false;
                                                                                                        String input = scanner.nextLine().trim();

                                                                                                                    // Delegates to Account's own authenticate() -- ATM never touches the PIN directly.
                                                                                                                                if (account.authenticate(input)) {
                                                                                                                                                    System.out.println("Authentication successful!\n");
                                                                                                                                                                    return true;
                                                                                                                                } else {
                                                                                                                                                    attempts--;
                                                                                                                                                                    System.out.println("Incorrect PIN. Attempts remaining: " + attempts);
                                                                                                                                }
                                                                }
                                                                        return false;
                                            }

                                                private void printMenu() {
                                                            System.out.println("-------------------------------------");
                                                                    System.out.println("1. Check Balance");
                                                                            System.out.println("2. Deposit");
                                                                                    System.out.println("3. Withdraw");
                                                                                            System.out.println("4. Account Info");
                                                                                                    System.out.println("5. Exit");
                                                                                                            System.out.println("-------------------------------------");
                                                }

                                                    private void checkBalance() {
                                                                System.out.printf("Your current balance is: $%.2f%n", account.getBalance());
                                                    }

                                                        private void deposit() {
                                                                    double amount = readDouble("Enter amount to deposit: $");
                                                                            if (account.deposit(amount)) {
                                                                                            System.out.printf("Deposit successful! New balance: $%.2f%n", account.getBalance());
                                                                            } else {
                                                                                            System.out.println("Deposit amount must be positive.");
                                                                            }
                                                        }

                                                            private void withdraw() {
                                                                        double amount = readDouble("Enter amount to withdraw: $");
                                                                                // Polymorphic call: which withdraw() rules apply depends on the
                                                                                        // actual runtime type of 'account' (SavingsAccount or CheckingAccount),
                                                                                                // even though this code only ever refers to the parent type Account.
                                                                                                        if (account.withdraw(amount)) {
                                                                                                                        System.out.printf("Withdrawal successful! New balance: $%.2f%n", account.getBalance());
                                                                                                        } else {
                                                                                                                        System.out.println("Withdrawal failed (invalid amount or account limit reached).");
                                                                                                        }
                                                            }

                                                                private int readInt(String prompt) {
                                                                            while (true) {
                                                                                            System.out.print(prompt);
                                                                                                        if (!scanner.hasNextLine()) return EOF_SENTINEL;
                                                                                                                    String input = scanner.nextLine().trim();
                                                                                                                                try {
                                                                                                                                                    return Integer.parseInt(input);
                                                                                                                                } catch (NumberFormatException e) {
                                                                                                                                                    System.out.println("Please enter a valid number.");
                                                                                                                                }
                                                                            }
                                                                }

                                                                    private double readDouble(String prompt) {
                                                                                while (true) {
                                                                                                System.out.print(prompt);
                                                                                                            if (!scanner.hasNextLine()) return -1;
                                                                                                                        String input = scanner.nextLine().trim();
                                                                                                                                    try {
                                                                                                                                                        return Double.parseDouble(input);
                                                                                                                                    } catch (NumberFormatException e) {
                                                                                                                                                        System.out.println("Please enter a valid amount.");
                                                                                                                                    }
                                                                                }
                                                                    }
             }
             
                                                                                                                                    }
                                                                                                                                    }
                                                                                }
                                                                    }
                                                                                                                                }
                                                                                                                                }
                                                                            }
                                                                }
                                                                                                        }
                                                                                                        }
                                                            }
                                                                            }
                                                                            }
                                                        }
                                                    }
                                                }
                                                                                                                                }
                                                                                                                                }
                                                                }
                                            }
                                                                                                                                }
                                                                                                                    }
                                                                            }
                                                    }
                                        }
                                    }
                                }
             }
                                    }
                            }
                    }
         }
                                }
                        }
                }
      }
                                    }
                                }
                            }
                        }
                    }
   }