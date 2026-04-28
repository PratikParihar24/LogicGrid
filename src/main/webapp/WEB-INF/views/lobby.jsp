<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>LogicGrid | Lobby</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0A0E17; color: #E5E7EB; margin: 0; padding: 40px; text-align: center; background-image: radial-gradient(circle at top, #111827, #0A0E17); min-height: 100vh;}
        .header { background: rgba(17, 24, 39, 0.7); padding: 30px; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.3); backdrop-filter: blur(10px); border: 1px solid #1F2937; margin-bottom: 40px; display: flex; justify-content: space-between; align-items: center; max-width: 900px; margin-left: auto; margin-right: auto;}
        h1 { margin: 0; color: #FFFFFF; font-weight: 300; letter-spacing: 1px;}
        .highlight { color: #00F0FF; font-weight: bold; text-shadow: 0 0 10px rgba(0, 240, 255, 0.3); }
        .stats-box { background-color: #0A0E17; padding: 15px 30px; border-radius: 8px; font-size: 1.2em; border: 1px solid #374151; color: #9CA3AF; }
        
        .main-action { margin: 50px 0; }
        .btn-play { background-color: #00F0FF; color: #0A0E17; padding: 20px 60px; font-size: 1.5em; text-decoration: none; border-radius: 50px; font-weight: bold; letter-spacing: 2px; transition: all 0.3s ease; box-shadow: 0 0 20px rgba(0, 240, 255, 0.4); display: inline-block;}
        .btn-play:hover { background-color: #FFFFFF; box-shadow: 0 0 40px rgba(0, 240, 255, 0.8); transform: scale(1.05); }
        
        h2 { color: #9CA3AF; text-transform: uppercase; letter-spacing: 2px; font-size: 1.1em; margin-bottom: 20px;}
        table { width: 100%; max-width: 900px; margin: 0 auto; border-collapse: separate; border-spacing: 0 8px; }
        th { color: #6B7280; font-weight: 600; text-transform: uppercase; font-size: 0.85em; letter-spacing: 1px; padding: 10px 20px; text-align: left;}
        td { padding: 20px; background: rgba(17, 24, 39, 0.5); border-top: 1px solid #1F2937; border-bottom: 1px solid #1F2937; backdrop-filter: blur(5px); text-align: left; }
        td:first-child { border-left: 1px solid #1F2937; border-top-left-radius: 8px; border-bottom-left-radius: 8px; color: #FFFFFF; font-weight: bold;}
        td:last-child { border-right: 1px solid #1F2937; border-top-right-radius: 8px; border-bottom-right-radius: 8px; text-align: right;}
        
        .btn-home { color: #FF003C; padding: 10px 20px; text-decoration: none; border: 1px solid #FF003C; border-radius: 4px; margin-top: 50px; display: inline-block; transition: all 0.3s; background: transparent;}
        .btn-home:hover { background: #FF003C; color: white; box-shadow: 0 0 15px rgba(255, 0, 60, 0.4);}
    </style>
</head>
<body>

    <div class="header">
        <h1>Welcome, <span class="highlight">${sessionScope.loggedInUser.username}</span></h1>
        <div class="stats-box">
            ELO <span class="highlight" style="margin-left: 10px; font-size: 1.2em;">${sessionScope.loggedInUser.eloRating}</span>
        </div>
    </div>

    <div class="main-action">
        <a href="matchmake" class="btn-play">FIND MATCH (1v1)</a>
    </div>

    <h2>Recent Battles</h2>
    <table>
        <thead>
            <tr>
                <th>Opponent</th>
                <th>Result</th>
                <th>Elo Change</th>
                <th style="text-align: right;">Date</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty matchHistory}">
                <tr>
                    <td colspan="4" style="text-align: center; color: #6B7280; font-style: italic; background: transparent; border: none;">
                        No matches played yet. Step into the arena to begin your legacy.
                    </td>
                </tr>
            </c:if>

            <c:forEach var="match" items="${matchHistory}">
                <tr>
                    <td>${match.opponentName}</td>
                    <td>
                        <span style="padding: 5px 10px; border-radius: 4px; font-size: 0.9em; background: rgba(0,0,0,0.3); color: ${match.eloChange >= 0 ? '#00FF9D' : '#FF003C'}; border: 1px solid ${match.eloChange >= 0 ? '#00FF9D' : '#FF003C'};">
                            ${match.result}
                        </span>
                    </td>
                    <td>
                        <b style="color: ${match.eloChange >= 0 ? '#00FF9D' : '#FF003C'}">
                            ${match.eloChange >= 0 ? '+' : ''}${match.eloChange}
                        </b>
                    </td>
                    <td style="font-size: 0.9em; color: #6B7280;">
                        ${match.matchDate}
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <a href="logout" class="btn-home">Logout</a>

</body>
</html>