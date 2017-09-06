# java-zcs
Java implementation of a simplified strength based learning classifier system

This implementation of ZCS was first written in 2005, and revised/revived in 2017. If you catch any bugs, let me know!

##Background

[Learning classifier systems (LCS)](https://en.wikipedia.org/wiki/Learning_classifier_system) are unsupervised rule based inductive learning systems. The system is comprised of many condition-action rules called classifiers, these classifiers compete to receive credit within the LCS. When the classifier system receives input from its environment the population of known classifiers is scanned for those whose condition part matches the current environment state. A selection mechanism determines which of the candidate classifiers becomes active, and the action of the active classifier is executed. When credit is received from the environment the LCS distributes the credit to reinforce the classifiers that advocatd the action. In addition to reinforcing classifiers the LCS has a discovery component, typically a genetic algorithm, which is used to discover new credit worthy classifiers. ZCS is a simplified strength based classifier system.

The ZCS is documented in
+ [Stewart W. Wilson, ZCS: A Zeroth Level Classifier System, Evolutionary Computation, vol 2:1, pg 1 - 18, 1993](http://www.eskimo.com/~wilson/ps/zcs.pdf).

ZCS has been extended/implemented/used/discussed in

+ [Bull, L. (1999) On using ZCS in a Simulated Continuous Double-Auction Market. In W. Banzhaf, J. Daida, A.E. Eiben, M.H. Garzon, V. Honavar, M. Jakiela & R.E. Smith (eds) GECCO-99: Proceedings of the Genetic and Evolutionary Computation Conference. Morgan Kaufmann, pp83-90.](http://www.cs.bham.ac.uk/~wbl/biblio/gecco1999/GA-806.pdf)
+ [Dave Cliff and Susi Ross, Adding temporary memory to ZCS, Adaptive behavior Volume 3, Issue 2, 1994, Pages: 101 - 150](http://journals.sagepub.com/doi/pdf/10.1177/105971239400300201)
+ [ZCS Redux, Larry Bull and Jacob Hurst, Evolutionary Computation, Vol. 10, Issue 2 - Summer 2002, pp. 185 - 205](http://www.mitpressjournals.org/doi/abs/10.1162/106365602320169848?journalCode=evco)
+ [Applications of Learning Classifier Systems, Larry Bull](http://www.springer.com/gp/book/9783540211099)


In the original publication the ZCS was tested on a 'Woods' problem. The idea was to have an animat controlled by a ZCS pursue food (payoff) by sensing it's position (the environmental condition) and taking a move toward the food (reward). With these problems the LCS could be evaluated as learning mechanism in a range of well understood environments.


## Running the code
There are two problem files, woods1.problem and woods2.problem. The parameters in the problem files are self explanatory with ZCS parameters specified as you would expect (check Wilson's paper for details). Setting debug.zcs to true will output (a lot) of debug information to a text file. moving.avg specifies the moving average window for measuring steps to food, at the end of the runs a CSV file is output with two column for each run, one containing the moving average, and the other the absolute of steps to food. I'll leave calculating the average over the runs to the user :)

A optional woods map file maybe specified, if no value for woods.mapfile is present the woods1 problem is assumed. Alternative map files maybe generated and specified with woods.mapfile as long as the map contains '.' (blank), 'O' (tree or rock depending on what you read), and 'F' (food), and the map is rectangular.
 
 
 ## Results
 ### Woods 1
 ```
.....
.OOF.
.OOO.
.OOO.
.....
```
![alt text](./resources/woods1_it.png "Woods1")

![alt text](./resources/woods1_wilson.png "Woods1")

 ### Woods 7
 ```   
 ...........O.........................OO.........O.........
 .OFO.......F........F.........O.......F.........FO........
...........O........OO.......F............................
............................O......O.........F......O.....
...F.......OFO........OFO..........F.........OO......F....
...OO..............................O..................O...
..........OO.......O........OO....................O.......
.OFO.......F.......OF........F.........OFO........F.......
..................................................O.......
...OO.......O.................O........OO.......O.........
...F.......F.......O.........FO........F........OF....OFO.
...........O.......OF.....................................
..O......................O............O......O.......O....
..F.......F...............F..........FO.......F......OF...
..O.......OO...............O...................O..........
.....................O.....................O..............
...F........OFO......F..........F.........F......OF.......
...OO................O..........OO.......O.......O........
```
## TODO
+ Implement more targeted genetic algorithm, only classifiers that have participated in a significant number of action sets [A] should be eligible for the genetic algorithm.