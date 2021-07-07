package bgu.spl.mics.application;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


import java.io.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
//import java.util.concurrent.Flow;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner  {
    public static void main(String[] args) {

        List<Subscriber> subscriberList = new LinkedList<>();
        File f = new File(args[0]);
        JsonParser jsonParser = new JsonParser();

        JsonElement jsonElement = null;
        try {
            jsonElement = jsonParser.parse(new FileReader(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //init inventory array
        JsonArray inventory_array = jsonElement.getAsJsonObject().getAsJsonArray("inventory");


        //init squad array
        JsonArray squad_array_gson = jsonElement.getAsJsonObject().getAsJsonArray("squad");
        Agent[] agents_array = new Agent[squad_array_gson.size()];
        for(int i =0 ; i< squad_array_gson.size() ; i++)
        {
            JsonElement agent = squad_array_gson.get(i);
            String name =agent.getAsJsonObject().get("name").getAsString();
            String serialnumber = agent.getAsJsonObject().get("serialNumber").getAsString();
            Agent a = new Agent();
            a.setName(name);
            a.setSerialNumber(serialnumber);
            agents_array[i] = a;
        }

        Squad s = Squad.getInstance();
        s.load(agents_array);

        //init M instances
        HashMap<Integer, M> MMap = new HashMap();
        JsonElement M_element = jsonElement.getAsJsonObject().getAsJsonObject("services").get("M");
        for(int i = 0 ; i <M_element.getAsInt() ; i++ )
        {
            M m = new M(i);
            subscriberList.add(m);
            MMap.put(i,m);
        }


        //init Moneypenny instances
        HashMap<Integer, Moneypenny> moneyPennyMap = new HashMap();
        JsonElement Moneypenny_element = jsonElement.getAsJsonObject().getAsJsonObject("services").get("Moneypenny");
        for(int i = 0 ; i <Moneypenny_element.getAsInt() ; i++ )
        {
            Moneypenny moneypenny = new Moneypenny(i);
            subscriberList.add(moneypenny);
            moneyPennyMap.put(i,moneypenny);
        }


        //time
        JsonElement time = jsonElement.getAsJsonObject().getAsJsonObject("services").get("time");

        TimeService timeService = TimeService.getInstance();
        timeService.setDuration(time.getAsInt());


        //init intelligence
        JsonArray intelligence_array = jsonElement.getAsJsonObject().getAsJsonObject("services").getAsJsonArray("intelligence");
        List<Intelligence> list_intelligence = new LinkedList<>();
        for(int i =0 ; i < intelligence_array.size() ; i++)
        {
            List<MissionInfo> listOFMissions = new LinkedList<>();
            JsonElement mission = intelligence_array.get(i).getAsJsonObject().get("missions");
            for(int j = 0 ; j < mission.getAsJsonArray().size() ; j++) {
                MissionInfo missionInfo = new MissionInfo();
                missionInfo.setDuration(mission.getAsJsonArray().get(j).getAsJsonObject().get("duration").getAsInt());
                missionInfo.setGadget(mission.getAsJsonArray().get(j).getAsJsonObject().get("gadget").getAsString());
                missionInfo.setMissionName(mission.getAsJsonArray().get(j).getAsJsonObject().get("name").getAsString());
                missionInfo.setTimeExpired(mission.getAsJsonArray().get(j).getAsJsonObject().get("timeExpired").getAsInt());
                missionInfo.setTimeIssued(mission.getAsJsonArray().get(j).getAsJsonObject().get("timeIssued").getAsInt());

                List<String> serialnumberagents = new LinkedList<>();
                for(int k = 0 ; k < mission.getAsJsonArray().get(j).getAsJsonObject().get("serialAgentsNumbers").getAsJsonArray().size() ; k++)
                {
                    serialnumberagents.add(mission.getAsJsonArray().get(j).getAsJsonObject().get("serialAgentsNumbers").getAsJsonArray().get(k).getAsString());
                }
                missionInfo.setSerialAgentsNumbers(serialnumberagents);

                listOFMissions.add(missionInfo);

            }

            Intelligence intelligence = new Intelligence(listOFMissions);
            subscriberList.add(intelligence);
            list_intelligence.add(intelligence);

        }
        Inventory inventory = Inventory.getInstance();
        String[] inv_array = new String[inventory_array.size()];
        for(int i=0; i < inventory_array.size() ; i++)
        {
            inv_array[i] = inventory_array.get(i).getAsString();
        }

        inventory.load(inv_array);
        Q q = new Q();

        subscriberList.add(q);


        List<Thread> threads = new LinkedList<>();
        for(int j = 0 ; j<subscriberList.size() ; j++)
        {
            Subscriber task = subscriberList.get(j);
            Thread thread = new Thread(task,task.getName());
            threads.add(thread);
            thread.start();
        }
        Thread timeThread = new Thread(timeService,"timeService");
        timeThread.start();

        for(Thread t : threads)
        {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        try {
            timeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Diary diary = Diary.getInstance();
        inventory.printToFile(args[1]);
        diary.printToFile(args[2]);

    }


}