package Metrics;

import AndroidProjectRepresentation.APICallUtil;
import AndroidProjectRepresentation.AppInfo;
import AndroidProjectRepresentation.ClassInfo;
import GreenSourceBridge.GreenSourceAPI;
import org.json.simple.JSONObject;

public class tester {

    // deploy -> mvn install:install-file -DgroupId=com.greenlab -DartifactId=Metrics -Dversion=1.0 -Dpackaging=jar -Dfile=target/Metrics-1.0-SNAPSHOT.jar
   public static void main(String[] args) throws Exception {
          testJSONLoad();
        //   testServer();
        // testClassInst();
   }

    //


    public static void testClassInst() throws Exception{
      APICallUtil apu =    new APICallUtil();
      String file = "/Users/ruirua/tests/actual/27d5f1b6-d1b3-496b-b6c8-9ba25532a0b7/latest/_TRANSFORMED_/" +
              "app/src/main/java/com/micnubinub/materiallibrary/MaterialCheckBox.java";
      apu.proj.apps.add(new AppInfo());
      apu.processJavaFile(file);
      System.out.println(apu.proj);
    }


    public static void testJSONLoad(){

       String file = "/Users/ruirua/GDResults/27d5f1b6-d1b3-496b-b6c8-9ba25532a0b7/"
               + "MonkeyTest07_01_19_13_36_18/27d5f1b6-d1b3-496b-b6c8-9ba25532a0b7#com.micnubinub.materiallibrary.json";
       JSONObject jo = new APICallUtil().fromJSONFile(file);
        APICallUtil acu = ((APICallUtil) new APICallUtil().fromJSONObject( jo));
       for (ClassInfo c : acu.proj.getCurrentApp().allJavaClasses){
           System.out.println(c.className);
       }


   }


    public static void testServer(){
        JSONObject jo = new JSONObject();
        jo.put("device_serial_number", "1") ;
        jo.put("device_brand", "marroco21") ;
        jo.put("device_model", "ipheno21") ;

        GreenSourceAPI.sendDeviceToDB(jo.toJSONString());

       // GSUtils.sendJSONtoDB("http://greensource.di.uminho.pt/devices/", jo.toJSONString());

    }


}
