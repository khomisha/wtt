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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * Java byte code holder
 */
public class JavaByteCode extends SimpleJavaFileObject {
	private ByteArrayOutputStream stream;
	
	/**
	 * @param sClassName
	 *            the file class name represented by this file object
	 */
	public JavaByteCode( String sClassName ) {
		super( URI.create( "bytes:///" + sClassName ), Kind.CLASS );
		stream = new ByteArrayOutputStream( );
	}

	/**
	 * @see javax.tools.SimpleJavaFileObject#openOutputStream()
	 */
	@Override
	public OutputStream openOutputStream( ) throws IOException {
		return( stream );
	}
   
	/**
	 * Returns byte code as byte array
	 * 
	 * @return the byte array
	 */
	public byte[] getBytes( ) {
      return( stream.toByteArray( ) );
	}
}
