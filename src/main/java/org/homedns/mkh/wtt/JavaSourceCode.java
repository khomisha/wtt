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
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * Java source code holder
 *
 */
public class JavaSourceCode extends SimpleJavaFileObject {
    private String sSource = null;
    
	/**
	 * @param sClassName
	 *            the class name
	 * @param sSource
	 *            the source code string
	 */
    public JavaSourceCode( String sClassName, String sSource ) {
        super( 
        	URI.create( "string:///" + sClassName.replace('.', '/') + Kind.SOURCE.extension ), 
        	Kind.SOURCE
        );
    	this.sSource = sSource;
    }
    
    /**
     * @see javax.tools.SimpleJavaFileObject#getCharContent(boolean)
     */
    @Override
    public CharSequence getCharContent( boolean ignoreEncodingErrors ) throws IOException {
    	return( sSource );
    }
}
