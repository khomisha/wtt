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

/**
 * Java source compiler
 *
 */
public class Compiler extends AbstractCompiler {
	public static final String FIREFOX = "firefox";
	public static final String IE = "ie";
	public static final String CHROME = "chrome";
	
	private String sBrowser;
	private String sDriverLine;
	private String sImportDriverLine;
	
	/**
	 * Sets browser which will be used {@link this#CHROME, this#FIREFOX, this#HTMLUNIT, this#IE}
	 * 
	 * @param sBrowser the browser to set
	 */
	public void setBrowser( String sBrowser ) {
		this.sBrowser = sBrowser;
		if( FIREFOX.equals( sBrowser ) ) {
			sDriverLine = "driver = new FirefoxDriver();\n";
			sImportDriverLine = "import org.openqa.selenium.firefox.FirefoxDriver;\n";
		} else if( IE.equals( sBrowser ) ) {
			sDriverLine = "driver = new InternetExplorerDriver();\n";			
			sImportDriverLine = "import org.openqa.selenium.ie.InternetExplorerDriver;\n";
		} else if( CHROME.equals( sBrowser ) ) {
			sDriverLine = "driver = new ChromeDriver();\n";
			sImportDriverLine = "import org.openqa.selenium.chrome.ChromeDriver;\n";
		} else {
			throw new IllegalArgumentException( sBrowser );
		}
	}

	/**
	 * Returns using browser
	 * 
	 * @return the browser {@link this#CHROME, this#FIREFOX, this#HTMLUNIT, this#IE}
	 */
	public String getBrowser( ) {
		return( sBrowser );
	}
	
	/**
	 * @see org.homedns.mkh.wtt.AbstractCompiler#onBeforeAppend(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	@Override
	protected String onBeforeAppend( String sClassName, String sLine, StringBuffer source ) {
		if( sLine.contains( "driver = new " ) ) {
			sLine = sDriverLine;
		}
		if( sLine.contains( "public class " + sClassName ) ) {
			sLine = "public class " + sClassName + " extends GenericScreenplay {\n";
		}
		if( sLine.contains( "public void test" + sClassName ) ) {
			sLine = "public void test() throws Exception {\n";
		}
		if( sLine.contains( "System.out.println" ) ) {
			sLine = sLine.replace(  "System.out.println", "output" );
		}
		if( sLine.contains( "import " ) && sLine.contains( "Driver" ) ) {
			sLine = sImportDriverLine;
		}
		return( sLine );
	}

	/**
	 * @see org.homedns.mkh.wtt.AbstractCompiler#onAfterAppend(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	@Override
	protected void onAfterAppend( String sClassName, String sLine, StringBuffer source ) {
		if( sLine.startsWith( "package" ) ) {
			source.append( "import org.homedns.mkh.wtt.GenericScreenplay;\n" );
		}
	}
}
