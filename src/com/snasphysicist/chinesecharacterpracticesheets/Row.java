
package com.snasphysicist.chinesecharacterpracticesheets;

import java.util.ArrayList ;
import java.awt.Graphics2D ;

public class Row {

	private RowSpecification specification ;
	private ArrayList<Cell> cells ;
	private Character character ;
	
	public Row( RowSpecification specification , Character character ) {
		this.specification = specification ;
		this.character = character ;
		cells = new ArrayList<Cell>() ;
	}
	
	/*
	 * Calculates in which inner row of the row's
	 * internal grid a cell lies based on the sequential
	 * number of the cell, assuming upper left to right
	 * then downward counting
	 * 0 -> 1 -> 2 ...
	 * 10 -> 11 -> 12 ...
	 */
	private int calculateRowNumber( int cellNumber ) {
		return cellNumber/specification.getTotalColumns() ;
	}
	
	/*
	 * As the above function, but calculate in which
	 * column of the grid instead of which row
	 */
	private int calculateColumnNumber( int cellNumber ) {
		return cellNumber%specification.getTotalColumns() ;
	}
	
	private float calculateCellOpacity( int cellNumber ) {
		
		int rowNumber = calculateRowNumber( cellNumber ) ;
		int columnNumber = calculateColumnNumber( cellNumber ) ;
		
		switch( specification.getSheetStyle() ) {
			
			/*
			 * For style over, the upper inner row
			 * should be visible, and the lower not
			 */
			case OVER :
				if( rowNumber == 0 ) {
					return 1.0f ;
				} else {
					return 0.0f ;
				}
			/*
			 * For style fade, the characters gradually fade from left
			 * until we get halfway through the columns
			 */
			case FADE :
				return Math.max( 0.0f , 
								 (specification.getTotalColumns() - 2.0f*columnNumber)/
								 	specification.getTotalColumns() ) ;
			
			/*
			 * For style single, only the character in
			 * the leftmost column should be visible
			 */
			case SINGLE :
			default :
				if( columnNumber == 0 ) {
					return 1.0f ;
				} else {
					return 0.0f ;
				}
				
		}

	}
	
	//Creates each cell in the row
	private void generateCells() {
		int totalCells = specification.getTotalColumns()*specification.getRowMultiplier() ;
		float opacity ;
		CellSpecification cellSpecification ;
		for( int i = 0 ; i<totalCells ; i++ ) {
			opacity = calculateCellOpacity(i) ;
			cellSpecification = new CellSpecification( specification , opacity ) ;
			cells.add( new Cell( cellSpecification , character ) ) ;
		}
	}
	
	//x position of top left, relative to row top left
	private double cellxPositionRelative( int cellNumber ) {
		return calculateColumnNumber( cellNumber )*specification.getBoxSize() ;
	}
	
	private double cellyPositionRelative( int cellNumber ) {
		return calculateRowNumber( cellNumber )*specification.getBoxSize() ;
	}
	
	//Draw the row onto a graphics object
	public void draw( Graphics2D graphics , double xPosition , double yPosition ) {
		//Generate all the cells first
		generateCells() ;
		for( int i=0 ; i<cells.size(); i++ ) {
			cells.get(i).draw( graphics , 
								   xPosition + cellxPositionRelative(i) , 
								   yPosition + cellyPositionRelative(i) ) ;
		}
	}

}
