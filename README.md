# DWS Dev Challenge

--

## Solution Design

•	Created new REST API for a transfer of money between accounts. 
•	This API has following inputs
•	accountFrom id
•	accountTo id
•	amount to transfer between accounts
•	Validate account from id and account to id should be in memory data.
•	Validate amount to transfer should always be a positive number.
•	Check from account balance should be more or equals to amount. If not then throe the exception
•	Withdraw amount from account and deposit in to account.
•	If any error occurred while depositing amount then revert the withdraw transection from account.
•	Used lock per account to avoid deadlock. Used acquire locks in the same order always.
•	After successful transfer is made, a notification sent to both account holders, with a message containing id of the other account and amount transferred.

![Solution Design](https://github.com/sidheshwar-kumbhar/java-spring-boot-challenge/blob/main/amount-tranfer.jpg?raw=true)

--

## Improvements/add, given more time
•	Implement user authentication and authorization
•	Create bank account with more information (e.g., name address, phone number).
•	Add rate limiter for request in specific time
•	Database can be used for account and balance management
•	Use transaction management
•	Use JPA for database operations
•	Can be use Kafka messaging broker for amount transfer and more fault tolerance
--