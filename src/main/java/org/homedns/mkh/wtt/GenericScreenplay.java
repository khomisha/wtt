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

import java.util.Date;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Generic screen play
 *
 */
public abstract class GenericScreenplay implements Job {
	private static final Logger LOG = Logger.getLogger( GenericScreenplay.class );
	
	private String sJobName;

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute( JobExecutionContext context ) throws JobExecutionException {
		sJobName = JobScheduler.getJobKey( context.getJobDetail( ).getKey( ) );
	    LOG.debug( "=== " + sJobName + ": executing at: " + new Date( ) + " ===" );
		try {
			setUp( );
			test( );
			tearDown( );
		} catch( Exception e ) {
			LOG.error( sJobName + ": " + e.getMessage( ), e );
			if( e instanceof JobExecutionException ) {
				JobExecutionException ex = ( JobExecutionException )e;
				ex.setUnscheduleFiringTrigger( true );
				LOG.error( sJobName + " is stopped" );
			}
		}
	    LOG.debug( "=== " + sJobName + ": completed: " + new Date( ) + " ===" );
	}
	
	/**
	 * Logs message
	 * 
	 * @param message the message to log
	 */
	public void output( Object message ) {
		LOG.info( sJobName + ": " + message );
	}
	
	public abstract void setUp( ) throws Exception;
	
	public abstract void test( ) throws Exception;
	
	public abstract void tearDown( ) throws Exception;
}
