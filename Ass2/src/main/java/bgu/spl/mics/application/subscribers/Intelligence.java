package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TerminateProg;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.sql.Array;
import java.util.*;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	private MissionInfo[] infoObjects;
	private int currentTick;
	private MessageBroker msgBroker = MessageBrokerImpl.getInstance();
	private int index;

	public Intelligence(List<MissionInfo> missionInfoList) {
		super("intelligence");
		this.index = 0;
		this.infoObjects = new MissionInfo[missionInfoList.size()];
		int i=0;
		for(MissionInfo mission : missionInfoList)
		{
			this.infoObjects[i] = mission;
			i++;
		}
		Arrays.sort(this.infoObjects,Comparator.comparingInt(MissionInfo::getTimeIssued));

	}



	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateProg.class, (TerminateProgram)->{ terminate();});
		subscribeBroadcast(TickBroadcast.class,
				(k)->{
			this.currentTick = k.getCurrDuration();

			MissionInfo mission;
			for(int i = 0 ; i < this.infoObjects.length ; i ++)
			{
				int time = this.currentTick;
				if(this.infoObjects[i].getTimeIssued() == time)
				{
					mission = this.infoObjects[i];
					MissionReceivedEvent missionReceivedEvent = new MissionReceivedEvent(mission.getMissionName(), mission.getSerialAgentsNumbers() , mission.getGadget(),mission.getTimeExpired(),currentTick,mission.getDuration() , mission.getTimeIssued());
					msgBroker.sendEvent(missionReceivedEvent);
				}
				if(this.infoObjects[i].getTimeIssued() > time) break;
			}
		 if(currentTick==-1)
		 	terminate();
		});



	}
}