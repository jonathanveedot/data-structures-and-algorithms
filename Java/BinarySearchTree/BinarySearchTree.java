/**
 * BinarySearchTree.java
 *
 * This recitation demonstrates the fundamentals of binary search trees (BSTs).
 * In this implementation, each node in the BST tracks its size and rank within
 * the tree (the lowest value is ranked #1). Insertion is constrained by some 
 * value k, representing the minimum acceptable distance from other nodes in the
 * tree. 
 */
package binarysearchtree;

public class BinarySearchTree
{
	static BST m_objBST = new BST();
	
	public static void main(String[] args) 
	{
		int[] nTestSet = {13, 69, 9, 89, 50};
		BST objSearchTree = new BST();
		int KEY = 69;
		
		for(int i = 0; i < nTestSet.length; i++)
		{
			objSearchTree.insert(nTestSet[i]);
		}
		
		objSearchTree.preOrderPrint();
		objSearchTree.inOrderPrint();
    System.out.println( objSearchTree.toString() );
	}
}
