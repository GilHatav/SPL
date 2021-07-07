package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.List;


/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

	private int serialNumber;
	private Squad s;
	private int currentTick;


	public Moneypenny(int serial){
		super("MonneyPenny" + "_" + serial);
		s = Squad.getInstance();
		serialNumber = serial;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class,  (TickCallBack)->{this.currentTick = TickCallBack.getCurrDuration();
			if(this.currentTick==-1) {
				if (this.getSerialNumber() % 2 == 1)
					s.releaseAllAgents();
				terminate();
			}
		});

		subscribeBroadcast(TerminateProg.class, (TerminateProgram)->{
			if (this.getSerialNumber() % 2 == 1) {
				s.releaseAllAgents();
			}

			terminate();

			});

		if (this.getSerialNumber() % 2 == 0) {
			subscribeEvent(AgentsAvailableEvent.class, (CheckAvailAble) -> {
				List<String> ag = CheckAvailAble.getSerials();

				boolean x = s.getAgents(ag);

					if (x) {
						complete(CheckAvailAble, true);
					} else {
						complete(CheckAvailAble, false);
					}

				});
		}
		if(this.getSerialNumber() % 2 == 1) {
			subscribeEvent(AgentsSendEvent.class, (SendAgents) -> {

				s.sendAgents(SendAgents.getSerials(), SendAgents.getDuration());
				List<String> Names = s.getAgentsNames(SendAgents.getSerials());
				SendAgents.getR().setMoneypenny(serialNumber);
				SendAgents.getR().setAgentsNames(Names);
			});

			subscribeEvent(AgentReleaseEvent.class , (Release)->{s.releaseAgents(Release.getSerials());
			});
		}
	}
}