import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

public class DeviceHealthCheck {
    
        Timer   cop_timer                   = null;
        int     alive_msg_id                = 98;
        String  device_id                   = null;
        int     timer_rate                  = 1000;
        MessageManagerInterface msg_mgr_if  = null;
	
	// override this method in your subclass if there is an IP set
	// Unfortunately, fields don't participate in polymorphism the way methods do - you can't override a static field in a subclass 
	// and the field can't be non-static because it's being called from a static context (main()) - so we have to do it this way
	public String MsgMgrIP() { return ""; }
	
	public MessageManagerInterface getMessageManager() {
		// sub classes should instantiate themselves, and then call this method to get the message manager
                // they can't override the messagemanager variable, but they can inherit it
                // this version of the class means that the manager is not instantiated every time you need it
                // but rather saved in the instantiated object
                // a subclass could still override this method and call the superclass method if they wanted to
		String MsgMgrIP = MsgMgrIP();
		
                if (msg_mgr_if == null) {
                    try {
                        if (MsgMgrIP.equals("")) {
                            msg_mgr_if = new MessageManagerInterface();
                        } else {
                            msg_mgr_if = new MessageManagerInterface(MsgMgrIP);
                        }
                    } catch (Exception e) {
                        System.out.println("Error instantiating message manager interface: "+e);
                    }
                }
		return msg_mgr_if;
	}
	
	
	public void isAlive(String deviceID) {
		
            Message msg = new Message (alive_msg_id, deviceID);
		
            try {
                if (msg_mgr_if == null)
                {
                    msg_mgr_if = getMessageManager();
                }
                
                if(msg_mgr_if != null)
                {
                    msg_mgr_if.SendMessage(msg);
                }
                else 
                {
                    System.out.println("Unable to get Message Manager Interface\n");
                }
			
            } catch (Exception e) {
		System.out.println("Error sending isAlive message "+e);
            }
	}
        
        public void setup(String deviceID, int timerRate)
        {
            if (msg_mgr_if == null) {
                 msg_mgr_if = getMessageManager();
            }
            
            if (msg_mgr_if != null) {
                device_id       = deviceID;
                if(timerRate != -1)
                {
                    timer_rate      = timerRate;
                }
            } else {
                System.out.println("Unable to get Message Manager Interface\n");
            }
        }
	
	public void start() {
		
            if (msg_mgr_if == null) {
                msg_mgr_if = getMessageManager();
            }
            
            if (msg_mgr_if != null) {
                cop_timer = new Timer();
                cop_timer.scheduleAtFixedRate(new HealthReportTask(), timer_rate, timer_rate);
            } else {
                System.out.println("Unable to start due because no Message Manager Interface\n");
            }
	}
        
        class HealthReportTask extends TimerTask {
            public void run () {
                isAlive(device_id);
            }   
        }
        
        public void stop() {
            if (cop_timer !=null) {
                cop_timer.cancel();
            }
        }
}