
package com.snasphysicist.chinesecharacterpracticesheets ;

import java.io.BufferedOutputStream ;
import java.io.ByteArrayOutputStream ;
import java.io.IOException ;
import java.io.PrintStream ;
import java.net.Socket ;
import com.snasphysicist.simplewebserver.* ;

public class FileResponse extends Response {

	ByteArrayOutputStream file ;
	FileType type ;
	String fileName ;
	
	public FileResponse( Protocol protocol, int status, String title, 
						 FileType type, 
						 String fileName ) {
		super( protocol, status, title ) ;
		this.type = type ;
		this.file = new ByteArrayOutputStream() ; 
		this.fileName = fileName ; 
		setContentHinting() ;
	}
	
	private void setContentHinting() {
		
		//Content type header
		switch( type ) {
			case ZIP :
				addHeader( new Header( "content-type", "application/zip" ) ) ;
				break ;
			default :
				addHeader( new Header( "content-type", "application/text" ) ) ;
		}
		
		//File name header
		addHeader( new Header( "content-disposition", 
							   String.format( "attachment; filename=\"%s\"", fileName ) ) ) ;
		
	}
	
	public ByteArrayOutputStream getFileStream() {
		return file ;
	}
	
	public boolean send( Socket connection ) {
		generateContent() ;
		try {
			BufferedOutputStream byteDataOut = new BufferedOutputStream( connection.getOutputStream() ) ;
			PrintStream dataOut = new PrintStream( byteDataOut ) ;
			//Write status line & headers
			dataOut.print( content + "\r\n" ) ;
			//Write file contents
			file.writeTo( byteDataOut ) ;
			dataOut.flush() ;
			byteDataOut.flush() ;
			dataOut.close() ;
			byteDataOut.close() ;
			return true ;
		} catch( IOException e ) {
			return false ;
		}
	}
	
}
