package Analyzer;


import Analyzer.Results.Results;
import Analyzer.Results.TestResults;
import AndroidProjectRepresentation.APICallUtil;
import AndroidProjectRepresentation.AppInfo;
import AndroidProjectRepresentation.ClassInfo;
import AndroidProjectRepresentation.MethodInfo;
import GreenSourceBridge.GDConventions;
import GreenSourceBridge.GreenSourceAPI;

import GreenSourceBridge.JSONContainer;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class Analyzer {

    public static String projectAppJSONFile ="";
    public static String stopTag="stopped"; // TODO put this in GDconventions
    public static String startTag="started"; // TODO put this in GDconventions
    public static boolean isTestOriented=false;
    public static HashMap<String,Integer> allTracedMethods = new HashMap<>(); // Method name -> times invoked
    public  static String resultDirPath = "results/" ;
    //public static Double [] returnList = new Double[13];
    public static Results actualResult = new Results();
    public static HashMap<String , Results>  globalReturnList = new HashMap<>();
    public static HashMap<String ,String>  testNameNumber = new HashMap<>();
    public static String allMethodsDir = "";
    public static List<String> alltests= new ArrayList<>();
    public static HashSet<String> allmethods= new HashSet<>();
    public static APICallUtil acu = null;
    public static final String testResultsFile = GDConventions.TestOutputName;
    public static final String appResultsFile = GDConventions.AppOutputName;
    public static final String appIssuesFile = GDConventions.IssuesOutputName;
    public static final String serializedFile = GDConventions.fileStreamName;
    public static final String analyzerTag= "[Analyzer] ";
    public static  String folderPrefix= "Test";
    public static int stoppedState = 0;
    public static boolean monkey = false;
    private static boolean mergeOldRuns= false;
    public static String applicationID = "unknown";
    public static List<String> actualTestMethods = new ArrayList<>();
    public static GreenSourceAPI grr = new GreenSourceAPI();
    public static JSONArray energyGreadyAPIS = new JSONArray();
    public static Hashtable<String, String> deviceStates = new Hashtable();

    public static Set<ClassInfo> classesSet = new HashSet<>();
    public static Map<String, Integer> methodsSet = new HashMap<>();


    private static List<String> loadTests(String csvFile) throws Exception {
        alltests = new ArrayList<String>();
        File f = new File(csvFile);
        Path path = Paths.get(f.getAbsoluteFile().getParent());
        Path p = null;
        try{
            DirectoryStream<Path> stream;
            stream = Files.newDirectoryStream(path);
            for (Path entry : stream)
            {
                if(entry.getFileName().toString().matches("TracedTests\\.txt")){
                    p = entry;//break;
                    break;
                }
            }
            stream.close();
            if (p==null)
                throw new IOException("TracedTests not found");
        }
        catch (IOException e)
        {
            System.out.println("TracedTests not found");
            return alltests;
        }

        try (Stream<String> lines = Files.lines (p, StandardCharsets.UTF_8)) {
                for (String line : (Iterable<String>) lines::iterator)
                {
                    alltests.add(line);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        return alltests;
    }



    public static String processMethodInvoked (String methodName,String className){
       // System.out.println(acu.proj.getCurrentApp().allJavaClasses);
        MethodInfo mi = acu.getMethodOfClass(methodName,className);
        String metId= mi.getMethodID();
        //System.out.println(acu.proj.apps);
        if (methodsSet.containsKey(metId)){
           methodsSet.put(metId ,methodsSet.get(metId)+1);
        }
        else {
            methodsSet.put(metId,1);
            if(mi.ci!=null){
                grr.methods.add(mi.toJSONObject(mi.ci.getClassID()),  mi.getMethodID());
                grr.methodMetrics.addAll(Utils.getMethodsMetrics(mi));
                if( !classesSet.contains(mi.ci)){
                    grr.classes.add(mi.ci.toJSONObject(applicationID),  mi.ci.getClassID());
                    classesSet.add(mi.ci);
                }
            }
        }
        return metId;
    }

    public static JSONArray getClassMetrics() {
       JSONArray ja  = new JSONArray();
        for (ClassInfo ci : classesSet){
            JSONObject jo = new JSONObject();
            String s = ci.getClassID();
            jo.put("cm_class",s);
            jo.put("cm_metric","nr_declared_vars");
            jo.put("cm_value", ci.classVariables.size());
            jo.put("cm_coeficient", 1);
            ja.add(jo);
            jo.put("cm_class",s);
            jo.put("cm_metric","nr_imports");
            jo.put("cm_coeficient", 1);
            jo.put("cm_value", ci.classImports);
            ja.add(jo);
            jo.put("cm_class",s);
            jo.put("cm_metric","nr_methods");
            jo.put("cm_coeficient", 1);
            jo.put("cm_value", ci.classMethods.size());
            ja.add(jo);
        }

         return ja;
        //return new JSONArray();
    }


    public void cleanThrash(){
        classesSet.clear();
    }

    private static HashSet<String> loadMethods(String allMethods) {
        allmethods = new HashSet<String>();

        Path path = Paths.get(allMethods + "allMethods.txt");
        try {
            try (Stream<String> lines = Files.lines (path, StandardCharsets.UTF_8))
            {
                for (String line : (Iterable<String>) lines::iterator)
                {
                    allmethods.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println(analyzerTag + " : File containing methods (allMethods.txt) not found!");
            // e.printStackTrace();
        }

        return allmethods;
    }

    public static void copyMethodMap (HashMap<String, Integer> h) {

        for (String s : h.keySet()) {
            if (allTracedMethods.containsKey(s)){
                int x = allTracedMethods.get(s);
                int y = h.get(s);
                allTracedMethods.put(s, y+x);
            }
            else {
                allTracedMethods.put(s,1);
            }
        }
    }


    public static void write (Writer w, List<String> l) throws IOException {

        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String value: l)
        {
            if(!first){
                sb.append(",");
                sb.append(value);
            }
            else {
                sb.append(value);
                first=false;
            }
        }

        sb.append("\n");
        w.append(sb.toString());
    }


    private static void testOriented( List<String> args) throws NullPointerException{
        System.out.println("Nr of tests: " +args.size()+ " tests");
        CsvParserSettings settings = new CsvParserSettings();
        Map<Integer,Integer> timeConsumption = new HashMap<>();
        List <Consumption> consumptionList = new ArrayList<>();
        Set<Integer> timeStates = new TreeSet<>();
        settings.getFormat().setLineSeparator("\n");
        FileWriter fw = null;
        FileWriter fwApp = null;

        try {
            File f = new File(resultDirPath+"/" + testResultsFile);
            File f2 = new File(resultDirPath+"/" + appResultsFile);

            if (!f.exists()){
                f.createNewFile();
            }
            if (!f2.exists()){
                f2.createNewFile();
            }
            fw = new FileWriter(f);
            fwApp = new FileWriter(f2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        actualResult = new TestResults();
        List<String> l = ((TestResults) actualResult).metricsName;
        try {
            write(fw,l);
        } catch (IOException e) {
            e.printStackTrace();
        }
        l.clear();

        // for each csv file found
        for (int j = 0; j <args.size() ; j++) {
            actualResult = new TestResults();

            if (!args.get(j).matches(".*.csv.*") || args.get(j).matches(".*Testresults.csv")) continue;
            System.out.println("--- " + args.get(j) + " ---");
            CsvParser parser = new CsvParser(settings);
            // 3rd, parses all rows of data in selected columns from the CSV file into a matrix
            List<String[]> resolvedData = null;
            try {
                File f = new File(args.get(j));
                resolvedData = parser.parseAll(new FileReader(f.getAbsolutePath()));
            } catch (FileNotFoundException e) {
                System.out.println("[ANALYZER]: File Not Found: There is no " + args.get(j) + " csv file in directory! to generate results");
                continue;
            }

            HashMap<String, Pair<Integer, Integer>> columns = null;
            try {
                columns = Utils.fetchColumns(resolvedData);
            } catch (Exception e) {
                System.out.println("[ANALYZER] Error fetching columns. Result csv might have an error");
            }
            String number = args.get(j).replaceAll(".+GreendroidResultTrace(.+)\\..+", "$1");

            String[] row = new String[32];

            // Get data from rows
            for (int i = 4; i < resolvedData.size(); i++) {
                row = resolvedData.get(i);
                if (row.length==0 || row[0]==null)
                    break;
                consumptionList.add(getDataFromRow(columns, row));

            }

            Path p = getRespectiveTracedMethodsFile(args.get(j));
            double totalconsumption = actualResult.getTotalConsumption();
            String [] res = ((TestResults) actualResult).showGlobalData();

            if (actualResult.time==0) {
                System.out.println("[Analyzer] Warning: Ignoring test " + number + "; Missing start or stop tags in resultant .csv file");
                testNameNumber.put(getTestName(number), number);

                double totalcoverage = (methodCoverageTestOriented(p) * 100);
                System.out.println("---------------Method Coverage of Test------------------");
                System.out.println("percentage: " + totalcoverage + " %");
                System.out.println("------------------------------------------------");
                globalReturnList.put(number,actualResult);
               // copyToGlobalReturnList(number);
                continue;
            }

            ((TestResults) actualResult).testName = getTestName(number);
            ((TestResults) actualResult).testId= number;

            try {
                alltests = loadTests(args.get(j));
            } catch (Exception e) {
                System.out.println("[ANALYZER] Error tracing tests... Assuming order of tests instead of names");
            }

            if (actualResult.time > 0) {
                System.out.println("---------" + " TEST CONSUMPTION" + "-----------");
                //System.out.println("--"+ " Filename: " + args[j] + " -----------");
                System.out.println("--" + " Test Name: " + getTestName(number) + " --");
                System.out.println("----------------------------------------------");
                System.out.println("| Test Total Consumption (J) : " + totalconsumption + " J|");
                // System.out.println("| Test Total Consumption (W) : " + watt + " W|");
                System.out.println("| Test Total Time (ms)       : " + actualResult.time + " ms|");
                System.out.println("----------------------------------------------");

            }
            testNameNumber.put(getTestName(number), number);
            double totalcoverage = (methodCoverageTestOriented(p) * 100);
            System.out.println("---------------Method Coverage of Test------------------");
            System.out.println("percentage: " + totalcoverage + " %");
            System.out.println("------------------------------------------------");
            showData(consumptionList);
            //TODO
            //get resume of test results
            globalReturnList.put(number, actualResult);

            if (GreenSourceAPI.operationalBackend){
                JSONObject stateInit= grr.loadDeviceState(deviceStates.get("begin_state"+ number+".json"));
                JSONObject stateEnd= grr.loadDeviceState(deviceStates.get("end_state"+ number+".json"));
                grr.testResults = (Utils.getTestResult("0", "", grr.getActualTestID(), "greendroid", grr.getActualDeviceID(),  stateInit, stateEnd)); // TODO
                //System.out.println(grr.testResults.toJSONString());
                grr.testResults = GreenSourceAPI.sendTestResultToDB(grr.testResults.toJSONString());
                grr.allTestResults.add(grr.testResults, "");
                grr.testMetrics.addAll(Utils.getTestMetrics(grr.getActualTestResultsID(), res, actualResult.getTotalConsumption(), actualResult.time, ((TestResults) actualResult).getCoverage()));
                getMethodHashList();
                grr.methodsInvoked.addAll(Utils.getMethodsInvoked(grr.getActualTestResultsID()),"method");
                // System.out.println("");
            }

     // end of csv file processing [END]
        }

        // final Results
        //  print to file the results and name of test
        for (Results resu : globalReturnList.values()) {
            String [] testResults = ((TestResults) resu).showGlobalData();
            for (int i = 0; i < testResults.length ; i++) { l.add(testResults[i]); }
            try {
                write(fw,l);
            } catch (IOException e) {
                e.printStackTrace();
            }
            l.clear();
        }

        double tc = totalCoverage()*100;
        System.out.println("\n///////////////////////////////////////////////");
        System.out.println("---------------Total Coverage------------------");
        System.out.println("percentage: " + (tc) + " %");
        System.out.println("------------------------------------------------");

        try {
            l.add("Total method coverage"); l.add(String.valueOf(0));l.add(String.valueOf(0)); l.add( String.valueOf(0)); l.add(String.valueOf(tc));
            write(fw,l);
            l.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        l.add("Class"); l.add("Method"); l.add("Times invoked");
        l.add("CC");l.add("LoC"); l.add("AndroidAPIs"); l.add("JavaAPIs"); l.add("ExternalAPIs"); l.add("Declared vars"); l.add("N args"); //l.add("EnergyGreadyAPIS");
        for ( String s : allTracedMethods.keySet()){
            try {
                write(fwApp,l);
                l.clear();
                String [] xx = s.split("<");
                String methodName = xx[xx.length-1].replace(">","");
                l.add(xx[0]); l.add(methodName) ;l.add(String.valueOf(allTracedMethods.get(s)));
                String s1 = s.replaceAll("<.*?>", "");
                MethodInfo mi = acu!=null? acu.getMethodOfClass(methodName,s1) : new MethodInfo();
                grr.methodsInvoked.add(Utils.getMethodAPIS(mi), "method_id");
                l.add(String.valueOf(mi.cyclomaticComplexity)); l.add(String.valueOf(mi.linesOfCode)); l.add(String.valueOf(mi.androidApi.size()));l.add(String.valueOf(mi.javaApi.size())); l.add(String.valueOf(mi.externalApi.size())); l.add(String.valueOf(mi.declaredVars.size()));l.add(String.valueOf(mi.args.size())); //l.add(redapis);

            } catch (IOException  | NullPointerException e) {
                e.printStackTrace();
            }
        }
        try {
            write(fwApp,l);
            l.clear();
            fwApp.flush();
            fwApp.close();

            write(fw,l);
            l.clear();
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            File f3 = new File(resultDirPath+"/" + appIssuesFile);
//            if (!f3.exists()){
//                f3.createNewFile();
//            }
//            FileWriter fwIssues = new FileWriter(f3);
//            l.add("Issue");l.add("Severity");l.add("Category");l.add("Message"); l.add("File"); l.add("Line"); l.add("Explanation");
//            List<Issue> issueList = Utils.parseLintResulsXML()
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //Utils.sendApiCallUtil(acu);

        grr.classMetrics.addAll(getClassMetrics());
        Utils.writeJSONMethodAPIS(((JSONArray) grr.methodsInvoked.getAll()), resultDirPath+"/" + "methodsInvoked.json" );
        //send results do DBASE
        GreenSourceAPI.sendTestsMetricsToDB(grr.testMetrics.toJSONString());
        GreenSourceAPI.sendClassesToDB(grr.classes.toJSONString());
        grr.classImports = getAllImports(grr.classes.getAll());
        GreenSourceAPI.sendImportsToDB(grr.classImports.toJSONString());
        GreenSourceAPI.sendMethodsToDB(grr.methods.toJSONString());
        GreenSourceAPI.sendMethodsInvokedToDB(grr.methodsInvoked.toJSONString());
        GreenSourceAPI.sendMethodsMetricsToDB(grr.methodMetrics.toJSONString());
        // System.out.println(grr.classMetrics.toJSONString());
        GreenSourceAPI.sendClassMetricsToDB(grr.classMetrics.toJSONString());
    }

    public static boolean isValidMetric( HashMap<String, Pair<Integer, Integer>> columns,String metricDefinition, String [] rowFromCsv ){

        Pair<Integer,Integer> pair = Utils.getMatch(columns,metricDefinition);
        // if column exists and row contains value for that metric return true
        return (rowFromCsv.length>3 && pair!=null && rowFromCsv.length >=pair.second && rowFromCsv[pair.second]!=null);

    }

    public static JSONContainer getAllImports(Collection<Object> jc){
        JSONContainer jco = new JSONContainer();
        for (Object jj : jc){
            JSONObject jo = ((JSONObject) jj);
            JSONArray j = ((JSONArray) jo.get("class_imports"));
           for (Object obj : j){
               JSONObject jop = ((JSONObject) obj);
               jco.add(jop, jop.get("import_name").toString() +jop.get("import_class").toString() );
           }
        }
        return jco;
    }


//    public static Consumption getDataFromRow( HashMap<String, Pair<Integer, Integer>> columns,String[] row) {
//        double wifiState = Utils.getMatch(columns, Utils.wifiState)!=null? (row[Utils.getMatch(columns, Utils.wifiState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiState).second])) : returnList[0]) : returnList[0];
//              //  Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiState).second]);
//        double mobileData = Utils.getMatch(columns, Utils.mobileData)!=null? (row[Utils.getMatch(columns, Utils.mobileData).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.mobileData).second])) : returnList[1]) : returnList[1];
//        double screenState = Utils.getMatch(columns, Utils.screenState)!=null?  (row[Utils.getMatch(columns, Utils.screenState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.screenState).second])) : returnList[2]) : returnList[2];
//        double batteryStatus = Utils.getMatch(columns, Utils.batteryStatus)!=null?  (row[Utils.getMatch(columns, Utils.batteryStatus).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryStatus).second])) : returnList[3]) : returnList[3];
//        double wifiRSSI = Utils.getMatch(columns, Utils.wifiRSSILevel)!=null?  (row[Utils.getMatch(columns, Utils.wifiRSSILevel).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiRSSILevel).second])) : returnList[4]) : returnList[4];
//        double memUsage = Utils.getMatch(columns, Utils.memory)!=null?  (row[Utils.getMatch(columns, Utils.memory).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.memory).second])) : returnList[5]) : returnList[5];
//        double bluetooth = Utils.getMatch(columns, Utils.bluetoothState)!=null?  (row[Utils.getMatch(columns, Utils.bluetoothState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.bluetoothState).second])) : returnList[6]) : returnList[6];
//        double gpuLoad = Utils.getMatch(columns, Utils.gpuLoad)!=null? (row[Utils.getMatch(columns, Utils.gpuLoad).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.gpuLoad).second])) : returnList[7]) : returnList[7];
//        double cpuLoadNormalized = Utils.getMatch(columns, Utils.cpuLoadNormalized)!=null?  (row[Utils.getMatch(columns, Utils.cpuLoadNormalized).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.cpuLoadNormalized).second])) : returnList[8]) : returnList[8];
//        double gps = Utils.getMatch(columns, Utils.gpsState)!=null? (row[Utils.getMatch(columns, Utils.gpsState).second]!=null ? (Integer.parseInt(row[Utils.getMatch(columns, Utils.gpsState).second])) : returnList[9]) : returnList[9];
//        Consumption c = new  Consumption(((int) memUsage), ((int) mobileData), ((int) wifiState), ((int) wifiRSSI), ((int) screenState), 0, 0, ((int) batteryStatus), ((int) bluetooth), ((int) gpuLoad), ((int) gps), ((int) cpuLoadNormalized));
//        return c;
//    }


    public static Consumption getDataFromRow( HashMap<String, Pair<Integer, Integer>> columns,String[] row) {

        Consumption c = new Consumption();

        Pair<Integer, Integer> wifiState = null, mobileData = null  , screenState = null , batteryStatus =null, wifiRSSI = null,
                memUsage = null, bluetooth = null, gpuLoad = null, cpuLoadNormalized = null, gps=null, power = null, state = null;

        if (isValidMetric(columns,Utils.stateInt,row) && isValidMetric(columns,Utils.stateDescription,row)){
            state = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.stateInt).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.stateInt).second]));
            if (!(actualResult).hasStartTime()&& row[Utils.getMatch(columns, Utils.stateDescription).second].equals(startTag)){
                ((TestResults) actualResult).startTime = state.first;
            }
            if (!actualResult.hasEndTime()&&row[Utils.getMatch(columns, Utils.stateDescription).second].equals(stopTag)){
                ((TestResults) actualResult).stopTime = state.first;
            }

            actualResult.timeSamples.put(state.first,  (state.second));
            c.applicationState = new Pair<Integer, Integer>(state.first,(state.second));
        }


        if (isValidMetric(columns,Utils.batteryPower,row)){
            power = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryPower).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.batteryPower).second]));
            actualResult.powerSamples.put(power.first,  new Double(power.second));
            c.batteryPowerRaw = new Pair<Integer, Double>(power.first, new Double(power.second));
        }


        if (isValidMetric(columns,Utils.wifiState,row)){
            wifiState = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiState).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.wifiState).second]));
            actualResult.wifiStateSamples.put(wifiState.first, wifiState.second);
            c.wifiState = new Pair<Integer, Integer>(wifiState.first, wifiState.second);
        }

        if (isValidMetric(columns,Utils.mobileData,row)){
            mobileData = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.mobileData).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.mobileData).second]));
            actualResult.mobileDataStateSamples.put(mobileData.first, mobileData.second);
            c.mobileDataState = new Pair<Integer, Integer>(mobileData.first, mobileData.second);
        }

        if (isValidMetric(columns,Utils.screenState,row)){
            screenState = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.screenState).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.screenState).second]));
            actualResult.screenStateSamples.put(screenState.first, screenState.second);
            c.screenState = new Pair<Integer, Integer>(screenState.first, screenState.second);
        }

        if (isValidMetric(columns,Utils.batteryStatus,row)){
            batteryStatus = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.batteryStatus).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.batteryStatus).second]));
            actualResult.batteryStatusSamples.put(batteryStatus.first, batteryStatus.second);
            c.batteryStatus = new Pair<Integer, Integer>(batteryStatus.first, batteryStatus.second);
        }

        if (isValidMetric(columns,Utils.wifiRSSILevel,row)){
            wifiRSSI = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.wifiRSSILevel).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.wifiRSSILevel).second]));
            actualResult.rSSILevelSamples.put(wifiRSSI.first, wifiRSSI.second);
            c.rssiLevel = new Pair<Integer, Integer>(wifiRSSI.first, wifiRSSI.second);
        }

        if (isValidMetric(columns,Utils.memory,row)){
            memUsage = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.memory).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.memory).second]));
            actualResult.memorySamples.put(memUsage.first, memUsage.second);
            c.memUsage = new Pair<Integer, Integer>(memUsage.first, memUsage.second);
        }

        if (isValidMetric(columns,Utils.bluetoothState,row)){
            bluetooth = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.bluetoothState).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.bluetoothState).second]));
            actualResult.bluetoothStateSamples.put(bluetooth.first, bluetooth.second);
            c.bluetoothState = new Pair<Integer, Integer>(bluetooth.first, bluetooth.second);
        }
        if (isValidMetric(columns,Utils.gpuLoad,row)){
            gpuLoad = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.gpuLoad).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.gpuLoad).second]));
            actualResult.gpuLoadSamples.put(gpuLoad.first, gpuLoad.second);
            c.gpuLoad = new Pair<Integer, Integer>(gpuLoad.first, gpuLoad.second);
        }

        if (isValidMetric(columns,Utils.cpuLoadNormalized,row)){
            cpuLoadNormalized = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.cpuLoadNormalized).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.cpuLoadNormalized).second]));
            actualResult.addCpuLoadSample(0, cpuLoadNormalized.first, cpuLoadNormalized.second);
            c.cpuLoads.put(0,  new Pair<Integer, Integer>(cpuLoadNormalized.first, cpuLoadNormalized.second));
        }

        if (isValidMetric(columns,Utils.gpsState,row)){
            gps = new Pair<Integer, Integer> (Integer.parseInt(row[Utils.getMatch(columns, Utils.gpsState).first]),Integer.parseInt (row[Utils.getMatch(columns, Utils.gpsState).second]));
            actualResult.gPSStateSamples.put(gps.first, gps.second);
            c.gpsState = new Pair<Integer, Integer>(gps.first, gps.second);
        }

        //Consumption c = new  Consumption(((int) memUsage), ((int) mobileData), ((int) wifiState), ((int) wifiRSSI), ((int) screenState), 0, 0, ((int) batteryStatus), ((int) bluetooth), ((int) gpuLoad), ((int) gps), ((int) cpuLoadNormalized));
        return c;
    }


    public static void getMethodHashList(){

        for(String s: actualTestMethods){
            String [] xx = s.split("<");
            String methodName = xx[xx.length-1].replace(">","");
            String className = s.replaceAll("<.*?>", "");
            processMethodInvoked(methodName,className);
        }

    }


    public static String []  showData(List<Consumption> list){

       return  ((TestResults) actualResult).showGlobalData();
    }



    public static Path getRespectiveTracedMethodsFile(String csvFile){
        // get TracedMethodsX.txt file in the folder of the csv file
        String number =csvFile.replaceAll(".+GreendroidResultTrace(.+)\\..+","$1");
        File f = new File(csvFile);
        Path path = Paths.get(f.getAbsoluteFile().getParent());
        Path p = null;
        try{
            DirectoryStream<Path> stream;
            stream = Files.newDirectoryStream(path);
            for (Path entry : stream)
            {
                if(entry.getFileName().toString().matches("TracedMethods"+number+".txt")){
                    p = entry;//break;
                    break;
                }
            }
            stream.close();
        if (p==null)
            throw new IOException("TracedMethods"+number+".txt not found");
        }
        catch (IOException e)
        {
            System.out.println(analyzerTag + " : File containing traced Methods of file (TracedMethods"+number+".txt) not found! Assumed 0 traced methods");
            //e.printStackTrace();
        }
        return p;

    }


    public  static void methodOriented(List<String> arg1) {
       // TODO remove comments
//        try {
//            MethodOriented.methodOriented(arg1);
//        } catch (FileNotFoundException e) {
//            System.out.println("[ANALYZER]: File Not Found: There is no .csv file in directory! to generate results");
//          //s  e.printStackTrace();
//        }
    }

//    public static void copyToGlobalReturnList(String testName){
//        Double [] newReturnList = new Double[13];
//        for (int i = 0; i <returnList.length ; i++) {
//            newReturnList[i] = returnList[i];
//        }
//        if(globalReturnList.get(testName)!=null){
//
//            globalReturnList.get(testName).add(newReturnList);
//        }
//        else {
//            List<Double []> l = new ArrayList<>();
//            l.add(newReturnList);
//            globalReturnList.put(testName, l);
//        }
//    }

    public static Integer closestMemMeasure(Map<Integer,Integer> timeConsumption, int time){
        // calcular a medida de bateria mais aproximada
        int closestStart = 1000000, closestStop = 1000000;
        int difStart = 1000000, diffEnd = 1000000;
//    int alternativeEnd = 0, alternativeStart=0;
        for (Integer i : timeConsumption.keySet()) {
            if(Math.abs(time-i) < difStart){
                difStart = Math.abs(time-i);
                //alternativeStart =closestStart;
                closestStart = i;
            }

        }

        return timeConsumption.get(closestStart);

    }


    public static double totalCoverage(){

        double percentageCoverage = ((double)allTracedMethods.size()/(double)(allmethods.size()));
        return percentageCoverage;
    }

    // recebe como paramentro o Path para ficheiro TracedMethods[0-9].txt correspondente
    public static double methodCoverageTestOriented(Path pathTraced){
        HashSet<String> set = allmethods;
        actualTestMethods = new ArrayList<>();
        if(pathTraced==null) return 0;

        HashMap<String, Integer> thisTest = new HashMap<>();

        // pass all profiled methods to set
        // TODO change to better approach
        try {
            try (Stream<String> lines = Files.lines (pathTraced, StandardCharsets.UTF_8))
            {
                for (String line : (Iterable<String>) lines::iterator) {

                    if(!allmethods.contains(line))
                        continue;

                    if(thisTest.containsKey(line)){
                        int x = thisTest.get(line);
                        thisTest.put(line,++x);
                        actualTestMethods.add(line);
                    }
                    else{
                        thisTest.put(line,1);
                        actualTestMethods.add(line);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // now I have all profiled methods, search for them in the allMethods file

        ((TestResults) actualResult).invokedMethods = thisTest;
        copyMethodMap(thisTest);
        double percentageCoverage = ((double)thisTest.size()/(double)(set.size()));
        return percentageCoverage;
    }

    public static  String getTestName(String number){
        if(!alltests.isEmpty() && Integer.parseInt(number)<= alltests.size()){
            return alltests.get(Integer.parseInt(number));
        }
        else
            return number;

    }




    public static  List <String> getAllCsvsAndSendAppToDB(String resultDirPath){

        List <String> list = new ArrayList<>( );
        Path path = Paths.get(resultDirPath);
        String pathPermissions = "";
        String pathApp = "";
        String pathDevice = "";
        try{
            DirectoryStream<Path> stream;
            stream = Files.newDirectoryStream(path);
            // foreach file in resulDir folder
            for (Path entry : stream) {
                if(!entry.getFileName().toString().startsWith(folderPrefix)){
                    continue;
                }

                if(Files.isDirectory(entry)) { // if is a folder
                    //get files of that folder
                    DirectoryStream<Path> streamChild = null;
                    try {
                        streamChild = Files.newDirectoryStream(Paths.get(entry.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return list;
                    }
                    for (Path entry1 : streamChild) { //foreach file of that folder
                        //System.out.println(entry1.getFileName().toString());
                        if (entry.equals(path))
                            continue;
                        if (entry1.getFileName().toString().matches("GreendroidResultTrace[0-9]+\\.csv")) {
                            list.add(entry1.toString());
                        }
                        if (entry1.getFileName().toString().matches("begin_state[0-9]+\\.json")) {
                            deviceStates.put(entry1.getFileName().toString(),entry1.toString());
                        }
                        if (entry1.getFileName().toString().matches("end_state[0-9]+\\.json")) {
                            deviceStates.put(entry1.getFileName().toString(),entry1.toString());
                        }

                        if (entry1.getFileName().toString().matches("appPermissions.json")) {
                            pathPermissions = entry1.toString();
                            //System.out.println(pathPermissions);
                        }
                        if (entry1.getFileName().toString().contains("#")) {
                            if (entry1.getFileName().toString().contains(".json")){
                                pathApp=entry1.toString();
                              //  System.out.println(pathApp);
                                projectAppJSONFile =pathApp;
                            }

                        }
                        if (entry1.getFileName().toString().matches("device.json")) {
                            pathDevice= entry1.toString();
                           // System.out.println(pathDevice);
                        }
                        if (entry.getFileName().toString().matches(".json")) {
                            pathApp=entry.getFileName().toString();
                            //System.out.println(pathApp);
                        }
                    }
                }
                else {

                    if (entry.equals(path))
                        continue;
                    if (entry.getFileName().toString().matches("GreendroidResultTrace[0-9]+\\.csv")) {
                        list.add(entry.toString());
                    }
                    if (entry.getFileName().toString().matches("begin_state[0-9]+\\.json")) {
                        deviceStates.put(entry.getFileName().toString(),entry.toString());
                    }
                    if (entry.getFileName().toString().matches("end_state[0-9]+\\.json")) {
                        deviceStates.put(entry.getFileName().toString(),entry.toString());
                    }
                    if (entry.getFileName().toString().matches("appPermissions.json")) {
                        pathPermissions = entry.getFileName().toString();
                       // System.out.println(pathPermissions);
                    }
                    if (entry.getFileName().toString().contains("#")) {
                        if (entry.getFileName().toString().contains(".json")){
                            pathApp=entry.toString();
                           // System.out.println(pathApp);
                            projectAppJSONFile =pathApp;
                        }

                    }
                    if (entry.getFileName().toString().matches("device.json")) {
                        pathDevice= entry.getFileName().toString();
                       // System.out.println(pathDevice);
                    }
                }
            }
            stream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
       /* for (String l: list) {
            System.out.println("ficheiro " +l);
        }*/

        if (mergeOldRuns){
            resultDirPath+="oldRuns/";
            mergeOldRuns = false;
            List<String> x = getAllCsvsAndSendAppToDB(resultDirPath);
            list.addAll(x);
        mergeOldRuns = true;
    }

        try{
            JSONObject jj =  new APICallUtil().fromJSONFile(projectAppJSONFile);
            acu = ((APICallUtil) new APICallUtil().fromJSONObject(jj));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        JSONObject jo = acu.proj.toJSONObject("");
        //String projectToSend = jo.toJSONString();
        String projectToSend = getSimpleProjectJSON(jo); // ProjectInfo.getSimpleProjectJSON(jo); //TODO
       grr.project = ((JSONObject) GreenSourceAPI.sendProjectToDB(projectToSend));
        AppInfo app = acu.proj.getCurrentApp();
        app.appFlavor="demo";
        app.buildType="c";
        app.appDescription="";

        JSONObject appl =  app.toJSONObject(acu.proj.projectID);
        String apptosend = getSimpleAppJSON(acu.proj.apps.get(0).toJSONObject(acu.proj.projectID));
        applicationID = (String) (acu.proj.apps.get(0).toJSONObject(acu.proj.projectID)).get("app_id");
       grr.app=GreenSourceAPI.sendApplicationToDB(apptosend);
        GreenSourceAPI.sendAppPermissionsToDB( GreenSourceAPI.loadAppPermissions(pathPermissions).toJSONString());
       grr.device= GreenSourceAPI.loadDevice(pathDevice);
       GreenSourceAPI.sendDeviceToDB( grr.device.toJSONString());
        return list;
    }


    public static String getSimpleProjectJSON (JSONObject jo ){
        JSONObject pro = new JSONObject();
        pro.put("project_id", jo.get("project_id") );
        pro.put("project_build_tool", jo.get("project_build_tool") );
        pro.put("project_description", jo.get("project_description") );
        return pro.toJSONString();
    }

    public static String getSimpleAppJSON (JSONObject jo ){
        JSONObject pro = new JSONObject();
        pro.put("app_id", jo.get("app_id") );
        pro.put("app_location", jo.get("app_location") );
        pro.put("app_description", jo.get("app_description") );
        pro.put("app_description", jo.get("app_description") );
        pro.put("app_flavor", jo.get("app_flavor") );
        pro.put("app_build_type", jo.get("app_build_type") );
        pro.put("app_project", jo.get("app_project") );

        return pro.toJSONString();

    }



    public static void main(String[] args) {
        Utils u = new Utils();
        GreenSourceAPI.operationalBackend = true; //TODO
        GreenSourceAPI gapi = new GreenSourceAPI();
        //gapi.setGreenRepoUrl("http://localhost:8000/");
        System.out.println("Sending results to " + gapi.getGreenRepoURL());
        mergeOldRuns = false; // TODO
        if (args.length>3) {
            isTestOriented = args[0].equals("-TestOriented");
            if(!isTestOriented){
                folderPrefix="Method";
            }
            resultDirPath =args[1];
            stoppedState = args[2].equals("-Monkey")?2:0;
            if(args[3].equals("localhost") ){
                gapi.setGreenRepoUrl("http://localhost:8000/");
            }
            monkey=stoppedState==2;
            folderPrefix = monkey? "Monkey" + folderPrefix : folderPrefix;
           // boolean analyzeOldRuns=  args[2].equals("-oldRuns");
            allMethodsDir = resultDirPath + "/all/";
            allmethods = loadMethods(allMethodsDir);

            if (isTestOriented) {
                try {
                    List<String> allcsvs = getAllCsvsAndSendAppToDB(resultDirPath); // this send apppermissions.json
                    if(GreenSourceAPI.operationalBackend){
                        grr.test =  Utils.getTest();
                        grr.test = GreenSourceAPI.sendTestToDB(grr.test.toJSONString()); // must be this way to get retrive internal test id
                        //grr.methods.addAll(Utils.getAppMethodsAndMetrics(acu) ,"method_id");
                    }
                    testOriented(allcsvs);
                } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else {
                List<String> allcsvs = getAllCsvsAndSendAppToDB(resultDirPath);
                try{
                    grr.test =  Utils.getTest();
                    grr.test = GreenSourceAPI.sendTestToDB(grr.test.toJSONString());
                    acu = ((APICallUtil) new APICallUtil().fromJSONObject(new APICallUtil().fromJSONFile((projectAppJSONFile))));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                //grr.methods.addAll(Utils.getAppMethodsAndMetrics(acu),"method_id");
                methodOriented(allcsvs);
                // methodOriented(Arrays.copyOfRange(args, 3, args.length));
            }
        }
        else {
            System.out.println("Bad argument length for Greendroid Analyzer! Usage ->  [-(Test|Method)Oriented] resultsdir [-Monkey|XXX]");
        }
    }

}
