Kademlia
========

This is an implementation of the Kademlia (http://en.wikipedia.org/wiki/Kademlia) routing protocol and DHT. 
Kademlia origainal Publication: http://link.springer.com/chapter/10.1007/3-540-45748-8_5

Note: This repository is a Netbeans project which you can simply download and import. 

Usage
-----
The Implementation is meant to be self contained and very easy to setup and use. There are several tests (https://github.com/JoshuaKissoon/Kademlia/tree/master/src/kademlia/tests) which demonstrates the usage of the protocol and DHT.

**Configuration**
There is a configuration file available in the kademlia.core package which have all settings used throughout the protocol, all of these settings are described in depth in the Configuration file.

** Creating a Kad Instance **
All of Kademlia's sub-components (DHT, Node, Routing Table, Server, etc) are wrapped within the Kademlia object to simplify the usage of the protocol. To create an instance, simply call:

```Java
Kademlia kad1 = new Kademlia("OwnerName1", new NodeId("ASF45678947584567463"), 12049);
Kademlia kad2 = new Kademlia("OwnerName2", new NodeId(), 12057);  // Random NodeId will be generated
```
Param 1: The Name of the owner of this instance, can be any name.
Param 2: A NodeId for this node
Param 3: The port on which this Kademlia instance will run on.

After this initialization phase, the 2 Kad instances will basically be 2 separate networks. Lets connect them so they'll be in the same network.

** Connecting Nodes **
```Java
kad2.bootstrap(kad1.getNode());   // Bootstrap kad2 by using kad1 as the main network node
```

** Storing Content **

```Java
/* Working example at: https://github.com/JoshuaKissoon/Kademlia/blob/master/src/kademlia/tests/ContentSendingTest.java */
DHTContentImpl c = new DHTContentImpl(kad2.getOwnerId(), "Some Data");  // Create a content
kad2.put(c);    // Put the content on the network

```

** Retrieving Content **
```Java
/* Create a GetParameter object with the parameters of the content to retrieve */
GetParameter gp = new GetParameter(c.getKey());   // Lets look for content by key
gp.setType(DHTContentImpl.TYPE);                  // We also only want content of this type
gp.setOwnerId(c.getOwnerId());                    // And content from this owner

/* Now we call get specifying the GetParameters and the Number of results we want */
List<KadContent> conte = kad2.get(gp, 1);
```

** Saving and Retrieving a Node State **
You may want to save the Node state when your application is shut down and Retrieve the Node state on startup to remove the need of rebuilding the Node State (Routing Table, DHT Content Entries, etc). Lets look at how we do this.
Test: https://github.com/JoshuaKissoon/Kademlia/blob/master/src/kademlia/tests/SaveStateTest.java

```Java
/** 
 * Shutting down the Kad instance.
 * Calling .shutdown() ill automatically store the node state in the location specified in the Configuration file 
 */
kad1.shutdown();

/**
 * Retrieving the Node state
 * This is done by simply building the Kademlia instance by calling .loadFromFile()
 * and passing in the instance Owner name as a parameter
 */
 Kademlia kad1Reloaded = Kademlia.loadFromFile("OwnerName1");
```

For more information on using Kademlia, check the tests at: https://github.com/JoshuaKissoon/Kademlia/tree/master/src/kademlia/tests

Usage in a Real Project
-----------------------
I am currently using this implementation of Kademlia in developing a Distributed Online Social Network Architecture, you can look at that project at https://github.com/JoshuaKissoon/DOSNA for more ideas on using Kademlia.
