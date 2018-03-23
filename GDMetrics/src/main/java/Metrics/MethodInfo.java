package Metrics;



import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class MethodInfo implements Serializable {
    public Set<String> externalApi = new HashSet<>();
    public Set<String> androidApi = new HashSet<>();
    public Set<String> javaApi = new HashSet<>();
    public Set<String> unknownApi = new HashSet<>();
    public Set<Variable> declaredVars = new HashSet<>();
    public Set<Variable> args = new HashSet<>();
    public int linesOfCode = 0;
    public int cyclomaticComplexity = 0;
    public int nr_args = 0; // TODO remove this
    public String methodName = "";
    public ClassInfo ci = null;
    public boolean isStatic = false;


    @Override
    public int hashCode() {
        return this.methodName.hashCode() + new Integer(this.nr_args).hashCode();
    }

    @Override
    public String toString() {
        return methodName + "  cc -> " + cyclomaticComplexity + " loc -> " + linesOfCode  ;
    }

    // Add correct
    public void addRespectiveAPI(String x){
        if (!this.declaredVars.contains(x)){
            if(this.ci.classVariables.containsKey(x)){
                this.unknownApi.add(this.ci.classVariables.get(x).type);
            }
            else {
                for (Variable v : this.args){
                    if (v.varName.equals(x))
                        this.unknownApi.add(v.type);
                }
            }
        }
        else {
            for (Variable v : this.declaredVars){
                if (v.varName.equals(x))
                    this.unknownApi.add(v.type);
            }
        }

    }
}
