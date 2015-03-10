/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid;

import java.util.Objects;

/**
 *
 * @author User
 */
public class TracedMethod {
    
    private String method;
    private int executed; //1-> executed; 0->not executed
    private int num_exec;

    public TracedMethod(String method, int executed) {
        this.method = method;
        this.executed = executed;
        this.num_exec = 0;
    }
    
    public TracedMethod(String method) {
        this.method = method;
        this.executed = 1;
    }

    public TracedMethod(String method, int executed, int num_exec) {
        this.method = method;
        this.num_exec = num_exec;
        this.executed = executed;
    }
    
    public TracedMethod(TracedMethod tm){
        this.method = tm.getMethod();
        this.executed = tm.getExecuted();
        this.num_exec = tm.getExecuted();
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
}
