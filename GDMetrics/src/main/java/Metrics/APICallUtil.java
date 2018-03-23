package Metrics;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ReferenceType;

import java.io.*;
import java.util.*;

public class APICallUtil implements Serializable {

    public   Set<String> allPackagesOfProject = new HashSet<>();
    public  Set<ClassInfo> allJavaClasses = new HashSet<>();

    public  ClassInfo getClassInfo(String javaFilePath) throws IOException, ParseException {

        ClassInfo thisClass = new ClassInfo();
        File file = new File(javaFilePath);
        FileInputStream in = new FileInputStream(file);
        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in,null, false);
        } finally {
            in.close();

        }

        for (Node n: cu.getChildrenNodes()) {

            if (n instanceof ClassOrInterfaceDeclaration){

                thisClass = new ClassInfo();
                thisClass.extendedClass = ((ClassOrInterfaceDeclaration) n).getExtends()==null? null : ((ClassOrInterfaceDeclaration) n).getExtends().get(0).getName();
                //allJavaClasses.add(thisClass);
                //add package declaration
                allPackagesOfProject.add(cu.getPackage().getName().toString());
                thisClass.classPackage= cu.getPackage().getName().toString();
                thisClass.className = ((ClassOrInterfaceDeclaration) n).getName();
                //set Imports
                if(cu.getImports()!=null){
                    for (ImportDeclaration id : cu.getImports()) {
                        NameExpression ne = new NameExpression(((QualifiedNameExpr) id.getName()).getQualifier().toString(),((QualifiedNameExpr) id.getName()).getName());
                        thisClass.classImports.add(ne);
                    }
                }

                if(n.getChildrenNodes()!=null){
                    //get local vars first , do not assume declaration at beggining
                    for(Node x: n.getChildrenNodes()) {
                        if (x instanceof FieldDeclaration) {
                            for (VariableDeclarator vd : ((FieldDeclaration) x).getVariables()) {
                                Variable cv = new Variable();
                                cv.type = ((FieldDeclaration) x).getType().toString();
                                cv.varName = vd.getId().getName();
                                thisClass.classVariables.put(cv.varName, cv);
                            }
                        }
                    }
                    for(Node x: n.getChildrenNodes()){
                        if (x instanceof MethodDeclaration){
                            MethodInfo mi = new MethodInfo();
                            if(((MethodDeclaration) x).getParameters()!=null){
                                for (Parameter m : ((MethodDeclaration) x).getParameters()){
                                    boolean isArray = (m.getType() instanceof ReferenceType) ? ((ReferenceType) m.getType()).getArrayCount()>0 : false;
                                   mi.args.add(new Variable(m.getId().getName() ,m.getType().toStringWithoutComments(), isArray));
                                }
                            }
                            mi.ci = thisClass;
                            mi.methodName = ((MethodDeclaration) x).getName();
                            mi.cyclomaticComplexity=CyclomaticCalculator.cyclomaticAndAPI(x, mi);
                            mi.nr_args = ((MethodDeclaration) x).getParameters() == null? 0 : ((MethodDeclaration) x).getParameters().size();
                            mi.linesOfCode =  ((MethodDeclaration) x).getBody()!=null ?    SourceCodeLineCounter.getNumberOfLines(((MethodDeclaration) x).getBody().toStringWithoutComments())-2 : 0;

                            thisClass.classMethods.put(mi.methodName, mi);
                        }

                        else if(x instanceof ConstructorDeclaration){
                            // iterate over stmt to sse class/instance vars usage
                            // check if methods called are internal or external
                            MethodInfo mi = new MethodInfo();
                            mi.ci = thisClass;
                            mi.methodName = ((ConstructorDeclaration) x).getName();
                            mi.cyclomaticComplexity=CyclomaticCalculator.cyclomaticAndAPI(x, mi);
                            mi.linesOfCode = ((ConstructorDeclaration) x).getBlock()!=null ?SourceCodeLineCounter.getNumberOfLines(((ConstructorDeclaration) x).getBlock().toStringWithoutComments())-2 :0;
                            mi.nr_args = ((ConstructorDeclaration) x).getParameters() == null? 0 : ((ConstructorDeclaration) x).getParameters().size();
                            thisClass.classMethods.put(mi.methodName, mi);

                        }
                    }
                }
            }


        }

        MethodClassifier(thisClass);
        this.allJavaClasses.add(thisClass);

       return thisClass;
    }



    private static void MethodClassifier(ClassInfo thisClass) {

        if (thisClass.classMethods.size()>0){
            for (MethodInfo m: thisClass.classMethods.values()) {
                Iterator<String> it = m.unknownApi.iterator();
                while (it.hasNext()){
                    String s = it.next();
                    String [] x = s.split("<|,");
                    boolean added =false;
                    if(x.length>1) {
                        for (String st : x) {
                            st = st.replaceAll(">", "").replace(" ", "");
                            if(!isPrimitiveType(st)) {
                                if (isAndroidApi(st, thisClass)) {
                                    m.androidApi.add(st);
                                    added = true;

                                } else if (isJavaApi(st, thisClass)) {
                                    m.javaApi.add(st);
                                    added = true;

                                } else {
                                    m.externalApi.add(st);
                                    added = true;

                                }
                            }
                        }
                        if (added){
                            added=false;
                            it.remove();
                        }
                    }
                    else{
                        if(!isPrimitiveType(s)) {
                            if (isAndroidApi(s, thisClass)) {
                                m.androidApi.add(s);
                                it.remove();
                            } else if (isJavaApi(s, thisClass)) {
                                m.javaApi.add(s);
                                it.remove();
                            } else {
                                m.externalApi.add(s);
                                it.remove();
                            }
                        }
                        else it.remove();
                    }
                }

            }
        }
    }



    private static boolean isJavaApi(String s, ClassInfo thisClass) {
         return getCorrespondantImport(s,thisClass).startsWith("java") ||s.equals("Integer") || s.equals("Double") || s.equals("Byte") || s.equals("Short") || s.equals("Long") || s.equals("Float") || s.equals("Character") || s.equals("Boolean") || s.equals("String") ;
    }

    private static boolean isPrimitiveType(String s) {
        return s.equals("int") || s.equals("double") || s.equals("byte") || s.equals("short") || s.equals("long") || s.equals("float") || s.equals("char") || s.equals("boolean");
    }

    private static boolean isAndroidApi(String s, ClassInfo ci) {
        return getCorrespondantImport(s,ci).startsWith("android") || getCorrespondantImport(s,ci).startsWith("org.apache.http") || getCorrespondantImport(s,ci).startsWith("org.xml") || getCorrespondantImport(s,ci).startsWith("org.w3c.dom") ||
                getCorrespondantImport(s,ci).startsWith("com.android.internal") || getCorrespondantImport(s,ci).startsWith("dalvik") ;

    }

    private static String getCorrespondantImport(String apiReference, ClassInfo ci) {
        String s = "";
        for (NameExpression importDec: ci.classImports) {
            if(importDec.name.equals(apiReference))
                return importDec.qualifier;
        }
        return s;
    }


    public static Set<String> getClassesUsed(MethodDeclaration md ){
    List <String> list = new ArrayList<>();
    for (Statement st: md.getBody().getStmts()) {
        if (st instanceof ExpressionStmt){

            if (((ExpressionStmt) st).getExpression() instanceof MethodCallExpr){

                if(((MethodCallExpr) ((ExpressionStmt) st).getExpression()).getScope()!=null)
                    list.add(((MethodCallExpr) ((ExpressionStmt) st).getExpression()).getScope().toString());
            }
        }
    }

    return new HashSet<>();

}

    public MethodInfo getMethodOfClass(String method, String fullClassName) {
        MethodInfo m = new MethodInfo();
        for (ClassInfo mi: allJavaClasses) {
            if(mi.getFullClassName().equals(fullClassName)){
                if(mi.getMethod(method)!=null)
                    return  mi.getMethod(method);
            }
        }
        return m;
    }


    public static void serializeAPICallUtil (APICallUtil acu, String path) {

        FileOutputStream fout = null;
        ObjectOutputStream oos = null;

        try {

            fout = new FileOutputStream(path);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(acu);

        } catch (Exception ex) {

            ex.printStackTrace();

        } finally {

            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    public static APICallUtil deserializeAPiCallUtil(String filename) {

        APICallUtil acu = null;

        FileInputStream fin = null;
        ObjectInputStream ois = null;

        try {

            fin = new FileInputStream(filename);
            ois = new ObjectInputStream(fin);
            acu = (APICallUtil) ois.readObject();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return acu;

    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ClassInfo ci : allJavaClasses) {
            sb.append(" " + ci.getFullClassName());
            for(MethodInfo mi : ci.classMethods.values()) {
               sb.append(mi);

           }
           sb.append("\n");
        }
        return sb.toString();
    }
}
