# SubscriptionSentry 

### Smart Subscription Tracking and Expense Management System

SubscriptionSentry is a Java-based desktop application developed using Java Swing, JDBC, and MySQL. The system helps users manage and monitor recurring subscriptions such as Netflix, Spotify, GitHub Pro, Google Drive, and other digital services.

This project demonstrates the integration of Java Swing, JDBC, and MySQL along with advanced DBMS concepts such as Triggers, Views, Stored Procedures, Cursors, Aggregate Functions, and Event Scheduling.

---

## Features

### Subscription Management

* Add new subscriptions
* Update subscription details
* Delete subscriptions
* Cancel subscriptions
* View all subscriptions

### Search & Filtering

* Search subscriptions by Service Name
* Filter subscriptions by Category
* Filter subscriptions by Status

### Dashboard Analytics

* Total Subscriptions
* Active Subscriptions
* Cancelled Subscriptions
* Monthly Spending Analysis
* Minimum Cost
* Maximum Cost
* Average Cost
* Upcoming Renewals
* Expired Free Trials

### Audit Logging

The system automatically records:

* Subscription Added
* Subscription Updated
* Subscription Deleted
* Subscription Cancelled

---

## 🛠️ Technology Stack

| Technology | Purpose |
| :--- | :--- |
| Java | Core Programming Language |
| Java Swing | Desktop User Interface (GUI) |
| JDBC | Database Connectivity Layer |
| MySQL | Relational Database Management System |
| SQL | Data Manipulation and Query Processing |
| IntelliJ IDEA | Development Environment |
| MySQL Workbench | Database Administration |

---

## Database Architecture & Schema

### Entity-Relationship Components

* **Tables:** Categories, Subscriptions, SubscriptionAudit, PaymentHistory
* **Advanced Features:** Primary Keys, Foreign Keys, Joins, Aggregate Functions, Views, Triggers, Stored Procedures, Cursors, and Event Scheduler

### Schema Definition

#### 1. Categories

* `category_id` (INT, PK, Auto Increment)
* `category_name` (VARCHAR)

#### 2. Subscriptions

* `subscription_id` (INT, PK, Auto Increment)
* `service_name` (VARCHAR)
* `cost` (DECIMAL)
* `billing_cycle` (VARCHAR)
* `next_renewal_date` (DATE)
* `category_id` (INT, FK)
* `is_free_trial` (BOOLEAN)
* `status` (VARCHAR)

#### 3. SubscriptionAudit

* `audit_id` (INT, PK, Auto Increment)
* `subscription_id` (INT)
* `action_performed` (VARCHAR)
* `log_timestamp` (TIMESTAMP)

#### 4. PaymentHistory

* `payment_id` (INT, PK, Auto Increment)
* `subscription_id` (INT, FK)
* `amount` (DECIMAL)
* `payment_date` (DATE)

---

## Project Design

### Architecture Flow

```text
User
 │
 ▼
Java Swing GUI
 │
 ▼
JDBC Layer
 │
 ▼
MySQL Database
 │
 ├── Tables
 ├── Views
 ├── Triggers
 ├── Stored Procedures
 └── Events
```

### Project Structure

```text
SubscriptionSentry
│
├── src
│   ├── model
│   │   ├── Category.java
│   │   └── Subscription.java
│   │
│   ├── dao
│   │   ├── DBConnection.java
│   │   ├── SubscriptionDAO.java
│   │   ├── AnalyticsDAO.java
│   │   └── AuditDAO.java
│   │
│   ├── ui
│   │   ├── DashboardPanel.java
│   │   ├── SubscriptionPanel.java
│   │   └── AuditLogPanel.java
│   │
│   └── Main.java
│
└── database
    ├── schema.sql
    ├── triggers.sql
    ├── procedures.sql
    └── events.sql
```

---

## 🔧 Setup & Installation

### Prerequisites

* Java JDK 17+
* MySQL Server
* MySQL Workbench
* IntelliJ IDEA

### Database Setup

#### 1. Create Database

```sql
CREATE DATABASE subscriptionsentry_db;
```

#### 2. Import SQL Files

* schema.sql
* triggers.sql
* procedures.sql
* events.sql

#### 3. Configure Database Connection

Update credentials in `DBConnection.java`

```java
private static final String URL =
"jdbc:mysql://localhost:3306/subscriptionsentry_db";

private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

#### 4. Run Application

Run:

```java
Main.java
```

---

## Sample Output

```text
Total Active Subscriptions : 5
Monthly Spending           : ₹1238
Minimum Cost               : ₹119
Maximum Cost               : ₹1300
Average Cost               : ₹512
Expired Trials             : 1
```

##  Future Enhancements

* User Authentication
* Email Notifications
* SMS Alerts
* Cloud Synchronization
* Mobile Application
* AI-Based Expense Recommendations

---

##  Learning Outcomes

* Java Swing GUI Development
* JDBC Connectivity
* Database Design
* SQL Query Writing
* CRUD Operations
* Triggers and Audit Logging
* Stored Procedures
* Views and Aggregate Functions

---

##  Author

**Unnathi U Hegde**

B.E. Computer Science and Engineering

BMS Institute of Technology and Management

---

## 📝 License

This project is developed for educational and academic purposes.
