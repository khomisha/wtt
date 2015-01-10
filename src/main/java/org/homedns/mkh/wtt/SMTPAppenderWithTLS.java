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

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

/**
 * SMTPAppender with TLS support
 *
 * http://stackoverflow.com/questions/6242838/log4j-failing-to-send-an-email-when-logging-an-error
 * http://www.codereye.com/2009/04/adding-tls-support-to-log4j-smtp.html
 */
public class SMTPAppenderWithTLS extends SMTPAppender {
	private boolean bTLS = false;

	public SMTPAppenderWithTLS( ) {
		super( );
		evaluator = new Evaluator( );
	}

	/**
	 * @param evaluator
	 */
	public SMTPAppenderWithTLS( TriggeringEventEvaluator evaluator ) {
		super( evaluator );
	}

	/**
	 * Sets TLS flag
	 * 
	 * @param bTLS the TLS flag to set
	 */
	public void setTLS( boolean bTLS ) {
		this.bTLS = bTLS;
	}

	/**
	 * @see org.apache.log4j.net.SMTPAppender#createSession()
	 */
	@Override
	protected Session createSession( ) {
	    Properties props = null;
	    try {
	        props = new Properties ( System.getProperties( ) );
	    } 
	    catch( SecurityException ex ) {
	        props = new Properties( );
	    }

	    String prefix = "mail.smtp";
	    String smtpProtocol = getSMTPProtocol( );
	    if( smtpProtocol != null ) {
	        props.put( "mail.transport.protocol", smtpProtocol );
	        prefix = "mail." + smtpProtocol;
	    }
	    String smtpHost = getSMTPHost( );
	    if( smtpHost != null ) {
	      props.put( prefix + ".host", smtpHost );
	    }
	    int smtpPort = getSMTPPort( );
	    if( smtpPort > 0 ) {
	        props.put( prefix + ".port", String.valueOf( smtpPort ) );
	    }
	    
	    Authenticator auth = null;
	    final String smtpPassword = getSMTPPassword( );
	    final String smtpUsername = getSMTPUsername( );
	    if( smtpPassword != null && smtpUsername != null ) {
	    	props.put( prefix + ".auth", "true" );
	    	auth = new Authenticator( ) {
	    		protected PasswordAuthentication getPasswordAuthentication( ) {
	    			return( new PasswordAuthentication( smtpUsername, smtpPassword ) );
	    		}
	    	};
	    }
	    if( bTLS ) {
	    	props.put( "mail.smtp.starttls.enable", "true" );
	    }
	    Session session = Session.getInstance( props, auth );
	    if( smtpProtocol != null ) {
	        session.setProtocolForAddress( "rfc822", smtpProtocol );
	    }
	    boolean smtpDebug = getSMTPDebug( );
	    if( smtpDebug ) {
	        session.setDebug( smtpDebug );
	    }
	    return( session );
	}
	
	/**
	 * Overrides default evaluator
	 *
	 */
	private class Evaluator implements TriggeringEventEvaluator {
		/**
		 * @see org.apache.log4j.spi.TriggeringEventEvaluator#isTriggeringEvent(org.apache.log4j.spi.LoggingEvent)
		 */
		public boolean isTriggeringEvent( LoggingEvent event ) {
			return event.getLevel( ).isGreaterOrEqual( getThreshold( ) );
		}
	}
}

