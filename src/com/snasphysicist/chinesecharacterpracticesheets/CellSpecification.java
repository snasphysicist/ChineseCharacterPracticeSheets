
package com.snasphysicist.chinesecharacterpracticesheets;

public class CellSpecification extends Specification {
	
	private final int SMALL_FONT_SIZE = 144 ;
	private final int MEDIUM_FONT_SIZE = 180 ;
	private final int LARGE_FONT_SIZE = 240 ;
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

}
