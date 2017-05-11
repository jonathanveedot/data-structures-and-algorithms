/**
 * BST.java 
 * This class contains functions for maintaining a BST that tracks the value, 
 * subtree size, and rank of each node contained in the tree. 
 * Dependencies: BSTNode.java
 */

package binarysearchtree;

import java.util.ArrayList;

public class BST 
{
    /* the root of this tree */
    BSTNode m_objRootNode;
    /* k-value for insertion criterion (min distance from other node values) */
    private int K_VALUE;
	
    /**
     * Constructor
     * Each tree is initialized as an empty tree with root set to null and a 
     * default k-value of zero
     */
    public BST()
    {
        m_objRootNode = null;
        K_VALUE = 0; 
    }
    
    /**
     * Constructor
     * Each tree is initialized as an empty tree with root set to null, and sets
     * the k-value constraint. 
     * @param k, the K-value constraint for insertion.
     */
    public BST(int k)
    {
        m_objRootNode = null;
        K_VALUE = k;
    }

    /* METHODS FOR INSERTION/DELETION/SEARCH */ 
    
    /**
     * This is a wrapper method for inserting a node into the tree based on its 
     * key value then adjusting the rank of each node in the tree.
     * @param nKeyValue, the key value of the new node. 
     */
    public void insert( int nKeyValue ) 
    {
    	// The root node is returned to m_objRootNode from Insert()
    	m_objRootNode = Insert( nKeyValue, m_objRootNode );
        adjustRanks();
    }    

    /**
     * Recursive method to insert a new node into the tree based on its key value.
     * Any node inserted into the tree must pass the k-time test, where each node
     * in the tree cannot be within k distance of any other node. The k-value 
     * for the insertion criterion is set to zero upon initialization, but can be
     * reset to any value. 
     * @param nKeyValue, the key value of the new node.
     * @param objNode, the current node as the tree is traversed. 
     * @return the root of this tree after insertion. 
     */
    private BSTNode Insert( int nKeyValue, BSTNode objNode ) 
    {
    	// This node is null and simply needs to be allocated.
        if( objNode == null )
        {
            return( new BSTNode(nKeyValue) );
        }
        // if this node violates the K-constraint, just return
        else if(objNode.GetKeyValue()- nKeyValue < K_VALUE 
                && objNode.GetKeyValue() - nKeyValue > - K_VALUE)
        {
            return( objNode );
        }
        // here we need to walk left.
        else if( nKeyValue < objNode.GetKeyValue() )
        {
            // Set the left node of this object by recursively walking left.
            objNode.SetLeftNode( Insert( nKeyValue, objNode.GetLeftNode() ) );
        }
        
        // here we need to walk right.
        else if( nKeyValue > objNode.GetKeyValue() )
        {
            // set the right node of this object by recursively walking right.
            objNode.SetRightNode( Insert( nKeyValue, objNode.GetRightNode() ) );
        }
   
        objNode.SetTreeSize();
        return( objNode );
    }
    
    /**
     * This is a wrapper method for deleting a node from the tree based on its 
     * key value then adjusting the rank of each node in the tree.
     * @param nKeyValue, the key value of the new node. 
     */
    public void Delete(int nKeyValue)
    {
    	m_objRootNode = Delete( nKeyValue, m_objRootNode );
        adjustRanks();
    }
    
    /**
     * This method traverses the binary tree in search of the key value and
     * deletes that node.
     * @param nKeyValue, the key value of the node that is to be deleted.
     * @param objNode, the current node as the tree is traversed. 
     * @return the root of this tree after deletion. 
     */
    protected BSTNode Delete(int nKeyValue, BSTNode objNode)
    {
    	// if the tree is empty, there is nothing to be deleted
    	if(objNode == null)
    	{
            return null;
    	}
    	// search the left subtree
    	else if(nKeyValue < objNode.GetKeyValue())
    	{
            // continue to search recursively for key
            objNode.SetLeftNode( Delete(nKeyValue, objNode.GetLeftNode()) );
            // adjust tree size on the way up
            objNode.SetTreeSize();
    	}
    	// search the right subtree
    	else if(nKeyValue > objNode.GetKeyValue())
    	{
            objNode.SetRightNode( Delete(nKeyValue, objNode.GetRightNode()) );
            // adjust treesize on the way up
            objNode.SetTreeSize();
    	}
    	// hit on key value
    	else
    	{
            // if it's a leaf node, just remove it
            if(objNode.GetLeftNode() == null && objNode.GetRightNode() == null)
            {
                return null;
            }
            // if the right child is an only child, move that child up
            else if(objNode.GetLeftNode() == null)
            {
                return(objNode.GetRightNode());
            }
            // if the left child is an only child, move that child up
            else if(objNode.GetRightNode() == null)
            {
                return(objNode.GetLeftNode());
            }
            // if the node has two children, find the max value of the left subtree
            // to move up, and recursively delete that value from the left subtree
            else
            {
                // find the max node in the left subtree 
                //   Note: you can alternatively use the min node of the right subtree
                BSTNode objMaxNode = getMaxNode(objNode.GetLeftNode());
                // copy it and set its children to the node to be deleted
                BSTNode objReplacementNode = new BSTNode(objMaxNode.GetKeyValue());
                objReplacementNode.SetLeftNode(objNode.GetLeftNode());
                objReplacementNode.SetRightNode(objNode.GetRightNode());
                // recursively delete objMaxNode from its former location in the left subtree
                objReplacementNode.SetLeftNode(Delete(objMaxNode.GetKeyValue(), objReplacementNode.GetLeftNode()));
    		// update the tree-size of objReplacementNode and move it up.
                objReplacementNode.SetTreeSize();
    		return objReplacementNode;
            }
    	}
    	// in all other cases, return the original node
        return objNode;
    }
    
    /**
     * This wrapper method searches for a specified key value within this tree, 
     * and notifies the user if it is not contained within the tree. 
     * @param nKeyValue, the key value queried.
     * @return the node containing the key value or otherwise null.
     */
    public BSTNode Search( int nKeyValue )
    {
    	BSTNode objKeyNode = Search( m_objRootNode, nKeyValue );
    	if(objKeyNode == null)
    	{
            System.out.println("WARNING: Node("+nKeyValue+") not within tree");
    	}
        return( objKeyNode );
    }
    
    /**
     * This method searches for the target node containing the key value.
     * @param objNode, the current node.
     * @param nKeyValue, the key value queried. 
     * @return the node containing the key value or otherwise null. 
     */
    private BSTNode Search( BSTNode objNode, int nKeyValue )
    {
    	if( objNode == null )
    	{
            return( null );
    	}
    	
    	// First, we get the key value for this node.
    	int nThisKeyValue = objNode.GetKeyValue();
    	
    	// If the passed in key value is a hit, return the node
    	if( nKeyValue == nThisKeyValue )
    	{
            return( objNode );
    	}
            
    	// Otherwise keep searching
    	if( nKeyValue < nThisKeyValue )
    	{
            objNode = objNode.GetLeftNode();
    	}
    	else if( nKeyValue > nThisKeyValue )
    	{
            objNode = objNode.GetRightNode();
    	}
        
    	return( Search( objNode, nKeyValue ) );
    }
    
    /* SUPPORT METHODS FOR INSERTION/DELETION */
    
    /**
     * This is a quick and dirty method for adjusting the ranks of all nodes in 
     * the tree after inserting/deleting a node by adding all the nodes, in-order 
     * by key value, into a list and then setting rank as we iterate through each 
     * one. 
     */
    private void adjustRanks()
    {
        ArrayList<BSTNode> list = new ArrayList<>();
        Rank(m_objRootNode, list);
        
        for(int i = 0; i < list.size(); i++)
        {
            list.get(i).SetRank(i);
        }
    }
    
    /**
     * This method performs an in-order traversal of this tree and adds each node
     * to the specified list. 
     * @param objNode, the current node. 
     * @param list, the in-order list of nodes. 
     */
    private void Rank(BSTNode objNode, ArrayList<BSTNode> list)
    {
        if(objNode == null || list == null)
        {
            return;
        }
        Rank(objNode.GetLeftNode(), list);
        list.add(objNode);
        Rank(objNode.GetRightNode(), list);
    }
    
    /**
     * This method searches for the node with the maximum key value in a given 
     * subtree. 
     * @param objNode, the current node. 
     * @return the node with the maximum key value.
     */
    private BSTNode getMaxNode(BSTNode objNode)
    {
    	// if the tree is empty, there is no max
    	if(objNode == null)
    	{
            return null;
    	}
    	// Keep traversing up until the right-most node, this is the max
    	while(objNode.GetRightNode() != null)
    	{
            objNode = objNode.GetRightNode();
    	}
    	
    	return(objNode);
    }
    
    /**
     * This method searches for the node with the minimum key value in a given 
     * subtree. 
     * @param objNode, the current node. 
     * @return the node with the minimum key value.
     */
    private BSTNode getMinNode(BSTNode objNode)
    {
    	// if the tree is empty, there is no max
    	if(objNode == null)
    	{
            return null;
    	}
    	// Keep traversing up until the right-most node, this is the max
    	while(objNode.GetLeftNode() != null)
    	{
            objNode = objNode.GetLeftNode();
    	}
        
    	return(objNode);
    }
    
    /* PRINT METHODS */
    
    /**
     * This is a wrapper method that calls a recursive method to print the 
     * pre-order traversal of this tree followed by a new line. 
     */
    public void preOrderPrint()
    {
        preOrderPrint(m_objRootNode);
        System.out.println();
    }
    
    /**
     * This recursive method prints the pre-order traversal of this tree.
     * @param objNode, the current node. 
     */
    private void preOrderPrint(BSTNode objNode)
    {
        if(objNode == null)
        {
            return;
        }
        System.out.print(objNode.GetKeyValue() + " ");
        preOrderPrint(objNode.GetLeftNode());
        preOrderPrint(objNode.GetRightNode());
    }
    
    /**
     * This is a wrapper method that calls a recursive method to print the 
     * in-order traversal of this tree followed by a new line. 
     */
    public void inOrderPrint()
    {
        inOrderPrint(m_objRootNode);
        System.out.println();
    }
    
    /**
     * This recursive method prints the in-order traversal of this tree.
     * @param objNode, the current node. 
     */
    private void inOrderPrint(BSTNode objNode)
    {
        if(objNode == null)
        {
            return;
        }
        inOrderPrint(objNode.GetLeftNode());
        System.out.print(objNode.GetKeyValue() + " ");
        inOrderPrint(objNode.GetRightNode());
    }
    
    /**
     * This is a wrapper method that calls a recursive method to print the 
     * post-order traversal of this tree followed by a new line. 
     */
    public void postOrderPrint()
    {
        postOrderPrint(m_objRootNode);
        System.out.println();
    }
    
    /**
     * This recursive method prints the post-order traversal of this tree.
     * @param objNode, the current node. 
     */
    private void postOrderPrint(BSTNode objNode)
    {
        if(objNode == null)
        {
            return;
        }
        postOrderPrint(objNode.GetLeftNode());
        postOrderPrint(objNode.GetRightNode());
        System.out.print(objNode.GetKeyValue() + " ");
    }
    
    /* OTHER METHODS */
    
    public boolean IsEmpty()
    {
        return( m_objRootNode == null );
    }
    
    public void setK_VALUE(int k)
    {
        this.K_VALUE = k;
    }
    
    public int getK_VALUE()
    {
        return this.K_VALUE;
    }
    
    /**
     * This method overrides the toString function and uses the support function,
     * report, to report the value, rank, and subtree size of each node in this
     * tree.
     */
    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        report(m_objRootNode, buffer);
        return buffer.toString();
    }
    
    private void report(BSTNode node, StringBuffer buffer)
    {
        if(node == null)
        {
            return; 
        }
        
        report(node.GetLeftNode(), buffer);
        
        String current = "node("+node.GetKeyValue()+") is ranked #"+node.GetRank()+
                ", and has size "+node.GetTreeSize()+".\n";
        buffer.append(current);
        
        report(node.GetRightNode(), buffer);
    }
    
}
