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

import java.io.IOException;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;


/**
 * Web Testing Tool
 *
 */
public class Wtt {
	private static final Logger LOG = Logger.getLogger( Wtt.class );
	
	private JobScheduler scheduler;
	
	/**
	 * @param parameters
	 *            the configuration parameters
	 *            
	 * @throws SchedulerException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	protected Wtt( 
		String sFileName 
	) throws SchedulerException, ClassNotFoundException, IOException {
		Runtime.getRuntime( ).addShutdownHook( new ShutdownHook( ) );
		Parameters parameters = new Parameters( sFileName );
		scheduler = new JobScheduler( parameters );
	}

	public static void main( String[] args ) {
		try	{
			LOG.info( "Wtt is starting..." );
			Wtt wtt = new Wtt( ( args == null || args.length == 0 ) ? "" : args[ 0 ] );
			wtt.execute( );
		}
		catch( Exception e ) {
			LOG.error( e.getMessage( ), e );
			System.exit( 1 );
		}
    }

	/**
	 * Executes scheduler jobs
	 * 
	 * @throws SchedulerException 
	 */
	private void execute( ) throws SchedulerException {
		if( JobScheduler.isJobPoolEmpty( ) ) {
			LOG.info( "Jobs pool is empty. Exiting..." );
			System.exit( 0 );
		} else {
			scheduler.getScheduler( ).start( );
		}
	}
	
	/**
	 * Shutdowns web testing tool
	 * 
	 * @throws SchedulerException 
	 */
	private void shutdown( ) throws SchedulerException {
		LOG.info( "Shutdown..." );
		if( scheduler != null ) {
			scheduler.getScheduler( ).shutdown( );
		}
	}

	/**
	 * The shutdown hook object
	 *
	 */
	private class ShutdownHook extends Thread {
		public void run( ) {
			try {
				shutdown( );
			}
			catch( Exception e ) {
				LOG.error( e.getMessage( ), e );
			}
		}
	}
}