/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 *
 * @author User
 */
public class SwitchEntryChangerVisitor extends VoidVisitorAdapter {

    @Override
    public void visit(SwitchEntryStmt n, Object arg) {
        BlockStmt b = new BlockStmt(n.getStmts());
        ReturnFinder.verify(b, (Statement)arg);
    }
    
}
