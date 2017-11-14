/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import jInst.visitors.utils.ReturnFlag;
import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 *
 * @author User
 */
class ForChangerVisitor extends VoidVisitorAdapter{

    @Override
    public void visit(ForStmt n, Object arg) {
        if(n.getCompare().toString().equals("true")){
           ReturnFlag x = (ReturnFlag)arg;
           x.setRet(true);
        }
    }
    
}
