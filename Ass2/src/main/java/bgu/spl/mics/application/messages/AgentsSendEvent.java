package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;

public class AgentsSendEvent implements Event {

    private List<String> serials;
    private int duration;
    private Report r;

    public AgentsSendEvent(List<String> srl, int duration, Report r) {serials = srl;
    this.duration=duration;
    this.r =r;}

    /**
     * @return serials of the Agents
     */
    public List<String> getSerials() {
        return serials;
    }

    /**
     * @return duration of the mission that will executed
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @return the report (in order to update MonneyPenny serial number and Agents_Names)
     */
    public Report getR() {
        return r;
    }
}
