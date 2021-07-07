package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class AgentsAvailableEvent implements Event {

    List<String> serials;

    public AgentsAvailableEvent (List<String> srl){
        serials = srl;
    }

    /**
     * @return serials of the Agents
     */
    public List<String> getSerials() {
        return serials;
    }
}