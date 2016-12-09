# Readme file
## Intention
Ce projet met en oeuvre un Simple Cluster, avec lequel il est possible de lancer plusieurs membres.

Le package `com.akio.vertx.cluster` 
 contient la classe SimpleCluster, qui possède des méthodes staiques permettant de creer les membres du cluster,
 et de le démmarer.
 
 ## Créer les membre du cluster
 ### Ajout import static
 import static SimpleHazelcastCluster.*;
 
 ### Création de membre du cluster
```
 // create a local member of the cluster. It is possible to launch multiple time the same Verticle instance
 final Member member1 = newLocalMember(Server.class.getName(), Server.class.getName());

 // create one remote member, with an other guy of the Feature Team
 final Member member2 = newMember("10.34.1.23", false, Server.class.getName());
 
 // create another local member
 final Member member3 = newLocalMember("uicdev.akio.fr",Server.class.getName(), Server.class.getName());
 ``` 
 
