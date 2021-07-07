package bgu.spl.mics.application.publishers;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.TerminateProg;
import bgu.spl.mics.application.messages.TickBroadcast;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

	private static class SingletonHolder {
		private static TimeService instance = new TimeService();
	}
	private int duration;
	private MessageBroker msgBroke = MessageBrokerImpl.getInstance();
	private Timer timer = new Timer();
	private int maxTime;

	public TimeService() {
		super("TimeService");
	}


	public void setDuration(int duration) {
		this.duration = 0;
		this.maxTime = duration;
	}

	/**
	 * @return the instance of TimeService
	 */
	public static TimeService getInstance() {
		return TimeService.SingletonHolder.instance;
	}

	/**
	 * a wraped function in order to use TimerTask (Which is abstract)
	 */
	private static TimerTask wrap(Runnable r) {
		return new TimerTask() {
			@Override
			public void run() {
				r.run();
			}
		};
	}

	/**
	 * @return current duration
	 */
	public int getDuration() {
		return duration;
	}

	@Override
	protected void initialize() {}

	@Override
	public void run() {
		initialize();
		timer.schedule(wrap(()->
				{
					if(duration<maxTime)
					{
						msgBroke.sendBroadcast(new TickBroadcast(duration));
						duration=duration+1;
					}
					else
						{
							msgBroke.sendBroadcast(new TerminateProg());
							timer.cancel();
						}

				})
				,0, 100); //change to 1000
	}

}