/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package instrumentation.visitors;

import instrumentation.visitors.utils.ReturnFlag;
import japa.parser.ASTHelper;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.List;
import instrumentation.visitors.utils.ClassDefs;
import instrumentation.util.ClassM;
import instrumentation.util.PackageM;

/**
 *
 * @author User
 */
public class MethodChangerVisitor extends VoidVisitorAdapter {
    private static ArrayList<PackageM> packages = new ArrayList<PackageM>();
    
    public static void restartPackages(){
        packages.clear();
    }
    
    public static ArrayList<PackageM> getPackages(){
        return packages;
    }
    
    private ClassM getClass(String cla, String pack){
        ClassM newC = null; PackageM newP = null;
        for(PackageM p : this.packages){
            if(p.getName().equals(pack)){
                newP = p;
                for(ClassM c : newP.getClasses()){
                    if(c.getName().equals(cla)){
                        newC = c;
                    }
                }
                if(newC == null){
                    newC = new ClassM(cla);
                    newP.getClasses().add(newC);
                }
            }
        }
        if(newP == null){
            newP = new PackageM();
            newP.setName(pack);
            newC = new ClassM(cla);
            newP.getClasses().add(newC);
            packages.add(newP);
        }
        return newC;
    }
    
    @Override
    public void visit(MethodDeclaration n, Object arg) {
        ClassDefs cDef = (ClassDefs)arg;
        String retType = n.getType().getClass().getName();
        
        //All the arguments for the function call
        Expression className = new StringLiteralExpr(cDef.getDescriptor());
        Expression method = new StringLiteralExpr(n.getName());
        Expression flagB = new IntegerLiteralExpr("0");
        Expression flagE = new IntegerLiteralExpr("1");
        
        if(n.getBody() != null){
            if(n.getBody().getStmts() != null){
                List<Statement> x = n.getBody().getStmts();

                MethodCallExpr mcB = new MethodCallExpr();
                mcB.setName("StaticEstimator.traceMethod");
                ASTHelper.addArgument(mcB, className);
                ASTHelper.addArgument(mcB, method);
                ASTHelper.addArgument(mcB, flagB);
                int insertIn = 0;
                if(n.getName().equals("onCreate")){
                    if(cDef.isLauncher()){
                        MethodCallExpr mcConfig = new MethodCallExpr();
                        mcConfig.setName("StaticEstimator.config");
                        MethodCallExpr getUid = new MethodCallExpr();
                        getUid.setName("SystemInfo.getInstance().getUidForPid");
                        MethodCallExpr getPid = new MethodCallExpr();
                        getPid.setName("android.os.Process.myPid");
                        ASTHelper.addArgument(getUid, getPid);
                        ASTHelper.addArgument(mcConfig, new ThisExpr());
                        ASTHelper.addArgument(mcConfig, getUid);
                        
                        MethodCallExpr mcStart = new MethodCallExpr();
                        mcStart.setName("StaticEstimator.start");

                        n.getBody().getStmts().add(1, new ExpressionStmt(mcConfig));
                        n.getBody().getStmts().add(2, new ExpressionStmt(mcStart));
                        insertIn += 2;
                    }
                    insertIn++;
                }
                x.add(insertIn, new ExpressionStmt(mcB));
                
                MethodCallExpr mcE = new MethodCallExpr();
                mcE.setName("StaticEstimator.traceMethod");
                ASTHelper.addArgument(mcE, className);
                ASTHelper.addArgument(mcE, method);
                ASTHelper.addArgument(mcE, flagE);

                ReturnFlag hasRet = new ReturnFlag();
                ReturnFlag unReachable = new ReturnFlag();
                new ReturnVisitor().visit(n, hasRet);
                new WhileChangerVisitor().visit(n, unReachable);
                new ForChangerVisitor().visit(n, unReachable);
                
                if(hasRet.hasRet()){
                    new GenericBlockVisitor().visit(n, new ExpressionStmt(mcE));
                }

                MethodDeclaration mt;
                String stm = x.get(x.size()-1).getClass().getName();
                if(stm.contains("ReturnStmt") || stm.contains("ThrowStmt")){
                    x.add((x.size()-1), new ExpressionStmt(mcE));
                }else if(retType.contains("VoidType")){
                    if(!unReachable.hasRet()){
                        x.add(new ExpressionStmt(mcE));
                    }
                }

                ClassM cm = this.getClass(cDef.getName(), cDef.getPack());
                cm.getMethods().add(n.getName());
            }
        }
    }

}
