CS455 - PC1: Overlay Network
Stephen Porsche

Creates an overlay network of connected client nodes that can send and receive integer packets

===========================================================================

Files include:
 * source code: all *.java files
 * Makefile with 'make clean' and 'make all'
 * This README.txt file

===========================================================================

How to Run:
 * Run the Makefile by using 'make all' to compile the code

 * Next remotely connect into a host machine and start the Registry with the following command: 'java cs455.overlay.node.Registry [enter favorite port number]

 * Next start up to 128 MessagingNodes on any number of remote or local hosts by using the following command: java cs455.overlay.node.MessagingNode [RegistryServer name or IP] [RegistryServer port] (name/IP and port should be the same as the above step

===========================================================================

Commands to run once the program has been started:

Registry Commands:
 * list-messaging-nodes: Prints out a list of all MessagingNodes currently connected to the Registry. Includes information about the nodes ID, IP address, and Port number.

* setup-overlay [number]: Creates the overlay, by sending each MessagingNode a routing table or list of other MessagingNodes to connect to. E.g. setup-overlay 3 sets up the overlay with routing tables of size 3

* list-routing-tables: prints out a list of all the routing tables that have been created.

* start [number of packets to send]: Starts the message passing, and tells each Messaging node how many packets to send

MessagingNode Commands:
 * print-counters-and-diagnostics: Prints information for the specific node for how many Messages it has send, received, relayed, along with the packet sums

 * exit-overlay: Allows the MessagingNode to exit the overlay

===========================================================================

This section contains information about each of the source files

cs455.overlay.node package contains:
 * Registry.java: Sets up the server, and allows the MessagingNodes to register in order to set up the overlay. Also acts as a controller for the program, it sets up the routing tables for each MessagingNode, determines when the Nodes should send packets, and how many packets each node should send.

 * MessaginNode.java: Creates the client nodes. Responds to commands from the Registry. Sends and relays packets to other nodes based on the routing table given by the registry.

 * Node.java: an Interface that both Registry and MessagingNode implement

 * Tuple.java: Creates a Tuple object to help the Registry store information about the Registered Nodes

cs455.overlay.routing package contains:
 * RoutngTable.java: An ArrayList to let the Registry store information about each nodes routing table

 * Route.java: Helps RoutingTable.java contain information about nodes id's IP addresses, and port numbers

cs455.overlay.transport package contains:
 * TCPServerThread.java: Is called by both the Registry, and the MessagingNodes upon startup. Helps to create a server socket on all nodes so that they may receive future connections from other nodes.

 * TCPConnection.java: Creates connections between sockets and allows for communication through those sockets.Contains send message methods to allow for message passing between the Registry and MessagingNodes. Also allows for message passing between MessagingNodes. Has a receiveMessage method to allow nodes to receive the incoming messages

 * TCPCache.java: A Hashmap that contains information for each node about which nodes are connected, and the specific connection that they are using

* TCPSender.java Thread for sending messages

* TCPReceiverThread.java: Thread for receiving messages

cs455.overlay.util package contains:
 * CommandParser.java: Calls certain methods in the Register or MessagingNode. Listens for specific commands .e.g setup-overlay 4. Upon receiving the command, it will call the specified method to perform an action based on the command

 * DisplayStatistics.java: Stores the statistics created by each MessagingNode during packet sending. It is called automatically once each MessagingNode has finished sending its messages, and prints out the information to the console.

cs455.overlay.wireformats package contains:
 * Event.java: An interface that all Message classes implement

 * EventFactory.java: Each time a message is sent, it travels through the EventFactory, which determines the type of message, and where the message should be routed.  It calls the unpackbytes method of the message as it routes it to the appropriate destination.

 * NodeReportsOverlaySetup.java: A message sent from MessagingNode to Registry confirming that it has connected to each node in its specific routing table

 * OverlayNodeReportsTaskFinished.java: A message sent from MessagingNode to Registry confirming that it has finished sending its packets

 * OverlayNodeReportsTrafficSummary.java: A message sent from MessagingNode to Registry containing information about the number of packets sent, received, relayed, and the sums from the packet sending. Sent upon request from the registry

 * OverlayNodeSendsData.java: A message sent from MessagingNode to MessagingNode. Message is a packet containing an integer

 * Protocol.java: A list of all the messaging classes

 * RegistryReportsDeregistrationStatus: A message sent from Registy to MessagingNode confirming exit status. Sent upon request from a MessagingNode.  Sends -1 if exit is not approved, 1 if exit is approved

 * RegistryReportsRegistrationStatus.java: A message sent from Registry to MessagingNode confirming the node has been registered. Sends -1 if Registration failed, otherwise sends an ID the MessagingNode should use from now on. Sent when a MessagingNode attempts to connect to the Registry

 * RegistryRequestsTaskInitiate: A message sent from Registry to all MessagingNodes. Tells each messaging node to start sending packets, and how many packets to send

 * RegistryRequestsTrafficSummary.java: A message sent from Registry to all MessagingNodes.  Requests each MessagingNodes traffic statistics from sending packets.  Sent once the registry receives confirmation that all MessaginNodes have finished sending their packets

 * RegistrySendsNodeManifest.java: A message sent from Registry to all MessagingNodes. Contains the routing table for each node (tells the nodes which other nodes they should connect to), as well as the list of all nodes in the overlay

 * SendDeregistration.java: A message sent from MessagingNode to Registry. Requests permission to leave the overlay

 * SendRegistration.java: A message Sent from MessagingNode to Registry.  Requests permission to join the overlay.
