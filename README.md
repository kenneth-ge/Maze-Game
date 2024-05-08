# Maze-Game

![image](https://github.com/kenneth-ge/Maze-Game/assets/57784063/d1ab81dd-89ca-40fd-9456-47b918b64acd)


This program was developed by Kenny Ge and Anuj Jain from Edgemont HS and Varun Venkatesh from Dublin HS

It is a procedurally generated maze in which you use WASD + Mouse to navigate around the maze to find the Arctic Fox (that we downloaded through EchoAR’s API). Once you find the fox, the game should end. The game has a multiplayer component, where players can explore the same map together. 

Instructions:
** Note: This may not work on Mac -- this might work on Linux, but it is as of yet untested. This does work on Windows

Prerequisites:
  OpenGL 3.2
  Java 11 or later
  Eclipse Java
  Python 3

How to run singleplayer:
  Windows/Linux: java -jar "Maze Game.jar"
  Mac: java -XstartOnFirstThread -jar "Maze Game.jar"

How to run multiplayer:
  1. Start the server using python server.py or equivalent
    * Note: If you're running the server on another machine, make sure the program is allowed through port 5555
  2. Run each of the clients (at most two) and supply the IP address of the server as an argument. E.g.
    Windows/Linux: java -jar "Maze Game.jar" localhost
    Mac: java -XstartOnFirstThread -jar "Maze Game.jar" localhost

The other player will appear as a red cube. Their position updates 10 times every second. 

Cool technology used:
* OpenGL/LWJGL
* TCP
* Random seed-based recursive algorithm to procedurally generate a maze 
* Java
* Python
