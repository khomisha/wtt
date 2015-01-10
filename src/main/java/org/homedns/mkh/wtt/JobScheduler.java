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
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Job scheduler object
 *
 */
public class JobScheduler {
	private static final Logger LOG = Logger.getLogger( JobScheduler.class );
	private static final String JOB_PREFIX = "JOB_";

	private Scheduler scheduler;
	private static Map< JobKey, String > jobs = new HashMap< JobKey, String >( );
	private String sSourceDir;
	private String sPackage;
	private Compiler compiler;
	
	/**
	 * @param parameters
	 *            the configuration parameters
	 * 
	 * @throws SchedulerException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public JobScheduler( 
		Parameters parameters 
	) throws SchedulerException, ClassNotFoundException, IOException {
		SchedulerFactory sf = new StdSchedulerFactory( );
		scheduler = sf.getScheduler( );
		compiler = new Compiler( );
		compiler.setBrowser( parameters.getParameters( ).getProperty( "BROWSER", Compiler.FIREFOX ) );
		sSourceDir = parameters.getParameters( ).getProperty( "SOURCE_DIR" );
		sPackage = parameters.getParameters( ).getProperty( "PACKAGE", "com.example.tests" );
		for( String sProperty : parameters.getParameters( ).stringPropertyNames( ) ) {
			if( sProperty.startsWith( JOB_PREFIX ) ) {
				String sValue = parameters.getParameters( ).getProperty( sProperty );
				addJob( sProperty, sValue );
			}
		}
	}
	
	/**
	 * Adds job to the scheduler
	 * 
	 * @param sJobProperty
	 *            the job property name
	 * @param sJobValue
	 *            the job property value
	 * 
	 * @throws ClassNotFoundException
	 * @throws SchedulerException
	 * @throws IOException
	 */
	private void addJob( 
		String sJobProperty, 
		String sJobValue 
	) throws ClassNotFoundException, SchedulerException, IOException {
		String[] as = sJobValue.split( "," );
		String sClassName = as[ 0 ].trim( );
		compiler.setSource2Compile( sSourceDir, sPackage, sClassName );
		if( compiler.compile( ) ) {
			ClassLoader loader = new MemoryClassLoader( compiler.getByteCode( ) );
			Class< ? > clazz = loader.loadClass( sPackage + "." + sClassName );
			addJob( sJobProperty, clazz.asSubclass( Job.class ), as[ 1 ].trim( ) );
		} else {
			LOG.error( sClassName + ": Compilation failed." );			
		}
	}

	/**
	 * Adds new job to scheduler
	 * 
	 * @param sName
	 *            the job unique name
	 * @param clazz
	 *            the job class to add
	 * @param sCronSchedule
	 *            the cron expression string to base the schedule on
	 * 
	 * @throws ClassNotFoundException
	 * @throws SchedulerException
	 */
	private void addJob( 
		String sName, 
		Class< ? extends Job > clazz, 
		String sCronSchedule 
	) throws ClassNotFoundException, SchedulerException {
		JobKey key = generateJobKey( );
		JobDetail job = JobBuilder.newJob( clazz ).withIdentity( key ).build( );
		jobs.put( key, sName );
		CronScheduleBuilder csb = CronScheduleBuilder.cronSchedule( sCronSchedule );
		CronTrigger trigger = TriggerBuilder.newTrigger( ).withIdentity( 
			generateTriggerKey( ) 
		).withSchedule( csb ).build( );
		scheduler.scheduleJob( job, trigger );
		LOG.info( sName + " is compiled and scheduled successfully" );
	}
	
	/**
	 * Returns job scheduler
	 * 
	 * @return the job scheduler
	 */
	public Scheduler getScheduler( ) {
		return( scheduler );
	}
	
	/**
	 * Returns job's name by it's key
	 * 
	 * @param jobKey
	 *            the job key
	 * 
	 * @return the job name
	 */
	public static String getJobKey( JobKey jobKey ) {
		return( jobs.get( jobKey ) );
	}
	
	/**
	 * Returns jobs pool empty flag
	 * 
	 * @return true if jobs pool is empty and false otherwise
	 */
	public static boolean isJobPoolEmpty( ) {
		return( jobs.isEmpty( ) );
	}
	
	/**
	 * Generates job key
	 * 
	 * @return the job key
	 */
	private JobKey generateJobKey( ) {
		String sUID = getUID( );
		return( JobKey.jobKey( sUID, "group_" + sUID ) );
	}
	
	/**
	 * Generates trigger key
	 * 
	 * @return the trigger key
	 */
	private TriggerKey generateTriggerKey( ) {
		String sUID = getUID( );
		return( TriggerKey.triggerKey( sUID, "group_" + sUID ) ); 
	}
	
	/**
	* Returns generated unique id.
	*/
	private String getUID( ) {
		return( String.valueOf( System.currentTimeMillis( ) ) );
	}
}