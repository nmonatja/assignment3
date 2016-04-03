/******************************************************************************************************************
* File:SecurityMonitor.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 March 2009 - Initial rewrite of original assignment 3 (ajl).
*
* Description:
*
* This class monitors the security control systems. 
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
*
******************************************************************************************************************/
import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

class SecurityMonitor extends Thread
{
	private MessageManagerInterface em = null;	// Interface object to the message manager
	private String MsgMgrIP = null;				// Message Manager IP address
	boolean Registered = true;					// Signifies that this class is registered with an message manager.
	MessageWindow mw = null;					// This is the message window
	Indicator si;								// Security indicator
        Indicator si2;								// Fire indicator
        boolean sprinklerUIDisplayed = false;
        

            
	public SecurityMonitor()
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
			System.out.println("SecurityMonitor::Error instantiating message manager interface: " + e);
			Registered = false;

		} // catch

	} //Constructor

	public SecurityMonitor( String MsgIpAddress )
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
			System.out.println("SecurityMonitor::Error instantiating message manager interface: " + e);
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
		boolean ON = true;				// Used to turn on heaters, chillers, humidifiers, and dehumidifiers
		boolean OFF = false;			// Used to turn off heaters, chillers, humidifiers, and dehumidifiers
                String sysAlert = null;
                String fireAlert = null;

		if (em != null)
		{
			// Now we create the ECS status and message panel
			// Note that we set up two indicators that are initially yellow. This is
			// because we do not know if the temperature/humidity is high/low.
			// This panel is placed in the upper left hand corner and the status
			// indicators are placed directly to the right, one on top of the other

			mw = new MessageWindow("Security Monitoring Console", 0, 0);
			si = new Indicator ("SECURITY UNK", mw.GetX()+ mw.Width(), 0);
                        si2 = new Indicator ("FIRE ALERT UNK", si.GetX()+ 2*si.Width(), 0);

			mw.WriteMessage( "Registered with the message manager." );

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
				// We are looking for MessageIDs = 3 (security event triggered) or 6 (fire event). 
                                // Note that we get all the messages at once... there is a 1
				// second delay between samples,.. so the assumption is that there should
				// only be a message at most. If there are more, it is the last message
				// that will effect the status of the temperature and humidity controllers
				// as it would in reality.

				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();

					if ( Msg.GetMessageId() == 22) // Security reading
					{
						try
						{
							sysAlert = Msg.GetMessage();

						} // try

						catch( Exception e )
						{
							mw.WriteMessage("Error reading security alert: " + e);

						} // catch

					} // if
                                        
                                        //secuirty controller confirming that system is disarmed
                                        if ( Msg.GetMessageId() == -7) 
					{
						try
						{
							if (Msg.GetMessage().equalsIgnoreCase("A0")) // disarm security
                                                        {
                                                            mw.WriteMessage("Received system disarmed message" );
                                                            sysAlert = null;
                                                        } 
						} // try

						catch( Exception e )
						{
							mw.WriteMessage("Error reading security alert: " + e);

						} // catch

					} // if
                                        
                                        if ( Msg.GetMessageId() == -8) // Fire reading
					{
						try
						{
							fireAlert = Msg.GetMessage();
                                                        if (!(fireAlert == null)) {//there is fire alarm
                                                            DisplaySprinklerUI(); //display sprinkler UI
                                                            
                                                        }
                                                       
						} // try

						catch( Exception e )
						{
							mw.WriteMessage("Error reading fire alert: " + e);

						} // catch

					} // if

					// If the message ID == 99 then this is a signal that the simulation
					// is to end. At this point, the loop termination flag is set to
					// true and this process unregisters from the message manager.

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

						// Get rid of the indicators. The message panel is left for the
						// user to exit so they can see the last message posted.

						si.dispose();
                                                si2.dispose();

					} // if

				} // for

				mw.WriteMessage("Security event:: " + sysAlert);
                                mw.WriteMessage("Fire alert event:: " + fireAlert);
				// Check temperature and effect control as necessary

				if (!(sysAlert == null)) // something triggered security alert
				{
					si.SetLampColorAndMessage(sysAlert, 3);

				} 
                                else {
						si.SetLampColorAndMessage("NO ALERT", 1); // no security alert

				} // if

                                if (!(fireAlert == null)) {//there is fire alarm
                                    si2.SetLampColorAndMessage("FIRE ALARM", 3);
                                    
                                } 
                                else {
						si2.SetLampColorAndMessage("NO FIRE", 1); // no security alert

				} // if
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
	* Purpose: This method posts an message that stops the security control
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

	/***************************************************************************
	* CONCRETE METHOD:: ArmSecurity
	* Purpose: This method posts messages that will signal the security
	*		   controller to arm the security system
	*
	* Arguments: boolean ON(true)/OFF(false) - indicates whether to turn the
	*			 security system on or off.
	*
	* Returns: none
	*
	* Exceptions: Posting to message manager exception
	*
	***************************************************************************/

	public void ArmSecurity( boolean ON )
	{
		// Here we create the message.

		Message msg;

		if ( ON )
		{
			msg = new Message( (int) 7, "A1" );
		} else {

			msg = new Message( (int) 7, "A0" );
		} // if

		// Here we send the message to the message manager.

		try
		{
			em.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error sending security control message:: " + e);

		} // catch

	} // Heater

        //pop up a sprinkler UI for the user
        public void DisplaySprinklerUI()
        {     
                SprinklerUI SprinklerInterface = new SprinklerUI(); //for prompting user to take sprinkler action
                if (SprinklerInterface.IsRegistered() ){
                    SprinklerInterface.start(); // Here we start sprinkler action thread
                }			    
        }
        
        public Boolean isSprinklerOn() {
            return SprinklerController.SprinklerStatus(em);
        }
        
        public void stopSprinkler() {
            SprinklerController.SprinklerMessage(em, "S0");
        }
        
        
} // SecurityMonitor