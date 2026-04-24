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
        
        <div class="code-display" id="questionBox" style="display:none;">
            // The Question will appear here
        </div>
        
        <div id="answerArea" style="display:none; margin-top: 20px;">
            <input type="text" id="playerAnswer" placeholder="Type the exact output here..." style="padding: 10px; font-size: 1.2em; width: 60%; border-radius: 5px; border: 1px solid #ccc; color: #000;">
            <button onclick="submitAnswer()" style="padding: 10px 20px; font-size: 1.2em; background-color: #00ff00; color: #000; border: none; border-radius: 5px; cursor: pointer; font-weight: bold;">SUBMIT</button>
        	<button id="surrenderBtn" onclick="surrenderMatch()" style="padding: 10px 20px; font-size: 1.2em; background-color: #e74c3c; color: white; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; margin-left: 10px;">SURRENDER</button>
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
            
        };

     // 4. What to do when the Java server sends a message
        socket.onmessage = function(event) {
            const data = JSON.parse(event.data);
            
            // --- SCENARIO A: A New Question is loaded ---
            if (data.type === "MATCH_FOUND") {
                console.log("Match Found/Advanced! Questions loaded.", data.questions);
                
                statusText.innerHTML = "MATCH FOUND! Prepare for Battle.";
                statusText.style.animation = "none";
                statusText.style.color = "#00ff00"; 
                
                const qBox = document.getElementById("questionBox");
                const aArea = document.getElementById("answerArea"); 
                qBox.style.display = "block";
                aArea.style.display = "block"; 
                
                const currentQuestion = data.questions[0];
                qBox.innerHTML = "<h3>" + currentQuestion.title + " (" + currentQuestion.difficulty + ")</h3>" +
                                 "<pre>" + currentQuestion.codeSnippet + "</pre>";
            } 
            
            // --- SCENARIO B: The Game is Over ---
            else if (data.type === "GAME_OVER") {
                console.log("Match Complete: ", data.message);
                
                // Hide the arena board
                document.getElementById("questionBox").style.display = "none";
                document.getElementById("answerArea").style.display = "none";
                
                // Announce the end of the match
                statusText.innerHTML = "⚔️ " + data.message + " ⚔️";
                statusText.style.color = "#f1c40f"; // Turn it Gold
                
                // Change the cancel button to "Return to Lobby"
                const returnBtn = document.querySelector(".btn-cancel");
                returnBtn.innerHTML = "Return to Lobby";
                returnBtn.style.backgroundColor = "#3498db"; // Turn it blue
            }

            // --- SCENARIO C: The Player guessed wrong ---
            else if (data.type === "WRONG_ANSWER") {
                // Shake the input box or alert the user
                alert("❌ " + data.message);
                document.getElementById("playerAnswer").value = ""; // clear their wrong answer
            }
        };

        // 5. What to do if the server crashes or the connection dies
        socket.onclose = function(event) {
            console.log("Browser: Connection to server lost.");
            statusText.innerHTML = "Connection Lost. Please Return to Lobby.";
            statusText.style.color = "#e74c3c"; // Turn text red
            statusText.style.animation = "none"; // Stop pulsing
        };
        
     // 6. Function to submit the answer back to Java
        function submitAnswer() {
            const inputField = document.getElementById("playerAnswer");
            const answerText = inputField.value.trim();
            
            if (answerText === "") {
                alert("You must type an answer first!");
                return;
            }
            
            // Package the answer into a JSON object
            const payload = {
                type: "SUBMIT_ANSWER",
                answer: answerText
            };
            
            // Send it over the WebSocket!
            socket.send(JSON.stringify(payload));
            
            // Clear the box for the next question
            inputField.value = "";
            console.log("Sent answer to server: " + answerText);
        }
     // 7. Function to intentionally forfeit the match
        function surrenderMatch() {
            if(confirm("Are you sure you want to surrender? You will lose Elo!")) {
                const payload = { type: "SURRENDER" };
                socket.send(JSON.stringify(payload));
            }
        }
    </script>
    
</body>
</html>