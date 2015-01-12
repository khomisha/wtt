/*
 * Copyright 2014 Mikhail Khodonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */

package org.homedns.mkh.wtt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaFileObject.Kind;
import org.apache.log4j.Logger;

/**
 * Abstract java source compiler
 *
 */
public abstract class AbstractCompiler {
	private static final Logger LOG = Logger.getLogger( AbstractCompiler.class );

	private String sPackage;
	private JavaFileObject sourceCode;
	private JavaCompiler compiler;
	private List< JavaByteCode > byteCode = new ArrayList< JavaByteCode >( );
	private DiagnosticCollector< JavaFileObject > diagnostics = new DiagnosticCollector< JavaFileObject >( );
	
	public AbstractCompiler( ) {
		compiler = ToolProvider.getSystemJavaCompiler( );
		if( compiler == null ) {
			String sMsg = (
				"For runtime compilation JDK must be used. Current JAVA_HOME = " + 
				System.getProperty( "java.home" )
			);
			throw new NullPointerException( sMsg );
		}
	}

	/**
	 * Compiles source code
	 * 
	 * @return true if succeed and false otherwise
	 * 
	 * @throws IOException 
	 */
	public boolean compile( ) throws IOException {
		JavaFileManager jfm = compiler.getStandardFileManager( diagnostics, null, null );
		jfm = new FileManager( jfm );
		JavaCompiler.CompilationTask task = compiler.getTask( 
			null, 
			jfm, 
			diagnostics, 
			null,
			null, 
			Arrays.asList( sourceCode )
		);
		Boolean result = task.call( );
		for( Diagnostic< ? extends JavaFileObject > d : diagnostics.getDiagnostics( ) ) {
			LOG.debug( d.getKind( ) + ": " + d.getMessage( null ) );
		}
		jfm.close( );
		return( result );
	}
	
	/**
	 * Returns compiled class byte code
	 * 
	 * @return the byte code
	 */
	public Map< String, byte[] > getByteCode( ) {
		Map< String, byte[] > byteCodeMap = new HashMap< String, byte[] >( );
		for( JavaByteCode clazz : byteCode ) {
			byteCodeMap.put( clazz.getName( ), clazz.getBytes( ) );
		}
		return( byteCodeMap );
	}
	
	/**
	 * Sets java source code and prepare it to compile
	 * 
	 * @param sSourceDir
	 *            the source code directory
	 * @param sPackage
	 *            the package name
	 * @param sClassName
	 *            the class name to set
	 * 
	 * @throws IOException
	 */
	public void setSource2Compile( 
		String sSourceDir, 
		String sPackage, 
		String sClassName 
	) throws IOException {
		BufferedReader in = null;
		this.sPackage = sPackage;
		StringBuffer source = new StringBuffer( );
		try {
			LOG.debug( "Sources dir: " + sSourceDir + " Class name: " + sClassName );
			in = new BufferedReader( new FileReader( sSourceDir + sClassName + ".java" ) );
			String sLine;
			while( ( sLine = in.readLine( ) ) != null ) {
				sLine = onBeforeAppend( sClassName, sLine, source );
				source.append( sLine + "\n" );
				onAfterAppend( sClassName, sLine, source );
			}
			LOG.debug( "Source code to compile:\n" + source.toString( ) );
			sourceCode = new JavaSourceCode( sPackage + "." + sClassName, source.toString( ) );
		}
		finally {
			if( in != null ) {
				in.close( );
			}
		}
	}
	
	/**
	 * Do before source code line will be appended
	 * 
	 * @param sClassName
	 *            the source code class name
	 * @param sLine
	 *            the raw source code line which will be append
	 * @param source
	 *            the source code
	 *            
	 * @return the result string 
	 */
	protected abstract String onBeforeAppend( String sClassName, String sLine, StringBuffer source );
	
	/**
	 * Do after source code line was appended
	 * 
	 * @param sClassName
	 *            the source code class name
	 * @param sLine
	 *            the appended source code line
	 * @param source
	 *            the source code
	 */
	protected abstract void onAfterAppend( String sClassName, String sLine, StringBuffer source );

	/**
	 * Customize file manager with ability to output specified byte code to
	 * handled structure
	 * 
	 */
	private class FileManager extends ForwardingJavaFileManager< JavaFileManager > {

		/**
		 * @param fileManager the raw file manager object
		 */
		protected FileManager( JavaFileManager fileManager ) {
			super( fileManager );
		}

		/**
		 * @see javax.tools.ForwardingJavaFileManager#getFileForOutput(javax.tools.JavaFileManager.Location, java.lang.String, java.lang.String, javax.tools.FileObject)
		 */
		@Override
        public JavaFileObject getJavaFileForOutput( 
        	Location location, 
			final String sClassName, 
			Kind kind, 
			FileObject sibling
		) throws IOException {
        	if( sClassName.startsWith( sPackage + "." ) ) {
        		JavaByteCode jfo = new JavaByteCode( sClassName );
        		byteCode.add( jfo );
              	return( jfo );
        	} else {
        	   	return( super.getJavaFileForOutput( location, sClassName, kind, sibling ) );
           	}
        }		
	}
}
