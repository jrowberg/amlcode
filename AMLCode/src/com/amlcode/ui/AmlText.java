/*
AmlText.java
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

package com.amlcode.ui;

import org.w3c.dom.Node;

import com.amlcode.core.AmlBuilder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

/**
 * @author Jeff Rowberg
 *
 */
public class AmlText {

	private static final String TAG = "amlcode";

	private TextView view;
	
	public AmlText(Context context, Node n) {
		this(context, n, 0);
	}
	
	public AmlText(Context context, Node n, int inflateId) {
		Log.d(TAG, "New Text from XML node " + n.toString());
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (inflateId == 0) inflateId = AmlBuilder.localResources.getInt("aml_text");
		view = (TextView) mInflater.inflate(inflateId, null);
		view.setText(n.getNodeValue());
		Log.d(TAG, "New Text construction complete from XML node " + n.toString());
	}
	
	/**
	 * Get the View created from this object
	 * @return View to be inserted into parent layout
	 */
	public TextView getView() {
		return view;
	}

}
