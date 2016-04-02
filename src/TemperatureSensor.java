/******************************************************************************************************************
* File:TemperatureSensor.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 March 2009 - Initial rewrite of original assignment 3 (ajl).
*
* Description:
*
* This class simulates a temperature sensor. It polls the message manager for messages corresponding to changes in state
* of the heater or chiller and reacts to them by trending the ambient temperature up or down. The current ambient
* room temperature is posted to the message manager.
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.   
*
* Internal Methods:
*	float GetRandomNumber()
*	boolean CoinToss()
*   void PostTemperature(MessageManagerInterface ei, float temperature )
*
******************************************************************************************************************/
import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

class TemperatureSensor extends DeviceHealthCheck
{
    static String deviceID = "2";
    static String msgMgrIP = "";
	
    @Override 
    public String MsgMgrIP() { 
        return msgMgrIP; 
    }
    
	public static void main(String args[])
	{
		// MsgMgrIP is instantiated in the superclass but may need to be set to an IP below
		Message Msg = null;				// Message object
		MessageQueue eq = null;			// Message Queue
		int MsgId = 0;					// User specified message ID
		MessageManagerInterface em = null;// Interface object to the message manager
		boolean HeaterState = false;	// Heater state: false == off, true == on
		boolean ChillerState = false;	// Chiller state: false == off, true == on
		float CurrentTemperature;		// Current simulated ambient room temperature
		float DriftValue;				// The amount of temperature gained or lost
		int	Delay = 2500;				// The loop delay (2.5 seconds)
		boolean Done = false;			// Loop termination flag
                //DeviceHealthCheck ts1;

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
                TemperatureSensor ts = new TemperatureSensor();
		em = ts.getMessageManager();
                /* Setup and start the device to start health check*/
                ts.setup(deviceID, -1); //-1 uses default timer rate. The timer rate unit is ms
                ts.start(); //Start the device health check

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
			MessageWindow mw = new MessageWindow("Temperature Sensor", WinPosX, WinPosY );
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
			mw.WriteMessage("\nInitializing Temperature Simulation::" );
			CurrentTemperature = (float)50.00;
			if ( CoinToss() )
			{
				DriftValue = GetRandomNumber() * (float) -1.0;
			} else {
				DriftValue = GetRandomNumber();
			} // if

			mw.WriteMessage("   Initial Temperature Set:: " + CurrentTemperature );
			mw.WriteMessage("   Drift Value Set:: " + DriftValue );
                        
			/********************************************************************
			** Here we start the main simulation loop
			*********************************************************************/

			mw.WriteMessage("Beginning Simulation... ");


			while ( !Done )
			{
				// Post the current temperature

				PostTemperature( em, CurrentTemperature );
				mw.WriteMessage("Current Temperature::  " + CurrentTemperature + " F");
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
				// We are looking for MessageIDs = -5, this means the the heater
				// or chiller has been turned on/off. Note that we get all the messages
				// at once... there is a 2.5 second delay between samples,.. so
				// the assumption is that there should only be a message at most.
				// If there are more, it is the last message that will effect the
				// output of the temperature as it would in reality.

				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();

					if ( Msg.GetMessageId() == -5 )
					{
						if (Msg.GetMessage().equalsIgnoreCase("H1")) // heater on
						{
							HeaterState = true;

						} // if

						if (Msg.GetMessage().equalsIgnoreCase("H0")) // heater off
						{
							HeaterState = false;

						} // if

						if (Msg.GetMessage().equalsIgnoreCase("C1")) // chiller on
						{
							ChillerState = true;

						} // if

						if (Msg.GetMessage().equalsIgnoreCase("C0")) // chiller off
						{
							ChillerState = false;

						} // if

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

				    	mw.WriteMessage("\n\nSimulation Stopped. \n");

					} // if

				} // for

				// Now we trend the temperature according to the status of the
				// heater/chiller controller.

				if (HeaterState)
				{
					CurrentTemperature += GetRandomNumber();

				} // if heater is on

				if (!HeaterState && !ChillerState)
				{
					CurrentTemperature += DriftValue;

				} // if both the heater and chiller are off

				if (ChillerState)
				{
					CurrentTemperature -= GetRandomNumber();

				} // if chiller is on

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
	* CONCRETE METHOD:: GetRandomNumber
	* Purpose: This method provides the simulation with random floating point
	*		   temperature values between 0.1 and 0.9.
	*
	* Arguments: None.
	*
	* Returns: float
	*
	* Exceptions: None
	*
	***************************************************************************/

	static private float GetRandomNumber()
	{
		Random r = new Random();
		Float Val;

		Val = Float.valueOf((float)-1.0);

		while( Val < 0.1 )
		{
			Val = r.nextFloat();
	 	}

		return( Val.floatValue() );

	} // GetRandomNumber

	/***************************************************************************
	* CONCRETE METHOD:: CoinToss
	* Purpose: This method provides a random true or false value used for
	* determining the positiveness or negativeness of the drift value.
	*
	* Arguments: None.
	*
	* Returns: boolean
	*
	* Exceptions: None
	*
	***************************************************************************/

	static private boolean CoinToss()
	{
		Random r = new Random();

		return(r.nextBoolean());

	} // CoinToss

	/***************************************************************************
	* CONCRETE METHOD:: PostTemperature
	* Purpose: This method posts the specified temperature value to the
	* specified message manager. This method assumes an message ID of 1.
	*
	* Arguments: MessageManagerInterface ei - this is the messagemanger interface
	*			 where the message will be posted.
	*
	*			 float temperature - this is the temp value.
	*
	* Returns: none
	*
	* Exceptions: None
	*
	***************************************************************************/

	static private void PostTemperature(MessageManagerInterface ei, float temperature )
	{
		// Here we create the message.

		Message msg = new Message( (int) 1, String.valueOf(temperature) );
		//Message isAlive = new Message ( isAliveMsgID, deviceID);

		// Here we send the message to the message manager.

		try
		{
			ei.SendMessage( msg );
			//ei.SendMessage( isAlive );
			//System.out.println( "Sent Temp Message" );

		} // try

		catch (Exception e)
		{
			System.out.println( "Error Posting Temperature:: " + e );

		} // catch

	} // PostTemperature

} // TemperatureSensor