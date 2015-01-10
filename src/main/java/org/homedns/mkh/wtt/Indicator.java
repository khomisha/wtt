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
 * Progress indicator
 *
 */
public class Indicator extends Thread {
	private String sText;
	private String sIndicator = "|/-\\";

	/**
	 * @param sText the progress indicator text
	 */
	public Indicator( String sText ) {
		this.sText = sText;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run( ) {
        try {
        	int i = 0;
    		while( true ) {
    			System.out.print( "\r" + sText + " " + sIndicator.charAt( i ) );
    			i = ( i < sIndicator.length( ) ) ? i + 1 : 0;
        		Thread.sleep( 100 );
        	}
        }
        catch( InterruptedException e ) {
        	Thread.currentThread( ).interrupt( );        
        } 
	}
}
