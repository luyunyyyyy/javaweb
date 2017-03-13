<%--
  Created by IntelliJ IDEA.
  User: LYY
  Date: 2017/3/8
  Time: 14:37
  To change this template use File | Settings | File Templates.
--%><%--
  Created by IntelliJ IDEA.
  User: chenmeiji
  Date: 2016/12/4
  Time: 下午5:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html>
<head>
  <title>My WebSocket</title>
</head>

<body>
Welcome<br/>
<input id="nickname" type="text" />
<button onclick="init()">login</button>
<input id="text" type="text" /><button onclick="send()">Send</button>    <button onclick="closeWebSocket()">Close</button>
<div id="message">
</div>
</body>

<script type="text/javascript">

//    //判断当前浏览器是否支持WebSocket
//    function saveNickname(){
//        nickname = document.getElementById('nickname').value;
//        alert(nickname)
//    }
//    if('WebSocket' in window){
//        saveNickname()
//        websocket = new WebSocket("ws://localhost:8081/javaweb/Chat/"+nickname);
//    }
//    else{
//        alert('Not support websocket')
//    }
//
//    //连接发生错误的回调方法
//    websocket.onerror = function(){
//        setMessageInnerHTML("error");
//    };
//
//    //连接成功建立的回调方法
//    websocket.onopen = function(event){
//        setMessageInnerHTML("open");
//    }
//
//    //接收到消息的回调方法
//    websocket.onmessage = function(){
//        setMessageInnerHTML(event.data);
//    }
//
//    //连接关闭的回调方法
//    websocket.onclose = function(){
//        setMessageInnerHTML("close");
//    }
//
//    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function(){
        websocket.close();
        alert("websocket 已经关闭")
    }
//
//    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML){
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }
//
//    //关闭连接
    function closeWebSocket(){
        websocket.close();
        alert("websocket关闭")
    }
//
//    //发送消息
    function send(){
        var message = document.getElementById('text').value;
        alert("fasong")
        websocket.send(message);
    }
var websocket = null;
var nickname = "Guide";
    function init() {
        alert("init")
        nickname = document.getElementById('nickname').value;
//        output = document.getElementById("output");
        alert("nickname"+nickname)
        testWebSocket();
    }

    function testWebSocket() {
        alert("testwebsocket")
        websocket = new WebSocket("ws://localhost:8081/javaweb/Chat/"+nickname);
        websocket.onopen = function(evt) {
            onOpen(evt)
        };
        websocket.onclose = function(evt) {
            onClose(evt)
        };
        websocket.onmessage = function(evt) {
            onMessage(evt)
        };
        websocket.onerror = function(evt) {
            onError(evt)
        };
    }

    function onOpen(evt) {
        setMessageInnerHTML("open");
    }

    function onClose(evt) {
        setMessageInnerHTML("error");
    }

    function onMessage(evt) {
        setMessageInnerHTML(event.data);
    }

    function onError(evt) {
        setMessageInnerHTML("error");
    }
</script>
</html>