<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>LogicGrid | The Arena Lobby</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #1a1a2e; color: #e0e0e0; margin: 0; padding: 20px; text-align: center; }
        .header { background-color: #16213e; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.5); margin-bottom: 30px; }
        .stats-box { display: inline-block; background-color: #0f3460; padding: 15px 30px; border-radius: 8px; margin: 10px; font-size: 1.2em; }
        .highlight { color: #e94560; font-weight: bold; }
        
        .main-action { margin: 40px 0; }
        .btn-play { background-color: #e94560; color: white; padding: 15px 40px; font-size: 1.5em; text-decoration: none; border-radius: 5px; font-weight: bold; transition: 0.3s; box-shadow: 0 0 15px #e94560; }
        .btn-play:hover { background-color: #c81d49; box-shadow: 0 0 25px #c81d49; }
        
        .btn-home { background-color: #533483; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 20px; display: inline-block; }
        
        table { width: 60%; margin: 0 auto; border-collapse: collapse; background-color: #16213e; border-radius: 10px; overflow: hidden; }
        th, td { padding: 15px; border-bottom: 1px solid #0f3460; }
        th { background-color: #e94560; color: white; }
    </style>
</head>
<body>

    <div class="header">
        <h1>Welcome to the Lobby, <span class="highlight">${sessionScope.loggedInUser.username}</span></h1>
        
        <div class="stats-box">
            Elo Rating: <span class="highlight">${sessionScope.loggedInUser.eloRating}</span>
        </div>
        </div>

    <div class="main-action">
        <a href="matchmake" class="btn-play">FIND MATCH (1v1)</a>
    </div>

    <h2>Recent Battles</h2>
    <table>
        <tr>
            <th>Opponent</th>
            <th>Result</th>
            <th>Elo Change</th>
            <th>Date</th>
        </tr>
        <tr>
            <td colspan="4" style="text-align: center; color: #888; font-style: italic;">
                No matches played yet. Step into the arena to begin your legacy.
            </td>
        </tr>
    </table>

    <a href="${pageContext.request.contextPath}/" class="btn-home">Return to Home</a>

</body>
</html>