/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import jInst.visitors.utils.ReturnFlag;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 *
 * @author User
 */
public class ReturnVisitor extends VoidVisitorAdapter {

    @Override
    public void visit(ReturnStmt n, Object arg) {
        ReturnFlag x = (ReturnFlag)arg;
        x.setRet(true);
        
    }
    
}
