/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.transform;

//import greendroid.tools.Util;
import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ASTHelper;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import jInst.profiler.Profiler;
import jInst.profiler.ProfilerAbstractFactory;
import jInst.profiler.trepn.TrepnProfilerFactory;
import jInst.visitors.ClassOrInterfaceVisitor;
import jInst.visitors.utils.ClassDefs;
import jInst.util.FileUtils;
import jInst.util.PackageM;
import jInst.util.XMLParser;
import jInst.visitors.MethodChangerVisitor;
import jInst.visitors.MethodVisitor;
import jInst.visitors.TestChangerVisitor;

/**
 *
 * @author User
 */
public class InstrumentHelper {

//    public static String runnerClass = "com.zutubi.android.junitreport.JUnitReportTestRunner";
    protected static ArrayList<String> instrumented = new ArrayList<String>();
    protected static ArrayList<String> testCase = new ArrayList<String>();
    public static final String testClasses = "testClasses.txt";
    protected static String notTestable = "TestCase";
    public static Integer compiledSdkVersion=0;
    protected String tName;
    protected String workspace;
    protected String project;
    protected String tests;
    protected String transFolder;
    protected String transTests;
    protected String aux;
    protected String manifest;
    protected String manifestTest;
    protected String projectDesc;
    protected String devPackage;
    protected ArrayList<PackageM> packages;
    protected boolean traceMethods;
    protected String originalManifest;


    //protected int JUnitVersion;
    protected Map<String,String> testType = new HashMap<>();
    private static ClassDefs appPackage = new ClassDefs();
    private static Profiler profiler;







    public static boolean isApplicationClass(String s){
        return appPackage.getName()!=null;
    }

    public static String getApplicationClass(){
        return appPackage.getName();
    }
    public static String getApplicationFullName(){
        return appPackage.getPack()  +"." +appPackage.getName();
    }



    public InstrumentHelper() {
    }



    public void setProfiler(boolean traceMethods){

        ProfilerAbstractFactory pfact = new TrepnProfilerFactory();
        if (traceMethods){
            this.profiler = pfact.createTestOrientedProfiler();
        }
        else {
            System.out.println("Criei method oriented profiler");
            this.profiler = pfact.createMethodOrientedProfiler();
        }
    }


    public InstrumentHelper(String tName, String work, String proj, String tests, boolean trace) {
        this.tName = tName;
        this.workspace = work;
        this.project = proj+"/";
        this.tests = tests;
        this.transFolder = project+tName+"/";
        this.transTests = transFolder+"tests"+"/";
        this.manifest = transFolder+"AndroidManifest.xml";
        this.manifestTest = transTests+"AndroidManifest.xml";
        this.projectDesc = project+".project";
        this.aux = transFolder+"_aux_/";
        this.devPackage = "";
        this.packages = new ArrayList<>();
        this.traceMethods = trace;

        instrumented.add("ActivityUnitTestCase");
        instrumented.add("ActivityIntrumentationTestCase2");
        instrumented.add("ActivityTestCase");
        instrumented.add("ProviderTestCase");
        instrumented.add("SingleLaunchActivityTestCase");
        instrumented.add("SyncBaseInstrumentation");
        instrumented.add("ActivityInstrumentationTestCase");
        instrumented.add("ActivityInstrumentationTestCase2");
        //instrumented.add("WizardPageActivityTestBase");

        testCase.add("AndroidTestCase");
        testCase.add("ApplicationTestCase");
        testCase.add("LoaderTestCase");
        testCase.add("ProviderTestCase2");
        testCase.add("ServiceTestCase");
        //testCase.add("TestCase");
        //testCase.add()

        setProfiler(trace);

    }
    //public int getJUnitVersion(){return this.JUnitVersion;}
    //public void setJUnitVersion(int JUnitVersion){this.JUnitVersion = JUnitVersion;}

    public void setCompiledSdkVersion(String filename){
        compiledSdkVersion = getSdkVersion(filename);
    }

    public void addTestType(String path, String testType){
        this.testType.put(path,testType);
    }

    public String getTestType(String filepath){
        return this.testType.get(filepath);
    }


    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
        this.transFolder = project+tName+"/";
        this.transTests = transFolder+"tests"+"/";
        this.manifest = transFolder+"AndroidManifest.xml";
        this.manifestTest = transTests+"AndroidManifest.xml";
        this.devPackage = "";
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }


    public static int getSdkVersion(String filename){

        if(!filename.endsWith("build.gradle")){
           return 0;
        }
        int version =0;
        BufferedReader b = null;
        try {
            String s = "";
            b = new BufferedReader(new FileReader(filename));
            while ((s = b.readLine()) != null) {
                String s1 = new String(s);
                if(s.matches(".*compileSdkVersion.*")){
                    String [] tokens = s.trim().split("(\\s+)");
                    for (String tok:
                            tokens) {
                       //System.out.println("tok -> " + tok + " " + (tok.chars().allMatch( Character::isDigit)  ));
                        if (tok.chars().allMatch( Character::isDigit))
                            return Integer.parseInt(tok.trim());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;

    }

    public void generateTransformedTests() throws Exception{
        File fProject = new File(tests);
        File fTransf = new File(transTests); fTransf.mkdir();
        File[] listOfFiles = fProject.listFiles();

        //Copy all the files to the new project folder
        for(File f : listOfFiles){
            if(f.isDirectory()){
                if(!f.getName().equals("src") && !f.getName().equals(tName)){
                    FileUtils.copyFolder(f, new File(transTests+f.getName()));
                }else if(f.getName().equals("src")){
                    //PASS THE FILES THROUGH THE PARSING AND INSTRUMENTATION TOOL
                    this.instrumentSource(f, new File(transTests+"src"));
                }
            }else if(f.isFile()){
                File aux = new File(transTests+f.getName());
                aux.createNewFile();
                FileUtils.copyFile(f, aux);
            }
        }
        //this.editProjectDesc();

        File libsT = new File(transTests+"libs");
        if(!libsT.exists()){
            libsT.mkdir();
        }
        /*
        //File runner = new File(libsT.getAbsolutePath()+"/polidea_test_runner_1.1.jar");
        File runner = new File(libsT.getAbsolutePath()+"/android-junit-report-1.5.8.jar");
        
        runner.createNewFile();
        //FileUtils.copyFile(new File("libsAdded/polidea_test_runner_1.1.jar"), runner);
        FileUtils.copyFile(new File("libsAdded/android-junit-report-1.5.8.jar"), runner);
        */
        this.changeRunner();
    }

    public void generateTransformedProject() throws Exception{
        File fProject = new File(project);
        File fTransf = new File(transFolder); fTransf.mkdir();
        File[] listOfFiles = fProject.listFiles();

        //this.findLauncher();

        //Copy all the files to the new project folder
        for(File f : listOfFiles){
            if(f.isDirectory()){
                if(!f.getName().equals("src") && !f.getName().equals(tName) && !tests.contains(f.getAbsolutePath())){
                    FileUtils.copyFolder(f, new File(transFolder+f.getName()));
                }else if(f.getName().equals("src")){
                    //PASS THE FILES THROUGH THE PARSING AND INSTRUMENTATION TOOL
                    this.instrumentSource(f, new File(transFolder+"src"));
                }
            }else if(f.isFile()){
                File aux = new File(transFolder+f.getName());
                aux.createNewFile();
                FileUtils.copyFile(f, aux);
            }
        }
        //Save all methods definition in file
        String allMethods = "";
        FileUtils.copyAll(MethodChangerVisitor.getPackages(), packages);
        for(PackageM p : packages){
            allMethods += p.toString();
        }
        File auxF = new File(aux); auxF.mkdir();
        FileUtils.writeFile(new File(aux+"AllMethods"), allMethods);
        packages.clear();
        MethodChangerVisitor.restartPackages();

        this.addPermission();
        /*
        File libs = new File(transFolder+"libs");
        if (!libs.exists()) {
            libs.mkdir();
        }
        File greenDroid = new File(libs.getAbsolutePath()+"/greendroid.jar");
        greenDroid.createNewFile();
        FileUtils.copyFile(new File("libsAdded/greendroid.jar"), greenDroid);
        */
    }

    protected void findLauncher(){
        XMLParser.parseManifest(manifest);
        //this.devPackage = XMLParser.getDevPackage();
        this.devPackage = XMLParser.getBuildPackage();
    }

    protected void changeRunner(){
        File ax = new File(manifestTest);
        if(ax.exists()){
            XMLParser.editRunner(manifestTest);
        }
        String proj = XMLParser.getTestProjName()+".launch";
        //This line was only necessary if we were to run the tests via IDE
        //XMLParser.editRunConfiguration(workspace+".metadata/.plugins/org.eclipse.debug.core/.launches/"+proj, runnerClass);
    }

    protected void addPermission(){
        XMLParser.editManifest(manifest);
    }

    protected void editProjectDesc(){
        XMLParser.editProjectDesc(this.transFolder+".project");
        XMLParser.editProjectDesc(this.transTests+".project");
        XMLParser.editClasspath(this.transTests+".classpath");
    }



    protected void instrumentSource(File src, File dest) throws Exception{

        if(src.isDirectory()){
            //if directory not exists, create it
            if(!dest.exists()){
                dest.mkdir();
            }
            //list all the directory contents
            String files[] = src.list();
            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive transform
                instrumentSource(srcFile,destFile);

            }
        }
        else{
            //if file, then transform it
            if(src.getAbsolutePath().endsWith(".java")){
                String res = "";
                //if(src.getAbsolutePath().contains(this.tests) || src.getAbsolutePath().replace('\\', '/').contains(this.tests) || this.testType.containsKey(src)){
                if(this.isTestCase(src)){
                    res = transformTest(src.getAbsolutePath());
                
                }else{
                     res = transform(src.getAbsolutePath());
                }
                if(!res.equals("")){
                    dest.createNewFile();
                    FileUtils.writeFile(dest, res);
                }else{
                    dest.delete();
                }

            }else{
                setCompiledSdkVersion(src.getAbsolutePath());
                FileUtils.copyFile(src, dest);
            }
        }

    }

    /*This will be problematic in the future...*/
    protected boolean isInstrumentedTestCase(String filename){
        //System.out.println("file " +filename);
       try {
           return (filename.contains("androidTest")); //|| isTestCaseInstrumented(new File(filename));
       }catch (Exception e){
           System.out.println("[jInst] error in isInstrumentedtestCase");
       }
       return false;
    }


    static boolean stateSetup = false;
    protected void setupAppClass(String pack,String file){

        if (!stateSetup) {

            String appclass = XMLParser.getApplicationClass(originalManifest);
            if (appclass == null) {
                CreateAppClass(pack,file);
                this.appPackage = new ClassDefs(pack, "TrepApp");
            }
            else { // already exists a appclass

                String[] sp = appclass.split("\\.");
                this.appPackage = new ClassDefs(pack, sp[sp.length - 1]);
                // TODO check this î
            }
            stateSetup = true;
        }
    }


    protected String transform(String file) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(file);
        //System.out.println("Parsing & Instrumenting "+file);
        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in,null, false);
        } finally {
            in.close();
        }

        String pack = cu.getPackage().getName().toString();
        if(cu.getTypes()==null){
            return cu.toString();
        }
        String cl = cu.getTypes().get(0).getName();
        ClassDefs cDef = new ClassDefs();
        cDef.setName(cl); cDef.setPack(pack);

     //ImportDeclaration imp2 = new ImportDeclaration(ASTHelper.createNameExpr("com.greendroid.StaticEstimator"), false, false);
        // ImportDeclaration imp2 = new ImportDeclaration(ASTHelper.createNameExpr(" com.greenlab.trepnlib.TrepnLib"), false, false);
        ImportDeclaration imp2 = profiler.getLibrary();
        ImportDeclaration imp3 = null;
//        if (!traceMethods)
//             imp3 = new ImportDeclaration(ASTHelper.createNameExpr((appPackage.getPack()+"." + appPackage.getName())), false, false);


//        System.out.println("packs " + appPackage.getPack() + "|" + appPackage.getName() + "\n  cdef" + cDef.getPack() + "|" + cDef.getName());

//        if(this.appPackage.equals(cDef)){ // se é a appclass

        if(cu.getImports() != null){
            //cu.getImports().add(imp1);
            cu.getImports().add(imp2);
            //cu.getImports().add(imp1);
            if (!(this.appPackage.equals(cDef))&&imp3!=null)
                cu.getImports().add(imp3);
        }else{
            cu.setImports(new LinkedList<ImportDeclaration>());
            //cu.getImports().add(imp1);
            cu.getImports().add(imp2);
            //cu.getImports().add(imp1);
            if (!(this.appPackage.equals(cDef))&&imp3!=null)
                cu.getImports().add(imp3);
        }

        //TODO check if already has the import
        if(isApplicationClass(pack+"."+cl)){

            ImportDeclaration im = new ImportDeclaration(ASTHelper.createNameExpr("android.content.Context"), false, false);
            if(cu.getImports() != null){
                //cu.getImports().add(imp1);
                cu.getImports().add(im);
                //cu.getImports().add(imp1);
            }else{
                cu.setImports(new LinkedList<ImportDeclaration>());
                //cu.getImports().add(imp1);
                cu.getImports().add(im);
                //cu.getImports().add(imp1);
            }
        }

        // visit and change the methods names and parameters
        String classDec = cu.getTypes().get(0).getClass().getName();
        if(!classDec.contains("ClassOrInterfaceDeclaration")){
            return cu.toString();
        }
        ClassOrInterfaceDeclaration x = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);

//        if(new String(pack+"."+cl).equals(appPackage.getName())){
////            System.out.println("olha, encontrei-a!!!!!!!!!!!!!!!!!!");
//        }
        //The following condition is NEVER true
        String l = pack+"."+cl;
        if(this.devPackage.equals(l)){
            cDef.setLauncher(true);
        }
        if(!x.isInterface()){
            if(x.getExtends() != null){
                for(ClassOrInterfaceType ci: x.getExtends()){
                    String name = ci.getName();
                    if(name.contains("Activity")){
                        cDef.setActivity(true);
                    }
                }
            }

           if(!traceMethods) {
               ClassOrInterfaceVisitor v = new ClassOrInterfaceVisitor();
               v.setCu(cu);
               v.setTracedMethod(traceMethods);
               v.visit(cu, cDef);
           }
            MethodChangerVisitor v1= new MethodChangerVisitor();
            v1.setCu(cu);
            v1.setTracedMethod(traceMethods);
            v1.visit(cu, cDef);

        }
//        if(cu != null){
//            // prints the changed compilation unit
//            //System.out.println(cu.toString());
//        }
        return cu.toString();
    }


    protected String transformTest(String file) throws Exception {
        // append to file containing all classeswith tests
        try(FileWriter fw = new FileWriter(testClasses, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(file);
        } catch (IOException e) {
            System.out.println("[jInst] Error appending to testClasses.txt file");
        }


        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(file);
        ClassDefs cDef = new ClassDefs();
        //System.out.println("Parsing & Instrumenting "+file);
        CompilationUnit cu;
        boolean isJunitTest = false;
        boolean isTestable = false;
        boolean isJunitTest4 = false;
        try {
            // parse the file
            cu = JavaParser.parse(in,null, false);
        } finally {
            in.close();
        }
        //CHECK THE IMPORTS

        String pack = cu.getPackage().getName().toString();
        String cl = cu.getTypes().get(0).getName();
        cDef.setName(cl);
        cDef.setPack(pack);
        cDef.setAppName(this.devPackage);
        ImportDeclaration imp2=null, imp4 = null;

        //CHECK IF THE TEST CAN BE CONSIDERED FOR ENERGY MONITORING
        ClassOrInterfaceDeclaration x = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
        if(x.getExtends() != null){
            for(ClassOrInterfaceType ci: x.getExtends()){
                String name = ci.getName();
                if(testCase.contains(name)){
                    cDef.setInstrumented(false);
                    isTestable = true;
                }else if(instrumented.contains(name)){
                    cDef.setInstrumented(true);
                    isTestable = true;
                }else if(notTestable.equals(name)){
                    isJunitTest = true;
                }


                /*for(String ext : androidTests){
                    if(name.contains(ext)){
                        isTestable = true;
                    }
                }*/
            }
        }
        //added- check if is ijunit 4


            if(this.testType.get(file).equals("Junit4")){
                isTestable = true;
                cDef.setInstrumented(true);
                cDef.setJunit4(true);

            }

            if (this.testType.get(file).equals("Other")){
                cDef.setOther(true);
                isTestable=true;
            }


            else if(this.testType.get(file).equals("SuiteJunit4")) {
                isTestable = true;
                cDef.setInstrumented(true);
                cDef.setJunit4suite(true);
            }


//        if(isInstrumentedTestCase(file))
//            cDef.setInstrumented(true);
        if(isTestable){
            TestChangerVisitor t = new TestChangerVisitor();
            t.traceMethod = this.traceMethods;
            t.visit(cu, ((Object)cDef) );

            if(cDef.isJunit4suite()){
                if (!cDef.isBeforeClass()) {
                    //create the before method
                    LinkedList<NameExpr> _throws = new LinkedList<NameExpr>();
                    _throws.add(new NameExpr("Exception"));
                    MethodDeclaration newSetUp = new MethodDeclaration();
                    newSetUp.setName("before");
                    newSetUp.setModifiers(ModifierSet.PUBLIC);
                    newSetUp.setThrows(_throws);
                    newSetUp.setType(new VoidType());
                    LinkedList<AnnotationExpr> anot = new LinkedList<AnnotationExpr>();
                    anot.add(new MarkerAnnotationExpr(new NameExpr("BeforeClass")));
                    newSetUp.setAnnotations(anot);
                    newSetUp.setBody(new BlockStmt());
//                    MethodCallExpr mcS = new MethodCallExpr();

//                    if(traceMethods){
////                        mcS.setName("TrepnLib.startProfilingTest");
//
//                    }
//                    else {
//                        mcS.setName("TrepnLib.startProfiling");
//                    }

                    MethodCallExpr getContext = new MethodCallExpr();
                    if (!cDef.isInstrumented()) {
                        getContext.setName("this.getContext");
                    } else {
                        if (cDef.isOther()){
                            getContext.setName("this.getInstrumentation().getContext");
                        }
                        else{
                            getContext.setName("InstrumentationRegistry.getTargetContext");
                        }
                    }

                    MethodCallExpr mcS = profiler.startProfiler(getContext);
//                    ASTHelper.addArgument(mcS, getContext);

                    ArrayList<Statement> body = new ArrayList<Statement>();
                    body.add(new ExpressionStmt(mcS));
                    newSetUp.getBody().setStmts(body);



                    x.getMembers().add(0, newSetUp);
                    imp2 = new ImportDeclaration(ASTHelper.createNameExpr("org.junit.BeforeClass"), false, false);


                }
                if (!cDef.isAfterClass()) {
                    //create the tearDown method
                    LinkedList<NameExpr> _throws = new LinkedList<NameExpr>();
                    _throws.add(new NameExpr("Exception"));
                    MethodDeclaration newTearDown = new MethodDeclaration();
                    newTearDown.setName("after");
                    newTearDown.setThrows(_throws);
                    newTearDown.setModifiers(ModifierSet.PUBLIC);
                    newTearDown.setType(new VoidType());
                    LinkedList<AnnotationExpr> anot = new LinkedList<AnnotationExpr>();
                    anot.add(new MarkerAnnotationExpr(new NameExpr("AfterClass")));
                    newTearDown.setAnnotations(anot);
                    newTearDown.setBody(new BlockStmt());
//                    MethodCallExpr mcT = new MethodCallExpr();
//                    if(traceMethods){
//                        mcT.setName("TrepnLib.stopProfilingTest");
//                    }
//                    else {
//                        mcT.setName("TrepnLib.stopProfiling");
//                    }
                    MethodCallExpr getContext = new MethodCallExpr();
                    if (!cDef.isInstrumented()) {
                        getContext.setName("this.getContext");
                    } else {
                        if (cDef.isOther()){
                            getContext.setName("this.getInstrumentation().getContext");
                        }
                        else{
                            getContext.setName("InstrumentationRegistry.getTargetContext");
                        }
                    }
                    MethodCallExpr mcT = profiler.stopProfiler(getContext);
//                    ASTHelper.addArgument(mcT, getContext);
                    ArrayList<Statement> body = new ArrayList<Statement>();
                    body.add(new ExpressionStmt(mcT));
                    newTearDown.getBody().setStmts(body);

                    x.getMembers().add(0, newTearDown);
                    imp4 = new ImportDeclaration(ASTHelper.createNameExpr("org.junit.AfterClass"), false, false);
                }

            }
            else if(!cDef.isSuite()){

                if(cDef.isJunit4()){

                    if (!cDef.hasBefore()) {
                        //create the before method
                        LinkedList<NameExpr> _throws = new LinkedList<NameExpr>();
                        _throws.add(new NameExpr("Exception"));
                        MethodDeclaration newSetUp = new MethodDeclaration();
                        newSetUp.setName("before");
                        newSetUp.setModifiers(ModifierSet.PUBLIC);
                        newSetUp.setThrows(_throws);
                        newSetUp.setType(new VoidType());
                        LinkedList<AnnotationExpr> anot = new LinkedList<AnnotationExpr>();
                        anot.add(new MarkerAnnotationExpr(new NameExpr("Before")));
                        newSetUp.setAnnotations(anot);
                        newSetUp.setBody(new BlockStmt());

                        MethodCallExpr getContext = new MethodCallExpr();
                        if (!cDef.isInstrumented()) {
                            getContext.setName("this.getContext");
                        } else {
                            if (cDef.isOther()){
                                getContext.setName("this.getInstrumentation().getContext");
                            }
                            else{
                                getContext.setName("InstrumentationRegistry.getTargetContext");
                            }
                        }

//                        ASTHelper.addArgument(mcS, getContext);
                        MethodCallExpr mcS = profiler.startProfiler(getContext);
                        ArrayList<Statement> body = new ArrayList<Statement>();
                        body.add(new ExpressionStmt(mcS));

                        if(InstrumentHelper.compiledSdkVersion>22) {
                            ExpressionStmt exp = getReadPermissions();
                            ExpressionStmt exp1 = getWritePermissions();
                            ExpressionStmt ex2 = getTrepnlibReadPermissions();
                            ExpressionStmt exp3 = getTrepnlibWritePermissions();
                            body.add(0,exp);
                            body.add(0,exp1);
                            body.add(0,ex2);
                            body.add(0,exp3);

                        }
                        newSetUp.getBody().setStmts(body);
                        x.getMembers().add(0, newSetUp);
                        imp2 = new ImportDeclaration(ASTHelper.createNameExpr("org.junit.Before"), false, false);



                    }
                    if (!cDef.hasAfter()) {
                        //create the tearDown method
                        LinkedList<NameExpr> _throws = new LinkedList<NameExpr>();
                        _throws.add(new NameExpr("Exception"));
                        MethodDeclaration newTearDown = new MethodDeclaration();
                        newTearDown.setName("after");
                        newTearDown.setThrows(_throws);
                        newTearDown.setModifiers(ModifierSet.PUBLIC);
                        newTearDown.setType(new VoidType());
                        LinkedList<AnnotationExpr> anot = new LinkedList<AnnotationExpr>();
                        anot.add(new MarkerAnnotationExpr(new NameExpr("After")));
                        newTearDown.setAnnotations(anot);
                        newTearDown.setBody(new BlockStmt());
//                        MethodCallExpr mcT = new MethodCallExpr();
//                        if(traceMethods){
//                            mcT.setName("TrepnLib.stopProfilingTest");
//                        }
//                        else {
//                            mcT.setName("TrepnLib.stopProfiling");
//                        }
                        MethodCallExpr getContext = new MethodCallExpr();
                        if (!cDef.isInstrumented()) {
                            getContext.setName("this.getContext");
                        } else {
                            if (cDef.isOther()){
                                getContext.setName("this.getInstrumentation().getContext");
                            }
                            else{
                                getContext.setName("InstrumentationRegistry.getTargetContext");
                            }
                        }
//                        ASTHelper.addArgument(mcT, getContext);
                        MethodCallExpr mcT = profiler.stopProfiler(getContext);
                        ArrayList<Statement> body = new ArrayList<Statement>();
                        body.add(new ExpressionStmt(mcT));
                        newTearDown.getBody().setStmts(body);
                        x.getMembers().add(0, newTearDown);
                        imp4 = new ImportDeclaration(ASTHelper.createNameExpr("org.junit.After"), false, false);
                    }
                }
                else {

                    if (!cDef.hasSetUp()) {
                        //create the setUp method
                        LinkedList<NameExpr> _throws = new LinkedList<NameExpr>();
                        _throws.add(new NameExpr("Exception"));
                        MethodDeclaration newSetUp = new MethodDeclaration();
                        newSetUp.setName("setUp");
                        newSetUp.setModifiers(ModifierSet.PUBLIC);
                        newSetUp.setThrows(_throws);
                        newSetUp.setType(new VoidType());
                        LinkedList<AnnotationExpr> anot = new LinkedList<AnnotationExpr>();
                        anot.add(new MarkerAnnotationExpr(new NameExpr("Override")));
                        newSetUp.setAnnotations(anot);
                        newSetUp.setBody(new BlockStmt());

                        //add the setUp method
                        MethodCallExpr superSetUp = new MethodCallExpr();
                        superSetUp.setName("super.setUp");
                        MethodCallExpr getContext = new MethodCallExpr();
                        if (!cDef.isInstrumented()) {
                            getContext.setName("this.getContext");
                        } else {
                            if (cDef.isOther()){
                                getContext.setName("this.getInstrumentation().getContext");
                            }
                            else{
                                getContext.setName("InstrumentationRegistry.getTargetContext");
                            }
                        }
//                        ASTHelper.addArgument(mcS, getContext);
                        MethodCallExpr mcS = profiler.startProfiler(getContext);
                        ArrayList<Statement> body = new ArrayList<Statement>();
                        body.add(new ExpressionStmt(mcS));
                        body.add(new ExpressionStmt(superSetUp));
                        // -> invocacao das permissoes pode ser aqui???
                        if(InstrumentHelper.compiledSdkVersion>22) {
                            ExpressionStmt exp = InstrumentHelper.getReadPermissions();
                            ExpressionStmt exp1 = InstrumentHelper.getWritePermissions();
                            ExpressionStmt exp2 = InstrumentHelper.getTrepnlibReadPermissions();
                            ExpressionStmt exp3 = InstrumentHelper.getTrepnlibWritePermissions();
                            body.add(0,exp);
                            body.add(0,exp1);
                            body.add(0,exp2);
                            body.add(0,exp3);
                        }
                        newSetUp.getBody().setStmts(body);
                        x.getMembers().add(0, newSetUp);

                    }
                    if (!cDef.hasTearDown()) {
                        //create the tearDown method
                        LinkedList<NameExpr> _throws = new LinkedList<NameExpr>();
                        _throws.add(new NameExpr("Exception"));
                        MethodDeclaration newTearDown = new MethodDeclaration();
                        newTearDown.setName("tearDown");
                        newTearDown.setThrows(_throws);
                        newTearDown.setModifiers(ModifierSet.PUBLIC);
                        newTearDown.setType(new VoidType());
                        LinkedList<AnnotationExpr> anot = new LinkedList<AnnotationExpr>();
                        anot.add(new MarkerAnnotationExpr(new NameExpr("Override")));
                        newTearDown.setAnnotations(anot);
                        newTearDown.setBody(new BlockStmt());

                        MethodCallExpr superTearDown = new MethodCallExpr();
                        superTearDown.setName("super.tearDown");
//                        MethodCallExpr mcT = new MethodCallExpr();
//                        if(traceMethods){
//                            mcT.setName("TrepnLib.stopProfilingTest");
//                        }
//                        else {
//                            mcT.setName("TrepnLib.stopProfiling");
//                        }
                        MethodCallExpr getContext = new MethodCallExpr();
                        if (!cDef.isInstrumented()) {
                            getContext.setName("this.getContext");
                        } else {
                            if (cDef.isOther()){
                                getContext.setName("this.getInstrumentation().getContext");
                            }
                            else{
                                getContext.setName("InstrumentationRegistry.getTargetContext");
                            }
                        }
//                        ASTHelper.addArgument(mcT, getContext);
                        MethodCallExpr mcT = profiler.stopProfiler(getContext);
                        ArrayList<Statement> body = new ArrayList<Statement>();
                        body.add(new ExpressionStmt(mcT));
                        body.add(new ExpressionStmt(superTearDown));
                        newTearDown.getBody().setStmts(body);

                        x.getMembers().add(0, newTearDown);
                    }
                }
            }
//            ImportDeclaration imp1 = new ImportDeclaration(ASTHelper.createNameExpr("com.greenlab.trepnlib.TrepnLib"), false, false);
            ImportDeclaration imp1 = profiler.getLibrary();
            ImportDeclaration imp3 = null;
           if(cDef.isJunit4()||cDef.isJunit4suite()){
                imp3 = new ImportDeclaration(ASTHelper.createNameExpr("android.support.test.InstrumentationRegistry"), false, false);

           }

            if(cu.getImports() != null){
                cu.getImports().add(imp1);
                if(imp3!=null)
                    cu.getImports().add(imp3);
                if(imp2!=null)
                    cu.getImports().add(imp2);
                if(imp4!=null)
                    cu.getImports().add(imp4);
            }else{
                cu.setImports(new LinkedList<ImportDeclaration>());
                cu.getImports().add(imp1);
                if(imp2!=null)
                    cu.getImports().add(imp2);
                if(imp4!=null)
                    cu.getImports().add(imp4);
                if(imp3!=null)
                    cu.getImports().add(imp3);
            }
        }else if(x.getExtends() != null){
            if(isJunitTest){
                new MethodVisitor().visit(cu, cDef);
                if(cDef.hasTests()){
                    return "";
                } 
            }
        }
        return cu.toString();
    }





    /** creates application class
     * instruments manifest.xml to add android:name="appclass"
     * puts appclass in package pack
     * sets appPackage
     * use when there is no android:name yet
     */
    public void CreateAppClass(String pack, String file){
       // build path to  create class

        System.out.println(pack);
        String [] sp = file.split("/");
        String pathToFile ="";
        if(sp.length-1>0){
            for (int i = 0; i <sp.length-1 ; i++) {
                pathToFile += sp[i] +"/";
                if((pathToFile).equals(project)){
                    pathToFile +=  tName + "/";
                }
            }
        }
        pathToFile += "TrepApp.java";
        XMLParser.addApplicationName(manifest, pack + "." +"TrepApp");
        File src = new File("TrepApp.java");
        File dest = new File( pathToFile);
        try {
            FileUtils.copyFile(src, dest);
        }
        catch (Exception e){
            System.out.println("[JInst] CreateAppClass error in copying appfile ");
        }

        this.appPackage = new ClassDefs(pack,"TrepApp");


        //System.out.println("Parsing & Instrumenting "+file);
        CompilationUnit cu=null;
        FileInputStream in=null;
        try {
            // parse the file
             in = new FileInputStream(dest);
            cu = JavaParser.parse(in,null, false);
            PackageDeclaration p = new PackageDeclaration();
            p.setName(ASTHelper.createNameExpr(pack));
            cu.setPackage(p);
            FileUtils.writeFile(dest, cu.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public boolean isTestCase(File src) throws IOException {
        BufferedReader b = new BufferedReader(new FileReader(src));
        String s = "";
        boolean res = false;

        while ((s = b.readLine()) != null) {
            String s1 = new String(s);
            if(s.matches(".* class .+ extends (TestCase|ActivityUnitTestCase|ActivityIntrumentationTestCase2|ActivityTestCase|ProviderTestCase|SingleLaunchActivityTestCase|SyncBaseInstrumentation|ActivityInstrumentationTestCase|ActivityInstrumentationTestCase2|AndroidTestCase|ApplicationTestCase|LoaderTestCase|ProviderTestCase2|ServiceTestCasefail).*")){
                addTestType(src.getAbsolutePath(),"Other");
                res = true;

            }
            else if (s.matches(".*@Test.*")){
                res = true;
                addTestType(src.getAbsolutePath(), "Junit4");
            }
            if(s.matches(".*@SuiteClass.*")){
                addTestType(src.getAbsolutePath(), "SuiteJunit4");
                res = true;
            }

            if(res) break;
        }
        return res;
    }

    public boolean isTestCaseInstrumented(File src) throws IOException {
        //List<String> content = Files.readAllLines(Paths.get(src.getAbsolutePath()), Charset.defaultCharset());
//        System.out.println("fileeeee " + src.getAbsolutePath());
        BufferedReader b = new BufferedReader(new FileReader(src));
        String s = "";
        boolean res = false;

        while ((s = b.readLine()) != null) {
            String s1 = new String(s);
            if(s.matches(".* class .+ extends (ActivityUnitTestCase|ActivityIntrumentationTestCase2|ActivityTestCase|ProviderTestCase|SingleLaunchActivityTestCase|SyncBaseInstrumentation|ActivityInstrumentationTestCase).*")){
                return res;

            }
            else if (s.matches(".*import[ \\t]android.*"))
                return true;
        }
        return res;
    }

    public ClassDefs getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(ClassDefs appPackage) {
        this.appPackage = appPackage;
    }

    public  static Profiler getProfiler() {
        return profiler;
    }



    public static ExpressionStmt getWritePermissions(){
        ExpressionStmt exp = new ExpressionStmt();
        MethodCallExpr metodo = new MethodCallExpr();
        MethodCallExpr mce = new MethodCallExpr();
        MethodCallExpr mce1 = new MethodCallExpr();
        NameExpr mce2 = new NameExpr();
        mce2.setName("InstrumentationRegistry");
        mce1.setName("getInstrumentation");
        mce.setName("getUiAutomation");
        metodo.setName("executeShellCommand");
        mce.setScope(mce1);
        mce1.setScope(mce2);
        metodo.setScope(mce);
        exp.setExpression(metodo);
        LinkedList<Expression> list = new LinkedList<>();
        BinaryExpr bino = new BinaryExpr();
        bino.setOperator(BinaryExpr.Operator.plus);
        BinaryExpr left = new BinaryExpr();
        left.setLeft(new StringLiteralExpr("pm grant "));
        left.setOperator(BinaryExpr.Operator.plus);
        MethodCallExpr mceRight = new MethodCallExpr();
        MethodCallExpr mceRight1 = new MethodCallExpr();
        mceRight1.setScope(new NameExpr("InstrumentationRegistry"));
        mceRight1.setName("getTargetContext");
        mceRight.setScope(mceRight1);
        mceRight.setName("getPackageName");
        left.setRight(mceRight);
        bino.setLeft(left);
        bino.setRight(new StringLiteralExpr(" android.permission.WRITE_EXTERNAL_STORAGE"));
        list.add(bino);
        metodo.setArgs(list);
        return exp;
    }

    public static ExpressionStmt getTrepnlibWritePermissions(){
        ExpressionStmt exp = new ExpressionStmt();
        MethodCallExpr metodo = new MethodCallExpr();
        MethodCallExpr mce = new MethodCallExpr();
        MethodCallExpr mce1 = new MethodCallExpr();
        NameExpr mce2 = new NameExpr();
        mce2.setName("InstrumentationRegistry");
        mce1.setName("getInstrumentation");
        mce.setName("getUiAutomation");
        metodo.setName("executeShellCommand");
        mce.setScope(mce1);
        mce1.setScope(mce2);
        metodo.setScope(mce);
        exp.setExpression(metodo);
        LinkedList<Expression> list = new LinkedList<>();
        BinaryExpr bino = new BinaryExpr();
        bino.setOperator(BinaryExpr.Operator.plus);
        BinaryExpr left = new BinaryExpr();
        left.setLeft(new StringLiteralExpr("pm grant "));
        left.setOperator(BinaryExpr.Operator.plus);

        left.setRight(new StringLiteralExpr("com.greenlab.trepnlib"));
        bino.setLeft(left);
        bino.setRight(new StringLiteralExpr(" android.permission.WRITE_EXTERNAL_STORAGE"));
        list.add(bino);
        metodo.setArgs(list);
        return exp;
    }



    public static ExpressionStmt getReadPermissions(){
        ExpressionStmt exp = new ExpressionStmt();
        MethodCallExpr metodo = new MethodCallExpr();
        MethodCallExpr mce = new MethodCallExpr();
        MethodCallExpr mce1 = new MethodCallExpr();
        NameExpr mce2 = new NameExpr();
        mce2.setName("InstrumentationRegistry");
        mce1.setName("getInstrumentation");
        mce.setName("getUiAutomation");
        metodo.setName("executeShellCommand");
        mce.setScope(mce1);
        mce1.setScope(mce2);
        metodo.setScope(mce);
        exp.setExpression(metodo);
        LinkedList<Expression> list = new LinkedList<>();
        BinaryExpr bino = new BinaryExpr();
        bino.setOperator(BinaryExpr.Operator.plus);
        BinaryExpr left = new BinaryExpr();
        left.setLeft(new StringLiteralExpr("pm grant "));
        left.setOperator(BinaryExpr.Operator.plus);
        MethodCallExpr mceRight = new MethodCallExpr();
        MethodCallExpr mceRight1 = new MethodCallExpr();
        mceRight1.setScope(new NameExpr("InstrumentationRegistry"));
        mceRight1.setName("getTargetContext");
        mceRight.setScope(mceRight1);
        mceRight.setName("getPackageName");
        left.setRight(mceRight);
        bino.setLeft(left);
        bino.setRight(new StringLiteralExpr(" android.permission.READ_EXTERNAL_STORAGE"));
        list.add(bino);
        metodo.setArgs(list);
        return exp;
    }


    public static ExpressionStmt getTrepnlibReadPermissions(){
        ExpressionStmt exp = new ExpressionStmt();
        MethodCallExpr metodo = new MethodCallExpr();
        MethodCallExpr mce = new MethodCallExpr();
        MethodCallExpr mce1 = new MethodCallExpr();
        NameExpr mce2 = new NameExpr();
        mce2.setName("InstrumentationRegistry");
        mce1.setName("getInstrumentation");
        mce.setName("getUiAutomation");
        metodo.setName("executeShellCommand");
        mce.setScope(mce1);
        mce1.setScope(mce2);
        metodo.setScope(mce);
        exp.setExpression(metodo);
        LinkedList<Expression> list = new LinkedList<>();
        BinaryExpr bino = new BinaryExpr();
        bino.setOperator(BinaryExpr.Operator.plus);
        BinaryExpr left = new BinaryExpr();
        left.setLeft(new StringLiteralExpr("pm grant "));
        left.setOperator(BinaryExpr.Operator.plus);
        left.setRight(new StringLiteralExpr("com.greenlab.trepnlib"));
        bino.setLeft(left);
        bino.setRight(new StringLiteralExpr(" android.permission.READ_EXTERNAL_STORAGE"));
        list.add(bino);
        metodo.setArgs(list);
        return exp;
    }

}
