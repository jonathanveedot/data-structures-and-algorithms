/**
  * Minimax.java
  * The computer's playing behavior is determined by Minimax, a decision rule that 
  * minimizes the opponent's maximum payoff (in a zero-sum game, this is the same as 
  * minimizing one's own maximum loss, and to maximizing one's own minimum gain). 
  * A value is associated with each position or state of the game. This value is 
  * computed by means of a position evaluation function and it indicates how good it 
  * would be for a player to reach that position. The player then makes the move that 
  * maximizes the minimum value of the position resulting from the opponent's possible 
  * following moves.
  * Dependencies: Connect4.java, Board.java, Position.java
  */
  
package connect4;

import java.util.HashMap;

public class MiniMax 
{
	// hashmap containing previously explored board states
	HashMap<Board, Integer> m_hmBoardState;
	// Maximum ply (search depth)
	int m_nMaxPly = 7;
	//  This is the piece to search for. Should be either RED or YELLOW.
	int m_nSearchPiece = 0;
	
	// Set the piece for which a move will be found.
	public void SetSearchPiece( int nPiece )
	{
		m_nSearchPiece = nPiece;
	}
        
	boolean isMaximizingPlayer(int nPiece)
	{
		return nPiece == m_nSearchPiece;
	}
        
	int min(int a, int b)
	{
		return (a < b)? a : b;
	}
        
	int max(int a, int b)
	{
		return (a > b)? a : b;
	}

	// Compile a list of legal moves.
	int GetLegalMoves( int[][] nBoardData, int[] nMoveList )
	{
		int row, col;
		int nNumMoves = 0;

		// Loop through six rows.
		for( row=0; row<=5; row++ )
		{
			// Loop through seven columns.
			for( col=0; col<=6; col++ )
			{
				// If the board at this location is empty,
				//   then this square is a legal move.
				if( nBoardData[row][col] == Connect4.EMPTY &&
					( row == 5 || nBoardData[row+1][col] != Connect4.EMPTY ) )
				{
					// Put the row and column in the move list array.
					nMoveList[nNumMoves*2] = row;
					nMoveList[nNumMoves*2+1] = col;
					// Increment the index pointer.
					nNumMoves++;
				}
			}
		}
		// Return the number of legal moves that were found.
		return( nNumMoves );
	}  

  // Recursive method that searches for the best move
	//   pos -> the position of the next potential move
	//   pBoard -> state of board resulting from 'pos'
	//   nDepth -> depth of search from initial board state
	//   nAlpha -> the best maximum minimum
	//   nBeta -> the best minimum maximum
	//   nPiece -> represents which player's turn
	int DoSearch( Position pos, Board pBoard, int nDepth, int nAlpha, int nBeta, int nPiece)
	{
		// the current value
		int nValue;
		// Local arrays for the legal move list and the result list.
		int[] nMoveList = new int[7*2];
		// the result of exploring a possible move
		int nResult;
            
		// if the current board state has been previously explored, return it
		if(m_hmBoardState.containsKey(pBoard) && nDepth != 0)
		{
			return m_hmBoardState.get(pBoard);
		}
            
		// First, check if a side has won.
		if( pBoard.DidSideWin( nPiece ) )
		{
			if( nPiece == m_nSearchPiece )
			{
				return( (int)Double.POSITIVE_INFINITY );
			}
			else
			{
				return( (int)Double.NEGATIVE_INFINITY );
			}
		}
		// See if we have a Cats game.
		else if( pBoard.IsCatsGame() )
		{
			return( 0 ); // Score for Cats game is 0.
		}
		// If we are at a leaf node, hash the new board state and return the score.
		else if( nDepth >= m_nMaxPly )
		{
			int score = ScoreIt( m_nSearchPiece, pBoard.GetBoardData() );
			m_hmBoardState.put(pBoard, score);
			return score;
		}

		// Get the legal moves.
		int nMoves = GetLegalMoves( pBoard.GetBoardData(), nMoveList );
		if( nDepth == 0 )
		{
			pos.Row = nMoveList[0];
			pos.Col = nMoveList[1];
		}
    
		// If this is the maximizing player of minimax, then return the max.
		if( isMaximizingPlayer(nPiece) )
		{
			nValue = (int)Double.NEGATIVE_INFINITY;
			// Loop through the legal moves.
			for( int i=0; i<nMoves; i++ )
			{
				// We need a board clone so that we can place pieces
				//   without messing up previous board positions.
				Board SaveMe = pBoard.Clone();
				// Place the piece from the current move contained in the move list.
				pBoard.PlacePiece( nMoveList[i*2], nMoveList[i*2+1], nPiece );

				// Call DoSearch() recursively.
				nResult = DoSearch(pos, pBoard, nDepth + 1, nAlpha, nBeta, nPiece ^ 1);
				if( nResult > nValue && nDepth == 0 )
				{
					pos.Row = nMoveList[i*2];
					pos.Col = nMoveList[i*2+1];
				}
        
        // Check to see if this result is greater than the current value
				nValue = max( nValue, nResult );
				nAlpha = max( nAlpha, nValue );
                    
				// Restore the board.
				pBoard = SaveMe.Clone();
                    
				// check for beta cut-off
				if( nBeta <= nAlpha )
				{
					break;
				}
			}
                
			return( nValue );
		}
		// Otherwise, if this is the minimizing player of minimax, then return the min.
		else
		{
			nValue = (int)Double.POSITIVE_INFINITY;   
			// Loop through the legal moves.
			for( int i=0; i<nMoves; i++ )
			{
				// We need a board clone so that we can place pieces
				//   without messing up previous board positions.
				Board SaveMe = pBoard.Clone();
			
				// Place the piece from the current move in the list.
				pBoard.PlacePiece( nMoveList[i*2], nMoveList[i*2+1], nPiece );
                
				// Call DoSearch() recursively.
				nResult = DoSearch( pos, pBoard, nDepth + 1, nAlpha, nBeta, nPiece ^ 1);

				// Check to see if this result is less than the current value
				nValue = min(nValue, nResult);
				nBeta = min(nBeta, nValue);
			
				// Restore the board.
				pBoard = SaveMe.Clone();
                    
				// check alpha cut-off
				if( nBeta <= nAlpha )
				{
					break;
				}
			}
			return( nValue );
		}	
	}

	// Wrapper method that sets off minimax to get a move.
	public void GetMove( Position pos, int[][] BoardData, int nPiece )
	{
		// Set the search piece.
		SetSearchPiece( nPiece );
		
		// Create a new board with this board data.
		Board brd = new Board();
		brd.SetBoardData( BoardData );

		// Call the recursive method.
		DoSearch( pos, brd, 0, (int)Double.NEGATIVE_INFINITY, 
              (int)Double.POSITIVE_INFINITY, nPiece );
	}
	
  // Scores are assigned based on the board state wrt the current piece (player)
  //   of interest, and points are assigned based on the number of two, three, 
  //   and four consecutive sequences of that piece, as well as the difference 
  //   in positional advantages between players. 
	int ScoreIt( int nPiece, int[][] BoardData )
	{
		int Twos = 0;
		int Threes = 0;
		int Fours = 0 ;
		
		for( int nRow=0; nRow<6; nRow++ )
		{
			int nCount = 0;
			for( int nCol=0; nCol<7; nCol++ )
			{
				if( BoardData[nRow][nCol] == nPiece )
				{
					nCount++;
				}
				else
				{
					if( nCount == 2 )
					{
						Twos++;
					}
					else if( nCount == 3 )
					{
						Threes++;
					}
					else if( nCount == 4 )
					{
						Fours++;
					}
					nCount = 0;
				}
			}
			if( nCount == 2 )
			{
				Twos++;
			}
			else if( nCount == 3 )
			{
				Threes++;
			}
			else if( nCount == 4 )
			{
				Fours++;
			}
		}
		
		for( int nCol=0; nCol<7; nCol++ )
		{
			int nCount = 0;
			for( int nRow=0; nRow<6; nRow++ )
			{
				if( BoardData[nRow][nCol] == nPiece )
				{
					nCount++;
				}
				else
				{
					if( nCount == 2 )
					{
						Twos++;
					}
					else if( nCount == 3 )
					{
						Threes++;
					}
					else if( nCount == 4 )
					{
						Fours++;
					}
					nCount = 0;
				}
			}
			if( nCount == 2 )
			{
				Twos++;
			}
			else if( nCount == 3 )
			{
				Threes++;
			}
			else if( nCount == 4 )
			{
				Fours++;
			}
		}
		
		// Loop through the diagonal data.
		for( int nDiagonalTest=0; nDiagonalTest<Board.m_nDiagonalData.length/4; nDiagonalTest++)
		{
			int nCount = 0;
			// Starting row.
			int nRow = Board.m_nDiagonalData[nDiagonalTest*4];
			// Starting column.
			int nCol = Board.m_nDiagonalData[nDiagonalTest*4+1];
			// YDirection for the iterations.
			int nYDir = Board.m_nDiagonalData[nDiagonalTest*4+2];
			// Number of iterations.
			int nIterations = Board.m_nDiagonalData[nDiagonalTest*4+3];

			// Loop through the iterations.
			for( int i=0; i<nIterations; i++ )
			{
				// If this is nSide then increment the counter.
				if( BoardData[nRow][nCol] == nPiece )
				{
					// Increment the counter.
					nCount++;
				}
				
				// This square does not equal nSide.
				else
				{
					if( nCount == 2 )
					{
						Twos++;
					}
					else if( nCount == 3 )
					{
						Threes++;
					}
					else if( nCount == 4 )
					{
						Fours++;
					}
					
					// Reset the counter.
					nCount = 0;
				}
				
				// Move the row position.
				nRow += nYDir;
				// Move the column position.
				nCol++;
			}
			
			if( nCount == 2 )
			{
				Twos++;
			}
			else if( nCount == 3 )
			{
				Threes++;
			}
			else if( nCount == 4 )
			{
				Fours++;
			}
			
		}

		int nPositionalAdvantage = 0;
		for( int nCol=0; nCol<7; nCol++ )
		{
			int nCount = 0;
			for( int nRow=0; nRow<6; nRow++ )
			{
				if( BoardData[nRow][nCol] == nPiece )
				{
					nCount++;
				}
			}
			
			if( nCol == 2 || nCol == 3 )
			{
				nPositionalAdvantage += nCount * 2;
			}
			else if( nCol == 1 || nCol == 4 )
			{
				nPositionalAdvantage += nCount;
			}
			
		}
		for( int nCol=0; nCol<7; nCol++ )
		{
			int nCount = 0;
			for( int nRow=0; nRow<6; nRow++ )
			{
				if( BoardData[nRow][nCol] != nPiece )
				{
					nCount++;
				}
			}
			
			if( nCol == 2 || nCol == 3 )
			{
				//nPositionalAdvantage -= nCount * 2;
			}
			else if( nCol == 1 || nCol == 4 )
			{
				//nPositionalAdvantage -= nCount;
			}
		}
		return( nPositionalAdvantage + Twos + Threes * 2 + Fours * 4 );
	}

  // Used for debugging
	static String ArrayToString( int[] IntArray )
	{
		String strRet = "{ ";
		for( int i=0; i<IntArray.length; i++ )
		{
			strRet += (""+IntArray[i] );
			if( i < IntArray.length - 1 )
			{
				strRet += ", ";
			}
		}
		return( strRet + " }");
	}
	
}
