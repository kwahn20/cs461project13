/**
 * Filename: ClassTreeNodeBuilder
 * Names: Kevin Ahn and Kyle Slager
 * CS461
 * Project 12
 */
package proj12AhnSlager.bantam.semant;

import proj12AhnSlager.bantam.util.Error;
import proj12AhnSlager.bantam.visitor.Visitor;
import proj12AhnSlager.bantam.util.*;
import proj12AhnSlager.bantam.ast.*;

import java.util.HashSet;
import java.util.Hashtable;

/**
 * A class to build the ClassTreeNodes which extends the visitor
 * to override the visit method to put the proper nodes in the classMap
 * @author KevinAhn, KyleSlager
 */
public class ClassTreeNodeBuilder extends Visitor {

    /**
     * Maps class names to ClassTreeNode objects representing the class
     */
    private Hashtable<String, ClassTreeNode> classMap;
    private ErrorHandler errorHandler;
    private Program program;
    private HashSet<String> dependenciesSet;

    /**
     * Constructor for the class
     * @param classMap
     * @param errorHandler
     * @param program
     */
    public ClassTreeNodeBuilder(Hashtable<String, ClassTreeNode> classMap, ErrorHandler errorHandler, Program program){
        this.classMap = classMap;
        this.program = program;
        this.errorHandler = errorHandler;
        this.dependenciesSet = new HashSet<>();
    }

    /**
     * method to call the programs accept method
     */
    public void build(){
        this.program.accept(this);
    }

    /**
     * Checks within the dependenciesSet HashSet to determine if the dependenciesSet contains the current ClassTreeNode
     * If it does, it represents a cyclic dependency
     * If not, then we add the name to the HashSet
     * We always iterate through the ChildrenList and check the dependencies of the children then
     *
     * @param node
     */
    public void checkDependencies(ClassTreeNode node){
        if(this.dependenciesSet.contains(node.getName())){
            errorHandler.register(Error.Kind.SEMANT_ERROR, "Cyclic Dependency detected with " + node.getName());
        }
        else{
            this.dependenciesSet.add(node.getName());
        }
        node.getChildrenList().forEachRemaining(child -> checkDependencies(child));

    }

    /**
     * visits the class node and adds the proper ClassTreeNode to the classMap
     * @param node the class node
     * @return
     */
    @Override
    public Object visit(Class_ node){
        ClassTreeNode classNode = new ClassTreeNode(node, false, true, this.classMap);
        ClassTreeNode object = this.classMap.get("Object");
        checkDependencies(object);
        classMap.put(node.getName(), classNode);
        return null;

    }
}
