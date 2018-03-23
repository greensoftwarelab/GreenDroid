package Metrics;



import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;


public class ClassInfo implements Serializable {

    public Set<NameExpression> classImports = new HashSet<>();
    public Map<String, MethodInfo> classMethods = new HashMap<>();
    public String classPackage = "";
    public String className = "";
    public Map<String, Variable> classVariables = new HashMap<>();
    public String extendedClass = null;

    @Override
    public int hashCode() {
        return this.className.hashCode();
    }


    public String getFullClassName(){
        return classPackage +"." + className;
    }

    public MethodInfo getMethod(String methodName){
        return classMethods.get(methodName);
    }

}
