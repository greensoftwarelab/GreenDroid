package Metrics.AndroidProjectRepresentation;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.Serializable;

public class Variable implements Serializable, JSONSerializable{

    public  String   type = "";
    public String varName = "";
    public boolean isStatic = false;
    public boolean isArray =    false;
    public boolean isFinal = false;
    public boolean isVolatile = false;
    public boolean isTransient = false;




    public Variable(String varName, String type) {
        this.type = type;
        this.varName = varName;
    }

    public Variable(String varName, String type, boolean isArray) {
        this.type = type;
        this.varName = varName;
        this.isArray = isArray;
    }

    public Variable() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null)
            return false;
        Variable ne = (Variable) obj;
       // return this.type.equals(ne.type) && this.varName.equals(ne.varName);
        return this.varName.equals(ne.varName);
    }

    @Override
    public String toString() {
        return this.type + " " + this.varName;
    }

    @Override
    public int hashCode() {
        return this.varName.hashCode();
    }

    @Override
    public JSONObject toJSONObject(String requiredId) {
        JSONObject jo = new JSONObject();
        jo.put("var_type", type);
        jo.put("var_name", varName);
        jo.put("var_is_array", isArray);
        return jo;
    }

    @Override
    public JSONSerializable fromJSONObject(JSONObject jo) {
        Variable v = new Variable();
        v.type = ((String) jo.get("var_type"));
        v.varName = ((String) jo.get("var_name"));
        v.isArray = jo.get("var_is_array") != null && ((boolean) jo.get("var_is_array"));

        return v;
    }

    @Override
    public JSONObject fromJSONFile(String pathToJSONFile) {
        JSONParser parser = new JSONParser();
        JSONObject ja = new JSONObject();

        try {
            Object obj = parser.parse(new FileReader(pathToJSONFile));
            JSONObject jsonObject = (JSONObject)obj;
            if (jsonObject.containsKey("var_type")) {
                return jsonObject;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return ja;

    }
}
