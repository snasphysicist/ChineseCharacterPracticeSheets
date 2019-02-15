
package com.snasphysicist.chinesecharacterpracticesheets;

import java.util.ArrayList ;
import java.util.LinkedList ;
import java.awt.geom.Rectangle2D ;
import java.awt.Graphics2D ;
import java.awt.Color ;

public class Page {

	private final int PAGE_HEIGHT = 3564 ; // px, 297*12
	private final int PAGE_WIDTH = 2520 ; // px, 210*12
	
	private Specification specification ;
	private LinkedList<Character> characters ;
	private ArrayList<Row> rows ;
	private int totalRows ;
	private int totalColumns ;
	private double verticalMargin ;
	private double horizontalMargin ;
	
	public Page( Specification specification , LinkedList<Character> characters ) {
		
		//Set the raw instance variables
		this.specification = specification ;
		this.characters = characters ;
		
		//Set helper variables based on the specification
		parseSpecification() ;
		
		//Initialise row list
		rows = new ArrayList<Row>() ;
		
		//Create the rows to consume the characters
		generateRows() ;
	}
	
	public int getPageHeight() {
		return PAGE_HEIGHT ;
	}
	
	public int getPageWidth() {
		return PAGE_WIDTH ;
	}
	
	/*
	 * Calculate the height of the "content"
	 * i.e. excluding the vertical margins
	 */
	private double calculateContentHeight() {
		return (totalRows*specification.getBoxSize()*specification.getRowMultiplier())
				+ (totalRows-1)*specification.getBoxGap() ;
	}
	
	/*
	 * Calculate the width of the "content"
	 * i.e. excluding the horizontal margins
	 */
	private double calculateContentWidth() {
		return totalColumns*specification.getBoxSize() ;
	}
	
	/*
	 * Sets the internal page parameters
	 * based on the page specification
	 */
	
	private void parseSpecification() {
		
		//How many rows will fit on page?
		totalRows = 0 ;
		while( calculateContentHeight() < PAGE_HEIGHT ) {
			totalRows++ ;
		}
		
		//Subtract one row, since the loop exceeds the page size
		totalRows-- ;
		
		//Calculate margins based on what is left over
		verticalMargin = (PAGE_HEIGHT-calculateContentHeight())/2.0 ;
		
		//Check margin size is reasonable
		assert( verticalMargin >= 0 ) ;
		
		//How many columns will fit on the page?
		totalColumns = 0 ;
		while( calculateContentWidth() < PAGE_WIDTH ) {
			totalColumns++ ;
		}
		
		//Subtract one column, since the loop exceeds the page size
		totalColumns-- ;
		
		horizontalMargin = (PAGE_WIDTH-calculateContentWidth())/2.0 ;
		
		//Check margin size is reasonable
		assert( horizontalMargin > 0 ) ;
		
	}
	
	//Produces a row object for each row on the page
	private void generateRows() {
		//Up total rows, so long as there are characters left
		for( int i=0 ; i<totalRows && characters.size()>0 ; i++ ) {
			rows.add( 
						new Row( 
									new RowSpecification( specification , totalColumns ) , 
									characters.removeFirst() 
							   ) 
					) ;
		}
	}
	
	/*
	 * Returns the x position of the top left hand
	 * corner of a row relative to the page
	 */
	private double rowxPositionRelative() {
		return horizontalMargin ;
	}
	
	/*
	 * Returns the y position of the top left hand
	 * corner of a row relative to the page
	 */
	private double rowyPositionRelative( int i ) {
		return verticalMargin + i*(specification.getBoxSize()*specification.getRowMultiplier()+specification.getBoxGap()) ;
	}
	
	//Draw the whole page onto a graphics object
	public void draw( Graphics2D graphics ) {
		
		//First draw a big white rectangle for background
		graphics.setColor( Color.WHITE );
		graphics.fill( new Rectangle2D.Double( 0 , 0 , PAGE_WIDTH , PAGE_HEIGHT ));
		
		//Draw each one with appropriate starting coordinates
		for( int i = 0 ; i < rows.size() ; i++ ) {
			rows.get(i).draw( graphics , 
					          rowxPositionRelative() , 
					          rowyPositionRelative(i) );
		}
	}
	
}
