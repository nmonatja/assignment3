/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package a3;

import java.util.*;

/**
 *
 * @author Aranya
 */
 

    /*********************************************************************************
    * Class to store device details.
    ********************************************************************************/
    class deviceRecord 
    {
        private boolean status;
        private long timeStamp;
        public deviceRecord(boolean status, long timeStamp ){
            this.status = status;
            this.timeStamp = timeStamp;
        }
        boolean getStatus(){
            return status;
        }
        long getTime(){
            return timeStamp;
        }
    }
 
    //deviceRecord();
    
 

