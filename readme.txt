This project is designed to simulate a World of Warcraft game. 

It consists of two packages, warriors, which keeps the basic information about a warrior, and different warrior types are all derived from the same Warrior superclass, facilitating the game logic implementation. Furthermore, it models two throwable classes, cheer and death, to interrupt the attack routine when needed and displays the required message.

The second package, world, keeps the information necessary for game execution. It handles clock ticks, headquarter maintenance and city maintenance. It also holds the main function of the program to take in inputs and run the game.

—————————

To compile, please run “javac */*.java” under ./src

To run after compilation, please run “java world/Main” under ./src

