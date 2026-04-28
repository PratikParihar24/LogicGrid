<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>LogicGrid | The Arena</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #05070A; color: #E5E7EB; text-align: center; padding-top: 40px; margin: 0;}
        .arena-box { background: rgba(17, 24, 39, 0.8); max-width: 800px; margin: 0 auto; padding: 40px; border-radius: 16px; border: 1px solid #00F0FF; box-shadow: 0 0 30px rgba(0, 240, 255, 0.15); backdrop-filter: blur(10px); position: relative; overflow: hidden;}
        .arena-box::before { content: ""; position: absolute; top: 0; left: 0; width: 100%; height: 4px; background: linear-gradient(90deg, #00F0FF, #7000FF, #00F0FF); background-size: 200% 100%; animation: gradientMove 3s linear infinite;}
        
        @keyframes gradientMove { 0% {background-position: 100% 0;} 100% {background-position: -100% 0;} }

        .status { color: #F2A900; font-size: 1.5em; font-weight: 300; letter-spacing: 2px; margin-bottom: 30px; animation: pulse 2s infinite; text-transform: uppercase;}
        @keyframes pulse { 0% { opacity: 1; text-shadow: 0 0 10px currentColor; } 50% { opacity: 0.4; text-shadow: none; } 100% { opacity: 1; text-shadow: 0 0 10px currentColor; } }
        
        #countdownBox { font-size: 6em; color: #00F0FF; text-align: center; margin: 50px 0; text-shadow: 0 0 30px #00F0FF; animation: pop 1s infinite; font-weight: bold;}
        @keyframes pop { 0% { transform: scale(0.8); opacity: 0; } 50% { transform: scale(1.1); opacity: 1; } 100% { transform: scale(1); opacity: 0; } }

        .code-display { background-color: #0A0E17; color: #00FF9D; padding: 30px; font-family: 'Courier New', Courier, monospace; text-align: left; border-radius: 8px; margin: 20px 0; font-size: 1.1em; border: 1px solid #1F2937; box-shadow: inset 0 0 20px rgba(0,0,0,0.8); border-left: 4px solid #00FF9D;}
        .code-display h3 { margin-top: 0; color: #FFFFFF; font-family: 'Segoe UI', sans-serif; font-weight: 400; text-transform: uppercase; font-size: 0.9em; letter-spacing: 1px; margin-bottom: 20px; border-bottom: 1px solid #1F2937; padding-bottom: 10px;}
        
        #playerAnswer { padding: 15px 20px; font-size: 1.2em; width: 65%; border-radius: 4px; border: 1px solid #374151; background-color: #0A0E17; color: #FFFFFF; outline: none; transition: border 0.3s, box-shadow 0.3s; font-family: monospace; }
        #playerAnswer:focus { border-color: #00F0FF; box-shadow: 0 0 15px rgba(0, 240, 255, 0.3); }
        
        .action-btn { padding: 15px 30px; font-size: 1.1em; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; transition: all 0.3s; vertical-align: bottom;}
        .submit-btn { background-color: #00F0FF; color: #0A0E17; margin-left: 10px;}
        .submit-btn:hover { background-color: #FFFFFF; box-shadow: 0 0 20px rgba(0, 240, 255, 0.6); }
        .surrender-btn { background-color: transparent; border: 1px solid #FF003C; color: #FF003C; margin-top: 20px; display: block; margin-left: auto; margin-right: auto; font-size: 0.9em; padding: 10px 20px;}
        .surrender-btn:hover { background-color: #FF003C; color: white; box-shadow: 0 0 15px rgba(255, 0, 60, 0.5); }
        
        .btn-cancel { background-color: transparent; border: 1px solid #6B7280; color: #9CA3AF; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block; margin-top: 30px; transition: all 0.3s; text-transform: uppercase; font-size: 0.9em; letter-spacing: 1px;}
        .btn-cancel:hover { border-color: #FFFFFF; color: #FFFFFF; }
    </style>
</head>
<body>

    <div class="arena-box">
        <div class="status" id="gameStatus">Scanning Network for Opponent...</div>
        
        <div id="toastNotification" style="display:none; padding: 15px; margin: 15px auto; width: 80%; border-radius: 4px; font-weight: bold; text-align: center; font-size: 1.1em; transition: opacity 0.3s; border: 1px solid rgba(255,255,255,0.2);"></div>

    	<div id="countdownBox" style="display:none;">3</div>
        
        <div class="code-display" id="questionBox" style="display:none;">
            // The Question will appear here
        </div>
        
        <div id="answerArea" style="display:none; margin-top: 30px;">
            <div style="display: flex; justify-content: center; align-items: center;">
                <input type="text" id="playerAnswer" placeholder="Execute output string...">
                <button onclick="submitAnswer()" class="action-btn submit-btn">EXECUTE</button>
            </div>
        	<button id="surrenderBtn" onclick="surrenderMatch()" class="action-btn surrender-btn">ABORT / SURRENDER</button>
        </div>
        
        <a href="lobby" class="btn-cancel">Cancel Search</a>
    </div>

<script>
        const host = window.location.host; 
        const contextPath = "${pageContext.request.contextPath}"; 
        const wsUrl = "ws://" + host + contextPath + "/game";
        
        const socket = new WebSocket(wsUrl);
        const statusText = document.getElementById("gameStatus");

        socket.onopen = function(event) {
            console.log("Browser: Successfully connected to the Arena WebSocket!");
        };

        socket.onmessage = function(event) {
            const data = JSON.parse(event.data);
            
            if (data.type === "MATCH_FOUND") {
                const cancelBtn = document.querySelector(".btn-cancel");
                if(cancelBtn) cancelBtn.style.display = "none";
                
                statusText.innerHTML = "TARGET LOCKED. PREPARE FOR EXECUTION.";
                statusText.style.color = "#00F0FF";
                
                const cBox = document.getElementById("countdownBox");
                const qBox = document.getElementById("questionBox");
                const aArea = document.getElementById("answerArea");
                
                qBox.style.display = "none";
                aArea.style.display = "none";
                cBox.style.display = "block";
                
                let count = 3;
                cBox.innerHTML = count;
                
                let timer = setInterval(() => {
                    count--;
                    if(count > 0) {
                        cBox.innerHTML = count;
                    } else {
                        clearInterval(timer);
                        cBox.style.display = "none";
                        
                        qBox.style.display = "block";
                        aArea.style.display = "block"; 
                        
                        const currentQuestion = data.questions[0];
                        qBox.innerHTML = "<h3>" + currentQuestion.title + " (Class: " + currentQuestion.difficulty + ")</h3>" +
                                         "<pre>" + currentQuestion.codeSnippet + "</pre>";
                    }
                }, 1000);
            } 
            
            else if (data.type === "OPPONENT_SCORED") {
                showToast("⚠ " + data.message, "#F2A900");
            }
            
            else if (data.type === "GAME_OVER") {
                document.getElementById("questionBox").style.display = "none";
                document.getElementById("answerArea").style.display = "none";
                document.getElementById("countdownBox").style.display = "none";
                
                statusText.innerHTML = "TERMINAL: " + data.message;
                statusText.style.color = "#00FF9D";
                statusText.style.animation = "none";
                
                const returnBtn = document.querySelector(".btn-cancel");
                if(returnBtn) {
                    returnBtn.innerHTML = "Return to Base";
                    returnBtn.style.color = "#00F0FF"; 
                    returnBtn.style.borderColor = "#00F0FF";
                    returnBtn.style.display = "inline-block"; 
                }
            }

            else if (data.type === "WRONG_ANSWER") {
                showToast("✖ " + data.message, "#FF003C");
                document.getElementById("playerAnswer").value = ""; 
            }
        };
        
        socket.onclose = function(event) {
            console.log("Browser: Connection to server lost.");
            statusText.innerHTML = "CONNECTION SEVERED. RETURN TO BASE.";
            statusText.style.color = "#FF003C";
            statusText.style.animation = "none";
        };
        
        function submitAnswer() {
            const inputField = document.getElementById("playerAnswer");
            const answerText = inputField.value.trim();
            
            if (answerText === "") {
                alert("You must type an answer first!");
                return;
            }
            
            const payload = {
                type: "SUBMIT_ANSWER",
                answer: answerText
            };
            
            socket.send(JSON.stringify(payload));
            inputField.value = "";
        }
        
        function surrenderMatch() {
            if(confirm("CRITICAL WARNING: Are you sure you want to surrender? You will lose Elo!")) {
                const payload = { type: "SURRENDER" };
                socket.send(JSON.stringify(payload));
            }
        }
     
        function showToast(message, color) {
            const toast = document.getElementById("toastNotification");
            toast.innerHTML = message;
            toast.style.backgroundColor = color === "#F2A900" ? "rgba(242, 169, 0, 0.2)" : (color === "#FF003C" ? "rgba(255, 0, 60, 0.2)" : "rgba(0, 255, 157, 0.2)");
            toast.style.color = color;
            toast.style.borderColor = color;
            toast.style.display = "block";
            toast.style.opacity = "1";
            
            setTimeout(() => { toast.style.opacity = "0"; }, 3000);
            setTimeout(() => { toast.style.display = "none"; }, 3300);
        }
    </script>
</body>
</html>