/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jInst.transform.InstrumentHelper;
import jInst.visitors.utils.ClassDefs;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author User
 */

//changed
public class ActivityChangerVisitor extends VoidVisitorAdapter {


    // not using this class, this method is wrong
    @Override
    public void visit(MethodDeclaration n, Object arg) {
        // here you can access the attributes of the method.
        // this method will be called for all methods in this
        // CompilationUnit, including inner class methods

        /** Must not include replications! CHECK LATER!*/
        ClassDefs flags=(ClassDefs)arg;
        String name = n.getName();
        int insertIn = 0;
        if(name.equals("onCreate") && !flags.isLauncher()){
            MethodCallExpr setAppContext = new MethodCallExpr();
            setAppContext.setName(InstrumentHelper.getApplicationFullName() + ".setAppContext");

            MethodCallExpr mcStart = new MethodCallExpr();
            mcStart.setName("TrepnLib.updateState");
            StringLiteralExpr s = new StringLiteralExpr( ((ClassDefs) arg).getPack() + "." +n.getName());
            IntegerLiteralExpr i = new IntegerLiteralExpr("1");
            ASTHelper.addArgument(mcStart, s);
            ASTHelper.addArgument(mcStart, s);
//            List<Statement> x = n.getBody().getStmts() != null ? n.getBody().getStmts() : new LinkedList<Statement>();
//            x.add(0, new ExpressionStmt(mcStart));
//            n.getBody().setStmts(x);

            /*if(flags.isLauncher()){
                MethodCallExpr mcConfig = new MethodCallExpr();
                mcConfig.setName("StaticEstimator.config");
                MethodCallExpr getUid = new MethodCallExpr();
                getUid.setName("SystemInfo.getInstance().getUidForPid");
                MethodCallExpr getPid = new MethodCallExpr();
                getPid.setName("android.os.Process.myPid");
                ASTHelper.addArgument(getUid, getPid);
                ASTHelper.addArgument(mcConfig, new ThisExpr());
                ASTHelper.addArgument(mcConfig, getUid);

                n.getBody().getStmts().add(insertIn, new ExpressionStmt(mcConfig));
                insertIn++;
            }*/
            n.getBody().getStmts().add(insertIn, new ExpressionStmt(mcStart));
        }
        if(name.equals("onStop")){
            //flags.setStop(true);
            //include 'saveMatrix()' at the begining;
//            MethodCallExpr mcStart = new MethodCallExpr();
//            mcStart.setName("TrepnLib.updateState");
//            StringLiteralExpr s = new StringLiteralExpr( ((ClassDefs) arg).getPack() + "." +n.getName());
//            IntegerLiteralExpr i = new IntegerLiteralExpr("1");
//            ASTHelper.addArgument(mcStart, s);
//            ASTHelper.addArgument(mcStart, s);
//            n.getBody().getStmts().add(0, new ExpressionStmt(mcStart));
        }


    }
}
