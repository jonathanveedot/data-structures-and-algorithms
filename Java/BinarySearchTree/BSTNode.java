/**
 * BSTNode.java
 * An instance of this class represents a node within a BST. Each node tracks its
 * key value, tree-size, rank, and left and right sub-tree. The tree-size is the
 * number of nodes within the tree rooted at this node, including this node. The
 * rank is the ordinal position of this node in the tree, index starting at zero.
 * Dependencies: None
 */

package binarysearchtree;

public final class BSTNode 
{
        private static final int THIS_NODE = 1;
        private int m_nKeyValue;
        private int m_nTreeSize;
        private int m_nRank;
        private BSTNode m_objLeftNode, m_objRightNode;
        
        /**
         * This method constructs a new BST node. 
         * @param nKeyValue, the value of the current node.
         */
        protected BSTNode(int nKeyValue)
        {
                SetKeyValue(nKeyValue);
                SetTreeSize();
        }

        /**
         * Method to set the left node of this node and update its size.
         * @param objLeftNode, the root of the sub-tree connecting to the left of 
         * this node. 
         */
        protected void SetLeftNode(BSTNode objLeftNode)
        {
                this.m_objLeftNode = objLeftNode;
                SetTreeSize();
        }
        
        /**
         * Method to get the left node of this node.
         * @return the left node of this node.
         */
        protected BSTNode GetLeftNode()
        {
                return( m_objLeftNode );
        }
        
        /**
         * Method to set the right node of this node and update its size.
         * @param objRightNode, the root of the sub-tree connecting to the right 
         * of this node. 
         */
        protected void SetRightNode(BSTNode objRightNode)
        {
                this.m_objRightNode = objRightNode;
                SetTreeSize();
        }
	
        /**
         * Method to get the right node of this node.
         * @return the right node of this node.
         */
        protected BSTNode GetRightNode()
        {
                return( m_objRightNode );
        }

        /**
         * Method to set the key value of this node at construction. 
         * @param nKeyValue 
         */
        private void SetKeyValue( int nKeyValue )
        {
                this.m_nKeyValue = nKeyValue;
        }
	
        /**
         * Method to get the key value of this node. 
         * @return the key value of this node. 
         */
        protected int GetKeyValue()
        {
                return( m_nKeyValue );
        }
	
        /** 
         * Method to set the size of this node to the size of both of its 
         * sub-trees plus 1 for itself. 
         */
        protected void SetTreeSize()
        {
                int nLeftSize = (m_objLeftNode == null)? 0 : m_objLeftNode.GetTreeSize();
                int nRightSize = (m_objRightNode == null)?  0 : m_objRightNode.GetTreeSize();
                this.m_nTreeSize = THIS_NODE + nLeftSize + nRightSize;
        }
        
        /**
         * Method to get the size of the subtree rooted at this node.
         * @return the subtree size at this node. 
         */
        protected int GetTreeSize()
        {
                return(this.m_nTreeSize);
        }

        /**
         * Method to set the rank of this node.
         * @param nRank 
         */
        protected void SetRank(int nRank)
        {
                this.m_nRank = nRank;
        }
	
        /**
         * Method to get the rank of this node.
         * @return the rank of this node
         */
        protected int GetRank()
        {
                return(this.m_nRank);
        }
}
