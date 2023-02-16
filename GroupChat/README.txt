# These instructions are for running GroupChat application written in java

# Compile the client and server programs
% javac GroupChatServer.java
% javac GroupChatClient.java

# First, run the server program with a port number (say 50000)
% java GroupChatServer 50000

# Then, run the client program with the server's whereabouts and the name of the client
% java GroupChatClient localhost 50000 <name>

# Type messages at the client and see them displayed at the server and the other clients on the server

# To end the client, type Ctrl-D (on Windows Ctrl-Z followed by # return)
# This is referred to as EOF (end of file), meaning end of input