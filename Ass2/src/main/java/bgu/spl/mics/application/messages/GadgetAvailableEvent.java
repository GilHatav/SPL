package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class GadgetAvailableEvent implements Event {
    private String GadgetAvailable;


    public GadgetAvailableEvent (String gadget){ GadgetAvailable = gadget;}

    /**
     * @return the gadget
     */
    public String getGadgetAvailable()
    {
        return this.GadgetAvailable;
    }
}
