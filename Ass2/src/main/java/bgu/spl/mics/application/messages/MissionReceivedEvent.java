package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class MissionReceivedEvent implements Event {
    private String missionName;
    private List<String> serialNumbers;
    private String gadget;
    private int SendTime;
    private int ExpiredTick;
    private int duration;
    private int TimeIssued;

    public MissionReceivedEvent (String missionName , List<String> serialNumbers , String gadget,int ExpiredTick,int SentTime,int duration , int TimeIssued)
    {
        this.gadget = gadget;
        this.missionName = missionName;
        this.serialNumbers = serialNumbers;
        this.ExpiredTick=ExpiredTick;
        this.SendTime = SentTime;
        this.duration = duration;
        this.TimeIssued = TimeIssued;

    }

    /**
     * @return serial Number of Agents
     */
    public List<String> getSerialNumbers() {
        return serialNumbers;
    }

    /**
     * @return the gadget
     */
    public String getGadget() {
        return gadget;
    }

    /**
     * @return final tick of the mission
     */
    public int getExpiredTick() {
        return ExpiredTick;
    }

    /**
     * @return  mission Name
     */
    public String getMissionName() {
        return this.missionName;
    }

    /**
     * @return when the mission is sent
     */
    public int getSendTime() {
        return SendTime;
    }

    /**
     * @return the duration of the mission
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @return when the mission should start
     */
    public int getTimeIssued() {
        return TimeIssued;
    }
}