/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package greendroid.tools;

import greendroid.result.ResultMethod;
import greendroid.result.ResultClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import greendroid.result.Result;
import greendroid.TracedMethod;
import greendroid.result.ResultPackage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
        result.setName("0xBenchmark");
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
    
    public static void saveFile(String filename, String content, boolean append){
        try {
            File file = new File(filename);

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), append);

            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close(); 
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
    }
    
    public static long readLongsFromFile(String filename){
        long ret = 0;
        try{
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine="";
            long total=0, count=0;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                count++;
                total += Long.parseLong(strLine);
            }
            ret = (long)total/count;
            in.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e);
        }
        return ret;
    }
}
