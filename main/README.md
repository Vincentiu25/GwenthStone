Tarsoaga Vincentiu-Ionut, 
324Ca

# Tema POO 1  - GwentStone

##  Structure

-The first class is an abstarct class which implements a card.

-Minion extends CardClass and implements a minion, having all specific fields
(hp,mana, attack damage etc). It also has a method that implements the special
ability of each card.

-Environment extends CardClass and implements an environment class. It also has
a method that uses the special ability of the card

-Hero class implements a hero , with the specific atributes and special attack.

-Hand class is a class where information for the games are stored, like the hands
of both players, the mana, the heroes for both players and also matrices that 
store the frozen cards, the minions that have attacked and if the hero has attacked. 
Every field of those matrices represents a minion or a hero and is a number between 
0 and 3. If the minion or hero has attacked, the number becomes 1. When the number 
is 3, the player who attacked is at turn again and the minions can attack again. 
Same goes for the frozen status

-Magic number is a class where constants are stored

-GameImplementation is a class that creates the decks for the players

-ActionClass is the class where the game is played. Every command is read, executed
and implemented.


## Implementation

Every time a new game is started, every player gets a deck, that is copied
from the input, in order to be able to be modified. The game table is a 5 x 4 matrix 
of Minion cards, since only minions can be placed on the table. Every time a new
command is read, all error cases are verified and ,if there is an error, it is
printed and the command is not applied. Every time a card is placed on the table, 
an environment card is used or a hero uses the special ability, the players mana
decreases. Every time a card is attacked, it drops in health, until it becomes 0.
When a card has 0 health left, it dies and it is deleted from the table, while
the cards on the right move one place to the left, so that there is no empty place
in the middle of the table, only on the right. When a hero has 0 health, it is 
killed and the game ends, the player that has that hero losing. The statistics for 
wins and games played are implemented using static variables so that they can be
accessed from anywhere.

For most of the commands, there are always two cases: for player one and for 
player two, because every player has two specific rows of the game table. 


## What could have been implemented better?

There is a little bit of repeted code. The modularization could have been better,
but still the project is easy to understand 


## Feedback 

I enjoyed working on the project because it helped me understand java and object
oriented programming. The implementation was not very difficult, although the 
requirements from ocw were a little bit lacking. I had difficulties understanding
how to begin and what exactly I have to implement, but the implementation itself 
and the commands I had to implement were not hard, only long. I would say I have
learned a lot from this homework.




