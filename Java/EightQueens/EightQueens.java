/**
 * EightQueens.java
 * 
 * This is a programming recitation designed to demonstrate the basics of 
 * back-tracking and threading using the "Eight Queens" puzzle. This puzzle is 
 * the problem of placing eight chess queens on an 8x8 chessboard so that no two
 * queens "threaten" each other. This requires that no two queens share the same 
 * row, column, or diagonal.
 */

import java.applet.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * 
 * @author Jonathan Velez
 */

public class EightQueens extends Applet 
implements MouseListener, MouseMotionListener, Runnable, ActionListener
{
        /* Game Board Attributes */
	static final int SIZE = 8;
        static final int NUMROWS = SIZE;
        static final int NUMCOLS = SIZE;
        static final int SQUAREWIDTH = 50;
        static final int SQUAREHEIGHT = 50;
        static final int BOARDLEFT = 50;
        static final int BOARDTOP = 50;
        int m_nBoard[][] = new int[NUMROWS][NUMCOLS];
        Button m_objButton = new Button("Solve");
        
        /* boolean and String vars to track the state of the game-board */
        boolean m_bClash = false;
        boolean m_bSolving = false;
        String m_strStatus = "Eight Queens: Can you solve it?!";

        /* Image and MediaTracker objects to load game images */
        static final long serialVersionUID = 1L;
        MediaTracker tracker = new MediaTracker(this);
        BufferedImage m_imgQueen;
        Image m_objOffscreen;
        
        /* Thread for back-tracking algorithm that solves the game */
        Thread m_objThread;
        
        /* APPLET METHODS */
        
        /** 
         * Called by the browser or applet viewer to inform this applet that it 
         * has been loaded into the system.
         */
        @Override
        public void init()
        {
                 // set size
                setSize(1020, 700);
                
                // create off-screen image used for repaint()
                m_objOffscreen = createImage(1020, 700);
                
                // add action listeners
                addMouseListener(this);
                addMouseMotionListener(this);
                // add "solve" button
                add(m_objButton);
                m_objButton.addActionListener(this);
                
                // load queen image
                try
                {
                        m_imgQueen = ImageIO.read(EightQueens.class.getResourceAsStream("queen.png"));
                }
                catch(IOException e)
                {
                        e.printStackTrace();
                }
                tracker.addImage(m_imgQueen, 100);
                try
                {
                        tracker.waitForAll();
                }
                catch(InterruptedException e)
                {
                        e.printStackTrace();
                }
                
                // run the thread
                m_objThread = new Thread(this);
                m_objThread.start();
        }       // end init()
        
        /** 
         * This method is inherited from class java.awt.Container and paints the
         * container according to the board-state. 
         * @param canvas, the canvas to be painted.
         */
        @Override
        public void paint(Graphics canvas)
        {
                // initialize Clash state to false
                m_bClash = false;
                // draw the game-board
                DrawSquares(canvas);
                // set canvas color to red to draw a line between clashes
                canvas.setColor(Color.red);
                
                // check for clashes within column, row, and diagonals using the
                // four support methods below
                CheckColumns(canvas);
                CheckRows(canvas);
                CheckDiagonal1(canvas);
                CheckDiagonal2(canvas);
                
                canvas.setColor(Color.blue);
                canvas.drawString(
                        m_strStatus, 
                        BOARDLEFT, 
                        BOARDTOP+SQUAREHEIGHT*8+20
                );
        }

        /**
         * This method is inherited from class java.awt.Component, and repaints 
         * the updated canvas with a call to the paint(Graphics) method.
         */
        @Override
        public void repaint()
        {
        		Graphics g = m_objOffscreen.getGraphics();
        		g.setColor(Color.white);
        		g.fillRect(0, 0, 1020,700);
        		paint(g);
        		getGraphics().drawImage(m_objOffscreen, 0, 0, null);
        }
        
        /* SUPPORT METHODS FOR paint(Graphics) */
        
        /**
         * This method draws the canvas squares
         * @param canvas, the current game-board.
         */
        void DrawSquares(Graphics canvas)
        {
                canvas.setColor(Color.BLACK);
                
                // for each row
                for(int nRow = 0; nRow < NUMROWS; nRow++)
                {
                        // for each column
                        for(int nCol = 0; nCol < NUMCOLS; nCol++)
                        {
                                // draw this square on the board
                                canvas.drawRect(
                                        BOARDLEFT+nCol*SQUAREWIDTH, 
                                        BOARDTOP+nRow*SQUAREHEIGHT, 
                                        SQUAREWIDTH, 
                                        SQUAREHEIGHT
                                );
                                // if there is a queen on this square load its image
                                if(m_nBoard[nRow][nCol] != 0)
                                {
                                        canvas.drawImage(
                                                m_imgQueen, 
                                                BOARDLEFT+nCol*SQUAREWIDTH+8, 
                                                BOARDTOP+nRow*SQUAREHEIGHT+6, 
                                                null
                                        );
                                }
                        }
                }
        }
        
        /**
         * This method checks each column for collisions.
         * @param canvas, the current game-board.
         */
        void CheckColumns(Graphics canvas)
        {
                // for each column
                for(int nCol = 0; nCol < NUMCOLS; nCol++)
                {
                        int nQueenCount = 0;
                        // for each row
                        for(int nRow = 0; nRow < NUMROWS; nRow++)
                        {
                                // count how many queens are in that column
                                if(m_nBoard[nRow][nCol] != 0)
                                {
                                        nQueenCount++;
                                }
                        }
                        
                        // if there is more than one queen in that column
                        if(nQueenCount > 1)
                        {
                                // draw a line between the two clashing queens and set Clash state to true
                                canvas.drawLine(
                                        BOARDLEFT + nCol*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + (SQUAREWIDTH/2),
                                        BOARDLEFT + nCol*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + SQUAREHEIGHT*(NUMCOLS-1) + (SQUAREHEIGHT/2)
                                );
                                m_bClash = true;
                        }
                }
        }
        
        /**
         * This method checks each row for collisions.
         * @param canvas, the current game-board.
         */
        void CheckRows(Graphics canvas)
        {
                // for each row
                for(int nRow = 0; nRow < NUMROWS; nRow++)
                {
                        int nQueenCount = 0;
                        // for each column
                        for(int nCol = 0; nCol < NUMCOLS; nCol++)
                        {
                                // count how many queens are in that row
                                if(m_nBoard[nRow][nCol] != 0)
                                {
                                        nQueenCount++;
                                }
                        }
                        
                        // if there is more than one queen in that row
                        if(nQueenCount > 1)
                        {
                                // draw a line between the two clashing queens and set Clash state to true
                                canvas.drawLine(
                                        BOARDLEFT+(SQUAREWIDTH/2),
                                        BOARDTOP + nRow*SQUAREHEIGHT + (SQUAREHEIGHT/2),        
                                        BOARDLEFT + (NUMROWS-1)*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + nRow*SQUAREHEIGHT + (SQUAREHEIGHT/2)
                                );
                                m_bClash = true;
                        }
                }
        }
        
        /**
         * This method checks for collisions for each diagonal in a direction. 
         * @param canvas, the current game-board.
         */
        void CheckDiagonal1(Graphics canvas)
        {
                // for each row beginning from the last
                for( int nRow=NUMROWS-1; nRow >= 0; nRow-- )
                {
                        int nCol = 0;   
                        int nThisRow = nRow;
                        int nThisCol = nCol;
                        int nQueenCount = 0;
                        
                        // while within the bounds of the board
                        while(nThisCol < NUMCOLS && nThisRow < NUMROWS)
                        {
                                // count how many queens are on the diagonal
                                if( m_nBoard[nThisRow][nThisCol] != 0 )
                                {
                                        nQueenCount++;
                                }
                                nThisCol++;
                                nThisRow++;
                        }
                        
                        // if there is more than one queen on that diagonal
                        if( nQueenCount > 1 )
                        {
                                // draw a line between the two clashing queens and set Clash state to true
                                canvas.drawLine(
                                        BOARDLEFT + nCol*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + nRow*SQUAREHEIGHT + (SQUAREHEIGHT/2),        
                                        BOARDLEFT + (nThisCol-1)*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + (nThisRow-1)*SQUAREHEIGHT + (SQUAREHEIGHT/2)
                                );
                                m_bClash = true;
                        }
                }

                // for each column
                for( int nCol=1; nCol<NUMCOLS; nCol++)
                {
                        int nRow = 0;
                        int nThisRow = nRow;
                        int nThisCol = nCol;
                        int nQueenCount = 0;
                        
                        // while within the bounds of the board
                        while(nThisCol < NUMCOLS && nThisRow < NUMROWS)
                        {
                                // count how many queens are on the diagonal
                                if( m_nBoard[nThisRow][nThisCol] != 0 )
                                {
                                        nQueenCount++;
                                }
                                nThisCol++;
                                nThisRow++;
                        }
                        
                        // if there is more than one queen on that diagonal
                        if( nQueenCount > 1 )
                        {
                                // draw a line between the two clashing queens and set Clash state to true
                                canvas.drawLine(
                                        BOARDLEFT + nCol*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + nRow*SQUAREHEIGHT + (SQUAREHEIGHT/2),        
                                        BOARDLEFT + (nThisCol-1)*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + (nThisRow-1)*SQUAREHEIGHT + (SQUAREHEIGHT/2)
                                );
                                m_bClash = true;
                        }
                }
        }
        
        /**
         * This method checks for collisions for each diagonal in the other direction. 
         * @param canvas, the current game-board.
         */
        void CheckDiagonal2(Graphics canvas)
        {
                // for each row beginning from the last
                for( int nRow=NUMROWS-1; nRow>=0; nRow-- )
                {
                        int nCol = NUMCOLS - 1;
                        int nThisRow = nRow;
                        int nThisCol = nCol;
                        int nQueenCount = 0;
                        
                        // while within the bounds of the board
                        while(nThisCol >= 0 && nThisRow < NUMROWS)
                        {
                                // count how many queens are on the diagonal
                                if( m_nBoard[nThisRow][nThisCol] != 0 )
                                {
                                        nQueenCount++;
                                }
                                nThisCol--;
                                nThisRow++;
                        }
                        
                        // if there is more than one queen on that diagonal
                        if( nQueenCount > 1 )
                        {
                                // draw a line between the two clashing queens and set Clash state to true
                                canvas.drawLine(
                                        BOARDLEFT + nCol*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + nRow*SQUAREHEIGHT + (SQUAREHEIGHT/2),        
                                        BOARDLEFT + (nThisCol+1)*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + (nThisRow-1)*SQUAREHEIGHT + (SQUAREHEIGHT/2)
                                );
                                m_bClash = true;
                        }
                }

                // for each column
                for( int nCol=NUMCOLS-1; nCol>=0; nCol--)
                {
                        int nRow = 0;
                        int nThisRow = nRow;
                        int nThisCol = nCol;
                        int nQueenCount = 0;
                        
                        // while within the bounds of the board
                        while(nThisCol >= 0 && nThisRow < NUMROWS)
                        {
                                // count how many queens are on the diagonal
                                if( m_nBoard[nThisRow][nThisCol] != 0 )
                                {
                                        nQueenCount++;
                                }
                                nThisCol--;
                                nThisRow++;
                        }
                        
                        // if there is more than one queen on that diagonal
                        if( nQueenCount > 1 )
                        {
                                canvas.drawLine(
                                        BOARDLEFT + nCol*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + nRow*SQUAREHEIGHT + (SQUAREHEIGHT/2),        
                                        BOARDLEFT + (nThisCol+1)*SQUAREWIDTH + (SQUAREWIDTH/2),
                                        BOARDTOP + (nThisRow-1)*SQUAREHEIGHT + (SQUAREHEIGHT/2)
                                );
                                m_bClash = true;
                        }
                        
                }
        }
        
        /* METHODS FOR SOLVING GAME */
        
        /**
         * This method clears the game-board by resetting all Queen flags to zero.
         */
        public void ClearBoard()
        {
        		// for each column
        		for(int nCol = 0; nCol < NUMCOLS; nCol++)
        		{
        			// and for each row
        			for(int nRow = 0; nRow < NUMROWS; nRow++)
        			{
                        // remove the queen
        				m_nBoard[nCol][nRow]= 0;
        			}
        		}
        		// reset clash state
        		m_bClash = false;
        }
        
        /**
         * This method solves the current board by detecting collisions and 
         * back-tracking to make valid placements on the game-board. 
         * @param nCol, the current column that a piece is being placed onto. 
         * @return true if the puzzle is solved, otherwise false in all other 
         * cases when a solve attempt is invalid. 
         */
        boolean SolveIt(int nCol)
        {
          // base case: if the boards bounds are exceeded
        	// then each column must be occupied without collisions
        	if(nCol >= NUMCOLS)
        	{
                    return(true);
        	}
            
        	// for each row
        	for(int nRow = 0; nRow < NUMROWS; nRow++)
        	{
        	        m_strStatus = "Placing Piece";
                        
                        if(m_nBoard[nRow][nCol] == 0)
                        {
                            m_nBoard[nRow][nCol] = 1;
                        }
                        
                        repaint();
                        Delay(25);

                        // if there is a collision
                        if(m_bClash)
                        {
                            m_strStatus = "Conflict: Removing Piece";
                            m_nBoard[nRow][nCol] = 0;
                            repaint();
                            Delay(10);
                        }
                        // else continue to solve
                        else
                        {
                                // if solving the next column based on this piece's placement
                    	        // causes a conflict, then remove the piece
                                if(!SolveIt(nCol+1))
                                {
                                    m_strStatus = "Backtracking: Removing Piece";
                                    m_nBoard[nRow][nCol] = 0;
                                    repaint();
                                    Delay(10);
                                }
                                // otherwise this piece's placement is valid - return true
                                else
                                {
                                    return(true);
                                }
                        }
                }

                // return false in all other cases when a solve-attempt is invalid 
                return(false);
        }
        
        /* METHODS FOR INTERFACES */
        
        /**
         * This method runs the thread that solves the current game-board using 
         * the SolveIt(int) method while managing game-board updates.
         */
        @Override
        public void run() 
        {
                try
                {
                    while(true)
                    {
                         if(m_bSolving)
                         {
                                 SolveIt(0);
                                 m_strStatus = "Done Solving! Play Again?";
                         }
                         m_bSolving = false;
                         m_objButton.setEnabled(true);
                         
                         Delay(200);
                         repaint();
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

        }
        
        /**
         * This method inserts a delay that allows the thread to update the 
         * game-board as processing continues. 
         * @param nMilliseconds, the number of milliseconds to delay the thread.
         */
        void Delay(int nMilliseconds) 
        {
        	try 
        	{       
        		Thread.sleep(nMilliseconds);
        	} 
        	catch(InterruptedException e)
        	{
        		e.printStackTrace();
        	}
        }
        
        /**
         * This method is an event handler that places a queen on the board where the mouse is pressed
         * @param e, the event in which the button is clicked. 
         */
        @Override
        public void mousePressed(MouseEvent e)
        {
         // get column
         int nCol = (e.getX() - BOARDLEFT) / SQUAREWIDTH;
         // get row
                 int nRow = (e.getY() - BOARDTOP) / SQUAREHEIGHT;
                 // if the mouse is pressed within the bounds of the board, add a queen to that location
                 if (nCol >= 0 && nRow >= 0 && nCol < NUMCOLS && nRow < NUMROWS) 
                 {
                         m_nBoard[nRow][nCol] ^= 1;
                         repaint();
                 }
        }
        
        /**
         * This method initiates solving upon clicking the button
         * @param e, the event in which the button is clicked. 
         */
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            m_objButton.setEnabled(false);
            ClearBoard();
            m_strStatus = "Solving";
            repaint();
            m_bSolving = true;
        }
        
        /* UNIMPLEMENTED INTERFACE METHODS */
        
        @Override
        public void mouseClicked(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseDragged(MouseEvent e) { }

        @Override
        public void mouseMoved(MouseEvent e) { }

}
