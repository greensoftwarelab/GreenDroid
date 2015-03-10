/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.result;

/**
 *
 * @author User
 */
public class ResultMethod {
    private String name;
    private int size;
    private int factor;

    public ResultMethod(String name, int size, int factor) {
        this.name = name;
        this.size = size;
        this.factor = factor;
    }
    
    public ResultMethod(String name) {
        this.name = name;
        this.size = 1;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int fac) {
        if(fac == 0) this.factor = 1;
        else this.factor = fac;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
}
