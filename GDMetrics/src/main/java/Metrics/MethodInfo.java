package Metrics;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MethodInfo implements Serializable {
    public Set<MethodOfAPI> externalApi = new HashSet<>();
    public Set<MethodOfAPI> androidApi = new HashSet<>();
    public Set<MethodOfAPI> javaApi = new HashSet<>();
    public Set<MethodOfAPI> unknownApi = new HashSet<>();
    public Set<Variable> declaredVars = new HashSet<>();
    public Set<Variable> args = new HashSet<>();
    public int linesOfCode = 0;
    public int cyclomaticComplexity = 0;
    public int nr_args = 0; // TODO remove this
    public String methodName = "";
    public ClassInfo ci = null;
    public boolean isStatic = false;


    public  boolean isInDeclaredvars(String var){
        if (var==null)
            return false ;
        for (Variable v : this.declaredVars){
            if(v==null)
                continue;
            if(v.varName==null)
                continue;
            if (var.equals(v.varName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.methodName.hashCode() + new Integer(this.nr_args).hashCode();
    }

    @Override
    public String toString() {
        return methodName + "  cc -> " + cyclomaticComplexity + " loc -> " + linesOfCode  ;
    }

    // Add correct
    public void addRespectiveAPI(MethodOfAPI x){
        if (!this.declaredVars.contains(new Variable(x.api,""))){
            if(this.ci.classVariables.containsKey(x.api)){
                this.unknownApi.add(new MethodOfAPI(this.ci.classVariables.get(x.api).type,x.method));
                return;
            }
            else {
                for (Variable v : this.args){
                    if (v.varName.equals(x.api)){
                        this.unknownApi.add(new MethodOfAPI(v.type,x.method));
                        return;
                    }

                }
            }
        }
        else {
            for (Variable v : this.declaredVars){
                if (v.varName.equals(x.api)){
                    this.unknownApi.add(new MethodOfAPI(v.type,x.method));
                    return;
                }

            }
        }

        this.unknownApi.add(x);

    }


    public Set<String> getApisUsed(){
        Set <String> l = new HashSet<>();
        for (MethodOfAPI s : this.androidApi) {
            l.add(s.api);
        }
        for (MethodOfAPI s : this.javaApi) {
            l.add(s.api);
        }
        for (MethodOfAPI s : this.externalApi) {
            l.add(s.api);
        }
        return l;
    }
}
