import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

public class DeviceHealthCheck {
	
	// override this method in your subclass if there is an IP set
	// Unfortunately, fields don't participate in polymorphism the way methods do - you can't override a static field in a subclass 
	// and the field can't be non-static because it's being called from a static context (main()) - so we have to do it this way
	public String MsgMgrIP() { return ""; }
	
	public MessageManagerInterface getMessageManager() {
		
		MessageManagerInterface em = null; 
		String MsgMgrIP = MsgMgrIP();
		
		try {
			if (MsgMgrIP.equals("")) {
				em = new MessageManagerInterface();
			} else {
				em = new MessageManagerInterface(MsgMgrIP);
			}
		} catch (Exception e) {
			System.out.println("Error instantiating message manager interface: "+e);
		}
		
		return em;
	}
	
	
	public void isAlive(int isAliveMsgID, String deviceID) {
		
		Message msg = new Message (isAliveMsgID, deviceID);
		
		try {
			
			MessageManagerInterface em = getMessageManager();
			em.SendMessage(msg);
			
		} catch (Exception e) {
			System.out.println("Error sending isAlive message "+e);
		}
	}
	
	public void start() {
		
		// method to call to start the timer that calls isAlive() on a scheduled basis
	}
}