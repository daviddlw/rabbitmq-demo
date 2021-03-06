<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Hello WebSocket</title>
<script src="js/sockjs-0.3.4.js"></script>
<script src="js/stomp.js"></script>
<script type="text/javascript">
	var stompClient = null;

	function setConnected(connected) {
		document.getElementById('connect').disabled = connected;
		document.getElementById('disconnect').disabled = !connected;
		document.getElementById('conversationDiv').style.visibility = connected ? 'visible'
				: 'hidden';
		document.getElementById('response').innerHTML = '';
	}

	function connect() {

		/*var socket = new SockJS('/test');
		stompClient = Stomp.over(socket);*/

		var url = "ws://192.168.9.76:8080/rabbitmq-demo/mq";
		stompClient = Stomp.client(url);
		var headers = {
			login : "guest",
			passcode : "guest"
		};

		stompClient.connect({}, function(frame) {
			setConnected(true);
			console.log('Connected: ' + frame);
			//订阅
			stompClient.subscribe('/topic/greetings', function(greeting) {
				//"amq.topic", "greetings"
				alert(greeting);
				//showGreeting(JSON.parse(greeting.body).name);
			}, {

				"persistent" : true,
				"id" : "mchen"
			});
		});
	}

	function disconnect() {
		stompClient.disconnect();
		setConnected(false);
		console.log("Disconnected");
	}

	function sendName() {

		var name = document.getElementById('name').value;
		stompClient.send("/topic/greetings", {}, JSON.stringify({
			'name' : name
		}));
	}

	function showGreeting(message) {
		var response = document.getElementById('response');
		var p = document.createElement('p');
		p.style.wordWrap = 'break-word';
		p.appendChild(document.createTextNode(message));
		response.appendChild(p);
	}
</script>
</head>
<body>
	<noscript>
		<h2 style="color: #ff0000">Seems your browser doesn't support
			Javascript! Websocket relies on Javascript being enabled. Please
			enable Javascript and reload this page!</h2>
	</noscript>
	<div>
		<div>
			<button id="connect" onclick="connect();">Connect</button>
			<button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
		</div>

		<div id="conversationDiv">
			<label>What is your name?</label><input type="text" id="name" />
			<button id="sendName" onclick="sendName();">Send</button>
			<p id="response"></p>
		</div>

	</div>
</body>
</html>