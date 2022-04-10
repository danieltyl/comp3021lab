package lab6;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;



public class Account {
	public int id;
	public int balance;

	public Account(int id, int balance) {
		this.id = id;
		this.balance = balance;
	}

	// Task1
	// replace the null with a lambda expression
	public static Consumer<Account> add100 = a -> {a.balance += 100;};



	// Task2
	// define checkBound using lowerBound and upperBound
	// We want checkBound to check BOTH lowerBound AND upperBound.
	public static Predicate<Account> lowerBound = a -> a.balance >=0;
	public static Predicate<Account> upperBound = a -> a.balance <=10000;
	public static Predicate<Account> checkBound = lowerBound.and(upperBound);	// Compose two lambdas

	interface AddMaker {
		Consumer<Account> make(int N);
	}

	// Task3
	// replace the null with a lambda expression
	public static AddMaker maker = amount -> (a -> {a.balance += amount;});		// make returns a Consumer<Account> lambda


	// You can assume that all the Account in acconts have positive balances.
	public static int getMaxAccountID(List<Account> accounts) {
		// Task4
		// replace the null with a lambda expression
		// reduce takes an init object and a lambda
		// the lambda receives 2 obj (currentResult and ObjectToCheck)
		// result will change based on the returned Obj
		Account maxOne = accounts.stream().reduce(new Account(0, -100), (currentMaxAcc, givenAcc) -> {
			if (givenAcc.balance > currentMaxAcc.balance)
				return givenAcc;
			else return currentMaxAcc;
		});

		return maxOne.id;
	}


}
