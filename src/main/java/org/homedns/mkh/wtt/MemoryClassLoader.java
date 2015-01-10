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

import java.util.Map;

/**
 * A class loader loads compiled byte from memory (map), keys are class names,
 * values are byte code arrays.
 */
public class MemoryClassLoader extends ClassLoader {
	private Map< String, byte[] > classes;

	/**
	 * @param classes the classes map
	 */
	public MemoryClassLoader( Map< String, byte[] > classes ) {
		this.classes = classes;
	}

	/**
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	@Override
	protected Class< ? > findClass( String sClassName ) throws ClassNotFoundException {
		byte[] classBytes = classes.get( "/" + sClassName );
		if( classBytes == null ) {
			throw new ClassNotFoundException( sClassName );
		}
		Class< ? > clazz = defineClass( sClassName, classBytes, 0, classBytes.length );
		if( clazz == null ) {
			throw new ClassNotFoundException( sClassName );
		}
		return( clazz );
	}
}
