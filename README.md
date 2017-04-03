# Multiplayer Hangman Game
Console version of the Hangman game with server and multiple clients.
Achieved with multithreading and sockets.

To execute:
1) Run ChatServer.java to start ServerSocket on an unused port.
2) Place absolute path of valid1.xml when prompted
3) Run ChatClient.java to either start or join a game.

Features:
When a user tries to make a guess, a message is sent to all other clients connected
to the game stating whether the user made a correct or incorrect guess.

If user guesses the correct phrase, message is print to all clients stating who won and
all clients terminate.

If a user is out of lives and tries to send a message, server replies
to the corresponding user stating that he/she can only spectate.

![Alt text](cineman_gameplay.png?raw=true "Cineman Gameplay")
