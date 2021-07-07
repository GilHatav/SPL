package bgu.spl.net.api;

import bgu.spl.net.srv.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;

public class StompMessagingProtocolImpl implements StompMessagingProtocol {

    private AtomicInteger msgID = new AtomicInteger(0);
    private int connectionId;
    private Connections<String> connections;
    private String username;
    private boolean connectionSuccess = false;
    private boolean terminate = false;


    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }
    private String createMsgForCLient(String msgType , List<String> headrs , String body)
    {
        String s_headrs="";
        for(int i=0 ; i < headrs.size() ; i++)
            s_headrs += "\n" + headrs.get(i);
        return msgType + s_headrs + "\n"+ "\n" + body + "\n";
    }

    @Override
    public void process(String message) {
        String[] str  = message.split("[\\r\\n]+");

        String StompCommand = str[0];
        List<String> headers = new LinkedList<>();
        for(int i = 1 ; i< str.length ; i++)
        {
            headers.add(str[i]);
        }
        String framebody = str[str.length-1];

        if(StompCommand.equals("CONNECT"))
        {
            //include <HeaderName_1>:<HeaderValue_1>
            String accept_version = headers.get(0);
            String[] split = accept_version.split(":");
            accept_version ="version:" + split[1];
            String host = headers.get(1); //where to use?
            String login = headers.get(2);
            split = login.split(":");
            login= split[1];

            if(connectionSuccess==true)
            {
                List<String> errorHeadrs = new LinkedList<>();
                errorHeadrs.add("message:Can't connect twice from the same client");
                String errorFrame = createMsgForCLient("ERROR", errorHeadrs , "");
                this.connections.send(connectionId,errorFrame);
                //BookClub.getInstance().setActive(username,false);
                return;
            }

            this.username = login;
            String passcode = headers.get(3);
            split = passcode.split(":");
            passcode = split[1];
            List<String> ConnectHeadrs = new LinkedList<>();
            ConnectHeadrs.add(accept_version);

            HashMap<String,User> usersByUsername = BookClub.getInstance().getUsersByUsername();

            //user doesnt exist , create new user
            if(!usersByUsername.containsKey(login))
            {
                User u = new User(login,passcode,true,this.connectionId);
                BookClub.getInstance().setUsersByUsername(login,u);
                String connectedFrame = createMsgForCLient("CONNECTED" , ConnectHeadrs , "");
                this.connections.send(connectionId,connectedFrame);
                connectionSuccess = true;
            }

            //if user already exists, isnt active and password is correct
            else if(usersByUsername.containsKey(login) && !usersByUsername.get(login).getActive() && usersByUsername.get(login).getPassword().equals(passcode) )
            {
                usersByUsername.get(login).setActive(true);
                String connectedFrame = createMsgForCLient("CONNECTED" , ConnectHeadrs ,"");
                //String connectedFrame = "CONNECTED" + "\n" + "version:" + accept_version ; //send connected frame
                this.connections.send(connectionId,connectedFrame);
                connectionSuccess = true;
            }
            //if password isnt correct
            else if (usersByUsername.containsKey(login) && !usersByUsername.get(login).getPassword().equals(passcode))
            {
                List<String> errorHeadrs = new LinkedList<>();
                errorHeadrs.add("message:Wrong Password");
                String errorFrame = createMsgForCLient("ERROR", errorHeadrs , "");
                this.connections.send(connectionId,errorFrame);
                BookClub.getInstance().setActive(username,false);
                return;
                //this.connections.disconnect(this.connectionId);
            }
            //user already active
            else if (usersByUsername.containsKey(login) && usersByUsername.get(login).getActive())
            {
                List<String> errorHeadrs = new LinkedList<>();
                errorHeadrs.add("message:User already logged in");
                String errorFrame = createMsgForCLient("ERROR", errorHeadrs , "");
                this.connections.send(connectionId,errorFrame);
                BookClub.getInstance().setActive(username,false);
                return;
                //this.connections.disconnect(this.connectionId);
            }
        }

        else if(connectionSuccess) {

            if (StompCommand.equals("SEND")) {
                String destination, msg;
                destination = headers.get(0);
                String[] split = destination.split(":");
                destination = split[1];
                msg = framebody;
                String subscription = BookClub.getInstance().getSubID(username, destination);
                String Message_id = Integer.toString(msgID.get());

                List<String> send_headrs = new LinkedList<>();
                if(subscription!=null) {
                    send_headrs.add("subscription:" + subscription);
                }
                send_headrs.add("message id:" + Message_id);
                send_headrs.add("destination:" + destination);

                String sendFrame = createMsgForCLient("MESSAGE", send_headrs, msg);
                this.msgID.incrementAndGet();
                this.connections.send(destination, sendFrame);
            }
            else if (StompCommand.equals("SUBSCRIBE"))//need to check msg
            {
                String dest = headers.get(0);
                String[] split = dest.split(":");
                dest = split[1];
                String subid = headers.get(1);
                split = subid.split(":");
                subid = split[1];
                String receiptid = headers.get(2);
                split = receiptid.split(":");
                receiptid = split[1];
                List<String> sub_headrs = new LinkedList<>();
                sub_headrs.add(receiptid);
                BookClub.getInstance().subscribe_impl(dest, this.username, Integer.parseInt(subid)); //check
                String receiptFrame = createMsgForCLient("RECEIPT", sub_headrs, "join successful " + dest);
                this.connections.send(connectionId, receiptFrame);
            }
            else if (StompCommand.equals("UNSUBSCRIBE")) //need to check msg
            {
                //find sub id
                String subscriptionid = headers.get(1);
                String[] split = subscriptionid.split(":");
                subscriptionid = split[1];
                BookClub.getInstance().unsubscribe_imp(Integer.parseInt(subscriptionid), this.username);

                //find receipt id
                String recId = headers.get(2);
                split = recId.split(":");
                recId = split[1];

                //find dest
                String dest = headers.get(0);
                split = dest.split(":");
                dest = split[1];

                List<String> unsub_headrs = new LinkedList<>();
                unsub_headrs.add("receipt-id:" + subscriptionid);
                unsub_headrs.add(recId);
                String receiptFrame = createMsgForCLient("RECEIPT", unsub_headrs, "exit successful " +dest);
                this.connections.send(connectionId, receiptFrame);
            }
            else if (StompCommand.equals("DISCONNECT")) {
                BookClub.getInstance().deleteuser(this.username);
                String recId = headers.get(0);
                String[] a = recId.split(":");
                recId = a[1];
                List<String> disconnectHeadrs = new LinkedList<>();
                disconnectHeadrs.add("receipt-id:" + recId);
                String disconnectFrame = createMsgForCLient("RECEIPT", disconnectHeadrs, "Disconnect");
                this.connections.send(connectionId, disconnectFrame);
                this.connections.disconnect(this.connectionId);
                terminate = true;

            }
        }

    }


    @Override
    public boolean shouldTerminate() {
       // return false;
        return this.terminate;
    }

}
