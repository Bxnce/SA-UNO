# A SCALA UNO Project
Only playable on one computer.<br/>
TUI and GUI are both working and running synchron.
 
## TO START THE PROJECT, CLONE THE MASTER, START SBT AND EXECUTE 'Docker/publishLocal' AFTERWARDS EXECUTE THE DOCKER COMPOSE!

Program  | Badge
--------|--------
GitHub-Actions | [![Scala CI](https://github.com/Bxnce/uno/actions/workflows/scala.yml/badge.svg?branch=main)](https://github.com/Bxnce/uno/actions/workflows/scala.yml)
CoverAlls | [![Coverage Status](https://coveralls.io/repos/github/Bxnce/SA-UNO/badge.svg?branch=main)](https://coveralls.io/github/Bxnce/SA-UNO?branch=main)
Codecov | [![codecov](https://codecov.io/gh/Bxnce/SA-UNO/branch/main/graph/badge.svg?token=FLLPR81E5I)](https://codecov.io/gh/Bxnce/SA-UNO) 

![Alt text](https://codecov.io/gh/Bxnce/SA-UNO/branch/main/graphs/sunburst.svg?token=FLLPR81E5I "codecov sunburst graph")

 

 

  
### TUI commands and example
Command | Description 
--------|--------
help      / h          | prints all commands
exit      / q          | quits the program
new       /            | creates a new game
take      / +          | takes a card
place "i" / - "i"      | places card at index "i"
next      / n          | switches in the next state
undo      / z          | undos the last command
redo      / y          | redos the last undo command


```sh
Welcome to UNO! type help for all commands
>>>  help

              all commands for UNO:
              - help | h                       : shows all commands
              - exit | q                       : leaves the game
              - new  |                         : creates a new game
              - take | +                       : adds a random card from the stack to the player
              - place <index> | - <index>      : places the card at <index>
              - next | n                       : goes to the next state   
              - undo | z                       : undo the last command
              - redo | y                       : redo, undos the last undo
>>>  new
>>>  Name1:    Bence
>>>  Name2:    Timo
>>>  new
Name1:    Bence
Name2:    Timo
Bence
+--+
| 7|
+--+

+--+
|G1|
+--+

+--+
| 7|
+--+
Timo

>>>  next
Bence
+--+--+--+--+--+--+--+
|R0|R2|B4|Y4|B8|G6|R8|
+--+--+--+--+--+--+--+

+--+
|G1|
+--+

+--+
| 7|
+--+
Timo
>>>  place 6
Bence
+--+
| 6|
+--+

+--+
|G6|
+--+

+--+
| 7|
+--+
Timo
>>>  undo
Bence
+--+--+--+--+--+--+--+
|R0|R2|B4|Y4|B8|G6|R8|
+--+--+--+--+--+--+--+

+--+
|G1|
+--+

+--+
| 7|
+--+
Timo
>>>  take
Bence
+--+--+--+--+--+--+--+--+
|R0|R2|B4|Y4|B8|G6|R8|G7|
+--+--+--+--+--+--+--+--+

+--+
|G1|
+--+

+--+
| 7|
+--+
Timo
```

### GUI - simple and functional 

![GUI_create](https://media.giphy.com/media/BuomVNSdoNSzO4I00e/giphy.gif)<br/>
![GUI](https://media.giphy.com/media/WHAiEh2GOSL35SZAEo/giphy.gif)
<br/>
<br/>
### Authors: 
[@Bence Stuhlmann](https://github.com/Bxnce "Bences GitHub") <br/>
[@Timo Haas](https://github.com/haasentimo "Timos GitHub") 

