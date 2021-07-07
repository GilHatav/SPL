package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

public class SquadTest {
    Squad s;
    List<String> stringList;
    Agent[] agentsArr;
    @BeforeEach
    public void setUp()
    {
        s= Squad.getInstance();
        agentsArr = new Agent[3];
        s.load(agentsArr);
    }

    @Test
    public void test_getAgents(){

        boolean expected = true;

        for(String serial : stringList)
        {
            boolean isExsist = false;
            for (Agent agent : agentsArr)
            {
                if(agent.getSerialNumber() == serial)
                    isExsist = true;
            }

            if(!isExsist) expected = false;

        }


        assert expected = true ;
    }


    @Test
    public void test_releseAgents(){

        s.releaseAgents(stringList);

        boolean expected = true;
        for (Agent agent : agentsArr)
        {
            for(String serial : stringList)
            {
                if(agent.getSerialNumber() == serial)
                {
                    if(!agent.isAvailable())
                        expected=false;
                }
            }
        }

        assert expected = true ;
    }

    @Test
    public void getAgentsNames()
    {
        List<String> output = s.getAgentsNames(stringList);
        List<String> expected = new LinkedList<>();

        for(Agent a : agentsArr)
        {
            for(String serial : stringList)
                if(a.getSerialNumber() == serial)
                    expected.add(a.getName());
        }

        boolean result =true;
        for(String name1 : expected)
        {
            boolean exsists = false;
            for(String name2 : output)
                if(name1 == name2) exsists = true;

            if(!exsists){
                result = false;
                break;
            }

        }

        assert result = true;


    }



    @Test
    public void sendAgentsTest()  {

        boolean expected = true;
        try
        {

            s.sendAgents(stringList,1000);
            Thread.sleep(1000);
            for(String serial : stringList)
            {
                for(Agent a : agentsArr)
                {
                    if(a.getSerialNumber() == serial)
                    {
                        if (!a.isAvailable()) expected = false;
                    }
                }
            }
        }
        catch(Exception e)
        {
            expected = false;
        }
        assert expected = true;
    }
}