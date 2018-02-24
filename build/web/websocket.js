window.onload = init;
var socket = new WebSocket("ws://localhost:8080/JavaWebsocket/actions");
socket.onmessage = onMessage;
var names = ['Jack Sparrow', 'Hector Barbossa', 'Mat CauthonMat', 'Willy Turner'];
username = names[Math.floor(Math.random() * names.length)];
function onMessage(event) {
    var chat = JSON.parse(event.data);
    if (chat.action === "add") {
        printChatElement(chat);
    }
    if (chat.action === "remove") {
        document.getElementById(chat.id).remove();
        //chat.parentNode.removeChild(chat);
    }
    if (chat.action === "toggle") {
        var node = document.getElementById(chat.id);
        var statusText = node.children[2];
        if (chat.status === "On") {
            statusText.innerHTML = "Status: " + chat.status + " (<a href=\"#\" OnClick=toggleChat(" + chat.id + ")>Turn off</a>)";
        } else if (chat.status === "Off") {
            statusText.innerHTML = "Status: " + chat.status + " (<a href=\"#\" OnClick=toggleChat(" + chat.id + ")>Turn on</a>)";
        }
    }
}

function addChat(message) {
    var action = {
        action: "add",
        message: message,
        timestamp: new Date(),
        username: username
    };
    socket.send(JSON.stringify(action));
}

function removeChat(element) {
    var id = element;
    var ChatAction = {
        action: "remove",
        id: id
    };
    socket.send(JSON.stringify(ChatAction));
}

function toggleChat(element) {
    var id = element;
    var ChatAction = {
        action: "toggle",
        id: id
    };
    socket.send(JSON.stringify(ChatAction));
}
function add_menu_actions(chat_id) {
    let html = '<button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">';
    html += '<span class="glyphicon glyphicon-chevron-down"></span>';
    html += '</button>';
    html += '<ul class="dropdown-menu slidedown">';
    html += '</ul>';
    return html;
}
function printChatElement(chat) {
    let html = ' ';
    //html += add_menu_actions(chat.id);
    if (chat.username == username) {
        html += '<li class="right clearfix"><span class="chat-img pull-right">';
        html += '<img src="http://placehold.it/50/FA6F57/fff&text=' + chat.username.substring(0, 1) + '" alt="User Avatar" class="img-circle" />';
        html += '</span>';
        html += '<div class="chat-body clearfix">';
        html += '<div class="header">';
        html += '<small class=" text-muted"><span class="glyphicon glyphicon-time"></span>' + moment(chat.timestamp).fromNow() + '</small>';
        html += '<strong class="pull-right primary-font">' + chat.username + '</strong>';
        html += '</div>';
        html += '<p>' + chat.message + '</p>';
        html += '</div>';
        html += '</li>';

    } else {
        html += '<li class="left clearfix"><span class="chat-img pull-left">';
        html += '<img src="http://placehold.it/50/55C1E7/fff&text=' + chat.username.substring(0, 1) + '" alt="' + chat.username + '" class="img-circle" />';
        html += '</span>';
        html += '<div class="chat-body clearfix">';
        html += '<div class="header">';
        html += '<strong class="primary-font">' + chat.username + '</strong> <small class="pull-right text-muted">';
        html += '<span class="glyphicon glyphicon-time"></span>' + moment(chat.timestamp).fromNow() + '</small>';
        html += '</div>';
        html += '<p>' + chat.message + ' </p>';
        html += '</div>';
        html += '</li>';
    }
    $(".chat").append(html);
}

function showForm() {
    document.getElementById("addChatForm").style.display = '';
}

function hideForm() {
    document.getElementById("addChatForm").style.display = "none";
}

function formSubmit() {
    var message = $('#btn-input').val();
    addChat(message);
    $('#btn-input').val('');
}

function init() {

}