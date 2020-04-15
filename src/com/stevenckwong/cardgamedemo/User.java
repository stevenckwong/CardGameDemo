package com.stevenckwong.cardgamedemo;

public class User {
	
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private int accountBalance;
	
	public User() {
		this.firstName = "uninitialised";
		this.lastName = "uninitialised";
		this.email = "uninitialised";
		this.password = "uninitialised";
		this.accountBalance = 0;
	}
	
	public User(String fName, String lName, String e, String p, int b) {
		this.firstName = fName;
		this.lastName = lName;
		this.email = e;
		this.password = p;
		this.accountBalance = b;
	
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(int balance) {
		this.accountBalance = balance;
	}
	
	public void addToAccount(int amount) {
		this.accountBalance+=amount;
	}
	
	public void subtractFromAccount(int amount) {
		this.accountBalance-=amount;
	}

}
