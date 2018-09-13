/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import GreenSourceBridge.GSUtils;

import jInst.transform.InstrumentHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author User
 */
public class XMLParser {
    private static String launcher = "";
    private static String launcherLabel = "";
    private static String buildPackage = "";
    private static String devPackage = "";
    private static String testProjName = "";
    
    private static ArrayList<String> editedProjects = new ArrayList<String>();
    public static List<String> permissions = new ArrayList<>();
    private static final String permExtWrite = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String permExtRead = "android.permission.READ_EXTERNAL_STORAGE";
    private static final String permInt = "android.permission.INTERNET";
    private static final String permLoc = "android.permission.ACCESS_FINE_LOCATION";
    private static final String permWifi = "android.permission.ACCESS_WIFI_STATE";
    private static final String permPhone = "android.permission.READ_PHONE_STATE";
    private static final String permNet = "android.permission.ACCESS_NETWORK_STATE";
    private static final String permBoot = "android.permission.RECEIVE_BOOT_COMPLETED";

    public static String getXmlPropertyValue(String element, String property, String file){
        String res = "";
        File fXmlFile = new File(file);
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            Element cElem = doc.getDocumentElement();
            if(cElem.hasAttribute(property)){
                res = cElem.getAttribute(property);
                return res;
            }
            NodeList list = cElem.getElementsByTagName(element);
            for(int i=0; i<list.getLength(); i++){
                Element x = (Element)list.item(i);
                if(x.hasAttribute(property)) res = x.getAttribute(property);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }


    public static void loadAndSendApplicationJSON(String projectJSonPath){
        JSONParser parser = new JSONParser();
        JSONObject ja = new JSONObject();
        try {
            Object obj = parser.parse(new FileReader(projectJSonPath));
            ja = (JSONObject) obj;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        GSUtils.sendJSONtoDB("http://localhost:8000/application/",ja.toJSONString());

    }






    public static boolean buildAppPermissionsJSON (String manif, String folderDest){

        List<String> l = getPermissions(manif);
        JSONArray ja = new JSONArray();
        for (String permission : l){
            JSONObject jo = new JSONObject();
            jo.put("application",InstrumentHelper.projectID);
            jo.put("permission", permission.toLowerCase().replace("android.permission.", ""));
            ja.add(jo);
        }

        if (!ja.isEmpty()){
            System.out.println(ja.toJSONString());
            try {
                FileUtils.writeFile(new File(folderDest+"/appPermissions.json"), ja.toJSONString());
            } catch (IOException e) {
                System.out.println("!!!!!!!!!!! Error writing "+folderDest+"appPermissions.json" );
                return false;
            }

            return true;
        }
        return false;
    }

    public static List<String> getPermissions(String manifFile){
        List<String> l = new ArrayList<>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        Document doc = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
             doc = docBuilder.parse(manifFile);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Get the root element
        Node manifest = doc.getElementsByTagName("manifest").item(0);
        NodeList permissions = doc.getElementsByTagName("uses-permission");
        if(manifest==null)
            return l ;
        for(int i = 0; i<permissions.getLength(); i++){
            Node n = permissions.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                l.add(e.getAttribute("android:name"));
            }
        }
        return l;
    }
    



    public static void parseManifest(String file) {
        buildPackage = "";
        devPackage = "";
        try {
            String cName="", cLabel="", cPack="";
            File fXmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            Element cElem = doc.getDocumentElement();
            cPack = cElem.hasAttribute("package") ? cElem.getAttribute("package") : "";
            buildPackage = cPack;
            
            NodeList appList = doc.getElementsByTagName("application");
            Element app = (Element)appList.item(0);
            NodeList nList = app.getElementsByTagName("activity");


            for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element eElement = (Element) nNode;
                            
                            cName = eElement.hasAttribute("android:name") ? eElement.getAttribute("android:name") : "";
                            cLabel = eElement.hasAttribute("android:label") ? eElement.getAttribute("android:label") : "";
                            if(cName.startsWith(".")){
                                cName = cPack+cName;
                                devPackage = cName.substring(0, cName.lastIndexOf("."));
                            }else{
                                devPackage = cName.substring(0, cName.lastIndexOf("."));
                                if(buildPackage.equals("")){
                                    buildPackage = devPackage;
                                }
                            }
                            //System.out.println("Name : " + eElement.getAttribute("android:name"));
                            //System.out.println("Label : " + eElement.getAttribute("android:label"));
                            NodeList intents = eElement.getElementsByTagName("intent-filter");
                            for(int i = 0; i< intents.getLength(); i++){
                                Node nIntent = intents.item(i);
                                if (nIntent.getNodeType() == Node.ELEMENT_NODE) {
                                    Element eIntent = (Element) nIntent;
                                    NodeList categories = eIntent.getElementsByTagName("category");
                                    for(int j = 0; j< categories.getLength(); j++){
                                        Node nCategory = categories.item(j);
                                        if(nCategory.getNodeType() == Node.ELEMENT_NODE){
                                            Element eCategory = (Element)nCategory;
                                            if(eCategory.getAttribute("android:name").contains("LAUNCHER")){
                                                launcher = cName;
                                                launcherLabel = cLabel;
                                            }
                                        }
                                    }
                                }
                            }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 
   }


   public static void insertReadWriteExternalPermissions( String manifestFile) {
       DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
       DocumentBuilder docBuilder = null;
       boolean hasExtWrite = false, hasExtRead= false;
       String pName = "";
       try {
           docBuilder = docFactory.newDocumentBuilder();
           Document doc = docBuilder.parse(manifestFile);
           // Get the root element
           Node manifest = doc.getElementsByTagName("manifest").item(0);
           NodeList permissions = doc.getElementsByTagName("uses-permission");
           if(permissions == null){

           }else {
               for (int i = 0; i < permissions.getLength(); i++) {
                   Node n = permissions.item(i);
                   if (n.getNodeType() == Node.ELEMENT_NODE) {
                       Element e = (Element) n;
                       pName = e.hasAttribute("android:name") ? e.getAttribute("android:name") : "";
                       if (pName.equals(permExtWrite)) {
                           hasExtWrite = true;
                       }
                       if (pName.equals(permExtRead)) {
                           hasExtRead = true;
                       }
                   }
               }
           }
           if(!hasExtWrite){
               Element nPerm = doc.createElement("uses-permission");
               nPerm.setAttribute("android:name", permExtWrite);
               manifest.appendChild(nPerm);
           }
           if(!hasExtRead){
               Element nPerm = doc.createElement("uses-permission");
               nPerm.setAttribute("android:name", permExtRead);
               manifest.appendChild(nPerm);
           }

           // write the content into xml file
           TransformerFactory transformerFactory = TransformerFactory.newInstance();
           Transformer transformer = transformerFactory.newTransformer();
           DOMSource source = new DOMSource(doc);
           StreamResult result = new StreamResult(new File(manifestFile));
           transformer.transform(source, result);
       } catch (ParserConfigurationException e) {
           e.printStackTrace();
       } catch (SAXException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } catch (TransformerConfigurationException e) {
           e.printStackTrace();
       } catch (TransformerException e) {
           e.printStackTrace();
       }


   }
    
    public static void editManifest(String file){
        try {
                String pName = "";
                boolean hasExt = false, hasExtRead=false ,hasInt = false, hasLoc = false, hasWifi  = false, hasPhone = false, hasNet = false, hasBoot  = false;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(file);
 
		// Get the root element
		Node manifest = doc.getElementsByTagName("manifest").item(0);


		NodeList permissions = doc.getElementsByTagName("uses-permission");
                if(permissions == null){
                    
                }else{
                    for(int i = 0; i<permissions.getLength(); i++){
                        Node n = permissions.item(i);
                        if(n.getNodeType() == Node.ELEMENT_NODE){
                            Element e = (Element)n;
                            pName = e.hasAttribute("android:name") ? e.getAttribute("android:name") : "";
                            if(pName.equals(permExtWrite)) {
                                hasExt = true;
                            }
                            if(pName.equals(permExtRead)){
                                hasExtRead = true;
                            }if(pName.equals(permBoot)){
                                hasBoot = true;
                            }if(pName.equals(permInt)){
                                hasInt = true;
                            }if(pName.equals(permLoc)){
                                hasLoc = true;
                            }if(pName.equals(permWifi)){
                                hasWifi = true;
                            }if(pName.equals(permPhone)){
                                hasPhone = true;
                            }if(pName.equals(permNet)){
                                hasNet = true;
                            }
                        }
                    }
                }
                if(!hasExt){
                    Element nPerm = doc.createElement("uses-permission");
                    nPerm.setAttribute("android:name", permExtWrite);
                    manifest.appendChild(nPerm);
                }
                if(!hasExtRead){
                    Element nPerm = doc.createElement("uses-permission");
                    nPerm.setAttribute("android:name", permExtRead);
                    manifest.appendChild(nPerm);
                }if(!hasBoot){
                    Element nPerm = doc.createElement("uses-permission");
                    nPerm.setAttribute("android:name", permBoot);
                    manifest.appendChild(nPerm);
                }if(!hasInt){
                    Element nPerm = doc.createElement("uses-permission");
                    nPerm.setAttribute("android:name", permInt);
                    manifest.appendChild(nPerm);
                }if(!hasLoc){
                    Element nPerm = doc.createElement("uses-permission");
                    nPerm.setAttribute("android:name", permLoc);
                    manifest.appendChild(nPerm);
                }if(!hasWifi){
                    Element nPerm = doc.createElement("uses-permission");
                    nPerm.setAttribute("android:name", permWifi);
                    manifest.appendChild(nPerm);
                }if(!hasPhone){
                    Element nPerm = doc.createElement("uses-permission");
                    nPerm.setAttribute("android:name", permPhone);
                    manifest.appendChild(nPerm);
                }if(!hasNet){
                    Element nPerm = doc.createElement("uses-permission");
                    nPerm.setAttribute("android:name", permNet);
                    manifest.appendChild(nPerm);
                }

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(file));
		transformer.transform(source, result);
 
 
	   } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	   } catch (TransformerException tfe) {
		tfe.printStackTrace();
	   } catch (IOException ioe) {
		ioe.printStackTrace();
	   } catch (SAXException sae) {
		sae.printStackTrace();
	   }
    }
    
    public static void editProjectDesc(String file){
         try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(file);
                String pName = "";
		// Get the root element
		Node description = doc.getElementsByTagName("projectDescription").item(0);
                
		NodeList list = description.getChildNodes();
                for(int i = 0; i < list.getLength(); i++){
                    Node tag = list.item(i);
                    if(tag.getNodeName().equals("name")){
                        editedProjects.add(tag.getTextContent());
                        pName = "__"+tag.getTextContent();
                        testProjName = pName;
                        tag.setTextContent(pName);
                    }else if(tag.getNodeName().equals("projects")){
                        NodeList projects = tag.getChildNodes();
                        for(int j = 0; j < projects.getLength(); j++){
                            Node p = projects.item(j);
                            if(editedProjects.contains(p.getTextContent())){
                                p.setTextContent("__"+p.getTextContent());
                            }
                        }
                    }
                }

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(file));
		transformer.transform(source, result);
 
 
	   } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	   } catch (TransformerException tfe) {
		tfe.printStackTrace();
	   } catch (IOException ioe) {
		ioe.printStackTrace();
	   } catch (SAXException sae) {
		sae.printStackTrace();
	   }
    }
    
    public static void editClasspath(String file){
        try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(file);
                String pName = "";
		// Get the root element
		Node classpath = doc.getElementsByTagName("classpath").item(0);
                
		NodeList list = classpath.getChildNodes();
                for(int i = 0; i < list.getLength(); i++){
                    Node tag = list.item(i);
                    if(tag.hasAttributes()){
                        NamedNodeMap atts = tag.getAttributes();
                        if(atts.getNamedItem("combineaccessrules") != null){
                            pName = atts.getNamedItem("path").getTextContent().replaceFirst("/", "/__");
                            atts.getNamedItem("path").setTextContent(pName);
                        }
                        //pName = "__"+tag.getTextContent();
                        //tag.setTextContent(pName);
                    }
                }

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(file));
		transformer.transform(source, result);
 
 
	   } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	   } catch (TransformerException tfe) {
		tfe.printStackTrace();
	   } catch (IOException ioe) {
		ioe.printStackTrace();
	   } catch (SAXException sae) {
		sae.printStackTrace();
	   }
    }
    
    public static String getLauncher(){
        return launcher;
    }

    public static String getLauncherLabel() {
        return launcherLabel;
    }
    
    public static String getDevPackage(){
        return devPackage;
    }
    
    public static String getBuildPackage(){
        return buildPackage;
    }
    
    public static String getTestProjName(){
        return testProjName;
    }
    
    public static String getAppPackage(){
        String res = "";
        String[] tok = launcher.split("\\.");
        int tam = tok.length, i;
        for(i=0; i<tam-1; i++){
            res+=tok[i]+".";
        }
        return res.substring(0, res.length()-1);
    }

    public static void editRunConfiguration(String file, String value){
        try {
            String cName="", cLabel="", cPack="";
            File fXmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            
            NodeList attrList = doc.getElementsByTagName("stringAttribute");
            for(int i=0; i<attrList.getLength(); i++){
                Element elem = (Element)attrList.item(i);
                if(elem.hasAttribute("key")){
                    if(elem.getAttribute("key").equals("com.android.ide.adt.instrumentation")){
                        elem.setAttribute("value", value);
                    }
                }
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(file));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** returns null or classname if it has **/
    public static String getApplicationClass(String manifest){

        String s = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new File(manifest));

            NodeList nodeList = document.getElementsByTagName("application");
//        for(int x=0,size= nodeList.getLength(); x<size; x++) {
            if (nodeList!=null){
                Node nodo = nodeList.item(0).getAttributes().getNamedItem("android:name");
                if (nodo!=null){
                    s = nodo.getNodeValue();
                    System.out.println(" A classe de aplicattion s é " + s);
                }

            }
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return s;
        }
    }


    public static void addApplicationName(String file, String classe){
        try {
            File fXmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList attrList = doc.getElementsByTagName("application");
            if (attrList!=null){
                System.out.println("entrei aqui ");
                Element e = (Element) attrList.item(0);
                e.setAttribute("android:name" ,classe );
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(file));
                transformer.transform(source, result);
            }
            else
                System.out.println("olha nao tem application");


        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
        return;
    }


    public static void editRunner(String file) {
    
    try {
            String cName="", cLabel="", cPack="";
            File fXmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            
            NodeList instList = doc.getElementsByTagName("instrumentation");
            Element inst = (Element)instList.item(0);
            if(inst.hasAttribute("android:name")){
                //inst.setAttribute("android:name", "pl.polidea.instrumentation.PolideaInstrumentationTestRunner");
                inst.setAttribute("android:name", "com.zutubi.android.junitreport.JUnitReportTestRunner");
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(file));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
 
   }
    
}
