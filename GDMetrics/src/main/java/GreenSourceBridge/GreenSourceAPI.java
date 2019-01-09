package GreenSourceBridge;

import AndroidProjectRepresentation.MethodInfo;
import AndroidProjectRepresentation.Variable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GreenSourceAPI {

    public static boolean operationalBackend = true;
    //private static final String greenRepoURL = "http://localhost:8000/";
    private static  String greenRepoURL = "http://greensource.di.uminho.pt/";
    private static String testUlr = "tests/";
    private static String appsUlr = "apps/";
    private static String projectsUlr = "projects/";
    private static String testsResultsUlr = "tests/results/";
    private static String appPermissionsUrl = "apps/permissions/";
    private static String devicesUrl = "devices/";
    private static String methodsUrl = "methods/";
    private static String classesUrl = "classes/";
    private static String methodsMetricsUrl = "methods/metrics/";
    private static String appsMetricsUrl = "apps/metrics/";
    private static String classesMetricsUrl = "classes/metrics/";
    private static String methodsInvokedUrl = "methods/invoked/";
    private static String testsMetricsUrl = "tests/metrics/";
    private static String importsURL = "imports/";


    public JSONObject project;
    public JSONObject app;
    public JSONObject device;
    public JSONObject test;
    public JSONObject testResults;
    public JSONContainer allTestResults;
    public JSONContainer methods;
    public JSONContainer classes;
    public JSONArray methodMetrics;
    public JSONArray classMetrics;
    public JSONArray appMetrics;
    public JSONArray testMetrics;
    public JSONObject deviceStateEnd;
    public JSONObject deviceStateBegin;
    public JSONContainer methodsInvoked;
    public JSONContainer classImports;


    public GreenSourceAPI() {
        this.project = new JSONObject();
        this.app = new JSONObject();
        this.test = new JSONObject();
        this.testResults = new JSONObject();
        this.methods = new JSONContainer();
        this.classes = new JSONContainer();
        this.classMetrics = new JSONArray();
        this.appMetrics = new JSONArray();
        this.methodMetrics = new JSONArray();
        this.testMetrics = new JSONArray();
        this.methodsInvoked = new JSONContainer();
        this.deviceStateBegin = new JSONObject();
        this.deviceStateEnd = new JSONObject();
        this.device=new JSONObject();
        this.allTestResults = new JSONContainer();
        this.classImports = new JSONContainer();
    }

    public String getActualTestID(){
        if (this.test!=null){
            if (this.test.containsKey("id"))
                return this.test.get("id").toString();
        }
        return "-1";
    }



    public String getActualTestResultsID(){
        if (this.testResults.containsKey("test_results_id"))
            return this.testResults.get("test_results_id").toString();
        return "-1";
    }


    public String getActualDeviceID(){
        if (this.device.containsKey("device_serial_number"))
            return this.device.get("device_serial_number").toString();
        return "ERROR";
    }




    public String getGreenRepoURL(){
     return greenRepoURL;
    }

    public void setGreenRepoUrl(String url){
        greenRepoURL= url;
    }



    public static JSONArray loadAppPermissions(String appPermissionsJSONFile){
        JSONParser parser = new JSONParser();
        JSONArray ja = new JSONArray();
        try {
            Object obj = parser.parse(new FileReader(appPermissionsJSONFile));
            JSONArray jsonObject = (JSONArray) obj;
            ja = (JSONArray) jsonObject;

        } catch (FileNotFoundException e) {
            System.out.println("[ANALYZER] No appPermissions.json file. File not found");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ja;
    }




    public static JSONObject loadDevice(String deviceJSONFile){
        JSONParser parser = new JSONParser();
        JSONObject ja = new JSONObject();
        try {
            Object obj = parser.parse(new FileReader(deviceJSONFile));
            JSONObject jsonObject = (JSONObject) obj;
            if (!jsonObject.containsKey("device_serial_number")){
                System.out.println("FATAL ERROR! Error in application json");
            }
            else
                return jsonObject;

        } catch (FileNotFoundException e) {
            System.out.println("[ANALYZER] No device.json file. File not found");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ja;
    }


    public JSONObject loadDeviceState(String deviceStateFile) {
        return new DeviceState().fromJSONFile(deviceStateFile);
    }

    public static JSONObject loadApplication(String appJSONFile){
        JSONParser parser = new JSONParser();
        JSONObject ja = new JSONObject();
        try {
            Object obj = parser.parse(new FileReader(appJSONFile));
            JSONObject jsonObject = (JSONObject) obj;
            if (!jsonObject.containsKey("app_id")){
                System.out.println("FATAL ERROR! Error in application json");
            }
            else
                return jsonObject;

        } catch (FileNotFoundException e) {
            System.out.println("[ANALYZER] No application.json file. File not found");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ja;
    }

    public static Object sendImportsToDB(String json) {

        if (operationalBackend){
            String response = GSUtils.sendJSONtoDB(greenRepoURL + importsURL, json).second;
            JSONParser parser = new JSONParser();
            //JSONArray jsonObject = new JSONArray();
            Object jsonObject;
            try {
                Object obj = parser.parse(response);
               jsonObject = (Object) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
            return new JSONObject();

    }



    public static JSONObject sendTestToDB(String json) {

       if (operationalBackend){
           String response = GSUtils.sendJSONtoDB(greenRepoURL + testUlr, json).second;
           JSONParser parser = new JSONParser();
           JSONObject jsonObject = new JSONObject();
           try {
               Object obj = parser.parse(response);
               jsonObject = (JSONObject) obj;

           } catch (ParseException e) {
               e.printStackTrace();
           }

           if (!jsonObject.containsKey("id")) {
               System.out.println("FATAL ERROR ! There is an error in test JSON. Not submitting results do DBase");
               return null;

           } else return jsonObject;
       }
       else
           return new JSONObject();

    }






    public static JSONObject sendTestResultToDB(String json) {


        if (operationalBackend){
            String response = (GSUtils.sendJSONtoDB(greenRepoURL + testsResultsUlr, json)).second;
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = new JSONObject();
            try {
                Object obj = parser.parse(response);
                jsonObject = (JSONObject) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (!jsonObject.containsKey("test_results_id")) {
                System.out.println("FATAL ERROR ! There is an error in test results JSON. Not submitting results do DBase");
                return null;

            } else return jsonObject;
        }
        else
            return new JSONObject();

    }


    public static JSONArray sendTestResultsToDB(String json) {

        if (operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + testsResultsUlr, json);
            JSONParser parser = new JSONParser();
            JSONArray jsonObject = new JSONArray();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or allTestResults JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONArray) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }
        else
            return new JSONArray();

    }

    public static JSONArray sendMethodsToDB(String json) {


        if (operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + methodsUrl, json);
            JSONParser parser = new JSONParser();
            JSONArray jsonObject = new JSONArray();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or methods JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONArray) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }
        else
            return new JSONArray();

    }


    public static JSONArray sendMethodsMetricsToDB(String json) {

        if (operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + methodsMetricsUrl, json);
            JSONParser parser = new JSONParser();
            JSONArray jsonObject = new JSONArray();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or methods JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONArray) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }
        else
            return new JSONArray();

    }


    public static JSONArray sendTestsMetricsToDB(String json) {


        if (operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + testsMetricsUrl, json);
            JSONParser parser = new JSONParser();
            JSONArray jsonObject = new JSONArray();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or methods JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONArray) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }
        else
            return new JSONArray();
    }




    public String getIDFromMethodInvokation(String methodID){

        for (Object jo : this.methodsInvoked.getAll()){
            JSONObject o = ((JSONObject) jo);
            if (o.containsKey("method")){
                if (o.get("method").equals(methodID)){
                    return o.get("id").toString();
                }
            }
        }
        return "null";
    }


    public static String generateMethodID(MethodInfo mi ){
        String args = "";
        for (Variable v : mi.args){
            args+=v.isArray+v.type+v.varName;
        }
        String metId= mi.ci.classPackage+"."+mi.ci.className+"."+ mi.methodName+"."+args.hashCode();
        return  metId;
    }

    public static JSONObject getMethodInvoked ( String methodId, String testResID  ) {

        JSONObject test = new JSONObject();
        test.put("test_results", testResID);
        test.put("method", methodId);
        return test;
    }


    public static JSONObject sendMethodInvokedToDB(String json) {


        if(operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + methodsInvokedUrl, json);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = new JSONObject();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or methods JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONObject) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        else return new JSONObject();
    }



    public static JSONArray sendMethodsInvokedToDB(String json) {

        if (operationalBackend){

            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + methodsInvokedUrl, json);
            JSONParser parser = new JSONParser();
            JSONArray jsonObject = new JSONArray();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or methods JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONArray) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        else{
            return  new JSONArray();
        }


    }

    public static JSONObject sendProjectToDB(String json) {


        if (operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + projectsUlr, json);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = new JSONObject();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or methods JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONObject) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        else{
            return  new JSONObject();
        }


    }
    public static JSONArray sendClassesToDB(String json) {

        if (operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + classesUrl, json);
            JSONParser parser = new JSONParser();
            JSONArray jsonObject = new JSONArray();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or methods JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONArray) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        else{
            return  new JSONArray();
        }


    }

    public static JSONArray sendAppMetricsToDB(String json) {

        if (operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + appsMetricsUrl, json);
            JSONParser parser = new JSONParser();
            JSONArray jsonObject = new JSONArray();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or methods JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONArray) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        else{
            return  new JSONArray();
        }

    }

    public static JSONArray sendClassMetricsToDB(String json) {

        if (operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + classesMetricsUrl, json);
            JSONParser parser = new JSONParser();
            JSONArray jsonObject = new JSONArray();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or methods JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONArray) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        else{
            return  new JSONArray();
        }


    }





    public static JSONArray sendAppPermissionsToDB(String json) {


        if(operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + appPermissionsUrl, json);
            JSONParser parser = new JSONParser();
            JSONArray jsonObject = new JSONArray();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or AppPermissions JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONArray) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }
        else return new JSONArray();


    }

    public static JSONObject sendApplicationToDB(String json) {


        if(operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + appsUlr, json);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = new JSONObject();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or AppPermissions JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONObject) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }


            return jsonObject;
        }
        else return new JSONObject();


    }

    public static JSONObject sendDeviceToDB(String json) {

        if(operationalBackend){
            Pair<Integer,String> res = GSUtils.sendJSONtoDB(greenRepoURL + devicesUrl, json);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = new JSONObject();
            if (res.first!=200) {
                System.out.println("FATAL ERROR ! HTTP CODE != 200 . something went wrong check internet connection or AppPermissions JSON FILE");
                return null;
            }
            try {
                Object obj = parser.parse(res.second);
                jsonObject = (JSONObject) obj;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (!jsonObject.containsKey("device_serial_number")) {
                System.out.println("FATAL ERROR ! There is an error in test results JSON. Not submitting results do DBase");
                return null;

            } else return jsonObject;
        }
        else return new JSONObject();

    }

}
