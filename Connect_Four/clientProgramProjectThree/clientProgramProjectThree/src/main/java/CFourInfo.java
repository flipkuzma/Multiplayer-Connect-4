import java.io.Serializable;
import java.util.ArrayList;

public class CFourInfo implements Serializable {
    private ArrayList<Short> p1Checkers = new ArrayList<>();
    private ArrayList<Short> p2Checkers = new ArrayList<>();
    private String state;

    public CFourInfo(String state){
        this.state = state;
    }




    public void setP1Checkers(ArrayList<Short> p1Checkers){
        this.p1Checkers = p1Checkers;
    }
    public void setP2Checkers(ArrayList<Short> p2Checkers){
        this.p2Checkers = p2Checkers;
    }
    public void setState(String state){
        this.state = state;
    }
    public String getState(){
        return this.state;
    }
    public ArrayList<Short> getP1Checkers(){
        return p1Checkers;
    }
    public ArrayList<Short> getP2Checkers(){
        return p2Checkers;
    }


}
