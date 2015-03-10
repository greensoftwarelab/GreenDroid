/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.result;

import greendroid.result.ResultPackage;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class Result {
    private String name;
    private int factor;
    private ArrayList<ResultPackage> children;

    public Result() {
        
    }
    
    public Result(String name, int factor, ArrayList<ResultPackage> children) {
        this.name = name;
        this.factor = factor;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFactor() {
        if(children.isEmpty()) return 0;
        else return (factor/children.size());
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public ArrayList<ResultPackage> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ResultPackage> children) {
        this.children = children;
    }
    
    public void incrementFactor(int tam){
        this.factor += tam;
    }
    
}
