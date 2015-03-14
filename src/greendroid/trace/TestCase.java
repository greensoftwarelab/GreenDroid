/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.trace;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author User
 */
public class TestCase {
    //private String tag; ??
    private Map<String, LinkedList<TracedMethod>> traced;
    private double time;
    private Consumption consumption;
    //private long meanSecond;
    private int excessive;
    
    public TestCase(){
        this.traced = new LinkedHashMap<String, LinkedList<TracedMethod>>();
        this.time = 1;
        this.consumption = new Consumption();
        this.excessive = 0;
        //this.meanSecond = 0;
    }
    
    public TestCase(Map<String, LinkedList<TracedMethod>> map, double t, Consumption c){
        this.traced = map;
        this.time = t;
        this.consumption = c;
        this.excessive = 0;
        //this.meanSecond = 0;
    }

    public Map<String, LinkedList<TracedMethod>> getTraced() {
        return traced;
    }

    public void setTraced(Map<String, LinkedList<TracedMethod>> traced) {
        this.traced = traced;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Consumption getConsumption() {
        return consumption;
    }

    public void setConsumption(Consumption consumption) {
        this.consumption = consumption;
    }

    public int getExcessive() {
        return excessive;
    }

    public void setExcessive(int excessive) {
        this.excessive = excessive;
    }
    
    public boolean containsKey(String key){
        return traced.containsKey(key);
    }
    
    public LinkedList<TracedMethod> get(String key){
        return traced.get(key);
    }
    
    public int getNumExecutions(){
        int ret = 0;
        for(String c : traced.keySet()){
            for(TracedMethod m : traced.get(c)){
                ret += m.getNum_exec();
            }
        }
        return ret;
    }
    
    public TracedMethod getMethod(String cla, String met){
        TracedMethod res = null;
        for(TracedMethod m : this.traced.get(cla)){
            if(m.getMethod().equals(met)) res=m;
        }
        return res;
    }
    
    public List<TracedMethod> getExecutedMethods(){
        LinkedList<TracedMethod> res = new LinkedList<>();
        for(String s : this.traced.keySet()){
            for(TracedMethod t : this.traced.get(s)){
                if(t.getExecuted() == 1) res.add(t);
            }
        }
        return res;
    }
    
    public boolean hasMethod(String cla, String met){
        if(!traced.containsKey(cla)) return false;
        else{
            TracedMethod t = new TracedMethod(met);
            for(TracedMethod trc : traced.get(cla)){
                if(trc.equals(t)) return trc.getExecuted() == 1;
            }
        }
        return false;
    }
    public void put(String key, LinkedList<TracedMethod> list){
        this.traced.put(key, list);
    }
    
    public void copyAll(Map<String, LinkedList<TracedMethod>> all){
        LinkedList<TracedMethod> list = new LinkedList<>();
        for(String s : all.keySet()){
            list = new LinkedList<>();
            for(TracedMethod t : all.get(s)){
                list.add(t.clone());
            }
            this.traced.put(s, list);
        }
//        this.traced=all;
    }
    
    public void clear(){
        this.traced.clear();
        this.time = 0;
    }
    
    public void print(){
        for(String x : traced.keySet()){
            System.out.println("---> "+x);
        }
    }
    
    public long getMeanSecond(){
        double t = time;
        double mean = (double)consumption.sum()/t;
        //double aux = (double)(consumptions.get(i).getLcd()/ss + consumptions.get(i).getCpu()/ss + consumptions.get(i).getWifi()/ss + consumptions.get(i).getG3()/ss + consumptions.get(i).getGps()/ss + consumptions.get(i).getAudio()/ss);
        return (long)mean;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.traced);
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
        final TestCase other = (TestCase) obj;
        if (!Objects.equals(this.traced, other.traced)) {
            return false;
        }
        return true;
    }
    
    @Override
    public TestCase clone(){
        TestCase clone = new TestCase(new LinkedHashMap<String, LinkedList<TracedMethod>>(), time, consumption);
        for(String x : traced.keySet()){
            LinkedList<TracedMethod> list = new LinkedList<TracedMethod>();
            for(TracedMethod t : traced.get(x)){
                list.add(t.clone());
            }
            clone.getTraced().put(x, list);
        }
        return clone;
    }
}
