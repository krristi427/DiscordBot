package dataObjects;

import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;

public class Poll {
    String participants;
    ArrayList<Possibility> possibilities;
    public enum Pollingtypes{PRIVATE,PUBLIC,QUICK};
    public static Pollingtypes pollingtyp;
    //private static ArrayList<Integer> poll_inputs;

    public Poll(Pollingtypes pollingtyp, String[] answers)
    {
        this.possibilities = new ArrayList<>();
        this.pollingtyp=pollingtyp;
        for (String ans : answers)
        {
            this.possibilities.add(new Possibility(ans));
        }
    }
    public void addToPossibilitie(String answer)
    {
        for(Possibility p : possibilities)
        {
            if (p.answer.compareTo(answer)==0)
            {
                p.addCount();
            }
        }
    }

    public void addToPossibilitie(int answerIndex)
    {
        possibilities.get(answerIndex).addCount();
    }

    public String[] getAnswers() {
        String[] output = new String[possibilities.size()];
        for(int i=0;i<possibilities.size();i++)
        {
            output[i]=possibilities.get(i).answer;
        }
        return output;
    }

    public ArrayList<Possibility> getPossibilities() {
        return possibilities;
    }

    public String[] getPollingResults(){
        String[] output = new String[possibilities.size()];
        for(int i=0;i<possibilities.size();i++)
        {
            output[i]=possibilities.get(i).answer+":"+possibilities.get(i).count;
        }
        return output;
    }

    public void printPoll(MessageChannel channel, int index)
    {
        String output = "";
        for (int i=0; i < possibilities.size(); i++)
        {
            output += i+": "+possibilities.get(i)+"\n";
        }
        channel.sendMessage("--Poll: "+(index)+" --\n"+output+"---------").queue();
    }



}
