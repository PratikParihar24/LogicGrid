<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>LogicGrid | Login</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; text-align: center; background-color: #0A0E17; padding-top: 100px; color: #E5E7EB; margin: 0; background-image: radial-gradient(circle at center, #111827, #0A0E17); height: 100vh;}
        .form-box { background: rgba(17, 24, 39, 0.7); padding: 40px 50px; border-radius: 16px; display: inline-block; box-shadow: 0 10px 30px rgba(0,0,0,0.5); backdrop-filter: blur(10px); border: 1px solid #1F2937; width: 350px;}
        h2 { color: #FFFFFF; font-weight: 300; letter-spacing: 2px; text-transform: uppercase; margin-bottom: 30px; }
        input { display: block; margin: 20px auto; padding: 15px; width: 90%; border-radius: 4px; background: #0A0E17; border: 1px solid #374151; color: #00F0FF; font-size: 1em; outline: none; transition: border 0.3s, box-shadow 0.3s; }
        input:focus { border-color: #00F0FF; box-shadow: 0 0 10px rgba(0, 240, 255, 0.2); }
        button { margin-top: 10px; padding: 15px; width: 100%; background-color: #00F0FF; color: #0A0E17; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; transition: all 0.3s ease; }
        button:hover { background-color: #FFFFFF; box-shadow: 0 0 20px rgba(0, 240, 255, 0.6); }
        .error { color: #FF003C; font-weight: bold; margin-bottom: 20px; background: rgba(255, 0, 60, 0.1); padding: 10px; border-radius: 4px; border: 1px solid rgba(255, 0, 60, 0.3);}
        .back-link { display: block; margin-top: 20px; color: #9CA3AF; text-decoration: none; font-size: 0.9em; transition: color 0.3s;}
        .back-link:hover { color: #00F0FF; }
    </style>
</head>
<body>
    <div class="form-box">
        <h2>Welcome Back</h2>
        <c:if test="${not empty errorMessage}">
            <div class="error">${errorMessage}</div>
        </c:if>
        <form action="login" method="POST">
            <input type="text" name="username" placeholder="Username" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Login</button>
        </form>
        <a href="${pageContext.request.contextPath}/" class="back-link">← Back to Home</a>
    </div>
</body>
</html>