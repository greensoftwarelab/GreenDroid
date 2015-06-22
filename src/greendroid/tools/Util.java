/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.tools;

import greendroid.result.ResultMethod;
import greendroid.result.ResultClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import greendroid.Main;
import static greendroid.Main.tName;
import greendroid.project.Project;
import greendroid.result.Result;
import greendroid.trace.TracedMethod;
import greendroid.result.ResultPackage;
import instrumentation.util.PackageM;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author User
 */
public class Util {
    
    private static ResultPackage getPackage(String pkg, ArrayList<ResultPackage> list){
        ResultPackage res = null;
        for(ResultPackage p : list){
            if(pkg.equals(p.getName())) res = p;
        }
        
        if(res == null){
            res = new ResultPackage(pkg);
            list.add(res);
        }
        
        return res;
    }
    
    public static List<Double> getXMLTimes(String file){
        LinkedList<Double> res = new LinkedList<Double>();
        try {
            String time = "";
            File fXmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            Element cElem = doc.getDocumentElement();   //<testsuites> element
            
            NodeList appList = doc.getElementsByTagName("testcase");
            for(int i=0; i<appList.getLength();i++){
                Node node = appList.item(i);
                
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    time = elem.getAttribute("time");
                    res.add(Double.parseDouble(time));
                }
            }

	} catch (Exception e) {
            e.printStackTrace();
	}
        return res;
    }
    
    public static void toJSON(LinkedHashMap<String, LinkedList<TracedMethod>> map, LinkedList<Integer> sfl, String fileJSON) throws IOException{
        Result result = new Result();
        result.setName("GreenAnalysis");
        ArrayList<ResultPackage> package_list = new ArrayList<ResultPackage>();
        ArrayList<ResultMethod> method_list;
        int i=0; int size=0, factor=0;
        for(String className : map.keySet()){
            String[] tokens = className.split("[\\<\\>]");
            if(tokens.length !=3){
                System.err.println("ERROR: String '"+className+"' with length "+tokens.length);
                return;
            }
            String cl = tokens[2], pkg = tokens[1];
            ResultPackage rpkg = getPackage(pkg, package_list);
            ResultClass x = new ResultClass();
            x.setName(cl);
            method_list = new ArrayList<ResultMethod>();
            for(TracedMethod tm : map.get(className)){
                size = 1 ;//sfl.get(i)+1;   //+1 prevents methods from not appearing in the sunburst
                factor = sfl.get(i);
                ResultMethod r = new ResultMethod(tm.getMethod(), size, factor);
                x.incrementFactor(factor);
                method_list.add(r);
                i++;
            }
            x.setChildren(method_list);
            rpkg.addClass(x);
        }
        result.setChildren(package_list);
        /*for(ResultPackage rp : package_list){
            System.out.println(rp.getName());
        }*/
        ObjectMapper mapper = new ObjectMapper();
        //mapper.writeValue(new File("D://newsblur.json"), result);
        mapper.writeValue(new File(fileJSON), result);
    }
    
    private static int determineLevel(int max){
        if(max%10 == 0) return 10;
        if(max%9 == 0) return 9;
        if(max%8 == 0) return 8;
        if(max%7 == 0) return 7;
        if(max%6 == 0) return 6;
        if(max%5 == 0) return 5;
        return 4;
    }
    
    public static void createRadar(String className, TracedMethod method){
        String filename = "D:/tests/"+className+"."+method.getMethod()+".js";
        System.out.println(filename);
        ArrayList<Integer> list = new ArrayList(); 
        list.add(method.getGreen()); list.add(method.getRed()); list.add(method.getYellow()); list.add(method.getOrange()); 
        int levels = determineLevel(Collections.max(list));
        String legend = "var LegendOptions = ['"+className+"."+method.getMethod()+"']\n\n";
        String red = "{axis:\"RED\",value:"+method.getRed()+"}";
        String orange = "{axis:\"ORANGE\",value:"+method.getOrange()+"}";
        String yellow = "{axis:\"YELLOW\",value:"+method.getYellow()+"}";
        String green = "{axis:\"GREEN\",value:"+method.getGreen()+"}";
        String testResults = "["+red+","+orange+","+yellow+","+green+"]";
        String data = "var d = ["+testResults+"]\n\n";
        String size = "var w = 500,\n\th = 500;\n\n";
        String imported = "var imported = document.createElement('script');\n" +
                          "imported.src = 'script.js';\n" +
                          "document.head.appendChild(imported);";
        String other = "var mycfg = {\n" +
                       "  w: w,\n" +
                       "  h: h,\n" +
                       "  maxValue: "+Collections.max(list)+",\n" +
                       "  levels: "+levels+",\n" +
                       "  ExtraWidthX: 300\n" +
                       "}\n\n";
        String content = legend+data+size+other+imported;
        saveFile(filename, content, false);
    }
    
    public static void saveFile(String filename, String content, boolean append){
        try {
            File file = new File(filename);
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), append);

            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close(); 
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
    }
    
    public static List<Long> readLongsFromFile(String filename){
        List<Long> ret = new ArrayList<Long>();
        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine="";
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                ret.add(Long.parseLong(strLine));
            }
            in.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e);
        }
        return ret;
    }
    
    public static long sum(List<Long> list){
        long ret = 0;
        for(Long l : list){
            ret += l;
        }
        return ret;
    }
    
    public static long average(List<Long> list){
        long ret = 0, sum = 0;
        for(Long l : list){
            sum += l;
        }
        ret = (long)sum/list.size();
        return ret;
    }
    
    public static File[] listFilesFromDir(String directory){
        File dir = new File(directory);
        File[] list = dir.listFiles();
        return list;
    }

    public static File getFileWithName(String directory, String name) {
        int flagC = 0;
        
        File dir = new File(directory);
        File[] list = dir.listFiles();
        String compName = name;
        if(compName.startsWith("*")){
            flagC += 1;
            compName = compName.substring(1);
        }
        if(name.endsWith("*")){
            flagC += 2;
            compName = compName.substring(0, compName.length()-1);
        }
        for(int i = 0; i<list.length; i++){
            switch (flagC){
                case 0:
                    if (list[i].getName().equals(compName)) return list[i];
                    break;
                case 1:
                    if (list[i].getName().endsWith(compName)) return list[i];
                    break;
                case 2:
                    if (list[i].getName().startsWith(compName)) return list[i];
                    break;
                case 3:
                    if (list[i].getName().contains(compName)) return list[i];
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    public static void toCSV(LinkedHashMap<String, LinkedList<TracedMethod>> all, String fileCSV) {
        String content = "State,Green,Yellow,Orange,Red\n"; 
        int i = 0;
        for(String cl : all.keySet()){
            String cla = cl.replaceAll("<", "").replaceAll(">", ".");
            for(TracedMethod tm : all.get(cl)){
                i++;
                if(tm.getGreen()+tm.getYellow()+tm.getOrange()+tm.getRed() == 0){
                    
                }else{
                    content += cla+"."+tm.getMethod()+","+tm.getGreen()+","+tm.getYellow()+","+tm.getOrange()+","+tm.getRed()+""+"\n";
                    //content += i+","+tm.getGreen()+","+tm.getYellow()+","+tm.getOrange()+","+tm.getRed()+""+"\n";
                }
                
            }
        }
        saveFile(fileCSV, content, false);
    }

    public static void copyAll(List<PackageM> source, List<PackageM> destiny) {
        for(PackageM obj : source){
            destiny.add(obj.clone());
        }
    }
    
    public static List<Project> parseProjects(String csvfile) {
        BufferedReader br = null;
        List<Project> projects = new LinkedList<>();
	String line = "";
	String cvsSplitBy = ",";
        int cont = 0;
        try {
            br = new BufferedReader(new FileReader(csvfile));
            while ((line = br.readLine()) != null) {
                cont++;
                // use comma as separator
                if(!line.startsWith("#") && !line.isEmpty()){
                    String[] tokens = line.split(cvsSplitBy);
                    if(tokens.length != 4){
                        System.err.println("ERROR: Bad parsing for the projects file!!!");
                        System.out.println("W: Project described in line "+cont+" will not be considered!");
                    }else{
                        projects.add(new Project(tokens[0], tokens[1], tokens[2], tokens[3], tName));
                    }    
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
	} finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}
        return projects;
    }
    
    public static Config parseConfigs(String cfgFile){
        Config conf = new Config();
        BufferedReader br = null;
	String line = "", aux = "";
        int cont = 0;
        try {
            br = new BufferedReader(new FileReader(cfgFile));
            while ((line = br.readLine()) != null) {
                cont++;
                // use comma as separator
                if(!line.startsWith("#") && !line.isEmpty()){
                    aux = line.substring(line.indexOf(':'));
                    if(line.startsWith("JAVA_PATH")){
                        conf.setJavaPath(aux);
                    }else if(line.startsWith("ANT_PATH")){
                        conf.setAntPath(aux);
                    }else if(line.startsWith("ANDROID_TOOLS_PATH")){
                        conf.setAndroidPath(aux);
                    }else if(line.startsWith("LOCAL_RESULTS_DIR")){
                        System.out.println("\t\tLOCAL: "+aux);
                        conf.setLocalResDir(aux);
                    }else if(line.startsWith("DEVICE_RESULTS_DIR")){
                        System.out.println("\t\tDEVICE: "+aux);
                        conf.setDeviceResDir(aux);
                    }else{
                        String[] tokens = line.split(":");
                        System.err.println("WARNING: Unknown configuration "+tokens[0]);
                        System.out.println("[Ignoring...]");
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
	} finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}
        return conf;
    }
}
