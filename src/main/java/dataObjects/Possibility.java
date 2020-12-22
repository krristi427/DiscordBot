package dataObjects;

public class Possibility {
    String answer;
    int count;
    public Possibility(String answer)
    {
        this.answer=answer;
        this.count=0;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount(){
        count++;
    }
}
