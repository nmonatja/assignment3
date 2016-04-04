/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package a3;

import TermioPackage.*;
import MessagePackage.*;

import java.lang.System;

public class MaintenanceConsole
{
    public static void main(String args[])
    {
        Termio UserInput = new Termio();	// Termio IO Object
        boolean Done = false;				// Main loop flag
        String Option = null;				// Menu choice from user
        Message Msg = null;					// Message object
        boolean Error = false;				// Error flag
        MaintenanceMonitor Monitor = null;

        /////////////////////////////////////////////////////////////////////////////////
        // Get the IP address of the message manager
        /////////////////////////////////////////////////////////////////////////////////
        if ( args.length != 0 )
        {
            Monitor = new MaintenanceMonitor( args[0] );
        } else {
            Monitor = new MaintenanceMonitor();
        } // if

        // Here we check to see if registration worked. If ef is null then the
        // message manager interface was not properly created.
        if (Monitor.IsRegistered() )
        {
            Monitor.start(); // Here we start the monitoring and control thread
            System.out.println( "\n\n\n" );
            System.out.println( "Maintenance Console: \n" );

            if (args.length != 0)
                System.out.println( "Using message manager at: " + args[0] + "\n" );
            else
                System.out.println( "Using local message manager \n" );

            while (!Done)
            {
                // Here, the main thread continues and provides the main menu

                /*System.out.println( "\n Maintenance Console");
                System.out.println( "Select an Option: \n" );
                System.out.println( "1: List Responsive Devices" );
                System.out.println( "X: Stop System\n" );
                System.out.print( "\n>>>> " );
                Option = UserInput.KeyboardReadString();

                if ( Option.equals( "1" ) )
                {
                    Monitor.listAliveDevices();
                } // if

                else if ( Option.equalsIgnoreCase( "X" ) )
                {
                    Monitor.Halt();
                    Done = true;
                    System.out.println( "\nConsole Stopped... Exit monitor mindow to return to command prompt." );
                }
                else {
                    System.out.println("\n Not a valid option! Please try again!");
                }*/
                Monitor.listAliveDevices();
                Monitor.updateAllDevices();
                try {
                        Thread.sleep(1000);                 //1000 milliseconds is one second.
                       
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
            } // while
        } else {
            System.out.println("\n\nUnable start the monitor.\n\n" );
        } // if
    } // main
} // MaintenanceConsole
