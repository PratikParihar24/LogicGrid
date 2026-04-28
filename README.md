<h1 align="center">LogicGrid Arena 🌌</h1>

<p align="center">
  <strong>A Full-Stack, Real-Time Multiplayer Coding Arena</strong>
</p>

LogicGrid Arena is a competitive, real-time multiplayer platform where developers battle head-to-head in rapid-fire coding challenges. Built with a robust Java/Spring backend and a seamless WebSocket architecture, players matchmake, compete, and climb the global ELO leaderboard via a premium "Deep Space Obsidian" interface.

## ✨ Features
* **Real-Time Matchmaking:** WebSocket-driven queues pair players instantly.
* **Live Combat Engine:** Synchronized game state handling, live scoring, and instant win/loss/forfeit detection.
* **ELO Rating System:** Mathematical ranking system that updates player stats dynamically upon match completion.
* **Persistent Match History:** Every duel, victory, and surrender is logged in a relational database for complete telemetry.
* **Deep Space UI:** A high-contrast, dark-mode frontend featuring glassmorphism, responsive CSS, and dynamic JSTL data binding.
* **Bulletproof Auth:** Secure session management preventing ghost sessions and uncommitted transaction leaks.

---

## 🛠️ Technology Stack
* **Backend:** Java, Spring MVC, Hibernate ORM
* **Real-Time Communication:** Java WebSockets API (`javax.websocket`)
* **Database:** MySQL 8.0+
* **Server:** Apache Tomcat 9
* **Frontend:** HTML5, CSS3, JavaScript, JSP, JSTL

---

## 🚀 Step-by-Step Installation Guide

Follow these steps exactly to get the Arena running on your local machine.

### 1. Prerequisites (Download & Install)
You must have the following installed on your machine before proceeding:
1. **[Java Development Kit (JDK) 8 or 11+](https://www.oracle.com/java/technologies/downloads/)**: Ensure your `JAVA_HOME` environment variable is set.
2. **[Apache Tomcat 9](https://tomcat.apache.org/download-90.cgi)**: Download the Core `.zip` or installer. (Version 9 is recommended for `javax.*` compatibility).
3. **[MySQL Community Server](https://dev.mysql.com/downloads/mysql/)**: Install the database engine. You can also use [XAMPP](https://www.apachefriends.org/index.html) if you prefer a bundled control panel.
4. **IDE:** [Eclipse IDE for Enterprise Java](https://www.eclipse.org/downloads/packages/) or [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/download/).

### 2. Database Setup
LogicGrid relies on a relational MySQL database to track players and matches.

1. Open your MySQL Command Line Interface (CLI) or MySQL Workbench.
2. Run the following command to create the raw database:
   ```sql
   CREATE DATABASE IF NOT EXISTS logicgrid;
   ```
3. *Note: You do not need to manually create the tables. Hibernate's ORM will auto-generate the `users` and `match_history` tables the first time you boot the server.*

### 3. Clone the Repository
Open your terminal and clone the project to your local machine:
   ```bash
   git clone [https://github.com/YOUR_USERNAME/LogicGrid.git](https://github.com/YOUR_USERNAME/LogicGrid.git)
   cd LogicGrid
   ```

### 4. Configure Hibernate (Database Credentials)
The backend needs the keys to your MySQL database. 
1. Navigate to `src/main/resources/hibernate.cfg.xml` (or wherever your config file is located).
2. Update the `connection.username` and `connection.password` properties to match your local MySQL credentials:
   ```xml
   <property name="hibernate.connection.username">root</property>
   <property name="hibernate.connection.password">YOUR_MYSQL_PASSWORD</property>
   <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/logicgrid?useSSL=false&amp;serverTimezone=UTC</property>
   ```

### 5. IDE Setup & Deployment (Eclipse)
1. Open Eclipse and navigate to `File > Import > Existing Maven Projects` (or standard Dynamic Web Project depending on your structure).
2. Select the `LogicGrid` folder.
3. Configure the Server: Go to the "Servers" tab, right-click > `New > Server`. Select **Tomcat v9.0 Server** and point it to where you installed Tomcat.
4. Add the Project: Right-click your new Tomcat server > `Add and Remove...` > Move `LogicGrid` to the right side.
5. Right-click the project, select `Maven > Update Project` (if using Maven) to download dependencies.

### 6. Run the Arena
1. Start your MySQL Service (or start it via the XAMPP Control Panel).
2. Start the Tomcat Server in your IDE.
3. Open your browser and navigate to the local environment:
   ```text
   http://localhost:8080/LogicGrid/
   ```
   *(Note: Adjust `/LogicGrid/` if your context path differs).*

---

## 🔧 Troubleshooting & Common Issues

If you hit a roadblock, consult the **LogicGrid Survival Guide**:

#### 🔴 Error: `JDBCConnectionException: Error calling Driver#connect`
* **The Problem:** Java picked up the phone to call your database, but MySQL didn't answer. (No Dial Tone).
* **The Fix:** Your MySQL service is either asleep or wasn't started. Start MySQL via XAMPP or Windows Services, stop your Tomcat server, and restart Tomcat. You do *not* need to keep the MySQL CLI window open, but the background service must be running.

#### 🔴 Error: `TransientPropertyValueException (User#0)`
* **The Problem:** A "Ghost User" is stuck in the system. The WebSocket tried to save a match to a user ID of `0` because a previous database transaction failed to commit.
* **The Fix:** Log out of the web interface to destroy the broken session. Completely restart Tomcat to flush the cache. Register a brand new user.

#### 🔴 Error: `Port 8080 already in use`
* **The Problem:** Another application (like Skype or an orphaned Tomcat instance) is blocking the server port.
* **The Fix:** Stop the server. If it won't release the port, open your terminal and kill the process:
    * Windows: `netstat -ano | findstr :8080` then `taskkill /PID <PID_NUMBER> /F`
    * Mac/Linux: `lsof -i :8080` then `kill -9 <PID_NUMBER>`

#### 🔴 Error: `404 Not Found` on Cancel/Back buttons
* **The Problem:** The server context path doesn't match the routing.
* **The Fix:** Ensure you are accessing the app through the correct base URL. The UI uses `${pageContext.request.contextPath}` to dynamically route traffic, so do not alter the base directory name after deploying to Tomcat without cleaning the server.

---

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! Feel free to check the issues page to get involved.

## 📝 License
This project is licensed under the MIT License - see the LICENSE file for details.
