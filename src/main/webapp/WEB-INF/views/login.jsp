<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>LogicGrid | Login</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; text-align: center; background-color: #2c3e50; padding-top: 50px; color: white;}
        .form-box { background: #34495e; padding: 40px; border-radius: 10px; display: inline-block; box-shadow: 0 4px 8px rgba(0,0,0,0.3); }
        input { display: block; margin: 15px auto; padding: 10px; width: 80%; border-radius: 5px; border: none; }
        button { padding: 10px 20px; background-color: #3498db; color: white; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; }
        button:hover { background-color: #2980b9; }
        .error { color: #e74c3c; font-weight: bold; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="form-box">
        <h2>Return to the Arena</h2>
        
        <div class="error">${errorMessage}</div>
        
        <form action="login" method="POST">
            <input type="text" name="username" placeholder="Gladiator Name" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Login</button>
        </form>
    </div>
</body>
</html>