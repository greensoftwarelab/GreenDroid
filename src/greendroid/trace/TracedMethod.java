/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.trace;

import java.util.Objects;

/**
 *
 * @author User
 */
public class TracedMethod {
    
    private String method;
    private int executed; //1-> executed; 0->not executed
    private int num_exec;
    private int red;
    private int orange;
    private int yellow;
    private int green;

    public TracedMethod(String method, int executed) {
        this.method = method;
        this.executed = executed;
        this.num_exec = 0;
        this.red = 0;
        this.green = 0;
        this.orange = 0;
        this.yellow = 0;
    }
    
    public TracedMethod(String method) {
        this.method = method;
        this.executed = 1;
        this.red = 0;
        this.green = 0;
        this.orange = 0;
        this.yellow = 0;
    }

    public TracedMethod(String method, int executed, int num_exec) {
        this.method = method;
        this.num_exec = num_exec;
        this.executed = executed;
        this.red = 0;
        this.green = 0;
        this.orange = 0;
        this.yellow = 0;
    }
    
    public TracedMethod(TracedMethod tm){
        this.method = tm.getMethod();
        this.executed = tm.getExecuted();
        this.num_exec = tm.getExecuted();
        this.red = 0;
        this.green = 0;
        this.orange = 0;
        this.yellow = 0;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getExecuted() {
        return executed;
    }

    public void setExecuted(int executed) {
        this.executed = executed;
    }
    
    public int getNum_exec() {
        return num_exec;
    }

    public void setNum_exec(int num_exec) {
        this.num_exec = num_exec;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getOrange() {
        return orange;
    }

    public void setOrange(int orange) {
        this.orange = orange;
    }

    public int getYellow() {
        return yellow;
    }

    public void setYellow(int yellow) {
        this.yellow = yellow;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }
    
    public void incRed(){
        red++;
    }
    
    public void incOrange(){
        orange++;
    }
    
    public void incYellow(){
        yellow++;
    }
    
    public void incGreen(){
        green++;
    }
    
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.method);
        hash = 29 * hash + this.executed;
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TracedMethod other = (TracedMethod) obj;
        if (!Objects.equals(this.method, other.method)) {
            return false;
        }

        return true;
    }
    
    public TracedMethod clone(){
        TracedMethod tm = new TracedMethod(method, executed, num_exec);
        return tm;
    }

    @Override
    public String toString() {
        return "" + method + "[" + executed + ']';
    }
    
}
