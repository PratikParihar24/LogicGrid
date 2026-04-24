<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>LogicGrid | Home</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; text-align: center; background-color: #f4f4f4; padding-top: 50px; }
        .container { background: white; padding: 40px; border-radius: 10px; display: inline-block; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        .success { color: #27ae60; font-weight: bold; font-size: 1.2em; margin-bottom: 20px;}
        .btn { display: inline-block; padding: 10px 20px; color: white; text-decoration: none; border-radius: 5px; margin: 10px;}
        .btn-blue { background-color: #3498db; } .btn-blue:hover { background-color: #2980b9; }
        .btn-red { background-color: #e74c3c; } .btn-red:hover { background-color: #c0392b; }
        .btn-green { background-color: #2ecc71; } .btn-green:hover { background-color: #27ae60; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Welcome to LogicGrid</h1>
        
        <div class="success">${successMessage}</div>
        
        <c:choose>
            <c:when test="${not empty sessionScope.loggedInUser}">
                <h2>Welcome back, ${sessionScope.loggedInUser.username}!</h2>
                <p>Current Elo Rating: <b>${sessionScope.loggedInUser.eloRating}</b></p>
                <hr>
                <a href="lobby" class="btn btn-blue">Enter the Arena</a>
                <a href="logout" class="btn btn-red">Logout</a>
            </c:when>
            
            <c:otherwise>
                <p>The Environment is Live. Ready to build the Arena.</p>
                <a href="login" class="btn btn-blue">Login</a>
                <a href="register" class="btn btn-green">Register</a>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>