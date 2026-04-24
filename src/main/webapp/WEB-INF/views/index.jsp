<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>LogicGrid | Home</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; text-align: center; background-color: #f4f4f4; padding-top: 50px; }
        .container { background: white; padding: 40px; border-radius: 10px; display: inline-block; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        .success { color: #27ae60; font-weight: bold; font-size: 1.2em; margin-bottom: 20px;}
        a.btn { display: inline-block; padding: 10px 20px; background-color: #3498db; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px;}
        a.btn:hover { background-color: #2980b9; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Welcome to LogicGrid</h1>
        
        <div class="success">${successMessage}</div>
        
        <p>The Environment is Live. The Database is Connected.</p>
        
        <a href="register" class="btn">Register New Player</a>
    </div>
</body>
</html>