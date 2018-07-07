package jInst;

import Metrics.APICallUtil;
import Metrics.ClassInfo;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import jInst.util.XMLParser;

import java.io.File;
import java.io.IOException;

/**
 * Created by rrua on 30/05/17.
 */

public class tester {

    public static MethodCallExpr me = new MethodCallExpr();

   public static void main(String[] args){

       String file = "/Users/ruirua/repos/GreenDroid/tricky.xml";
       XMLParser.editManifest(file);

   }



    public static  int dummymethod(){
        MethodCallExpr mce = new MethodCallExpr();
        Integer [] batata = new Integer[10];
        Integer xx = new Integer(10).hashCode();
        xx.toString();
        me.getArgs();
        me = mce;
        System.out.println(me);
        int c = 0;
        //int x = c==0? 0 :1 ;
        ClassInfo ci = new ClassInfo();
        APICallUtil.getClassesUsed(new MethodDeclaration());
        //dummymethod();
        for (int i = 0; i <10  ; i++) {

            if(c > 0)
                return 2;
        }

        return 0;
    }
    }
