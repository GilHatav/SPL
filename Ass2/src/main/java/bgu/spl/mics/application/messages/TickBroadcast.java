package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;

import java.util.TimerTask;

public class TickBroadcast  implements Broadcast   {

    private int currDuration;

    public TickBroadcast(int dur){currDuration = dur;}

    /**
     * @return the last received  updated time of the program
     */
    public int getCurrDuration() {
        return currDuration;
    }
}