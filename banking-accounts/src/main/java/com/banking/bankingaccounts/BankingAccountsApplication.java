package com.banking.bankingaccounts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class BankingAccountsApplication {
	static String qry;
	Connection dbCon;
	
	static Statement theStatement;
	BankingAccountsApplication() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection dbCon=DriverManager.getConnection("jdbc:mysql://localhost:3306/banking", "root", "root");
			System.out.println("connected!");
			theStatement = dbCon.createStatement();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		BankingAccountsApplication  ac=new BankingAccountsApplication();
		int choice;
		boolean login=false;
		Scanner sc=new Scanner(System.in);
		boolean exiting=false;
		while(!exiting){
		System.out.println("\nEnter your choice:\n1: New customer:\n2: Customer Login\n3:exit\n");
		choice=sc.nextInt();
		
		switch (choice) {
			case 1:{
				System.out.println("Enter the Details of the  customer:\n");
				System.out.println("Name of the customer: ");
				String name= sc.next();
				System.out.println("Gender of the customer: ");
				String gender= sc.next();
				System.out.println("Age of the customer: ");
				int age= sc.nextInt();
				System.out.println("Pin of the customer: ");
				int pin= sc.nextInt();
				int id= BankingAccountsApplication.newCustomer(name,gender,age,pin);
				System.out.println("New customer id is: "+id);
				System.out.println("\nNow creating an account for that customer..\n");
				System.out.println("Enter the following details:\n");
				System.out.println("Account number: ");
				String accNo=sc.next();
				System.out.println("Account balance: ");
				int accBal=sc.nextInt();
				System.out.println("Account Type: ");
				String accType=sc.next();
				int accId=id;
				BankingAccountsApplication.newAccount(accNo, accId, accBal, accType);
				break;
			}
			case 2:
				System.out.println("Enter customer id and pin to login:\n");
				int cust_id=sc.nextInt();
				int cust_pin=sc.nextInt();
				qry="select Id,custPin,custName from Customer where Id='"+cust_id+"' and custPin='"+cust_pin+"'";
			try {
				ResultSet rs = theStatement.executeQuery(qry);
				rs.next();
					System.out.println(rs.getInt("Id"));
					System.out.println(rs.getInt("custPin"));
					System.out.println(rs.getString("custName"));
					if(cust_id==rs.getInt("Id") && cust_pin==rs.getInt("custPin")){
						System.out.println("login successful for: "+rs.getString("custName")+"!");
						login=true;
					}
					else{
						System.out.println("Wrong credentials!");
					}
				
			} catch (SQLException e) {
				System.out.println("login failed!");
				e.printStackTrace();
			}
			if(login){
				System.out.println("Enter the choice: \n");
				System.out.println("1:Check balance\n2:Transfer Funds\n3:Withdrawal Balance\n4:Change pin\n");
				int desire=sc.nextInt();
				switch (desire) {
					case 1:
						qry="select A.accBalance from Account A,Customer C where A.accId='"+cust_id+"'";
					try {
						ResultSet rs=theStatement.executeQuery(qry);
						rs.next();
						System.out.println("Current balance is: "+rs.getInt("accBalance"));
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
						break;
				
					case 2:
						System.out.println("Enter the account number of the receipent:\n");
						String rec_acc=sc.next();
						System.out.println("Enter the name of the receipent:\n");
						String rec_name=sc.next();
						qry="select A.* from Account A,Customer C where A.accNumber='"+rec_acc+"' and C.custName='"+rec_name+"' and A.accId=C.Id";
					try {
						ResultSet rs=theStatement.executeQuery(qry);
						//rs.next();
						if(rs.next()){
							int temp1=rs.getInt("accId");
							System.out.println("enter the amount to be transferred: ");
							int amt=sc.nextInt();
							qry="select A.accBalance from Account A, Customer C where C.Id='"+cust_id+"' and A.accId=C.Id";
							ResultSet rse=theStatement.executeQuery(qry);
							rse.next();
							int temp=rse.getInt("accBalance");
							if(temp>=amt){
								qry="update Account set accBalance=accBalance-'"+amt+"' where accId='"+cust_id+"'";
								theStatement.executeUpdate(qry);
								System.out.println("Amount has been deducted from sender's account..");
								System.out.println("-------Processing-------");
								qry="update Account set accBalance=accBalance+'"+amt+"' where accId='"+temp1+"'";
								theStatement.executeUpdate(qry);
								System.out.println("Amount has been added to the receipent's account");
							}
							else{
								System.out.println("insufficient fund!");
							}
						}
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
						break;
					case 3:
						System.out.println("Enter the amount to be Withdrawal: ");
						int amt=sc.nextInt();
						System.out.println(cust_id);
						qry="select A.accBalance from Account A,Customer C where A.accId='"+cust_id+"'";
					
					try {
						ResultSet rs1 = theStatement.executeQuery(qry);
						rs1.next();
						if(amt<=rs1.getInt("accBalance")){
							
							qry="update Account set accBalance=accBalance-'"+amt+"' where accId='"+cust_id+"'";
							theStatement.executeUpdate(qry);
							System.out.println("The money has been withdrawn!");
						}
						else{
							System.out.println("Insufficient fund!");
						}
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
					
						break;
					case 4:
						System.out.println("Enter the new pin: ");
						int pin=sc.nextInt();
						qry="update Customer set custPin='"+pin+"' where Id='"+cust_id+"'";
					try {
						theStatement.executeUpdate(qry);
						System.out.println("Pin has been changed successfully!");
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
						
						break;
					}
				
			}
			break;
			case 3:	exiting=true;
					break;
		}
	}
	}
	public static int newCustomer(String name,String gender,int age,int pin){
		int id=0;
		qry="insert into Customer (custName,custGender,custAge,custPin) values ("+ "'"
		+ name + "', '"
		+ gender + "', '" 
		+ age + "', '" 
		+ pin + "')";
		
		try {
			if(theStatement.executeUpdate(qry) > 0)
				System.out.println("Customer added!");
			} catch (SQLException e) {
			System.out.println("Issues with dynamic added user query : " + e.getMessage());
		}
		qry="select Id from Customer where custName='"+name+"' and custPin='"+pin+"'";
		try {
			ResultSet rs=theStatement.executeQuery(qry);
			
			while(rs.next()){
				id=rs.getInt("Id");
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
		return id;
	}
	public static void newAccount(String accNo,int accId,int accBal,String accType){
		qry="insert into Account (accNumber,accId,accBalance,accType) values ("+ "'"
		+ accNo + "', '"
		+ accId + "', '" 
		+ accBal + "', '" 
		+ accType + "')";
		try {
			if(theStatement.executeUpdate(qry) > 0)
				System.out.println("Account created!");
			} catch (SQLException e) {
			System.out.println("Issues with dynamic added user query : " + e.getMessage());
		}
		
	}

}
