//package a3;

/******************************************************************************************************************
* File:MaintenanceMonitor.java Edited from ECSMonitor.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 March 2009 - Initial rewrite of original assignment 3 (ajl).
*
* Description:
*
* This class monitors the environmental control systems that control museum temperature and humidity. In addition to
* monitoring the temperature and humidity, the ECSMonitor also allows a user to set the humidity and temperature
* ranges to be maintained. If temperatures exceed those limits over/under alarm indicators are triggered.
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
* Internal Methods:
*	static private void Heater(MessageManagerInterface ei, boolean ON )
*	static private void Chiller(MessageManagerInterface ei, boolean ON )
*	static private void Humidifier(MessageManagerInterface ei, boolean ON )
*	static private void Dehumidifier(MessageManagerInterface ei, boolean ON )
*
******************************************************************************************************************/
import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;


class MaintenanceMonitor extends Thread
{
	private MessageManagerInterface em = null;	// Interface object to the message manager
	private String MsgMgrIP = null;				// Message Manager IP address
	boolean Registered = true;					// Signifies that this class is registered with an message manager.
	MessageWindow mw = null;					// This is the message window
        
        static int AliveTimeOut  = 10000; //ms
        
        
        public static final String TEMP_SENSOR = "Temperature Sensor";
        public static final String TEMP_SENSOR_DESC = "The temperature reading";
        public static final String HUM_SENSOR = "Humidity Sensor";
        public static final String HUM_SENSOR_DESC = "The humidity reading";
        
        //private deviceMonitor deviceList;
        //private deviceMonitor deviceD;
        //private static HashMap deviceMap1 = new HashMap();
        public static Map<Integer, deviceRecord> deviceMap1 = new HashMap<Integer, deviceRecord>();
        
        MaintenanceView maintView = null;
        
        class DeviceDesc
        {
            int         DeviceID = 0;
            String      DeviceName = "Unknown";
            Boolean     DeviceActive = false;
            long        SystemTimeAtActive = 0;
            long        LastUpdateTime = 0;
            
            DeviceDesc()
            {
            }
            DeviceDesc(int devID, String devName)
            {
                DeviceID = devID;
                DeviceName = devName;
            }
        }
        ArrayList<DeviceDesc> DeviceList  = new ArrayList<DeviceDesc>();
        
        
	public MaintenanceMonitor()
	{
		// message manager is on the local system
		try
		{
			// Here we create an message manager interface object. This assumes
			// that the message manager is on the local machine
			em = new MessageManagerInterface();
		}
		catch (Exception e)
		{
			System.out.println("MaintenanceMonitor::Error instantiating message manager interface: " + e);
			Registered = false;
		} // catch
	} //Constructor

	public MaintenanceMonitor( String MsgIpAddress )
	{
		// message manager is not on the local system
		MsgMgrIP = MsgIpAddress;
		try
		{
			// Here we create an message manager interface object. This assumes
			// that the message manager is NOT on the local machine
			em = new MessageManagerInterface( MsgMgrIP );
		}
		catch (Exception e)
		{
			System.out.println("MaintenanceMonitor::Error instantiating message manager interface: " + e);
			Registered = false;
		} // catch
	} // Constructor

	public void run()
	{
		Message Msg = null;				// Message object
		MessageQueue eq = null;			// Message Queue
		int MsgId = 0;					// User specified message ID
		int	Delay = 1000;				// The loop delay (1 second)
		boolean Done = false;			// Loop termination flag               
                
              
		if (em != null)
		{
			// Now we create the ECS status and message panel
			// Note that we set up two indicators that are initially yellow. This is
			// because we do not know if the temperature/humidity is high/low.
			// This panel is placed in the upper left hand corner and the status
			// indicators are placed directly to the right, one on top of the other
			mw = new MessageWindow("Maintenance Monitoring Console", 0.6f, 0);
			mw.WriteMessage( "Registered with the message manager." );
                        
                       
                        maintView = new MaintenanceView();
                        initMaintenanceView();
                        maintView.setVisible(true);
                            
                        
	    	try
	    	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );
			} // try
	    	catch (Exception e)
			{
				System.out.println("Error:: " + e);
			} // catch
			/********************************************************************
			** Here we start the main simulation loop
			*********************************************************************/
			while ( !Done )
			{
				// Here we get our message queue from the message manager
				try
				{
					eq = em.GetMessageQueue();
				} // try
				catch( Exception e )
				{
					mw.WriteMessage("Error getting message queue::" + e );
				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for MessageIDs = 1 or 2. Message IDs of 1 are temperature
				// readings from the temperature sensor; message IDs of 2 are humidity sensor
				// readings. Note that we get all the messages at once... there is a 1
				// second delay between samples,.. so the assumption is that there should
				// only be a message at most. If there are more, it is the last message
				// that will effect the status of the temperature and humidity controllers
				// as it would in reality.

				int qlen = eq.GetSize();
				for ( int i = 0; i < qlen; i++ )
				{
                                    Msg = eq.GetMessage();
                                    if ( Msg.GetMessageId() == 98 ) // isAlive message ID
					{                                        
						try
						{
                                                    //isAliveMon.isAliveList(Msg.GetMessage());
                                                    mw.WriteMessage( "Device " + Msg.GetMessage() + " connected" );
                                                    //deviceList.deviceMap.put(Msg.GetMessage(),1);
                                                    long lastUpdateTime = 0;
                                                    Integer deviceID = Integer.valueOf(Msg.GetMessage());
                                                    deviceRecord devRec = deviceMap1.get(deviceID);
                                                    if(devRec !=null)
                                                    {
                                                        lastUpdateTime= devRec.getTime();
                                                    }
                                                    
                                                    long currTime = System.currentTimeMillis();
                                                    deviceMap1.put(Integer.valueOf(deviceID),new deviceRecord(true,currTime));
                                                    
                                                    
                                                    //deviceList.deviceMap.put(Msg.GetMessage(), new deviceRecord.getStatus(1));
                                                    //new deviceMonitor.deviceRecord()
                                                    //mw.WriteMessage( "List "+ deviceList.deviceMap.get(0)+ " List" );                                                  
						} // try
						catch( Exception e )
						{
							mw.WriteMessage("Error : " + e);
						} // catch
					} // if
                                    
					if ( Msg.GetMessageId() == 99 )
					{
						Done = true;
						try
						{
                                                    em.UnRegister();
				    	} // try

				    	catch (Exception e)
				    	{
							mw.WriteMessage("Error unregistering: " + e);
				    	} // catch

				    	mw.WriteMessage( "\n\nSimulation Stopped. \n");
					} // if
				} // for
				// This delay slows down the sample rate to Delay milliseconds

				try
				{
					Thread.sleep( Delay );
				} // try

				catch( Exception e )
				{
					System.out.println( "Sleep error:: " + e );
				} // catch
			} // while
		} else {
			System.out.println("Unable to register with the message manager.\n\n" );
		} // if
	} // main
        
        /***************************************************************************
	* CONCRETE METHOD:: listAliveDevices
	* Purpose: Lists active responding devices
	***************************************************************************/
        public void listAliveDevices() 
        {            
            System.out.println("The following devices are responding:  \n");
            
            Iterator<Integer> keySet = deviceMap1.keySet().iterator();
            while (keySet.hasNext()){
                Integer key = keySet.next();
                //boolean status = deviceMap1.get(key).getStatus();
                String status = "";
                long msgTime = deviceMap1.get(key).getTime();
                long currTime = System.currentTimeMillis();
                long aliveTime = currTime - msgTime;
                if (aliveTime < AliveTimeOut){
                    status = "Alive";
                } else {
                    status = "No communication";
                }
                String deviceName = "";
                String deviceDesc = "";
                switch(key){
                    case 1: deviceName = TEMP_SENSOR;
                            deviceDesc = TEMP_SENSOR_DESC;
                            break;
                    case 2: deviceName = HUM_SENSOR;
                            deviceDesc = HUM_SENSOR_DESC;
                            break;
                }
                System.out.println("DeviceID:" + key + ",  " + deviceName + ", "+ deviceDesc + ","
                        + "\n Status:"+status+"  Last Msg Recieved:" + aliveTime+"ms\n");
                //Object[] dataValues = new Object[] {key, deviceName, status, aliveTime};
                //System.out.println("\n");
            }
            System.out.println("\n");
        }
        public void initMaintenanceView()
        {
            DeviceList.add(new DeviceDesc(1, "Humidity Sensor"));
            DeviceList.add(new DeviceDesc(2, "Temperature Sensor"));
            DeviceList.add(new DeviceDesc(3, "Security Alert Sensor"));
            DeviceList.add(new DeviceDesc(4, "Smoke Detector"));
            DeviceList.add(new DeviceDesc(5, "Sprinkler"));
            DeviceList.add(new DeviceDesc(6, "Fire Alarm Controller"));
            DeviceList.add(new DeviceDesc(7, "Humidity Controller"));
            DeviceList.add(new DeviceDesc(8, "Security Controller"));
            DeviceList.add(new DeviceDesc(9, "Sprinkler Controller"));
            DeviceList.add(new DeviceDesc(10, "Temperature Controller"));

            if(maintView !=null)
            {
                for(DeviceDesc d : DeviceList)
                {
                    maintView.InsertNewDeviceEntry(Integer.toString(d.DeviceID),d.DeviceName, "Unknown",  "Unknown", "Unknown");
                
                }
            }
        
        }
        public void updateAllDevices()
        {
            for(DeviceDesc d : DeviceList)
            {
                long lastUpdateTime = 0;
                deviceRecord devRec = deviceMap1.get(d.DeviceID);
                if(devRec !=null)
                {
                    lastUpdateTime= devRec.getTime();
                    d.LastUpdateTime = devRec.getTime();
                    
                    Boolean deviceActive = (System.currentTimeMillis() - lastUpdateTime) < AliveTimeOut;
                    
                    if(deviceActive)
                    {
                        if(!d.DeviceActive)
                        {
                            d.DeviceActive = true;
                            d.SystemTimeAtActive = System.currentTimeMillis();
                        }
                    }
                    else
                    {
                        d.DeviceActive = false;
                        d.SystemTimeAtActive = 0;
                    }
                }
                else
                {
                    d.DeviceActive = false;
                }
                updateMaintenanceView(Integer.valueOf(d.DeviceID), d.LastUpdateTime, d.SystemTimeAtActive);

            }   
        }
        public void updateMaintenanceView(Integer deviceID, long lastUpdateTime, long systemStartTime)
        {
            long currTime           = System.currentTimeMillis();
            long timeSinceLastMsg   = currTime - lastUpdateTime;
            long devStartTime       = currTime - systemStartTime;
            
            String strDeviceID      = Integer.toString(deviceID);
            String strDeviceStatus  = "Alive";
            String strDeviceTimeSinceLastUpdate = "Unknown";
            String strDeviceStartTime = "Unknown";
            
            if(lastUpdateTime >0)
            {
                strDeviceTimeSinceLastUpdate = String.format("%02d:%02d:%02d", 
                                                    TimeUnit.MILLISECONDS.toHours(timeSinceLastMsg),
                                                    TimeUnit.MILLISECONDS.toMinutes(timeSinceLastMsg) - 
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeSinceLastMsg)),
                                                    TimeUnit.MILLISECONDS.toSeconds(timeSinceLastMsg) - 
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSinceLastMsg)));
                
                strDeviceStartTime = String.format("%02d:%02d:%02d", 
                                                    TimeUnit.MILLISECONDS.toHours(devStartTime),
                                                    TimeUnit.MILLISECONDS.toMinutes(devStartTime) - 
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(devStartTime)),
                                                    TimeUnit.MILLISECONDS.toSeconds(devStartTime) - 
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(devStartTime)));   
            }

            if (timeSinceLastMsg > AliveTimeOut)
            {
                strDeviceStatus = "Lost Communications";
                strDeviceStartTime = "Unknown";
            }
            if(maintView !=null)
            {
                if(lastUpdateTime > 0)
                {
                    maintView.UpdateDeviceStatus(strDeviceID, strDeviceStatus,  strDeviceStartTime, strDeviceTimeSinceLastUpdate);
                    
                }
            }
            maintView.setVisible(true);   
        }

	/***************************************************************************
	* CONCRETE METHOD:: IsRegistered
	* Purpose: This method returns the registered status
	*
	* Arguments: none
	*
	* Returns: boolean true if registered, false if not registered
	*
	* Exceptions: None
	*
	***************************************************************************/

	public boolean IsRegistered()
	{
		return( Registered );

	} // SetTemperatureRange
        


	/***************************************************************************
	* CONCRETE METHOD:: Halt
	* Purpose: This method posts an message that stops the environmental control
	*		   system.
	*
	* Arguments: none
	*
	* Returns: none
	*
	* Exceptions: Posting to message manager exception
	*
	***************************************************************************/

	public void Halt()
	{
		mw.WriteMessage( "***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***" );

		// Here we create the stop message.
		Message msg;
		msg = new Message( (int) 99, "XXX" );
		// Here we send the message to the message manager.
		try
		{
			em.SendMessage( msg );
		} // try

		catch (Exception e)
		{
			System.out.println("Error sending halt message:: " + e);
		} // catch

	} // Halt
} // MaintenanceMonitor