package GreenSourceBridge;


import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class JSONContainer implements JSONAware {

    Set<String> keys = new HashSet<>();
    JSONArray ja = new JSONArray();




    public void add(JSONObject jo, String keyValue){

        if (keys.equals("")){
            ja.add(jo);
        }

        else if (!keys.contains(keyValue)){
            ja.add(jo);
            keys.add(keyValue);
        }

    }


    public void addAll(JSONArray jas, String keyAttr){
        for (Object o : jas){
            JSONObject jo = ((JSONObject) o);
            String key = "";
            try {
                key = ((String) jo.get(keyAttr));
            }catch (ClassCastException e){

            }
            add(jo,key);
        }
    }


    public Collection<Object> getAll(){
        return ja;
    }

    public void clear(){
       ja.clear();
    }

    @Override
    public String toJSONString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < ja.size() ; i++) {
            sb.append(((JSONAware) ja.get(i)).toJSONString());
            if (i+1!=ja.size()){
                sb.append(",\n");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
