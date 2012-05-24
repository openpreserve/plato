package eu.scape_project.planning.model.tree;


/**
 * Interface for tree nodes which support a visitor to walk the tree.
 *  
 * @author Michael Kraxner
 *
 */
public interface ITreeNode {
	
	boolean isLeaf();
	
	ITreeNode getParent();
	
	void walkTree(ITreeWalker treeWalker);
}
