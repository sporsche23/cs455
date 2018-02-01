package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Protocol;

public class EventFactory {
	
	//variable to ensure single instance is created for each class
	private static EventFactory single_Instance = null;
	
	private EventFactory(Node node) {}
	
	//method to create instance of Eventfactory
	public static synchronized EventFactory getInstance() {
		//create new method to create the instance

		return single_Instance;
	}
	
	public static synchronized void makeEventFactory(Node node) {
		if (single_Instance == null) {
			single_Instance = new EventFactory(node);
		}
		getInstance();
	}

	public void processMessage(byte[] message) throws IOException{
		//get the type of the message - read first 4 bytes
		byte type = getType(message);
		
		switch(type) {
		case Protocol.SendRegistration:
			//SendRegistration
			Event sendReg = new SendRegistration();
			node.OnEvent(sendReg);
			break;
		case Protocol.RegistryReportsRegistrationStatus:
			//RegistryReoportsRegistrationStatus
			Event reportReg = new RegistryReportsRegistrationStatus();
			node.OnEvent(sendReg);
			break;
		case Protocol.SendDeregistration:
			//SendDeregistration
			Event sendDereg = new SendDeregistration();
			node.OnEvent(sendDereg);
			break;
		case Protocol.RegistryReportsDeregistration:
			//RegistryReportsDeregistrationStatus
			Event reportDereg = new RegistryReportsDeregistration();
			node.OnEvent(reportDereg);
			break;
		case Protocol.RegistrySendsNodeManifest:
			//RegistrySendsNodeManifest
			Event sendMan = new RegistrySendsNodeManifest();
			node.OnEvent(sendMan);
			break;
		case Protocol.NodeReportsOverlaySetupStatus:
			//NodeReportsOverlaySetup
			Event OverSetStat = new NodeReportsOverlaySetupStatus();
			node.OnEvent(OverSetStat);
			break;
		case Protocol.RegistryRequestsTaskInitiate:
			//RegistryRequestsTaskInitiate
			Event reqTask = new RegistryRequestsTaskInitiate();
			node.OnEvent(reqTask);
			break;
		case Protocol.OverlayNodeSendsData:
			//OverlayNodeSendsData
			Event nodeData = new OverlayNodeSendsData();
			node.OnEvent(nodeData);
			break;
		case Protocol.OverlayNodeReportsTaskFinished:
			//OverlayNodeReportsTaskfinished
			Event taskFinished = new OverlayNodeReportsTaskFinished();
			node.OnEvent(taskFinished);
			break;
		case Protocol.RegistryRequestsTrafficSummary:
			//RegistryRequestsTrafficSummary
			Event requestTraffic = new RegistryRequestsTrafficSummary();
			node.OnEvent(requestTraffic);
			break;
		case Protocol.OverlayNodeReportsTrafficSummary:
			//OverlayNodeReportsTrafficSummary
			Event reportTraffic = new OverlayNodeReportsTrafficSummary();
			node.OnEvent(reportTraffic);
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
