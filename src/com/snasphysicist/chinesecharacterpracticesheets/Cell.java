
package com.snasphysicist.chinesecharacterpracticesheets;

import java.awt.geom.Rectangle2D ;
import java.awt.geom.Line2D ;
import java.awt.Graphics2D ;
import java.awt.BasicStroke;
import java.awt.Color ;
import java.awt.Font;
import java.awt.FontMetrics ;
import java.util.ArrayList ;

public class Cell {

	private final String FONT_NAME = "Noto Sans CJK SC Regular" ;
	
	private CellSpecification specification ;
	private Character character ;
	
	public Cell( CellSpecification specification , Character character ) {
		this.specification = specification ;
		this.character = character ;
	}
	
	private int characterxSize( Graphics2D graphics ) {
		FontMetrics fontMetrics = graphics.getFontMetrics() ;
		return fontMetrics.stringWidth( character.toString() ) ;
	}
	
	private int characterySize( Graphics2D graphics ) {
		FontMetrics fontMetrics = graphics.getFontMetrics() ;
		return fontMetrics.getAscent() ;
	}
	
	/*
	 * x position of leftmost part
	 * of baseline of the character
	 * relative to cell top left
	 */
	private double getCharacterxPositionRelative( Graphics2D graphics ) {
		return (specification.getBoxSize()/2.0) - (characterxSize( graphics )/2.0) ;
	}
	
	/*
	 * y position of leftmost part
	 * of baseline of the character
	 * relative to cell top left
	 */
	private double getCharacteryPositionRelative( Graphics2D graphics ) {
		return (specification.getBoxSize()/2.0) + (characterySize( graphics )/2.0) ;
	}
	
	/*
	 * Given a graphics instance and the x & y positions of the 
	 * top left corner of the cell, draws the cell's surrounding box
	 */
	private void drawBox( Graphics2D graphics, double xPosition, double yPosition ) {
		
		//Ensure that the stroke is reset to solid
		graphics.setStroke( new BasicStroke(5) ) ;
		//Ensure the opacity is set to full
		graphics.setColor( Color.BLACK ) ;
		
		//Draw the box
		Rectangle2D box = new Rectangle2D.Double( xPosition, yPosition, 
				specification.getBoxSize(), specification.getBoxSize() ) ;
		graphics.setColor( Color.BLACK ) ;
		graphics.draw( box ) ;
		
	}
	
	/*
	 * Given a graphics instance and coordinates for top left
	 * corner of cell, draws the helper lines within the
	 * surrounding box according to the specified target style
	 */
	private void drawTarget( Graphics2D graphics, double xPosition, double yPosition ) {
		
		//Reduce the opacity
		graphics.setColor( new Color( 0, 0, 0, 0.35f ) ) ;
		
		/*
		 * Calculate where lines should be
		 * List of 4 element arrays of double coordinates
		 * Format [x1,y1,x2,y2]
		 */
		ArrayList<double[]> coordinateSets = new ArrayList<double[]>() ;
		double[] coordinateSet = new double[4] ;
		
		//Diagonal
		if( specification.getTargetStyle() == TargetStyle.CROSS
			|| specification.getTargetStyle() == TargetStyle.BOTH ) {
			
			/*
			 * Set stroke to be dashed
			 * Note: need to shift phase to ensure 
			 * dashed 'centred' on centre of cell
			 */
			//float offset = specification.getBoxSize()/11.0f ;
			float dashSize = (((float)Math.sqrt(2))*specification.getBoxSize())/15.0f ;
			graphics.setStroke( new BasicStroke( 5,
												 BasicStroke.CAP_BUTT,
												 BasicStroke.JOIN_MITER,
												 10.0f,
												 new float[]{ dashSize },
												 0.0f ) ) ;
			
			//Top left to bottom right
			coordinateSet = new double[]{ 0, 								//x1
					                      0, 								//y1
					                      specification.getBoxSize(), 		//x2
					                      specification.getBoxSize() } ;	//y2
			
			coordinateSets.add( coordinateSet ) ;
			
			//Top right to bottom left
			coordinateSet = new double[]{ 0, 								//x1
										  specification.getBoxSize(), 		//y1
										  specification.getBoxSize(), 		//x2
										  0 } ;								//y2
			coordinateSets.add( coordinateSet ) ;
			
			//Draw lines
			for( double[] cSet : coordinateSets ) {
				graphics.draw( new Line2D.Double( cSet[0] + xPosition, 		//x1
												  cSet[1] + yPosition, 		//y1
												  cSet[2] + xPosition, 		//x2
												  cSet[3] + yPosition ) );	//y2
			}
			
		}
		
		coordinateSets = new ArrayList<double[]>() ;
		
		//Horizontal and vertical
		if( specification.getTargetStyle() == TargetStyle.PLUS
			|| specification.getTargetStyle() == TargetStyle.BOTH ) {
			
			graphics.setStroke( new BasicStroke( 5,
												 BasicStroke.CAP_BUTT,
												 BasicStroke.JOIN_MITER,
												 10.0f,
												 new float[]{ specification.getBoxSize()/15.0f },
												 0.0f ) ) ;
			
			//Halfway through top to halfway through bottom
			coordinateSet = new double[]{ specification.getBoxSize()/2.0, 		//x1
					                      0, 									//y1
					                      specification.getBoxSize()/2.0, 		//x2
					                      specification.getBoxSize() } ;		//y2
			coordinateSets.add( coordinateSet ) ;
			
			//Halfway through left to halfway through right
			coordinateSet = new double[]{ 0,							 		//x1
										  specification.getBoxSize()/2.0,		//y1
					                      specification.getBoxSize(), 			//x2
					                      specification.getBoxSize()/2.0 } ;	//y2
			coordinateSets.add( coordinateSet ) ;
			
			//Draw lines
			for( double[] cSet : coordinateSets ) {
				graphics.draw( new Line2D.Double( cSet[0] + xPosition, 		//x1
												  cSet[1] + yPosition, 		//y1
												  cSet[2] + xPosition, 		//x2
												  cSet[3] + yPosition ) );	//y2
			}
			
		}
		
	}
	
	/*
	 * Given a graphics instance and coordinates for top left
	 * corner of cell, draws the character centred in the cell
	 * with the correct opacity
	 */
	private void drawCharacter( Graphics2D graphics, double xPosition, double yPosition ) {
		//Set font size before calculating character size
		graphics.setFont( new Font( FONT_NAME , Font.PLAIN , specification.getFontSize() ) ) ;
		//Calculate character size
		double characterx = xPosition + getCharacterxPositionRelative( graphics ) ;
		double charactery = yPosition + getCharacteryPositionRelative( graphics ) ;
		//Check that we can cast doubles to integers
		assert( characterx < Integer.MAX_VALUE && charactery < Integer.MAX_VALUE ) ;
		//Set the colour to account for opacity
		graphics.setColor( new Color( 0 , 0 , 0 , specification.getOpacity() ) ) ;
		graphics.drawString( character.toString() , 
							 (int) Math.round( characterx ) , 
							 (int) Math.round( charactery ) ) ;
	}
	
	/*
	 * Draws the cell on a graphics object given
	 * coordinates for the top left corner of the cell
	 */
	public void draw( Graphics2D graphics , double xPosition , double yPosition ) {
		
		//Draw surrounding box
		drawBox( graphics, xPosition, yPosition ) ;
		
		//Draw 'target lines'

		drawTarget( graphics, xPosition, yPosition ) ;
		
		//Draw character in box
		drawCharacter( graphics, xPosition, yPosition ) ;
		
	}

}
