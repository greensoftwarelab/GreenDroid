package Analyzer;

import org.omg.CORBA.INTERNAL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public static final String gpsSate = "GPS\\ State.*";


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
                issue.priority = Integer.valueOf( issueList.item(temp).getAttributes().getNamedItem("priority").getNodeValue());
                issue.summary = issueList.item(temp).getAttributes().getNamedItem("summary").getNodeValue();
                for (int temp2 = 0; temp2 < issueList.item(temp).getChildNodes().getLength(); temp2++) {
                    if (issueList.item(temp).getChildNodes().item(temp2).getAttributes() != null){
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
}