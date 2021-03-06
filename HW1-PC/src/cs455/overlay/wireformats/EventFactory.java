package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import cs455.overlay.node.Node;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Protocol;

public class EventFactory {
	
	//variable to ensure single instance is created for each class
	private static EventFactory single_Instance = null;
	private Node mNode = null;
	
	public EventFactory(Node node) {
		mNode = node;
		single_Instance = this;
	}
	
	//method to create instance of Eventfactory
	public static EventFactory getInstance() {
		//create new method to create the instance
		return single_Instance;
	}
	
	public void receiveNode(Node node) {
		mNode = node;
	}


	public void processMessage(byte[] message, TCPConnection connection) throws IOException{
//		System.out.println("In Process Message");
		//get the type of the message - read first 4 bytes
		byte type = getType(message);
//		System.out.println("type of message is: " + type);
		
		switch(type) {
		case Protocol.SendRegistration:
			//SendRegistration
			Event sendReg = new SendRegistration();
			sendReg.unPackBytes(message);
			mNode.OnEvent(sendReg, connection);
			break;
		case Protocol.RegistryReportsRegistrationStatus:
			//RegistryReoportsRegistrationStatus
			Event reportReg = new RegistryReportsRegistrationStatus();
			reportReg.unPackBytes(message);
			mNode.OnEvent(reportReg, connection);
			break;
		case Protocol.SendDeregistration:
			//SendDeregistration
			Event sendDereg = new SendDeregistration();
			sendDereg.unPackBytes(message);
			mNode.OnEvent(sendDereg, connection);
			break;
		case Protocol.RegistryReportsDeregistrationStatus:
			//RegistryReportsDeregistrationStatus
			Event reportDereg = new RegistryReportsDeregistrationStatus();
			mNode.OnEvent(reportDereg, connection);
			break;
		case Protocol.RegistrySendsNodeManifest:
			//RegistrySendsNodeManifest
			Event sendMan = new RegistrySendsNodeManifest();
			sendMan.unPackBytes(message);
			mNode.OnEvent(sendMan, connection);
			break;
		case Protocol.NodeReportsOverlaySetupStatus:
			//NodeReportsOverlaySetup
			Event overSetStat = new NodeReportsOverlaySetupStatus();
			overSetStat.unPackBytes(message);
			mNode.OnEvent(overSetStat, connection);
			break;
		case Protocol.RegistryRequestsTaskInitiate:
			//RegistryRequestsTaskInitiate
			Event reqTask = new RegistryRequestsTaskInitiate();
			reqTask.unPackBytes(message);
			mNode.OnEvent(reqTask, connection);
			break;
		case Protocol.OverlayNodeSendsData:
			//OverlayNodeSendsData
			Event nodeData = new OverlayNodeSendsData();
			nodeData.unPackBytes(message);
			mNode.OnEvent(nodeData, connection);
			break;
		case Protocol.OverlayNodeReportsTaskFinished:
			//OverlayNodeReportsTaskfinished
			Event taskFinished = new OverlayNodeReportsTaskFinished();
			taskFinished.unPackBytes(message);
			mNode.OnEvent(taskFinished, connection);
			break;
		case Protocol.RegistryRequestsTrafficSummary:
			//RegistryRequestsTrafficSummary
			Event requestTraffic = new RegistryRequestsTrafficSummary();
			requestTraffic.unPackBytes(message);
			mNode.OnEvent(requestTraffic, connection);
			break;
		case Protocol.OverlayNodeReportsTrafficSummary:
			//OverlayNodeReportsTrafficSummary
			Event reportTraffic = new OverlayNodeReportsTrafficSummary();
			reportTraffic.unPackBytes(message);
			mNode.OnEvent(reportTraffic, connection);
			break;
		default :
			System.out.println("Invalid Message Type");
		}

	}
	
	private byte getType(byte[] message) throws IOException{
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		byte numType = din.readByte();
		
		baInputStream.close();
		din.close();
		
		return numType;
	}

}
