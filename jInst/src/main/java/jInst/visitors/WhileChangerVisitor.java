/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import jInst.visitors.utils.ReturnFlag;
import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 *
 * @author User
 */
class WhileChangerVisitor extends VoidVisitorAdapter{

    @Override
    public void visit(WhileStmt n, Object arg) {
        if(n.getCondition().toString().equals("true")){
            ReturnFlag x = (ReturnFlag)arg;
            x.setRet(true);
        }
    }
    
}
