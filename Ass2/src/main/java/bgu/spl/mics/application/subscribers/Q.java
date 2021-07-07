package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TerminateProg;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {

	private int currentTick;
	private Inventory inventory;

	public Q() {
		super("Q");
		this.inventory= Inventory.getInstance();
	}

	@Override
	protected void initialize()
	{
		subscribeBroadcast(TickBroadcast.class,  (TickCallBack)->{this.currentTick = TickCallBack.getCurrDuration();
		});
		subscribeBroadcast(TerminateProg.class, (TerminateProgram)->{terminate();});


		subscribeEvent(GadgetAvailableEvent.class, (GetGadget)->{
			boolean b =inventory.getItem(GetGadget.getGadgetAvailable());
			if(b)
				complete(GetGadget,currentTick);
			else
				complete(GetGadget,-1);
		});


	}

}