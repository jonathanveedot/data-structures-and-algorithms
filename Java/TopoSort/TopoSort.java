/**
 * TopoSort.java
 * 
 * This programming recitation is designed to demonstrate the fundamentals of 
 * TopoSort. A TopoSort of a directed graph is a linear ordering of its vertices 
 * such that for every directed edge uv from vertex u to vertex v, u comes 
 * before v in the ordering. A topological ordering is possible if and only if  
 * the graph has no directed cycles, that is, if it is a directed acyclic graph 
 * (DAG). Any DAG has at least one topological ordering, and algorithms are known  
 * for constructing a topological ordering of any DAG in linear time.
 * 
 * For instance, lets say we are editing a collection of essays for publication,
 * and many of the essays use terminology and concepts that are defined in other
 * essays. A TopoSort is an appropriate approach to arranging the collection of
 * essays so that an essay that uses a term must appear after the essay that 
 * defines that term. This program, given an input file, outputs whether there
 * are zero, one, or more than one arrangement of a collection of essays. 
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.System.exit;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonathan Velez
 */
public class TopoSort
{
    /**
     *
     * @param InputFile, an input file stored in the source package.
     * 
     * There will be multiple test cases in the input. 
     * 
     * Each test case will begin with a line with two integers, 
     * n (1 <= n <= 1000) and m (1 <= m <= 500000), where n is the number of 
     * essays, and m is the number of relationships between essays caused by 
     * sharing terms. They will be separated by a single space. On each of the 
     * next m lines will be two integers, d followed by u (1 <= d,u <= n, d != u) 
     * which indicate that some term is defined in essay d and used in essay u. 
     * Integers d and u will be separated by a single space. The input will end 
     * with two 0s on their own line.
     * 
     * For each test case, output a 0 if no arrangement is possible, a 1 if 
     * exactly one arrangement is possible, or a 2 if multiple arrangements are 
     * possible (output 2 no matter how many arrangements there are). Output no 
     * extra spaces, and do not separate answers with blank lines.
     */
    public TopoSort(File InputFile)
    {
        // V is the number of vertices in the graph
        // E is the number of edfes in the graph
        int V, E;
        int u, v;
        // input stream
        Scanner stream = null;
        try 
        {
            stream = new Scanner(InputFile);
        }
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(TopoSort.class.getName()).log(Level.SEVERE, null, ex);
            exit(1);
        }
        
        /* assumes input file is not empty and follows valid input format */
        V = stream.nextInt();
        E = stream.nextInt();
        
        while(V != 0 && E != 0)
        {
            // initialize the graph
            Graph BookOfEssays = new Graph(V);
            // add each edge
            for(int i = 0; i < E; i++)
            {
                u = stream.nextInt();
                v = stream.nextInt();
                
                // subtract 1 since our graph is zero-indexed
                BookOfEssays.addEdge(u-1, v-1);
            }
            
            // Topological sort and output number of arrangements
            System.out.println( BookOfEssays.Arrangements() );
            
            V = stream.nextInt();
            E = stream.nextInt();
        }   // end while
        
    }   // end Paths()
    
    /* EMBEDDED CLASS: Graph */
    protected class Graph 
    {
        final int m_nVertices;
        Vertex m_objGraph[];
    
        /**
         *
         * @param V, the number of vertices in this graph
         */
        public Graph(int V)
        {
            // initialize the graph
            this.m_nVertices = V;
            m_objGraph = new Vertex[m_nVertices];
            for(int i = 0; i < m_nVertices; i++)
            {
                m_objGraph[i] = new Vertex(i);
            }
           
        }   // end Graph(int)
        
        /**
         *
         * @param v, index of the vertex to be retrieved;
         * @return the vertex within the graph indexed @ v
         */
        protected Vertex getVertex(int v)
        {
            if(v >= m_nVertices)
            {
                return null;
            }
            
            return m_objGraph[v];
        }
        
        /**
         *
         * @param u, this is the source vertex
         * @param v, the target of the edge from the source (i.e., u -> v)
         */
        protected void addEdge(int u, int v)
        {
            if(u < m_nVertices && v < m_nVertices)
            {
                getVertex(u).addEdge( getVertex(v) );
            }
        }
        
        /**
         *
         * @return the number of possible topological sort arrangements 
         *  0 ==> exactly 0 arrangements
         *  1 ==> exactly 1 arrangement, 
         *  2 ==> more than 1 arrangement
         */
        protected int Arrangements()
        {
            // init array to track the in-degree of each vertex
            int Requirements[] = new int[m_nVertices];
            // init list with index of each complete essay.
            // complete implies it does not have any requirements other than 
            // those which have already been sorted.
            LinkedList Complete = new LinkedList();
            
            for(Vertex u : m_objGraph)
            {
                LinkedList<Vertex> Edges;
                Edges = u.getAdjList();
                for(Vertex v : Edges)
                {
                    Requirements[v.getIndex()]++;
                }
            }
            
            for(int i = 0; i < m_nVertices; i++)
            {
                if(Requirements[i] == 0)
                {
                    Complete.add(i);
                }
            }
            
            // assume one arrangement until proven otherwise
            int numArrangements = 1;
            
            for(int i = 0; i < m_nVertices; i++)
            {
                // if there are no complete essays
                if(Complete.size() == 0)
                {
                    // then this is a cycle 
                    numArrangements = 0;
                    break;
                }
                // if there is more than one complete essay
                else if(Complete.size() > 1)
                {
                    // then there is more than one solution
                    numArrangements = 2;
                }
                
                // take the next complete essay out of the sort
                int VertexIndex = (int)Complete.removeFirst();
                // the requirements of each essay still in the sort must be updated
                LinkedList<Vertex> Edges;
                Edges = getVertex(VertexIndex).getAdjList();
                for(Vertex v : Edges)
                {
                    --Requirements[v.getIndex()];
                    // if an essay becomes complete
                    if(Requirements[v.getIndex()] == 0)
                    {
                        // add it for placement in the topological sort
                        Complete.add(v.getIndex());
                    }
                }   // for v : Edges
                
            }   // end for i
            
            
            return numArrangements;
        }
        
     }   // end embedded class Graph
    
     /* EMBEDDED CLASS: Vertex */
     protected class Vertex
     {
         private final int index;    // index of this vertex within the graph
         LinkedList<Vertex> AdjList; // list of vertices adjacent to this
            
         /**
          *
          * @param n, the index of the vertex to be initialized
          */
         protected Vertex(int n)
         {
             this.index = n;
             AdjList = new LinkedList();
         }   // end Vertex(int, int)
        
         /**
          *
          * @param v, the vertex to be added to the adjacency list of this to form an edge 
          */
         protected void addEdge(Vertex v)
         {
             AdjList.add(v);
         }   // end addEdge(Vertex, int)
            
         /**
          *
          * @return the adjacency list of this
          */
         protected LinkedList getAdjList()
         {
             return AdjList;
         }   // end getAdjacent(int)
        
         /**
          *
          * @return the index of this
          */
         protected int getIndex()
         {
             return this.index;
         }   // end getIndex()
            
    }   // end embedded class Vertex
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        TopoSort topologicalOrder = new TopoSort(new File("input.txt"));
    }
    
}
