package bgu.spl.mics.application.passiveObjects;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {


	private Map<String, Agent> agents;
	private Squad() {}

	private static class SingletonHolder {
		private static Squad instance = new Squad();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Squad getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		this.agents = new HashMap();
		for(Agent a : agents)
		{
			this.agents.put(a.getSerialNumber(),a);
		}
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials){
		for(String serial : serials) {
			Agent a = this.agents.get(serial);
			if(a!=null) {
				a.release();
				synchronized (a) {
					a.notifyAll();
				}
			}
		}
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		for(String s : serials) {
			try {
				agents.get(s).acquire();
				Thread.currentThread().sleep(time*100);
			} catch (InterruptedException e) {
			}
		}
		releaseAgents(serials);
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		java.util.Collections.sort(serials);
		for(String serial : serials) {
			Agent a = this.agents.get(serial);
			if(a==null) return false;
			synchronized (a){

				while (!a.isAvailable()){
					try {
						a.wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}

	/**
	 * gets the agents names
	 * @param serials the serial numbers of the agents
	 * @return a list of the names of the agents with the specified serials.
	 */
	public List<String> getAgentsNames(List<String> serials){
		List<String> listofname = new LinkedList<String>();
		for (String serial : serials)
		{
			Agent a = this.agents.get(serial);
			listofname.add(a.getName());
		}
		return listofname;
	}

	/**
	 * release all the agents
	 */
	public void releaseAllAgents(){

		for (Map.Entry<String, Agent> entry : agents.entrySet()) {
			 Agent a = entry.getValue();
			 a.release();
			synchronized (a){
				a.notifyAll();
			}

		}
	}
}