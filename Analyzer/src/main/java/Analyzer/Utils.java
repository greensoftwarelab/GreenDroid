package Analyzer;

import Analyzer.Results.Results;
import Metrics.AndroidProjectRepresentation.MethodInfo;
import Metrics.*;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.omg.CORBA.INTERNAL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import Metrics.AndroidProjectRepresentation.*;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import  GreenSourceBridge.GreenSourceAPI;
import static GreenSourceBridge.GSUtils.sendJSONtoDB;

/**
 * Created by rrua on 03/07/17.
 */
public class Utils {

    // Time  [ms]	Battery Remaining (%) [%]	Time  [ms]	Battery Status	Time  [ms]	Screen Brightness	Time  [ms]	Battery Power* [uW] (Raw)	Battery Power* [uW] (Delta)	Time  [ms]	GPU Frequency [KHz]	Time  [ms]	GPU Load [%]	Time  [ms]	CPU1 Frequency [kHz]	Time  [ms]	CPU2 Frequency [kHz]	Time  [ms]	CPU3 Frequency [kHz]	Time  [ms]	CPU4 Frequency [kHz]	Time  [ms]	CPU1 Load [%]	Time  [ms]	CPU2 Load [%] Time  [ms]	CPU3 Load [%]	Time  [ms]	CPU4 Load [%]	Time  [ms]	Application State	Description


    public static final String timeNormal = "Time.*";
    public static final String batteryPower = "Battery\\ Power.*";
    public static final String batteryPowerDelta = "Battery\\ Power.*Delta.*";
    public static final String batteryStatus = "Battery\\ Status.*";
    public static final String stateInt = "Application\\ State.*";
    public static final String stateDescription = "Description.*";
    public static final String batteryRemaining = "Battery\\ Remaining.*";
    public static final String screenbrightness = "Screen\\ Brightness.*";
    public static final String gpufreq = "GPU\\ Frequency*";
    public static final String gpuLoad = "GPU\\ Load.*";
    public static final String cp1freq = "CPU1\\ Frequency.*";
    public static final String cp2freq = "CPU2\\ Frequency.*";
    public static final String cp3freq = "CPU3\\ Frequency.*";
    public static final String cp4freq = "CPU4\\ Frequency.*";
    public static final String cpufreq = "CPU\\ Frequency.*";
    public static final String cp1Load = "CPU1\\ Load.*";
    public static final String cp2Load = "CPU2\\ Load.*";
    public static final String cp3Load = "CPU3\\ Load.*";
    public static final String cp4Load = "CPU4\\ Load.*";
    public static final String cpuLoad = "CPU\\ Load.*";
    public static final String cpuLoadNormalized = "CPU\\ Load.*Normalized.*";
    public static final String memory = "Memory\\ Usage.*";
    public static final String mobileData = "Mobile.*";
    public static final String wifiState = "Wi-Fi\\ State.*";
    public static final String wifiRSSILevel = "Wi-Fi\\ RSSI.*";
    public static final String screenState = "Screen\\ State.*";
    public static final String bluetoothState = "Bluetooth\\ State.*";
    public static final String gpsState = "GPS\\ State.*";



    public enum Bluetooth {
        OFF , ON, BOTH
    }

    public enum BatteryStatus {
        CHARGING_USB, CHARGING_AC, CHARGING_UNKNOWN , NOT_CHARGING
    }
    public enum ScreenState {
        OFF , ON
    }

    public enum WifiState {
        DISABLED, UNKNOWN , ENABLED
    }

    public enum GPSState {
        DISABLED, UNKNOWN , ENABLED
    }

    public enum MobileDataState {
        DISCONNECTED, CONNECTED_DORMANT, CONNECTED_NO_TRAFFIC , CONNECTED_SENDING, CONNECTED_RECEIVING, CONNECTED_BOTH_RS
    }



    public static Pair<Integer, Integer> getMatch(HashMap<String, Pair<Integer, Integer>> hashMap, String s) {

        for (String st : hashMap.keySet()) {
            if (st.matches(s))
                return hashMap.get(st);
        }
        return null;
    }

    // pair tempo -> coluna
    public static HashMap<String, Pair<Integer, Integer>> fetchColumns(List<String[]> resolvedData) {
        HashMap<String, Pair<Integer, Integer>> hashMap = new HashMap<String, Pair<Integer, Integer>>(); //Column name -> (TimeColumn,valueColumnn)
        String[] row = new String[100];
        for (int i = 0; i < resolvedData.size(); i++) {
            row = resolvedData.get(i);
            if (row.length <= 0 || row[0] == null) continue;
            if (row[0].matches("Time.*")) {// tou na linha do cabeçalho da tabela
                for (int j = 0; j < row.length; j++) {
                    if (row[j] == null) continue;
                    if (row[j].matches("Time.*")) { // tou na linha do cabeçalho da tabela
                        hashMap.put(row[j + 1], new Pair<Integer, Integer>(j, j + 1));
                    } else if (row[j].matches(stateDescription + ".*")) {
                        hashMap.put(row[j], new Pair<Integer, Integer>(j - 2, j));
                    } else if (row[j].matches(batteryPowerDelta + ".*")) {
                        hashMap.put(row[j], new Pair<Integer, Integer>(j - 2, j));
                    }
                }
                break;
            }
        }
        return hashMap;
    }


    // XML UTILS

    public static List<Issue> parseLintResulsXML(String file) {
        List<Issue> list = new ArrayList<Issue>();
        try {
            File fXmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList issueList = doc.getDocumentElement().getElementsByTagName("issue");
            for (int temp = 0; temp < issueList.getLength(); temp++) {
                Issue issue = new Issue();
                issue.id = issueList.item(temp).getAttributes().getNamedItem("id").getNodeValue();
                issue.message = issueList.item(temp).getAttributes().getNamedItem("message").getNodeValue();
                issue.severity = issueList.item(temp).getAttributes().getNamedItem("severity").getNodeValue();
                issue.priority = Integer.valueOf(issueList.item(temp).getAttributes().getNamedItem("priority").getNodeValue());
                issue.summary = issueList.item(temp).getAttributes().getNamedItem("summary").getNodeValue();
                for (int temp2 = 0; temp2 < issueList.item(temp).getChildNodes().getLength(); temp2++) {
                    if (issueList.item(temp).getChildNodes().item(temp2).getAttributes() != null) {
                        issue.file = issueList.item(temp).getChildNodes().item(temp2).getAttributes().getNamedItem("file").getNodeValue();
                        issue.line = Integer.valueOf(issueList.item(temp).getChildNodes().item(temp2).getAttributes().getNamedItem("line").getNodeValue());
                    }
                }
                list.add(issue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (Issue i : list)
//            System.out.println(i.id +" " + i.line  +"  " + i.file);
        return list;
    }

    public  JSONArray parseAndroidApis() {
        JSONArray ja = new JSONArray();
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(classLoader.getResource("patterns_length1.csv").getFile());
        //File f = new File("/Users/ruirua/Downloads/patterns_length1.csv");
        CsvParserSettings settings = new CsvParserSettings();
        settings.setMaxCharsPerColumn(25000);
        settings.getFormat().setLineSeparator("\r");
        settings.getFormat().setDelimiter(';');
        CsvParser parser = new CsvParser(settings);
        String[] row = null;
        // 3rd, parses all rows of data in selected columns from the CSV file into a matrix
        List<String[]> resolvedData = null;
        try {
            resolvedData = parser.parseAll(new FileReader(f.getAbsolutePath()));
        } catch (FileNotFoundException e) {
            System.out.println("[ANALYZER]: File Not Found: There is no  csv file in directory! to generate results");

        }
        for (int i = 1; i < resolvedData.size(); i++) {

            row = resolvedData.get(i);
            if (row.length > 1) {
                JSONObject jo = new JSONObject();
                jo.put("category", row[1]);
                String s = row[0].replaceAll("\\(.*?\\)", "");
                s = s.replaceAll("-", "");
                String[] fullMethodDefinition = s.split("\\.");
                String methodName = fullMethodDefinition[fullMethodDefinition.length > 0 ? fullMethodDefinition.length - 1 : 0];
                jo.put("methodName", methodName);
                jo.put("fullMethodDefinition", s);
                ja.add(jo);


            }
        }
        try (FileWriter file = new FileWriter("redAPIS.json")) {

            file.write(ja.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ja;
    }


    public JSONArray decodeAndroidAPIS() {
        JSONParser parser = new JSONParser();
        JSONArray ja = new JSONArray();
        try {
            Object obj = parser.parse(new FileReader("redAPIS.json"));
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




    public static JSONObject getTest() {

        JSONObject test = new JSONObject();
        test.put("test_application", Analyzer.applicationID); //TODO
        test.put("test_tool", Analyzer.monkey ? "monkey" : "unittests"); // TODO
        test.put("test_orientation", Analyzer.isTestOriented ? "testoriented" : "methodoriented");
        return test;
    }

    public static JSONArray getAppMethodsAndMetrics(APICallUtil acu) {
        JSONArray ja = new JSONArray();
        for (ClassInfo ci : acu.proj.getCurrentApp().allJavaClasses) {
            for (MethodInfo mi : ci.classMethods.values()) {
                JSONObject jo = new JSONObject();
                jo.put("method_class", ci.classPackage + "." + ci.className);
                jo.put("method_name", mi.methodName);
                // hashArgs
                String args = "";
                for (Variable v : mi.args) {
                    args += v.isArray + v.type + v.varName;
                }

                jo.put("method_id", mi.getMethodID());
                jo.put("method_hash_args", args.hashCode());
                ja.add(jo);
                Analyzer.grr.methodMetrics.addAll(getMethodsMetrics(mi));
            }
        }
        return ja;
    }


     public static JSONObject getMethodAPIS(MethodInfo mi ){
         JSONObject jo = new JSONObject();
         jo.put("methodName",mi.methodName);
         JSONArray ja = new JSONArray();
         for (MethodOfAPI moa : mi.androidApi){
             JSONObject job = new JSONObject();
             job.put("class", moa.api );
             job.put("method", moa.method);
             ja.add(job);
         }
         jo.put("androidAPIS",ja);
         ja = new JSONArray();
         for (MethodOfAPI moa : mi.javaApi){
             JSONObject job = new JSONObject();
             job.put("class", moa.api );
             job.put("method", moa.method);
             ja.add(job);
         }
         jo.put("javaAPIS",ja);
         ja = new JSONArray();
         for (MethodOfAPI moa : mi.unknownApi){
             JSONObject job = new JSONObject();
             job.put("class", moa.api );
             job.put("method", moa.method);
             ja.add(job);
         }
         jo.put("unknownAPIS",ja);
         ja = new JSONArray();
         for (MethodOfAPI moa : mi.externalApi){
             JSONObject job = new JSONObject();
             job.put("class", moa.api );
             job.put("method", moa.method);
             ja.add(job);
         }
         jo.put("externalAPI",ja);
         return jo;
     }


    public static void writeJSONMethodAPIS (JSONArray ja, String file){

        try {
            writeFile(new File(file),ja.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(File file, String content) throws IOException{
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.flush();
        bw.close();
    }

    public static JSONArray getMethodsMetrics(MethodInfo mi) {
        JSONArray ja = new JSONArray();
        String args = "";
        for (Variable v : mi.args) {
            args += v.isArray + v.type + v.varName;
        }
        String idMethod =mi.getMethodID();
        JSONObject o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "androidapis");
        o.put("mm_value", mi.androidApi.size());
        o.put("mm_coeficient", 1);
        ja.add(o);
        o.put("mm_method", idMethod);
        o.put("mm_metric", "cc");
        o.put("mm_value", mi.cyclomaticComplexity);
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "loc");
        o.put("mm_value", mi.linesOfCode);
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "androidapis");
        o.put("mm_value", mi.androidApi.size());
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "javaapis");
        o.put("mm_value", mi.javaApi.size());
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "unknownapis");
        o.put("mm_value", mi.externalApi.size() + mi.unknownApi.size());
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "nrargs");
        o.put("mm_value", mi.args.size());
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "isstatic");
        o.put("mm_value", mi.isStatic ? 1 : 0);
        o.put("mm_coeficient", 1);
        ja.add(o);
        return ja;
    }



    public static JSONArray getMethodsMetricsMethodOriented(MethodInfo mi, String time, String energy, String methodInvoked, Double [] testResults) {
        JSONArray ja = new JSONArray();
        String idMethod = GreenSourceAPI.generateMethodID(mi);
        JSONObject o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "androidapis");
        o.put("mm_value", mi.androidApi.size());
        o.put("mm_coeficient", 1);
        ja.add(o);
        o.put("mm_method", idMethod);
        o.put("mm_metric", "cc");
        o.put("mm_value", mi.cyclomaticComplexity);
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "loc");
        o.put("mm_value", mi.linesOfCode);
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "androidapis");
        o.put("mm_value", mi.androidApi.size());
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "javaapis");
        o.put("mm_value", mi.javaApi.size());
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "unknownapis");
        o.put("mm_value", mi.externalApi.size() + mi.unknownApi.size());
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "nrargs");
        o.put("mm_value", mi.args.size());
        o.put("mm_coeficient", 1);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "isstatic");
        o.put("mm_value", mi.isStatic ? 1 : 0);
        o.put("mm_coeficient", 1);
        ja.add(o);


         // dynamic metrics
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "wifistate");
        o.put("mm_value", testResults[0]);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);

        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "mobiledatastate");
        o.put("mm_value", testResults[1]);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);

        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "screenstate");
        o.put("mm_value", testResults[2]);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);

        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "batterystatus");
        o.put("mm_value", testResults[3]);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);

        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "wifirssilevel");
        o.put("mm_value", testResults[4]);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);

        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "memory");
        o.put("mm_value", testResults[5]);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);

        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "bluetoothstate");
        o.put("mm_value", testResults[6]);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);

        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "gpufrequency");
        o.put("mm_value", testResults[7]);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);

        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "cpuloadnormalized");
        o.put("mm_value", testResults[8]);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);

        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "gpsstate");
        o.put("mm_value", testResults[9]);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);

        //
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "energy");
        o.put("mm_value", energy);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);
        o = new JSONObject();
        o.put("mm_method", idMethod);
        o.put("mm_metric", "time");
        o.put("mm_value", time);
        o.put("mm_coeficient", 1);
        o.put("mm_invokation", methodInvoked);
        ja.add(o);
        return ja;
    }


    public static JSONArray getMethodsInvoked(String testResultsID, List<String> methodIDs) {
        JSONArray ja = new JSONArray();
        for (String s : methodIDs) {
            JSONObject test = new JSONObject();
            test.put("test_results", testResultsID);
            test.put("method", s);
            ja.add(test);
        }

        return ja;
    }

    public static JSONObject getTestResult(String seed, String desc, String testiD, String profilerID, String deviceID, String deviceStartID, String deviceEndID) {
        JSONObject tesResults = new JSONObject();
        tesResults.put("test_results_seed", seed);
        tesResults.put("test_results_description", desc);
        tesResults.put("test_results_test", testiD);
        tesResults.put("test_results_profiler", profilerID);
        tesResults.put("test_results_device", deviceID);
        tesResults.put("test_results_device_begin_state", deviceStartID);
        tesResults.put("test_results_device_end_state", deviceEndID);
        return tesResults;
    }

    public static JSONObject getTestMetricsMethodOriented(String testid, double coverage) {
        JSONObject testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "coverage");
        testMetrics.put("value", coverage);
        testMetrics.put("coeficient", 1);
        return testMetrics;
    }




    public static JSONArray getClasses(Iterable<ClassInfo> classses){
        JSONArray ja = new JSONArray();
        for (ClassInfo ci : classses){
            JSONObject jo = new JSONObject();
            jo.put("class_id",  ci.classPackage + "." + ci.className); // TODO replace with ci.getClassID()
            jo.put("class_name", ci.className);
            jo.put("class_package", ci.classPackage);
           // jo.put("class_non_acc_mod", );
            jo.put("class_application", ci.classVariables.size());
           // jo.put("class_is_interface", ci.isInterface);
            jo.put("class_acc_mod", 1);
            if  (ci.extendedClass!=null)
                jo.put("class_superclass", ci.extendedClass);
            ja.add(jo);

        }
        return ja;
    }




    public static JSONArray getClassMetrics (String classId , ClassInfo ci ) {

        JSONArray ja = new JSONArray();
        JSONObject jo = new JSONObject();
        jo.put("class", classId);
        jo.put("cm_metric", "numberOfVars");
        jo.put("cm_value", ci.classVariables.size());
        jo.put("cm_coeficient", 1);
        ja.add(jo);
        jo = new JSONObject();
        jo.put("class", classId);
        jo.put("cm_metric", "numberOfMethods");
        jo.put("cm_value", ci.classMethods.size());
        jo.put("cm_coeficient", 1);
        ja.add(jo);
        return ja;


    }




    public static JSONArray getTestMetrics(String testid, String [] res, double energy, double time, double coverage) {
        JSONArray ja = new JSONArray();
        JSONObject testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "wifistate");
        testMetrics.put("value", res[0]);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "mobiledatastate");
        testMetrics.put("value", res[1]);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "screenstate");
        testMetrics.put("value",res[2]);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "batterystatus");
        testMetrics.put("value", res[3]);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "wifirssilevel");
        testMetrics.put("value", res[4]);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "memory");
        testMetrics.put("value", res[5]);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "bluetoothstate");
        testMetrics.put("value", res[6]);
        testMetrics.put("coeficient", 1);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "gpufrequency");
        testMetrics.put("value", res[7]);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "cpuloadnormalized");
        testMetrics.put("value", res[8]);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "gpsstate");
        testMetrics.put("value", res[9]);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "energy");
        testMetrics.put("value", energy);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "time");
        testMetrics.put("value", time);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);
        testMetrics = new JSONObject();
        testMetrics.put("test_results", testid);
        testMetrics.put("metric", "coverage");
        testMetrics.put("value", coverage);
        testMetrics.put("coeficient", 1);
        ja.add(testMetrics);

        return ja;


    }

}