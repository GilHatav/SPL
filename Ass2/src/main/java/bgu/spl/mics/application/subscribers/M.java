package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;
import bgu.spl.mics.application.publishers.TimeService;

import java.sql.Time;


/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {

	private int M_id;
	private int currTIme;
	private MessageBroker msgBroke = MessageBrokerImpl.getInstance();
	private Diary diary = Diary.getInstance();


	public M(int id) {
		super("M" + "_" + id);
		M_id = id;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class , (UpdateTime)-> {currTIme=UpdateTime.getCurrDuration();
		if(currTIme==-1)
			terminate();
		});
		subscribeBroadcast(TerminateProg.class, (TerminateProgram)->{terminate();});


		subscribeEvent(MissionReceivedEvent.class , (DoMission)->{
			Report r = new Report();
			r.setTimeCreated(currTIme);
			r.setMissionName(DoMission.getMissionName());
			r.setM(M_id);
			r.setAgentsSerialNumbersNumber(DoMission.getSerialNumbers());
			r.setGadgetName(DoMission.getGadget());
			r.setTimeCreated(DoMission.getSendTime());
			r.setTimeIssued(DoMission.getTimeIssued());

			synchronized (diary) {
				while (diary.isWriting()) { try{ diary.wait();} catch (Exception e){}}
				diary.setWriting();
				diary.setTotal();
				diary.setWriting();
				diary.notifyAll();

			}

				Future<Boolean> f_Agents = msgBroke.sendEvent(new AgentsAvailableEvent(DoMission.getSerialNumbers()));
				if(f_Agents!=null) {
					boolean b = f_Agents.get();
					if (b) {
						Future<Integer> Gadget_Time = msgBroke.sendEvent(new GadgetAvailableEvent(DoMission.getGadget()));
						if(Gadget_Time!=null) {
							Integer g = Gadget_Time.get();

							if (g > -1) {
								if (DoMission.getExpiredTick() >= currTIme + DoMission.getDuration()) {
									Future f_MoneyPenny_id = msgBroke.sendEvent(new AgentsSendEvent(DoMission.getSerialNumbers(), DoMission.getDuration(),r));
									if(f_MoneyPenny_id!=null)
									{
									r.setQTime(g);
									synchronized (diary) {
										while (diary.isWriting()) {
											try {
												diary.wait();
											} catch (Exception e) {
											}
										}
										diary.setWriting();
										diary.addReport(r);
										diary.setWriting();
										diary.notifyAll();
									}

									}
									else
									{
										msgBroke.sendEvent(new AgentReleaseEvent(DoMission.getSerialNumbers()));
									}
								} else {
									msgBroke.sendEvent(new AgentReleaseEvent(DoMission.getSerialNumbers()));
								}
							} else {
								msgBroke.sendEvent(new AgentReleaseEvent(DoMission.getSerialNumbers()));
							}
						}
						else
						{
							msgBroke.sendEvent(new AgentReleaseEvent(DoMission.getSerialNumbers()));
						}
					} else {
						msgBroke.sendEvent(new AgentReleaseEvent(DoMission.getSerialNumbers()));
					}
				}



	});

	}

}