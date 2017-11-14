/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst;

import jInst.transform.InstrumentGradleHelper;
import jInst.transform.InstrumentHelper;
import jInst.util.XMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marco
 */
public class JInst {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String projType = args[0];
        switch (projType){
            case "-sdk":
                if(args.length != 6){
                    System.err.println("[jInst] Error: Bad arguments length for SDK project. Expected 6, got "+args.length+".");
                    return;
                }else{
                    String tName = args[1];
                    String workspace = args[2];
                    String project = args[3];
                    String tests = args[4];
                    boolean tracemethods = args[5].equals("-TraceMethods") ? true :false;
                    if (tracemethods) System.out.println("just tracing......");
                    try {
                        InstrumentHelper helper = new InstrumentHelper(tName, workspace, project, tests,tracemethods);
                        helper.generateTransformedProject();
                        helper.generateTransformedTests();
                    } catch (Exception ex) {
                        Logger.getLogger(JInst.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case "-gradle":
                if(args.length != 7){
                    System.err.println("[jInst] Error: Bad arguments length for Gradle project. Expected 7, got "+args.length+".");
                    return;
                }else{
                    String tName = args[1];
                    String workspace = args[2];
                    String project = args[3];
                    String manifestSource = args[4];
                    String manifestTests = args[5];
                    boolean tracemethods = args[6].equals("-TraceMethods") ? true :false;
                    if (tracemethods) System.out.println(" Tracing tests......");
                    try {
                        InstrumentGradleHelper helper = new InstrumentGradleHelper(tName, workspace, project, "", manifestSource, manifestTests, tracemethods);
//                           if (!tracemethods) {
//                            helper.setApplicationClass(XMLParser.getApplicationClass(manifestSource));
//                        }
                        helper.generateTransformedProject();
                    } catch (Exception ex) {
                        Logger.getLogger(JInst.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            default : break;
                //
        }


   }



//    public static void main(String[] args) {
//
//        boolean trace = true;
//        try {
//
//            InstrumentGradleHelper helper = new InstrumentGradleHelper("TRANSFORMED", "X", "/home/rrua/Documents/bolsa/android_proj/gradle/0a22af29-bd03-4168-b9eb-20a7fcc02850/latest/", "", "/home/rrua/Documents/bolsa/android_proj/gradle/0a22af29-bd03-4168-b9eb-20a7fcc02850/latest/app/src/main/AndroidManifest.xml", "-", trace);
//                        helper.generateTransformedProject();
//                    } catch (Exception ex) {
//                        Logger.getLogger(JInst.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//    }
    
}
