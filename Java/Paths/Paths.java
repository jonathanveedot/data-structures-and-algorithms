/**
 * This programming recitation is designed to demonstrate some of the basic 
 * concepts of graphs and paths.
 * 
 * Given grid coordinates for a starting location and destination, as well as 
 * coordinates for "imperfect" intersections, determine the number of "perfect"
 * paths that can be traveled. In the grid, a path consists of a sequence of 
 * movements in one of four possible directions: North, South, East, West. 
 * Assume that each movement in a direction is a single block. A "perfect" path 
 * is a path that does not contain any movements in directly opposing directions 
 * (e.g., a path of NENNE is acceptable, but a path of NENSNNE is not). Blocks 
 * considered "imperfect" are to be avoided. 
 */
package paths;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonathan Velez
 */
public class Paths 
{   
    protected enum Direction {NORTH, SOUTH, EAST, WEST};
    private final static int COORDINATE_LIMIT = 10; 
    private final Graph Grid;
    
    public Paths()
    {
        Grid = new Graph(COORDINATE_LIMIT);
    } 
    
    /**
     * this method is wrapped by ComputePerfectPaths(xStart, yStart, xEnd, yEnd).
     * @param U, the current location.
     * @param V, the destination. 
     * @param xDirection, the valid movement direction on the x-axis for a 
     * "perfect" path given the origin and destination. 
     * @param yDirection, the valid movement direction on the y-axis for a 
     * "perfect" path given the origin and destination. 
     * @return the number of "perfect" paths given the origin and destination on
     * this graph. 
     */
    protected int PerfectPaths(Vertex U, Vertex V, Direction xDirection, Direction yDirection)
    {
        // if either U or V is invalid return 0
        if(U == null || V == null)
        {
            return 0;
        }
        // if this node U is to be avoided return 0
        if(U.Avoid())
        {
            return 0;
        }
        // if this node U is V, this path is valid, return 1
        if(U == V)
        {
            return 1;
        }
        // if either X or Y coordinate has not been reached
        // then explore paths in both directions
        if(U.getX() != V.getX() && U.getY() != V.getY())
        {
            return PerfectPaths(U.getAdjacent(xDirection), V, xDirection, yDirection)
                + PerfectPaths(U.getAdjacent(yDirection), V, xDirection, yDirection);
        }
        // if the X coordinate is a hit, continue exploring paths in the Y direction
        else if(U.getX() == V.getX())
        {
            return PerfectPaths(U.getAdjacent(yDirection), V, xDirection, yDirection);
        }
        // if the Y coordinate is a hit, continue exploring paths in the X direction
        else
        {
            return PerfectPaths(U.getAdjacent(xDirection), V, xDirection, yDirection);
        }
        
    }
    
    /**
     * this method wraps PerfectPaths(U, V, xDirection, yDirection), determining
     * the valid movement directions for a perfect path before invoking the 
     * recursive method that computes the number of possible "perfect" paths. 
     * @param xStart, the x coordinate of the origin location.
     * @param yStart, the y coordinate of the origin location. 
     * @param xEnd, the x coordinate of the destination location. 
     * @param yEnd, the y coordinate of the destination location. 
     * @return 
     */
    protected int ComputePerfectPaths(int xStart, int yStart, int xEnd, int yEnd)
    {
        Direction xDirection, yDirection;
        
        // determine which directions are valid for movement along a perfect
        // path according the start and end coordinates. 
        xDirection = (xEnd - xStart < 0) ? Direction.WEST : Direction.EAST;
        yDirection = (yEnd - yStart < 0) ? Direction.SOUTH : Direction.NORTH;
        
        return PerfectPaths(
                Grid.m_objVertices[xStart][yStart], 
                Grid.m_objVertices[xEnd][yEnd], 
                xDirection, 
                yDirection
        );
    }
    
    /**
     * 
     * @param file, the input file being processed.
     * There will be several sets of input. The first line will contain a single
     * positive integer n (n < 100) describing the number of test cases in the
     * data set. The first line in each data set has a single integer m (0 <= m < 10) 
     * which represents the number of "imperfect" intersections that must be 
     * avoided. The following m lines contain the coordinates x,y of the m 
     * intersections to avoid. All x and y coordinates will be non-negative integers 
     * less than the number of blocks on each axis of the grid (in this case, 10),
     * with the x coordinate appearing first on a line, followed by the y coordinate,
     * separated by a single space. The next line in the data set will be a single
     * positive integer p (0 < p < 10) that represents the number of sets of start
     * and end locations for which "perfect" paths are to be computed. The following
     * p lines of the data set will contain two pairs of x,y coordinates. The
     * first pair will be the starting location and the second pair will be the 
     * ending location. Each coordinate on each line is separated by a single space
     * from the previous and subsequent coordinates. None of the starting or ending
     * locations should occur at an intersection that is to be avoided. All of these
     * coordinates will also be non-negative integers less than the size of the grid
     * in either direction (in this case, 10), and separated by spaces on each line. 
     * <p>
     * The output will be in the following format:
     * "Test case c: There are P perfect paths."
     * or
     * "Test case c: There is 1 perfect path."
     */
    protected void ProcessPaths(String file)
    {
        // intialize Scanner to stream in input data for processing paths
        Scanner stream = null;
        try 
        {
            stream = new Scanner(new File(file));
        } 
        catch(FileNotFoundException ex) 
        {
            Logger.getLogger(Paths.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(stream != null)
        {
            int dataset = (stream.hasNext())? stream.nextInt() : 0;
            // for each dataset
            for(int i = 1; i <= dataset; i++)
            {
                System.out.println("Data Set " + i + ":");
                System.out.println();
                
                int avoidances = (stream.hasNext())? stream.nextInt(): 0;
                // for each avoidance
                for(int j = 0; j < avoidances; j++)
                {
                    // extract the coordinate of the imperfect vertex
                    // and set its avoidance flag
                    int x = (stream.hasNext())? stream.nextInt() : 0;
                    int y = (stream.hasNext())? stream.nextInt() : 0;
                    Grid.setVertexAvoidance(x, y);
                }
                
                int trips = (stream.hasNext())? stream.nextInt() : 0;
                // for each trip
                for(int j = 1; j <= trips; j++)
                {
                    // extract the start and end coordinates
                    int xStart = (stream.hasNext())? stream.nextInt() : 0;
                    int yStart = (stream.hasNext())? stream.nextInt() : 0;
                    int xEnd = (stream.hasNext())? stream.nextInt() : 0;
                    int yEnd = (stream.hasNext())? stream.nextInt() : 0;
                    
                    // compute the number of perfect paths
                    int P = ComputePerfectPaths(xStart, yStart, xEnd, yEnd);
                    if(P == 1)
                    {
                        System.out.println("  Test Case " + j + 
                            ": There is " + P + " perfect path.");
                    }
                    else
                    {
                        System.out.println("  Test Case " + j + 
                            ": There are " + P + " perfect paths.");
                    }
                    
                }
                System.out.println();
                Grid.resetAllAvoidances();
            }
            stream.close();
        }
    }
    
    /* This class is used to represent the grid within which paths are sought */
    protected class Graph 
    {
        // the limit of this graph's x and y plane
        // this graph contains (m_nLimit)^2 total intersections
        final int m_nLimit;  
        // contains this graph's vertices
        Vertex m_objVertices[][];
    
        /**
         *
         * @param axisLimit the limit of the graph's x and y axis
         */
        public Graph(int axisLimit)
        {
            m_nLimit = axisLimit;
            m_objVertices = new Vertex[m_nLimit][m_nLimit];
            // for each x coordinate
            for(int x = 0; x < m_nLimit; x++)
            {
                // and for each y coordinate
                for(int y = 0; y < m_nLimit; y++)
                {
                    // initialize that vertex
                    m_objVertices[x][y] = new Vertex(x, y);
                }
            }
            
            // for each x coordinate
            for(int x = 0; x < m_nLimit; x++)
            {
                // and for each y coordinate
                for(int y = 0; y < m_nLimit; y++)
                {
                    // find the adjacent vertex in each direction of this
                    // add and each valid one to its adjaceny list
                    int N = y + 1;
                    int S = y - 1;
                    int E = x + 1;
                    int W = x - 1;
                    
                    if(N < m_nLimit)
                    {
                        m_objVertices[x][y].addEdge(m_objVertices[x][N], Direction.NORTH);
                    }
                    if(S >= 0)
                    {
                        m_objVertices[x][y].addEdge(m_objVertices[x][S], Direction.SOUTH);
                    }
                    if(E < m_nLimit)
                    {
                        m_objVertices[x][y].addEdge(m_objVertices[E][y], Direction.EAST);
                    }
                    if(W >= 0)
                    {
                        m_objVertices[x][y].addEdge(m_objVertices[W][y], Direction.WEST);
                    }
                }   
            }   
        }   
        
        /**
         * this method sets the avoid flag to true at a given location.
         * @param x, the x coordinate of the vertex that is to be avoided.
         * @param y, the y coordinate of the vertex that is to be avoided. 
         */
        protected void setVertexAvoidance(int x, int y)
        {
            m_objVertices[x][y].setAvoid(true);
        }  
        
        /**
         * this method sets all avoid flags to false. 
         */
        protected void resetAllAvoidances()
        {
            for(int x = 0; x < m_nLimit; x++)
            {
                for(int y = 0; y < m_nLimit; y++)
                {
                    if(m_objVertices[x][y].Avoid())
                    {
                        m_objVertices[x][y].setAvoid(false);
                    }
                }   
            }   
        }   
    }   // end Class Graph
    
     /* This class is used to represent each vertex within the graph */
        protected class Vertex
        {
            private final int xCoord;   // x-coordinate of this within graph
            private final int yCoord;   // y-coordinate of this within graph
            private boolean avoid;      // flag for imperfect vertices to avoid
            Vertex AdjList[];           // list of vertices adjacent to this

            protected Vertex(int x, int y)
            {
                xCoord = x;
                yCoord = y;
                avoid = false;
                AdjList = new Vertex[4]; // each vertex can have at max 4 edges
                
                for(int i = 0; i < AdjList.length; i++)
                {
                    AdjList[i] = null;
                }   
            }   
        
            /**
             * 
             * @param v, the vertex that is being connected to this.
             * @param direction, the direction of v relative to this. 
             */
            protected void addEdge(Vertex v, Direction direction)
            {
                AdjList[direction.ordinal()] = v;
            }   
            
            /**
             * 
             * @param direction, the direction of the connected vertex of interest.
             * @return the vertex adjacent to this in the specified direction.
             */
            protected Vertex getAdjacent(Direction direction)
            {
                return AdjList[direction.ordinal()];
            }   
        
            protected int getX()
            {
                return xCoord;
            }  
            
            protected int getY()
            {
                return yCoord;
            }  
            
            protected void setAvoid(boolean set)
            {
                avoid = set;
            }   // end setAvoid(boolean)
            
            protected boolean Avoid()
            {
                return avoid;
            }   // end Avoid()
            
        }   // end Class Vertex
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        String file = "input.txt";
        Paths paths = new Paths();
        paths.ProcessPaths(file);
    }
    
}
