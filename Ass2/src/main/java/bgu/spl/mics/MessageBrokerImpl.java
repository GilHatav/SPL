package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminateProg;
import bgu.spl.mics.application.passiveObjects.Squad;
import bgu.spl.mics.application.subscribers.Moneypenny;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {
	/**
	 * Retrieves the single instance of this class.
	 */

	private ConcurrentHashMap<Subscriber, BlockingQueue<Message>> map_Sub_Msg = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Class,BlockingQueue<Subscriber>> map_Type_Sub = new ConcurrentHashMap<>();;
	private ConcurrentHashMap<Message,Future> map_Msg_Future=new ConcurrentHashMap<>();
	private boolean terminate = false;
	private static class SingletonHolder {
		private static MessageBroker instance = new MessageBrokerImpl();
	}

	public static MessageBroker getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {

		BlockingQueue q=new LinkedBlockingQueue();
		this.map_Type_Sub.putIfAbsent(type,q);
		BlockingQueue queue = this.map_Type_Sub.get(type);
			queue.add(m);
			synchronized (map_Type_Sub) {map_Type_Sub.notifyAll();}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {

		BlockingQueue q=new LinkedBlockingQueue();
		this.map_Type_Sub.putIfAbsent(type,q);
		BlockingQueue queue = this.map_Type_Sub.get(type);
			queue.add(m);
	}


	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> f = this.map_Msg_Future.get(e);
		f.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
			BlockingQueue<Subscriber> subscribers = this.map_Type_Sub.getOrDefault(b.getClass(), new LinkedBlockingQueue<>());
			while (subscribers.isEmpty()){
				subscribers = this.map_Type_Sub.getOrDefault(b.getClass(), new LinkedBlockingQueue<>());
			}

			if(b.getClass().equals(TerminateProg.class)){
				terminate = true;
				for (Subscriber s : subscribers) {
					BlockingQueue<Message> toAdd = this.map_Sub_Msg.get(s);
					toAdd.clear();
					toAdd.add(b);
				}
			}

			for (Subscriber s : subscribers) {
				BlockingQueue<Message> toAdd = this.map_Sub_Msg.get(s);
				toAdd.add(b);
			}



	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		BlockingQueue<Subscriber> subscribers = this.map_Type_Sub.get(e.getClass());

		if (subscribers.isEmpty()){
			return null;
		}
		Future f = new Future();
		this.map_Msg_Future.put(e, f);

		synchronized (subscribers){
			while(subscribers.size()<1) {try{subscribers.wait();} catch (Exception e1) {}}
			Subscriber receive = subscribers.poll();
			BlockingQueue<Message> toAdd = this.map_Sub_Msg.get(receive);
			toAdd.add(e);
			subscribers.add(receive);
			subscribers.notifyAll();
		}
		return f;
	}

	@Override
	public void register(Subscriber m) {
		BlockingQueue<Message> q = new LinkedBlockingQueue();
		this.map_Sub_Msg.put(m,q);
	}

	@Override
	public void unregister(Subscriber m) {
		if(map_Sub_Msg.containsKey(m))
		{
			for(BlockingQueue<Subscriber> queue : this.map_Type_Sub.values())
			{
				queue.remove(m);
			}
			while(!map_Sub_Msg.get(m).isEmpty())
			{
				map_Sub_Msg.get(m).poll();
			}
			map_Sub_Msg.remove(m);
		}

	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		BlockingQueue<Message> queue = map_Sub_Msg.getOrDefault(m,new LinkedBlockingQueue<>());
		Message q =queue.take();
			return q;
	}

}