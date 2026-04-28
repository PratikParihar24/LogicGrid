<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>LogicGrid | Home</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; text-align: center; background-color: #0A0E17; color: #E5E7EB; padding-top: 80px; margin: 0; background-image: radial-gradient(circle at top, #111827, #0A0E17); height: 100vh; }
        .container { background: rgba(17, 24, 39, 0.7); padding: 50px; border-radius: 16px; display: inline-block; box-shadow: 0 10px 30px rgba(0,0,0,0.5); backdrop-filter: blur(10px); border: 1px solid #1F2937; }
        h1 { color: #FFFFFF; font-size: 2.5em; margin-bottom: 10px; text-transform: uppercase; letter-spacing: 2px; }
        h1 span { color: #00F0FF; text-shadow: 0 0 10px rgba(0, 240, 255, 0.5); }
        .success { color: #00FF9D; font-weight: bold; font-size: 1.2em; margin-bottom: 20px; text-shadow: 0 0 8px rgba(0, 255, 157, 0.4);}
        .btn { display: inline-block; padding: 12px 30px; color: white; text-decoration: none; border-radius: 4px; margin: 10px; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; transition: all 0.3s ease; border: 1px solid transparent; }
        .btn-blue { background-color: transparent; border-color: #00F0FF; color: #00F0FF; }
        .btn-blue:hover { background-color: #00F0FF; color: #0A0E17; box-shadow: 0 0 20px rgba(0, 240, 255, 0.4); }
        .btn-red { background-color: transparent; border-color: #FF003C; color: #FF003C; }
        .btn-red:hover { background-color: #FF003C; color: #FFFFFF; box-shadow: 0 0 20px rgba(255, 0, 60, 0.4); }
        .btn-green { background-color: transparent; border-color: #00FF9D; color: #00FF9D; }
        .btn-green:hover { background-color: #00FF9D; color: #0A0E17; box-shadow: 0 0 20px rgba(0, 255, 157, 0.4); }
        .elo-text { font-size: 1.2em; color: #9CA3AF; }
        .elo-text b { color: #00F0FF; font-size: 1.5em; }
        hr { border-color: #1F2937; margin: 30px 0; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Logic<span>Grid</span></h1>
        
        <div class="success">${successMessage}</div>
        
        <c:choose>
            <c:when test="${not empty sessionScope.loggedInUser}">
                <h2 style="color: #D1D5DB;">Welcome back, ${sessionScope.loggedInUser.username}</h2>
                <p class="elo-text">Current Elo Rating: <b>${sessionScope.loggedInUser.eloRating}</b></p>
                <hr>
                <a href="lobby" class="btn btn-blue">Enter the Arena</a>
                <a href="logout" class="btn btn-red">Logout</a>
            </c:when>
            
            <c:otherwise>
                <p style="color: #9CA3AF; font-size: 1.2em;">The Arena is live. Prepare for battle.</p>
                <div style="margin-top: 30px;">
                    <a href="login" class="btn btn-blue">Login</a>
                    <a href="register" class="btn btn-green">Register</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>