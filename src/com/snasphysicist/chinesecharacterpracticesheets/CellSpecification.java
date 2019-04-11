
package com.snasphysicist.chinesecharacterpracticesheets;

public class CellSpecification extends Specification {
	
	// Sizes for fonts
	private final int SMALL_FONT_SIZE = 144 ;
	private final int MEDIUM_FONT_SIZE = 180 ;
	private final int LARGE_FONT_SIZE = 240 ;
	
	// Fudges to properly align in y
	private final int SMALL_FUDGE = -30 ;
	private final int MEDIUM_FUDGE = -40 ;
	private final int LARGE_FUDGE = -52 ;
	
	private float opacity ;
	
	public CellSpecification( Specification specification , float opacity ) {
		super( specification ) ;
		this.opacity = opacity ;
	}
	
	public int getFontSize() {
		switch ( getSize() ) {
			case MEDIUM :
				return MEDIUM_FONT_SIZE ;
			case LARGE :
				return LARGE_FONT_SIZE ;
			case SMALL :
			default:
				return SMALL_FONT_SIZE ;
		}
	}
	
	public float getOpacity() {
		return opacity ;
	}
	
	public int getStringPlacementFudge() {
		switch ( getSize() ) {
		case MEDIUM :
			return MEDIUM_FUDGE ;
		case LARGE :
			return LARGE_FUDGE ;
		case SMALL :
		default:
			return SMALL_FUDGE ;
	}	
	}

}