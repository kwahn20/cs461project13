/*
 * File: NumLocalVarsVisitor.java
 * Names: Jackie Hang, Kyle Slager
 * Class: CS361
 * Project 11
 * Date: February 13, 2019
 */

package proj12AhnSlager.bantam.semant;

import java.util.HashMap;
import java.util.Map;

import proj12AhnSlager.bantam.ast.*;
import proj12AhnSlager.bantam.visitor.Visitor;

/**
 * This Visitor class uses the Visitor pattern to
 * visit every node of a program AST, but specifically captures
 * the number of local variables in every method.
 *
 *
 * @author  Jackie Hang, Kyle Slager
 * @version 1.0
 * @since   2-13-19
 */
public class NumLocalVarsVisitor extends Visitor{

    private Map<String, Integer> localVars;
    private String className;
    private int numCurVars;

    /**
     * Creates a map of every method and the number of
     * local variables in them.
     *
     * @param ast
     * @return Map<String, Integer>
     */
    public Map<String,Integer> getNumLocalVars(Program ast){
        numCurVars = 0;
        localVars = new HashMap<>();
        ast.accept(this);
        return localVars;
    }

    /**
     * Overrides the visit method of the visitor
     * that takes in a Class node.
     *
     * Ensures that all method names have a single class
     * attached to it.
     *
     * @param node the class node
     * @return
     */
    public Object visit(Class_ node){
        className = node.getName();
        return super.visit(node);
    }

    /**
     * Overrides the visit method of the visitor
     * that takes in a Method node.
     *
     * Adds all the method names to a map.
     *
     * @param node the method node
     * @return
     */
    public Object visit(Method node){
        numCurVars = node.getFormalList().getSize();
        node.getStmtList().accept(this);
        localVars.put(className + "." + node.getName(), numCurVars);
        return null;
    }

    /**
     * Overrides the visit method of the visitor
     * that takes in a Method node.
     *
     * Increments the counter for every local var found
     * in a method
     * @param node the declaration statement node
     * @return
     */
    public Object visit(DeclStmt node){
        numCurVars++;
        return null;
    }

    /**
     * Overrides to avoid visiting
     * @param node the expression node
     * @return
     */
    public Object visit(Expr node){
        return null;
    }

    /**
     * Overrides to avoid visiting
     * @param node the if statement node
     * @return
     */
    public Object visit(IfStmt node){
        if(node.getElseStmt() != null){
            node.getElseStmt().accept(this);
        }
        return null;
    }

    /**
     * Overrides to avoid visiting
     * @param node the for statement node
     * @return
     */
    public Object visit(ForStmt node){
        node.getBodyStmt().accept(this);
        return null;
    }

    /**
     * Overrides to avoid visiting
     * @param node the while statement node
     * @return
     */
    public Object visit(WhileStmt node){
        node.getBodyStmt().accept(this);
        return null;
    }


    /**
     * Overrides to avoid visiting
     * @param node the return statement node
     * @return
     */
    public Object visit(ReturnStmt node){
        return null;
    }



}



