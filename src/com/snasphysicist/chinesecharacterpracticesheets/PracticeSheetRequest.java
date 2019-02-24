package com.snasphysicist.chinesecharacterpracticesheets;

import java.util.logging.Logger ;
import java.util.logging.Level;
import com.snasphysicist.simplewebserver.* ;

public class PracticeSheetRequest extends Request {

	private final static Logger LOG = Logger.getLogger( Logger.class.getName() ) ;
	
	private final Method CORRECT_METHOD = Method.POST ;
	private final String[] REQUIRED_FIELDS 
		= new String[]{ "characters", "size", "sheetStyle", "targetStyle" } ;
	
	private FormData formData ;
	private Specification specification ;
	
	public PracticeSheetRequest( Request request ) {
		super( request ) ;
		formData = null ;
		specification = null ;
	}
	
	public Specification getSpecification() {
		return specification ;
	}
	
	public String getCharacters() {
		return formData.getValue( "characters" ) ;
	}
	
	/*
	 * Returns true if the method
	 * used is correct (POST)
	 */
	private boolean isCorrectMethod() {
		return method.equals( CORRECT_METHOD ) ;
	}
	
	/*
	 * Returns true if the body
	 * is parseable as form data
	 */
	private boolean parseFormData() {
		formData = new FormData() ;
		return formData.fromString( body ) ;
	}
	
	/*
	 * Return true if the fields required
	 * are present in the formData supplied
	 */
	private boolean requiredFieldsPresent() {
		return formData.hasKeys( REQUIRED_FIELDS ) ;
	}
	
	/*
	 * Returns true if there are enough
	 * characters provided for a valid
	 * request (greater than zero)
	 */
	private boolean sufficientCharacters() {
		return formData.getValue( "characters" ).length() > 0 ;
	}
	
	/*
	 * Attempt to create a specification from
	 * the form data, return true if successful
	 */
	private boolean generateSpecification() {
		
		try {
			specification = new Specification(
						SheetStyle.valueOf( formData.getValue( "sheetStyle" ).toUpperCase() ),
						CharacterSize.valueOf( formData.getValue( "size" ).toUpperCase() ),
						TargetStyle.valueOf( formData.getValue( "targetStyle" ).toUpperCase() )
					) ;
		} catch( IllegalArgumentException e ) {
			//At least one option is not supported
			LOG.log( Level.WARNING, String.format( "unsupported sheet specification %s : %s : %s",
					   formData.getValue( "sheetStyle" ),
					   formData.getValue("size"),
					   formData.getValue( "targetStyle" ) ) ) ;
			//False to indicate failure
			return false ;

		}
		
		//Success
		return true ;
	
	}
	
	/*
	 * Checks that the request is valid
	 * and parses the form data & generates
	 * the specification in the process
	 */
	public boolean isValid() {
		return isCorrectMethod() && parseFormData() && 
				requiredFieldsPresent() && sufficientCharacters() &&
				generateSpecification() ;
	}
	

}
