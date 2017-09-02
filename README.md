# java-zcs
Java implementation of a simplified strength based learning classifier system

Learning classifier systems (LCS) are learning systems that use reinforcement learning i.e., they are unsupervised learning systems. LCS use a simple rule based production system consisting of many classifiers each having a condition and action part. When the classifier system receives environmental input the database of known classifiers is scanned for those whose condition part matches the current environment state. A selection mechanism selects a classifier to be active and it's action is executed. In interacting in some environment a LCS receives reward. A LCS aims to learn the optimal set of classifiers by reinforcing classifiers as a result of reward received through interaction in some environment. Periodically the LCS uses a genetic algorithm to explore the space of possible classifiers and uses strength (or accuracy) as a measure of fitness. ZCS is a simplified strength based classifier system.
The ZCS is documented in
+ [Stewart W. Wilson, ZCS: A Zeroth Level Classifier System, Evolutionary Computation, vol 2:1, pg 1 - 18, 1993](http://www.eskimo.com/~wilson/ps/zcs.pdf).

ZCS has been extended/implemented/used in

+ [Bull, L. (1999) On using ZCS in a Simulated Continuous Double-Auction Market. In W. Banzhaf, J. Daida, A.E. Eiben, M.H. Garzon, V. Honavar, M. Jakiela & R.E. Smith (eds) GECCO-99: Proceedings of the Genetic and Evolutionary Computation Conference. Morgan Kaufmann, pp83-90.](http://www.cs.bham.ac.uk/~wbl/biblio/gecco1999/GA-806.pdf)
+ [Dave Cliff and Susi Ross, Adding temporary memory to ZCS, Adaptive behavior Volume 3, Issue 2, 1994, Pages: 101 - 150](http://journals.sagepub.com/doi/pdf/10.1177/105971239400300201)
+ [ZCS Redux, Larry Bull and Jacob Hurst, Evolutionary Computation, Vol. 10, Issue 2 - Summer 2002, pp. 185 - 205](http://www.mitpressjournals.org/doi/abs/10.1162/106365602320169848?journalCode=evco)

In the original publication the ZCS was tested on a 'Woods' problem. The idea was to have an animat controlled by a ZCS pursue food (payoff) by sensing it's position (the environmental condition) and taking a move toward the food (reward). With these problems the LCS could be evaluated as learning mechanism in a range of well understood environments.
