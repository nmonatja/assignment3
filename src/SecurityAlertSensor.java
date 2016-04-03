/******************************************************************************************************************
* File:SecurityAlertSensor.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 March 2009 - Initial rewrite of original assignment 3 (ajl).
*
* Description:
*
* This class simulates a security sensor. The user simulates a security event by
* making a menu selection.
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
*
******************************************************************************************************************/
import InstrumentationPackage.*;
import MessagePackage.*;
import TermioPackage.Termio;
import java.util.*;

class SecurityAlertSensor extends DeviceHealthCheck
{
    static String deviceID = "3";
    static String msgMgrIP = "";
	
    @Override 
    public String MsgMgrIP() { 
        return msgMgrIP; 
    }
    
	public static void main(String args[])
	{
		String MsgMgrIP;				// Message Manager IP address
		Message Msg = null;				// Message object
		MessageQueue eq = null;			// Message Queue
		int MsgId = 0;					// User specified message ID
		MessageManagerInterface em = null;// Interface object to the message manager
		boolean ArmedState = false;	// Alarm state: false == off, true == on
		String securityAlert = null;      //stores security alert
		int	Delay = 2500;				// The loop delay (2.5 seconds)
		boolean Done = false;			// Loop termination flag
                Termio UserInput = new Termio();	// Termio IO Object
                String Option = null;				// Menu choice from user

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
                SecurityAlertSensor ss = new SecurityAlertSensor();
		em = ss.getMessageManager();
                /* Setup and start the device to start health check*/
                ss.setup(em, deviceID, -1); //-1 uses default timer rate. The timer rate unit is ms
                ss.start(); //Start the device health check
                
                
		// Here we check to see if registration worked. If ef is null then the
		// message manager interface was not properly created.


		if (em != null)
		{

			// We create a message window. Note that we place this panel about 1/2 across
			// and 1/3 down the screen

			float WinPosX = 0.5f; 	//This is the X position of the message window in terms
								 	//of a percentage of the screen height
			float WinPosY = 0.3f; 	//This is the Y position of the message window in terms
								 	//of a percentage of the screen height

			MessageWindow mw = new MessageWindow("Security Alert Sensor", WinPosX, WinPosY );

			mw.WriteMessage("Registered with the message manager." );

	    	try
	    	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );

			} // try

	    	catch (Exception e)
			{
				mw.WriteMessage("Error:: " + e);

			} // catch

			mw.WriteMessage("\nInitializing Security Alert Simulation::" );

			mw.WriteMessage("   Initial Security Alert:: " + securityAlert );

			/********************************************************************
			** Here we start the main simulation loop
			*********************************************************************/

			mw.WriteMessage("Beginning Simulation... ");


			while ( !Done )
			{
                            // Here, the main thread continues and provides the main menu

				System.out.println( "\n\n\n\n" );
				System.out.println( "Security Sensor Simulator: \n" );

				if (args.length != 0)
					System.out.println( "Using message manager at: " + args[0] + "\n" );
				else
					System.out.println( "Using local message manager \n" );

				
				System.out.println( "Select an security event: \n" );
				System.out.println( "1: Window break" );
				System.out.println( "2: Door break" );
                                System.out.println( "3: Motion detection" );
                                System.out.println( "4: STOP a security event" );
				System.out.print( "\n>>>> " );
				Option = UserInput.KeyboardReadString();

				//////////// option 1 ////////////

				if ( Option.equals( "1" ) )
				{
                                    securityAlert = "Window break";
				} // if

				//////////// option 2 ////////////

				if ( Option.equals( "2" ) )
				{
					securityAlert = "Door break";
				} // if

                                //////////// option 3 ////////////

				if ( Option.equals( "3" ) )
				{
					securityAlert = "Motion detection";
				} // if
				
                                //////////// option 4 ////////////

				if ( Option.equals( "4" ) )
				{
					securityAlert = null;
				} // if
				
				
				mw.WriteMessage("Current security alert::  " + securityAlert);
				// Get the message queue

				try
				{
					eq = em.GetMessageQueue();

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Error getting message queue::" + e );

				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for MessageIDs = -7, this means the security system is armed/disarmed
                                // Note that we get all the messages
				// at once... there is a 2.5 second delay between samples,.. so
				// the assumption is that there should only be a message at most.
				// If there are more, it is the last message that will effect the
				// output of the temperature as it would in reality.

				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();

					if ( Msg.GetMessageId() == -7 )
					{
						
						if (Msg.GetMessage().equalsIgnoreCase("A1")) // alert system armed
						{
							ArmedState = true;

						} // if

						if (Msg.GetMessage().equalsIgnoreCase("A0")) // alert system disarmed
						{
							ArmedState = false;

						} // if

					} // if

					// If the message ID == 99 then this is a signal that the simulation
					// is to end. At this point, the loop termination flag is set to
					// true and this process unregisters from the message manager.

					if ( Msg.GetMessageId() == 99 )
					{
						Done = true;
                                                
                                                ss.stop();
                                                
						try
						{
							em.UnRegister();

				    	} // try

				    	catch (Exception e)
				    	{
							mw.WriteMessage("Error unregistering: " + e);

				    	} // catch

				    	mw.WriteMessage("\n\nSimulation Stopped. \n");

					} // if

				} // for

                                // Post the security alert
                                if (ArmedState)
                                {
                                    PostSecurityAlert( em, securityAlert );
                                }
				// Here we wait for a 2.5 seconds before we start the next sample

				try
				{
					Thread.sleep( Delay );

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Sleep error:: " + e );

				} // catch

			} // while

		} else {

			System.out.println("Unable to register with the message manager.\n\n" );

		} // if

	} // main

	
	/***************************************************************************
	* CONCRETE METHOD:: PostSecurityAlert
	* Purpose: This method posts the specified security alert to the
	* specified message manager. This method assumes an message ID of 3.
	*
	* Arguments: MessageManagerInterface ei - this is the messagemanger interface
	*			 where the message will be posted.
	*
	*			 String securityAlert - this is the alert value.
	*
	* Returns: none
	*
	* Exceptions: None
	*
	***************************************************************************/

	static private void PostSecurityAlert(MessageManagerInterface ei, String securityAlert )
	{
		// Here we create the message.

		Message msg = new Message( (int) 3, securityAlert );

		// Here we send the message to the message manager.

		try
		{
			ei.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println( "Error Posting Security Alert:: " + e );

		} // catch

	} // PostSecurityAlert

} // SecurityAlertSensor