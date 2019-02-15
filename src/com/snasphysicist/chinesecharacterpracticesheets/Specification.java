
package com.snasphysicist.chinesecharacterpracticesheets;

/* 
 * Holds the specifications for the page
 */

public class Specification {

	private final int SMALL_BOX_SIZE = 180 ;
	private final int MEDIUM_BOX_SIZE = 240 ;
	private final int LARGE_BOX_SIZE = 360 ;
	
	private SheetStyle sheetStyle ;
	private CharacterSize size ;
	private TargetStyle targetStyle ;
	
	public Specification( SheetStyle sheetStyle , CharacterSize size , TargetStyle targetStyle ) {
		this.sheetStyle = sheetStyle ;
		this.size = size ;
		this.targetStyle = targetStyle ;
	}
	
	/*
	 * Making a copy of an existing specification
	 * Mostly useful for child classes
	 */
	public Specification( Specification specification ) {
		this.sheetStyle = specification.sheetStyle ;
		this.size = specification.size ;
		this.targetStyle = specification.targetStyle ;
	}
	
	public SheetStyle getSheetStyle() {
		return sheetStyle ;
	}
	
	public CharacterSize getSize() {
		return size ;
	}
	
	public TargetStyle getTargetStyle() {
		return targetStyle ;
	}
	
	/*
	 * Returns the side size of the boxes
	 * that contain the characters based
	 * on the size setting
	 * Defaults to smallest setting
	 */
	public int getBoxSize() {
		switch( size ) {
			case MEDIUM:
				return MEDIUM_BOX_SIZE ;
			case LARGE:
				return LARGE_BOX_SIZE ;
			case SMALL:
			default :
				return SMALL_BOX_SIZE ;
		}
	}
	
	/*
	 * Return the vertical gap between
	 * boxes (always the box size/2)
	 */
	public double getBoxGap() {
		return ((double) getBoxSize())/2.0 ;
	}
	
	
	/* 
	 * Return the number of vertical boxes
	 * per row, based on the style
	 * Defaults to 1
	 */
	public int getRowMultiplier() {
		switch( sheetStyle ) {
			case OVER: 
				return 2 ;
			case SINGLE :
			case FADE:
			default:
				return 1 ;
		}
	}
	
}
