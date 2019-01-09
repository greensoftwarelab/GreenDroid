package GreenSourceBridge;

import AndroidProjectRepresentation.JSONSerializable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.Serializable;

public class DeviceState implements Serializable, JSONSerializable {

    public double memory = 0;
    public double cpu = 0;
    public double gpu = 0;
    public float androidVersion=0;
    public float apiLevel=0;
    public int nr_processes_running=0;


    @Override
    public JSONObject toJSONObject(String requiredId) {
        JSONObject jo = new JSONObject();
        jo.put("device_state_mem", memory);
        jo.put("device_state_cpu_free",cpu );
        jo.put("device_state_nr_processes_running", nr_processes_running );
        jo.put("device_state_api_level",apiLevel );
        jo.put("device_state_android_version", androidVersion );
        return jo;
    }

    @Override
    public JSONSerializable fromJSONObject(JSONObject jo) {
        DeviceState d = new DeviceState();
        d.androidVersion = ((float) jo.get("device_state_android_version"));
        d.apiLevel = ((float) jo.get("device_state_api_level"));
        d.nr_processes_running = ((int) jo.get("device_state_nr_processes_running"));
        d.cpu = ((double) jo.get("device_state_cpu_free"));
        d.memory = ((double) jo.get("device_state_mem"));
        return d;
    }

    @Override
    public JSONObject fromJSONFile(String pathToJSONFile) {
        JSONParser parser = new JSONParser();
        JSONObject ja = new JSONObject();
        try {
            Object obj = parser.parse(new FileReader(pathToJSONFile));
            JSONObject jsonObject = (JSONObject)obj;
            if (jsonObject.containsKey("device_state_mem")) {
                return jsonObject;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }
        return ja;
    }
}
