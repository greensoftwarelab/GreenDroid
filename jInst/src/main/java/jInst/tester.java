package jInst;

import Metrics.APICallUtil;
import Metrics.AndroidProjectRepresentation.ClassInfo;
import Metrics.MethodOfAPI;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import jInst.util.XMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rrua on 30/05/17.
 */

public class tester {

//    public static MethodCallExpr me = new MethodCallExpr();

//   public static void main(String[] args) throws Exception{
//
//       String file = "/Users/ruirua/repos/GreenDroid/jInst/src/main/java/jInst/tester.java";
//       CompilationUnit cu;
//       FileInputStream in = new FileInputStream(file);
//       try {
//           // parse the file
//           cu = JavaParser.parse(in,null, false);
//           System.out.println("");
//       } finally {
//           in.close();
//       }
//
//   }
//
//
//
//
//
//
//
//   public static Map<String,String> apisUsed (MethodDeclaration me ){
//       Map<String,String> returnMap = new HashMap<>();
//
//
//
//       return returnMap;
//   }
//
//
//
//    public static  int dummymethod(){
//        MethodCallExpr mce = new MethodCallExpr();
//        Integer [] batata = new Integer[10];
//        Integer xx = new Integer(10).hashCode();
//        xx.toString();
//        me.getArgs();
//        me = mce;
//        System.out.println(me);
//        int c = 0;
//        //int x = c==0? 0 :1 ;
//        ClassInfo ci = new ClassInfo();
//        APICallUtil.getClassesUsed(new MethodDeclaration());
//        //dummymethod();
//        for (int i = 0; i <10  ; i++) {
//
//            if(c > 0)
//                return 2;
//        }
//
//        return 0;
//    }

}
