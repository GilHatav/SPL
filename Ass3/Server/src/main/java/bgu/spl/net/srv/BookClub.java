package bgu.spl.net.srv;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BookClub {
    private HashMap<String, List<User>> channels = new HashMap<>();
    private HashMap<String , User> usersByUsername = new HashMap<>(); //users exists
    private HashMap<Integer,List<Pair<String,User>>> subscriptionIdhashmap = new HashMap<>();
    private HashMap<String,List<Pair<String,Integer>>> SubByUsername = new HashMap<>();
    private static class SingletonHolder {
        private static BookClub instance = new BookClub();
    }
    public static BookClub getInstance() {
        return SingletonHolder.instance;
    }

    public HashMap<String, List<User>> getChannels() {
        return channels;
    }

    public void setActive(String username,Boolean b)
    {
        this.usersByUsername.get(username).setActive(b);
    }


    /**
     * finds the subscription id which represents the user subscription to a genre
     * @param dest genre
     * @param username
     * @return a string represents subscription id
            */
    public String getSubID(String username , String dest)
    {
        List l = this.SubByUsername.get(username);
        if(l==null)
            return null;
        for(int i = 0 ; i < l.size() ; i ++)
        {
            Pair p =(Pair) l.get(i);
            if (p.getKey().equals(dest)){
                int sub = (Integer)p.getValue();
                String x = Integer.toString(sub);
                return x;
            }

        }
        return null;
    }

    /**
     * updates subscription-by-username hash map with new subscription
     * @param username
     * @param dest
     * @param sub
     */
    private void updateMap(String username, String dest , Integer sub)
    {
        Pair p = new Pair(dest,sub);
        if(!SubByUsername.containsKey(username))
        {
            List<Pair<String,Integer>> listTOadd = new LinkedList<>();
            listTOadd.add(p);
            this.SubByUsername.put(username,listTOadd);
        }
        else
        {
            List<Pair<String,Integer>> listTOadd = this.SubByUsername.get(username);
            listTOadd.add(p);
        }
    }

    /**
     * subscribe user to a genre and save his subscription id
     * @param dest
     * @param username
     * @param subID
     */
    public void subscribe_impl(String dest, String username, Integer subID)
    {
        User u = this.usersByUsername.get(username);
        if(channels.get(dest)==null)
        {
            channels.put(dest,new LinkedList<>());
        }
        channels.get(dest).add(u);


        Pair<String,User> p = new Pair(dest,u);
        if(subscriptionIdhashmap.get(subID)==null)
        {
            List<Pair<String,User>> l = new LinkedList<>();
            this.subscriptionIdhashmap.put(subID,l);
        }
        List<Pair<String,User>> list = this.subscriptionIdhashmap.get(subID);
        list.add(p);
        this.subscriptionIdhashmap.remove(subID);
        this.subscriptionIdhashmap.put(subID,list);

        int x = 123;
        updateMap(username,dest,subID);
    }

    /**
     * unsubscribe user from a genre and delete his records
     * @param subid
     * @param username
     */
    public void unsubscribe_imp(Integer subid,String username)
    {
        List users = this.subscriptionIdhashmap.get(subid);
        String genre="";
        for(int i=0; i < users.size(); i++) {
           User check = (User) ((Pair)users.get(i)).getValue();
           User myuser = usersByUsername.get(username);
           if(check.equals(myuser))
           {
               Pair<String,User> p = (Pair<String, User>) users.get(i);
               genre = (String) p.getKey();
               users.remove(users.get(i));
           }
        }
        if(genre!="") {
            List<User> userregister = this.channels.get(genre);
            User u = this.usersByUsername.get(username);
            userregister.remove(username);
        }
    }

    /**
     * delete user from maps
     * @param username
     */
    public void deleteuser(String username)
    {
        User u = usersByUsername.get(username);
        u.setActive(false);
        //this.usersByUsername.remove(username);
        for(String s : channels.keySet())
        {
            List<User> userregister = this.channels.get(s);
            if(userregister.contains(u))
                userregister.remove(u);
        }
        for(List<Pair<String,User>> l : subscriptionIdhashmap.values()) //delete all subscriptions
        {
            for(int i=0 ; i < l.size(); i++ )
            {
                if(l.get(i).getValue().equals(u))
                {
                    l.remove(i);
                }
            }
        }

    }

    public HashMap<String, User> getUsersByUsername() {
        return usersByUsername;
    }

    public void setUsersByUsername(String username , User user) {
        this.usersByUsername.put(username,user);
    }

}
