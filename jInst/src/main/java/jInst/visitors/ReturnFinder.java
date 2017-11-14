/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jInst.visitors;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class ReturnFinder {
    
    public static boolean verify(Statement st, Statement mcE){
        boolean res = false;
        List<Statement> list;
        BlockStmt bl = (BlockStmt)st;
        list = bl.getStmts();
        
        int i = 0; ArrayList<Integer> insertIn = new ArrayList<Integer>();
        if(list != null){
            for(Statement s : list){
                String stm = s.getClass().getName();
                if(stm.equals("com.github.javaparser.stmt.ReturnStmt") || stm.equals("com.github.javaparser.stmt.ThrowStmt") || stm.equals("com.github.javaparser.stmt.ContinueStmt")){
                    insertIn.add(i);
                    res = true;
                }
                i++;
            }
        }
        if(res){
            for(Integer j : insertIn){
                list.add(j, mcE);
            }
        }


        return res;
    }
}
