package Metrics;

import java.io.Serializable;

public class Variable implements Serializable{

    public  String   type = "";
    public String varName = "";
    public boolean isStatic = false;
    public boolean isArray =    false;

    public Variable(String varName, String type) {
        this.type = type;
        this.varName = varName;
    }

    public Variable(String varName, String type, boolean isArray) {
        this.type = type;
        this.varName = varName;
        this.isArray = isArray;
    }

    public Variable() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null)
            return false;
        Variable ne = (Variable) obj;
       // return this.type.equals(ne.type) && this.varName.equals(ne.varName);
        return this.varName.equals(ne.varName);
    }

    @Override
    public String toString() {
        return this.type + " " + this.varName;
    }

    @Override
    public int hashCode() {
        return this.varName.hashCode();
    }
}
