/*
 * File: StringConstantsVisitor.java
 * Names: Jackie Hang, Kyle Slager
 * Class: CS361
 * Project 11
 * Date: February 13, 2019
 */

package proj12AhnSlager.bantam.semant;
import proj12AhnSlager.bantam.ast.*;
import proj12AhnSlager.bantam.visitor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * This class uses the Visitor pattern to traverse
 * a Program AST.
 *
 * Returns a Map of all the String constants in
 * the program and a unique identifier as
 * its key.
 *
 * @author  Jackie Hang, Kyle Slager
 * @version 1.0
 * @since   2-13-19
 */

public class StringConstantsVisitor extends Visitor {

    private ArrayList<String> stringList = new ArrayList<>();

    /**
     * Creates a unique key for every string
     * constant and adds to a Map
     * @param ast
     * @return Map of string constants
     */
    public Map<String, String> getStringConstants(Program ast) {
        Map<String,String> stringMap = new HashMap<String,String>();
        ast.accept(this);
        int stringNum = stringList.size();
        for(int i = 0; i < stringNum; i++){
            String strConstName = "StringConst_" + Integer.toString(i);
            stringMap.put(strConstName, stringList.get(i));
        }
        return stringMap;
    }

    /**
     * Overrides the visit method that takes in
     * a Constant String Expression.
     *
     * When the visitor encounters it, the constant gets
     * added to a string list
     * @param node the string constant expression node
     * @return
     */
    public Object visit(ConstStringExpr node){
        stringList.add(node.getConstant());
        return null;
    }
}
