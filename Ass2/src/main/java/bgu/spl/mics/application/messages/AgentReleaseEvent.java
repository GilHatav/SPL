package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class AgentReleaseEvent implements Event {

    List<String> serials;

    public AgentReleaseEvent(List<String> srl){serials = srl;}

    /**
     * @return serials of the Agents
     */
    public List<String> getSerials() {
        return serials;
    }
}