<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>LogicGrid | The Arena</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0f0f1a; color: white; text-align: center; padding-top: 50px;}
        .arena-box { background-color: #1a1a2e; width: 80%; margin: 0 auto; padding: 30px; border-radius: 10px; border: 2px solid #e94560; box-shadow: 0 0 20px rgba(233, 69, 96, 0.5); }
        .status { color: #f39c12; font-size: 1.5em; font-weight: bold; margin-bottom: 20px; animation: pulse 1.5s infinite; }
        
        @keyframes pulse {
            0% { opacity: 1; }
            50% { opacity: 0.5; }
            100% { opacity: 1; }
        }
        
        .code-display { background-color: #000; color: #0f0; padding: 20px; font-family: monospace; text-align: left; border-radius: 5px; margin: 20px 0; font-size: 1.2em; display: none; }
        .btn-cancel { background-color: #555; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
    </style>
</head>
<body>

    <div class="arena-box">
        <div class="status" id="gameStatus">Searching for Opponent...</div>
        
        <div class="code-display" id="questionBox">
            // The Question will appear here
        </div>
        
        <a href="lobby" class="btn-cancel">Cancel Search</a>
    </div>

<script>
        // 1. Figure out the URL for the WebSocket
        // We use ws:// instead of http:// to tell the browser to keep the connection open
        const host = window.location.host; // Usually localhost:8080
        const contextPath = "${pageContext.request.contextPath}"; // Usually /LogicGrid
        const wsUrl = "ws://" + host + contextPath + "/game";
        
        // 2. Dial the phone!
        const socket = new WebSocket(wsUrl);
        const statusText = document.getElementById("gameStatus");

        // 3. What to do when the connection is successfully established
        socket.onopen = function(event) {
            console.log("Browser: Successfully connected to the Arena WebSocket!");
            
            // Send a test message to the Java server
            socket.send("Hello from the browser! I am ready to fight.");
        };

        // 4. What to do when the Java server sends a message to the browser
        socket.onmessage = function(event) {
            console.log("Browser received: " + event.data);
        };

        // 5. What to do if the server crashes or the connection dies
        socket.onclose = function(event) {
            console.log("Browser: Connection to server lost.");
            statusText.innerHTML = "Connection Lost. Please Return to Lobby.";
            statusText.style.color = "#e74c3c"; // Turn text red
            statusText.style.animation = "none"; // Stop pulsing
        };
    </script>
    
</body>
</html>