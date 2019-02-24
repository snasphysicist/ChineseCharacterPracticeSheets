
package com.snasphysicist.chinesecharacterpracticesheets ;

import java.awt.image.BufferedImage ;
import java.io.File;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D ;
import javax.imageio.ImageIO ;
import java.awt.GraphicsEnvironment ;
import java.awt.RenderingHints ;
import java.util.LinkedList ;
import java.util.ArrayList ;
import java.io.BufferedOutputStream ;
import java.io.OutputStream ;
import java.io.FileOutputStream ;
import java.util.zip.ZipOutputStream ;
import java.util.zip.ZipEntry ;
import java.util.logging.Logger ;
import java.util.logging.Level ;
import java.util.function.Function ;
import java.net.URL ;
import java.util.logging.LogManager ;
import com.snasphysicist.simplewebserver.* ;

public class ChineseCharacterPracticeSheets {
	
	//Server port
	private static final int SERVER_PORT = 4001 ;
	
	/*
	 * Minimum and maximum integer values for 
	 * CJK characters in unicode
	 */
	private static final int CJK_MIN = 0x4E00 ;
	private static final int CJK_MAX = 0x9FFF ;
	
	/*
	 * Resource locations
	 */
	private static final String FONT_FILE = "resources/font.otf" ;
	private static final String LOG_FILE = "resources/logging.properties" ;
	
	/*
	 * Takes a Character and returns whether it is a CJK character
	 */
	private static boolean isCJKCodepoint( Character character ) {
		return ( CJK_MIN < Integer.valueOf( character ) ) && ( CJK_MAX > Integer.valueOf( character ) ) ;
	}
	
	/*
	 * Takes a linked list of Character objects and removes 
	 * any Characters which do not fall in the CJK range
	 */
	private static void removeNonCJKCharacters( LinkedList<Character> characters ) {
		int i = 0 ;
		while( i < characters.size() ) {
			if( !isCJKCodepoint( characters.get(i) ) ) {
				characters.remove(i) ;
			} else {
				i++ ;
			}
		}
	}
	
	/*
	 * Removes any duplicates from a linked list of Characters
	 */
	private static void deduplicate( LinkedList<Character> characters ) {
		int i = 0 ;
		int j = 0 ;
		while( i < characters.size() ) {
			j = i+1 ;
			while( j < characters.size() ) {
				//Remove element j if it matches
				if( characters.get(i).equals( characters.get(j) ) ) {
					characters.remove( j ) ;
				} else {
					//Otherwise, check next element
					j++ ;
				}
			}
			i++ ;
		}
	}
	
	//Returns a buffered image based on a page
	private static BufferedImage generateImage( Page page ) {
		
		//Create the image & graphics
		BufferedImage image = new BufferedImage( page.getPageWidth(), 
				                                 page.getPageHeight(), 
				                                 BufferedImage.TYPE_INT_ARGB ) ;
		Graphics2D graphics = image.createGraphics() ;
		
		/*
		 * Set up the graphics with the following
		 * White background colour (may not be necessary any more)
		 * Improve text rendering with magic
		 */
		graphics.setBackground( Color.WHITE );
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
			    				  RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		
		//Draw page to graphics
		page.draw( graphics ) ;
		
		return image ;
		
	}
	
	/*
	 * Sets up the graphics environment to use the included font that
	 * we know includes the required Chinese characters
	 */
	private static void setupFont() {
		try {
			URL url = ChineseCharacterPracticeSheets.class.getResource( FONT_FILE ) ;
			Font font = Font.createFont( Font.TRUETYPE_FONT , url.openStream() ) ;
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont( font ) ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
		}
	}
	
	/*
	 * Sets the logging configuration from included file
	 */
	private static void setupLogging() {
		try {
			URL url = ChineseCharacterPracticeSheets.class.getResource( LOG_FILE ) ;
			LogManager.getLogManager().readConfiguration( url.openStream() ) ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
		}
	}
	
	/*
	 * Converts a string into a linked list of Characters
	 */
	public static LinkedList<Character> stringToCharacterArray( String string ) {
		char[] chars = string.toCharArray() ;
		LinkedList<Character> characters = new LinkedList<Character>() ;
		for( int i=0 ; i<chars.length ; i++ ) {
			characters.add( new Character( chars[i] ) ) ;
		}
		return characters ;
	}
		
	/*
	 * Takes a specification and a set of characters as an input
	 * along with an output stream, produces a set of pages based
	 * on the specification/characters, zips them into a single file
	 * and streams the zip file out to the output stream
	 * Returns true if successful, false if it fails at any point
	 */
	public static boolean produceZippedPageSet( Specification specification, String charactersIn,
												OutputStream dataOut ) {
		
		//Hold images as they are created, before zipping
		BufferedImage image ;
		//Zip file creator
		ZipOutputStream zipDataOut ;
		//A list of pages as they are created
		ArrayList<Page> pages = new ArrayList<Page>() ;
		
		//Turn string into character linked list
		LinkedList<Character> characters = stringToCharacterArray( charactersIn ) ;
		
		//Remove duplicates and non CJK characters
		deduplicate( characters ) ;
		removeNonCJKCharacters( characters ) ;

		if( characters.size() == 0 ) {
			//Return false if there are no characters left to use
			return false ;
		}
		
		//Create pages until all characters consumed
		while( characters.size() > 0 ) {
			pages.add( new Page( specification , characters ) ) ;
		}
		
		/*
		 * Open a zip stream over the input stream
		 * generate the images and write them out to it
		 */
		try {
			zipDataOut = new ZipOutputStream( dataOut ) ; 
			for( int i=0 ; i<pages.size() ; i++ ) {	
				//Generate an image from the page
				image = generateImage( pages.get(i) ) ;
				//New file in the zip archive & write out the image to it
				zipDataOut.putNextEntry( new ZipEntry( String.format("page_%d.PNG", i ) ) ) ;
				ImageIO.write( image, "PNG", zipDataOut ) ;
				//Close the file in the archive
				zipDataOut.closeEntry() ;
			}
			/*
			 * Must close the zip stream before finishing 
			 * or the zip file will be mangled
			 */
			zipDataOut.close() ;
		} catch( Exception e ) {
			//Return false if it fails at any point
			return false ;
		}
		
		//Otherwise confirm it worked by returning true
		return true ;
	}
	
	/*
	 * Set up request handlers
	 */
	public static void addHandlers( WebServer server ) {
		
		//Page not found handler
		Function<Request,Response> pageNotFound =
				new Function<Request,Response>() {
					@Override
					public Response apply( Request request ) {
						return new TextResponse( Protocol.HTTP10, 404, 
												 "Page Not Found", 
												 "Error 404: Page Not Found" ) ;	
					}
		} ;
		
		server.addRoute( "/404.html", pageNotFound ) ;
		
		// /api/ccps/generate handler
		
		Function<Request,Response> generateSheet = 
				new Function<Request,Response>() {
					@Override
					public Response apply( Request rawRequest ) {
						
						Response response ;
						PracticeSheetRequest request = new PracticeSheetRequest( rawRequest ) ;
						
						/*
						 * If the request is properly formatted
						 * and contains the right information
						 */
						if( request.isValid() ) {
							//Generate a file response & create the file
							response = new FileResponse( 
									Protocol.HTTP10, 200, 
									 "Chinese Character Practice Sheet File Download",
									 FileType.ZIP, "practicesheets.zip" 
									 ) ;
							//TODO: Return false?
							ChineseCharacterPracticeSheets.produceZippedPageSet( 
									request.getSpecification(),
									request.getCharacters(),
									((FileResponse) response).getFileStream() 
									) ;
						} else {
							//Otherwise send an error response
							response = new TextResponse(
									Protocol.HTTP10, 400,
									"Invalid Request", 
									"Error 400: The Request Submitted Contained" 
									+ " Invalid or Malformatted Information" ) ;
						}
						return response ;
					}
		} ;
		
		server.addRoute( "/api/ccps/generate", generateSheet ) ;
		
	}
	
	public static void main( String[] args ) {
		
		Thread serverThread = null ;
		WebServer server = null ;
		
		//Set fine logging level
		Logger.getLogger( "com.snasphysicist.WebServer" ).setLevel( Level.FINE ) ;
		
		//Set up the Chinese language font
		setupFont() ;
		
		//Setup logging configuration
		setupLogging() ;
		
		while( true ) {
			//If the server is not running, for some reason
			if( serverThread == null || !serverThread.isAlive() ) {
				
				/*
				 * If the server gas previously been running
				 * then close the socket so the port is free
				 */
				if( server != null ) {
					if( !server.closeSocket() ) {
						System.exit(1) ;
					}
				}
				
				//(Re)Initialise server
				server = new WebServer( SERVER_PORT ) ;
				
				//Open the socket
				if( !server.openSocket() ) {
					System.exit( 1 ) ;
				}
				
				//Set up the routes for the server
				addHandlers( server ) ;
				
				//(Re)Start server running
				serverThread = new Thread( server ) ;
				//Set the exception handling behaviour
				serverThread.setDefaultUncaughtExceptionHandler( server.getexceptionHandler() ) ;
				serverThread.start() ;
			}
			
		}
		
	}

}
