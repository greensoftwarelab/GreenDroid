/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 *
 * @author User
 */
class TryChangerVisitor extends VoidVisitorAdapter{

    public TryChangerVisitor(){
    }
    
    @Override
    public void visit(TryStmt n, Object arg) {

        if(n.getTryBlock()!= null){
            ReturnFinder.verify(n.getTryBlock(), (Statement)arg);
        }
        
        if(n.getFinallyBlock() != null){
            ReturnFinder.verify(n.getFinallyBlock(), (Statement)arg);
        }
        
    }
    
}
