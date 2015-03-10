/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.result;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author User
 */
public class ResultPackage {
    private String name;
    private int factor;
    private ArrayList<ResultClass> children;

    public ResultPackage() {
        this.children = new ArrayList<ResultClass>();
    }

    public ResultPackage(String name) {
        this.name = name;
        this.children = new ArrayList<ResultClass>();
    }

    public ResultPackage(String name, ArrayList<ResultClass> children) {
        this.name = name;
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
        else return factor/(children.size());
        * */
        
        int tam = children.size();
        int r=0, y=0, g=0;
        if(tam == 0) return 1;
        if(tam == 1) return children.get(0).getFactor();
        for(ResultClass x : children){
            if(x.getFactor() == 200) r++;
            else if(x.getFactor() == 100) y++;
            else g++;
        }
        if(r > (tam/2)) return 200;
        if(g >= (tam/2)) return 1;
        
        return 100;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public ArrayList<ResultClass> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ResultClass> children) {
        this.children = children;
    }
    
    public void incrementFactor(int tam){
        this.factor += tam;
    }
    
    public void addClass(ResultClass rc){
        this.children.add(rc);
        this.factor += rc.getFactor();
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final ResultPackage other = (ResultPackage) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
    
    
}
