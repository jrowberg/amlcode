/*
AmlResourceException.java
Jeff Rowberg

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

To further contact the author please email jeff@rowberg.net
*/

package com.amlcode.core;

/**
 * @author Jeff Rowberg
 *
 */
public class AmlResourceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6754620309835784998L;

	public AmlResourceException() {
		super();
	}
	
	public AmlResourceException(String string) {
		super(string);
	}
	
}
