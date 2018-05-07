package GDUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GreenRepoRun {
    private static final String greenRepoURL = "http://localhost:8000/";
    private static String testUlr = "tests/";
    private static String appsUlr = "apps/";
    private static String testsResultsUlr = "tests/results/";
    private static String appPermissionsUrl = "apps/permissions/";
    private static String devicesUrl = "devices/";
    private static String methodsUrl = "methods/";
    private static String methodsMetricsUrl = "methods/metrics/";
    private static String methodsInvokedUrl = "methods/invoked/";
    private static String testsMetricsUrl = "tests/metrics/";

    public JSONObject app;
    public JSONObject device;
    public JSONObject test;
    public JSONObject testResults;
    public JSONArray allTestResults;
    public JSONArray methods;
    public JSONArray methodMetrics;
    public JSONArray testMetrics;
    public JSONObject deviceStateEnd;
    public JSONObject deviceStateBegin;
    public JSONArray methodsInvoked;




    public String getActualTestID(){
        if (this.test.containsKey("id"))
            return this.test.get("id").toString();
        return "";
    }



    public String getActualTestResultsID(){
        if (this.testResults.containsKey("test_results_id"))
            return this.testResults.get("test_results_id").toString();
        return "";
    }


    public String getActualDeviceID(){
        if (this.device.containsKey("device_serial_number"))
            return this.device.get("device_serial_number").toString();
        return "";
    }

    public GreenRepoRun() {
        this.app = new JSONObject();
        this.test = new JSONObject();
        this.testResults = new JSONObject();
        this.methods = new JSONArray();
        this.methodMetrics = new JSONArray();
        this.testMetrics = new JSONArray();
        this.methodsInvoked = new JSONArray();
        this.deviceStateBegin = new JSONObject();
        this.deviceStateEnd = new JSONObject();
        this.device=new JSONObject();
        this.allTestResults = new JSONArray();
    }

    public static JSONArray loadAppPermissions(String appPermissionsJSONFile){
        JSONParser parser = new JSONParser();
        JSONArray ja = new JSONArray();
        try {
            Object obj = parser.parse(new FileReader(appPermissionsJSONFile));
            JSONArray jsonObject = (JSONArray) obj;
            JSONArray msg = (JSONArray) jsonObject;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ja;
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ja;
    }


    public static JSONObject sendTestToDB(String json) {
        String response = GDUtils.sendJSONtoDB(greenRepoURL + testUlr, json).second;
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


    public static JSONObject sendTestResultToDB(String json) {
        String response = (GDUtils.sendJSONtoDB(greenRepoURL + testsResultsUlr, json)).second;
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


    public static JSONArray sendTestResultsToDB(String json) {

        Pair<Integer,String> res = GDUtils.sendJSONtoDB(greenRepoURL + testsResultsUlr, json);
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

    public static JSONArray sendMethodsToDB(String json) {

        Pair<Integer,String> res = GDUtils.sendJSONtoDB(greenRepoURL + methodsUrl, json);
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


    public static JSONArray sendMethodsMetricsToDB(String json) {

        Pair<Integer,String> res = GDUtils.sendJSONtoDB(greenRepoURL + methodsMetricsUrl, json);
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

    public static JSONArray sendTestsMetricsToDB(String json) {

        Pair<Integer,String> res = GDUtils.sendJSONtoDB(greenRepoURL + testsMetricsUrl, json);
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

    public static JSONArray sendMethodsInvokedToDB(String json) {

        Pair<Integer,String> res = GDUtils.sendJSONtoDB(greenRepoURL + methodsInvokedUrl, json);
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

    public static JSONArray sendAppPermissionsToDB(String json) {

        Pair<Integer,String> res = GDUtils.sendJSONtoDB(greenRepoURL + appPermissionsUrl, json);
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

    public static JSONObject sendApplicationToDB(String json) {

        Pair<Integer,String> res = GDUtils.sendJSONtoDB(greenRepoURL + appsUlr, json);
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

    public static JSONObject sendDeviceToDB(String json) {

        Pair<Integer,String> res = GDUtils.sendJSONtoDB(greenRepoURL + devicesUrl, json);
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

}
