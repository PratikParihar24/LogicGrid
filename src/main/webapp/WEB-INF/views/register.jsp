<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>LogicGrid | Register</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; text-align: center; background-color: #2c3e50; padding-top: 50px; color: white;}
        .form-box { background: #34495e; padding: 40px; border-radius: 10px; display: inline-block; box-shadow: 0 4px 8px rgba(0,0,0,0.3); }
        input { display: block; margin: 15px auto; padding: 10px; width: 80%; border-radius: 5px; border: none; }
        button { padding: 10px 20px; background-color: #e74c3c; color: white; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; }
        button:hover { background-color: #c0392b; }
    </style>
</head>
<body>
    <div class="form-box">
        <h2>Enter the Arena</h2>
        <form action="register" method="POST">
            <input type="text" name="username" placeholder="Choose your Gladiator Name" required>
            <input type="password" name="password" placeholder="Create a Password" required>
            <button type="submit">Register</button>
        </form>
    </div>
</body>
</html>