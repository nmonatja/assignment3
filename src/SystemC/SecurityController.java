/******************************************************************************************************************
* File:SecurityController.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 March 2009 - Initial rewrite of original assignment 3 (ajl).
*
* Description:
*
* This class simulates a device that controls security. It polls the message manager for message ids = 7
* and reacts to them by confirming a msg is received. 
*
* The state (on/off) is graphically displayed on the terminal in the indicator. Command messages are displayed in
* the message window. Once a valid command is received a confirmation message is sent with the id of -7 and the command in
* the command string.
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
* Internal Methods:
*	static private void ConfirmMessage(MessageManagerInterface ei, String m )
*
******************************************************************************************************************/
import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

class SecurityController extends DeviceHealthCheck
{
        static String deviceID = "8";
        static String msgMgrIP = "";
        
	public static void main(String args[])
	{
		String MsgMgrIP;					// Message Manager IP address
		Message Msg = null;					// Message object
		MessageQueue eq = null;				// Message Queue
		int MsgId = 0;						// User specified message ID
		MessageManagerInterface em = null;	// Interface object to the message manager
		boolean ArmedState = false;		// Armed state: false == off, true == on
		int	Delay = 2500;					// The loop delay (2.5 seconds)
		boolean Done = false;				// Loop termination flag
                String sysAlert = null;

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the message manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length == 0 )
 		{
			// message manager is on the local system
			System.out.println("\n\nAttempting to register on the local machine..." );

		} else {
			// message manager is not on the local system
			msgMgrIP = args[0];
			System.out.println("\n\nAttempting to register on the machine:: " + msgMgrIP );
		} // if
		
                // have to instantiate this class in order to reference the non-static getMessageManager() method
                SecurityController sc = new SecurityController();
		em = sc.getMessageManager();
                /* Setup and start the device to start health check*/
                sc.setup(em, deviceID, -1); //-1 uses default timer rate. The timer rate unit is ms
                sc.start(); //Start the device health check

		// Here we check to see if registration worked. If ef is null then the
		// message manager interface was not properly created.

		if (em != null)
		{
			System.out.println("Registered with the message manager." );

			/* Now we create the security control status and message panel
			** We put this panel about 1/3 the way down the terminal, aligned to the left
			** of the terminal. The status indicators are placed directly under this panel
			*/

			float WinPosX = 0.0f; 	//This is the X position of the message window in terms
								 	//of a percentage of the screen height
			float WinPosY = 0.3f; 	//This is the Y position of the message window in terms
								 	//of a percentage of the screen height

			MessageWindow mw = new MessageWindow("Security Controller Status Console", WinPosX, WinPosY);

			// Put the status indicators under the panel...

			Indicator si = new Indicator ("Security OFF", mw.GetX(), mw.GetY()+mw.Height());
			
			mw.WriteMessage("Registered with the message manager." );

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

				try
				{
					eq = em.GetMessageQueue();

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Error getting message queue::" + e );

				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for MessageIDs = 7, this is a request to turn the
				// security on/off. Note that we get all the messages
				// at once... there is a 2.5 second delay between samples,.. so
				// the assumption is that there should only be a message at most.
				// If there are more, it is the last message that will effect the
				// output of the security as it would in reality.

				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();

                                        if ( Msg.GetMessageId() == 3 && ArmedState) // Security reading
					{
						try
						{
							sysAlert = Msg.GetMessage();
                                                        //system is armed and alert is triggered
                                                        AlertTriggered( em, sysAlert );
						} // try

						catch( Exception e )
						{
							mw.WriteMessage("Error reading security alert: " + e);

						} // catch

					} // if
                                        
                                        
					if ( Msg.GetMessageId() == 7 )
					{
						if (Msg.GetMessage().equalsIgnoreCase("A1")) // security armed
						{
							ArmedState = true;
							mw.WriteMessage("Received security armed message" );

							// Confirm that the message was recieved and acted on

							ConfirmMessage( em, "A1" );

						} // if

						if (Msg.GetMessage().equalsIgnoreCase("A0")) // disarm security
						{
							ArmedState = false;
							mw.WriteMessage("Received disarm security  message" );

							// Confirm that the message was recieved and acted on

							ConfirmMessage( em, "A0" );

						} // 

					} // if

					// If the message ID == 99 then this is a signal that the simulation
					// is to end. At this point, the loop termination flag is set to
					// true and this process unregisters from the message manager.

					if ( Msg.GetMessageId() == 99 )
					{
						Done = true;
                                                sc.stop();

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

					} // if

				} // for

				// Update the lamp status

				if (ArmedState)
				{
					// Set to green, heater is on

					si.SetLampColorAndMessage("SECURITY ON", 1);

				} else {

					// Set to black, heater is off
					si.SetLampColorAndMessage("SECURITY OFF", 0);

				} // if


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
	* CONCRETE METHOD:: ConfirmMessage
	* Purpose: This method posts the specified message to the specified message
	* manager. This method assumes an message ID of -7 which indicates a confirma-
	* tion of a command.
	*
	* Arguments: MessageManagerInterface ei - this is the messagemanger interface
	*			 where the message will be posted.
	*
	*			 string m - this is the received command.
	*
	* Returns: none
	*
	* Exceptions: None
	*
	***************************************************************************/

	static private void ConfirmMessage(MessageManagerInterface ei, String m )
	{
		// Here we create the message.

		Message msg = new Message( (int) -7, m );

		// Here we send the message to the message manager.

		try
		{
			ei.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error Confirming Message:: " + e);

		} // catch

	} // PostMessage

        static private void AlertTriggered(MessageManagerInterface ei, String m )
	{
		// Here we create the message.

		Message msg = new Message( (int) 22, m );

		// Here we send the message to the message manager.

		try
		{
			ei.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error Confirming Message:: " + e);

		} // catch

	} // PostMessage
        
} // SecurityController