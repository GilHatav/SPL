package bgu.spl.net.srv;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl implements Connections {

    //connection id & connection handelr. only active users!!
    private HashMap<Integer,ConnectionHandler> users_connections = new HashMap<>();

    private static class SingletonHolder {
        private static ConnectionsImpl instance = new ConnectionsImpl();
    }
    public static ConnectionsImpl getInstance() {
        return ConnectionsImpl.SingletonHolder.instance;
    }
    @Override
    public boolean send(int connectionId, Object msg) {
        ConnectionHandler<Object> connectionHandler;
        try{
            connectionHandler =  users_connections.get(connectionId);
            connectionHandler.send(msg);
        }
        catch (Exception e){
        return false;
    }
        return true;
    }


    @Override
    public void send(String channel, Object msg) {

        List<User> registertochannel = BookClub.getInstance().getChannels().get(channel);
        if(registertochannel!=null) {
            for (int i = 0; i < registertochannel.size(); i++) {
                int connectionID = registertochannel.get(i).getId();
                send(connectionID, msg);
            }
        }

    }

    @Override
    public void disconnect(int connectionId) {
        //remove connection from map
        ConnectionHandler todelete = this.users_connections.get(connectionId);
        this.users_connections.remove(todelete);
        //close connection
        /*
        try {
            this.users_connections.get(connectionId).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
    }

    public HashMap<Integer, ConnectionHandler> getUsers_connections() {
        return users_connections;
    }

    public void addToUserConnections(Integer id, ConnectionHandler connectionHandler)
    {
        this.users_connections.put(id,connectionHandler);
    }

}
