/******************************************************************************************************************
* File:SprinklerUI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 March 2009 - Initial rewrite of original assignment 3 (ajl).
*
* Description:
*
* This class runs as a separate thread. It displays SprinklerInput and captures user response and 
* sends the response to the message manager
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

class SprinklerUI extends Thread
{
	private MessageManagerInterface em = null;	// Interface object to the message manager
	private String MsgMgrIP = null;				// Message Manager IP address
	boolean Registered = true;					// Signifies that this class is registered with an message manager.
        String Option = null;				// Menu choice from user
        
	public SprinklerUI()
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
			System.out.println("SprinklerUI::Error instantiating message manager interface: " + e);
			Registered = false;

		} // catch

	} //Constructor

	public SprinklerUI( String MsgIpAddress )
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
			System.out.println("SprinklerUI::Error instantiating message manager interface: " + e);
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
                String sysAlert = null;
                String fireAlert = null;
                String sprinklerMsg = null;

		if (em != null)
		{
                        SprinklerInput SprinklerBox = new SprinklerInput();
			/********************************************************************
			** Here we start the main simulation loop
			*********************************************************************/

			while ( !Done )
			{                                         
                            SprinklerBox.setVisible(true);
                         
                            Done = SprinklerBox.Done;
                            if (Done)
                            {
                                sprinklerMsg = SprinklerBox.sprinklerAction;
                                SprinklerBox.dispose();
                                if (sprinklerMsg.equals("activate"))
                                {
                                    Sprinkler (true);
                                }
                                else //cancel sprinkler
                                {
                                    Sprinkler(false);
                                }
                                
                            }
                            
				
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
	* CONCRETE METHOD:: Sprinkler
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

	public void Sprinkler( boolean ON )
	{
		// Here we create the message.

		Message msg;

		if ( ON )
		{
			msg = new Message( (int) 11, "S1" );//sprinkler on

		} else {

			msg = new Message( (int) 11, "S0" );//cancel sprinkler                        

		} // if

		// Here we send the message to the message manager.

		try
		{
			em.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error sending sprinkler control message:: " + e);

		} // catch

	} 

        
} // SprinklerUI