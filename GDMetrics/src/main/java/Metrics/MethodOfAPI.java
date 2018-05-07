package Metrics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MethodOfAPI implements Serializable {

    public String api ;
    public String method;
    public List<Variable> args = new ArrayList<>();


    public MethodOfAPI(String apis, String met) {
        this.api = apis;
        this.method = met;
    }

    public MethodOfAPI(String apis) {
        this.api = apis;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null)
            return false;
        MethodOfAPI ne = (MethodOfAPI) obj;
        return this.api.equals(ne.api) && ( (this.method!=null&& ((MethodOfAPI) obj).method!= null) ? this.method.equals(ne.method) : true);
    }

    @Override
    public int hashCode() {
        return this.api.hashCode() + ( this.method!=null? this.method.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "Class " + this.api + " Method " + this.method;
    }
}
