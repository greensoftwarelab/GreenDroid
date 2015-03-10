/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.result;

import java.util.ArrayList;

/**
 *
 * @author User
 */
public class ResultClass {
    private String name;
    private int factor;
    private ArrayList<ResultMethod> children;

    public ResultClass() {
        factor = 0;
    }

    public ResultClass(String name, int factor) {
        this.name = name;
        this.factor = factor;
    }

    public ResultClass(String name, int factor, ArrayList<ResultMethod> children) {
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
        /*
        if(children.isEmpty()) return 0;
        else return factor/(children.size());*/
        int tam = children.size();
        int r=0, y=0, g=0;
        if(tam == 0) return 1;
        if(tam == 1) return children.get(0).getFactor();
        for(ResultMethod x : children){
            if(x.getFactor() == 200) y++;
            else if(x.getFactor() == 300) r++;
            else if (x.getFactor() == 100) g++;
        }
        if(r==0 && g==0 && y==0) return 400;
        if(r > (tam/2)) return 300;
        if(g >= (tam/2)) return 100;
        
        return 200;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public ArrayList<ResultMethod> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ResultMethod> children) {
        this.children = children;
    }
    
    public void incrementFactor(int tam){
        this.factor += tam;
    }
    
    
}
