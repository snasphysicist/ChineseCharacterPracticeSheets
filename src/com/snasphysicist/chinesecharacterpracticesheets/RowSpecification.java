
package com.snasphysicist.chinesecharacterpracticesheets;

public class RowSpecification extends Specification {

	private int columns ;
	
	public RowSpecification( Specification specification, int columns ) {
		super( specification ) ;
		this.columns = columns ;
	}
	
	public int getTotalColumns() {
		return columns ;
	}
	
}
