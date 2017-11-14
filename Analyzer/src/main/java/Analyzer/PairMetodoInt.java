package Analyzer;

/**
 * Created by rrua on 22/06/17.
 */
public class PairMetodoInt {

    String metodo;
    Integer state;
    Integer timeState;

    public PairMetodoInt(String s, int state){
        this.metodo = s;
        this.state = state;
    }

    public PairMetodoInt(String s, int state, int timeState){
        this.metodo = s;
        this.state = state;
        this.timeState = timeState;
    }

    public boolean equals(Object o) {
        if(o==null) return false;
        if(this==o) return true;
        PairMetodoInt c = (PairMetodoInt) o;
        if(this.metodo.equals(c.metodo) && this.state==c.state && this.timeState==c.timeState)
            return true;
        else return false;
    }


}
