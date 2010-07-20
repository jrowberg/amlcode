/*
AmlRadioGroup.java
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
import org.w3c.dom.NodeList;

import com.amlcode.core.AmlBuilder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

/**
 * @author Jeff Rowberg
 *
 */
public class AmlRadioGroup {

	private static final String TAG = "amlcode";

	private RadioGroup view;
	
	public AmlRadioGroup(Context context, Node n) {
		Log.d(TAG, "New RadioGroup from XML node " + n.toString());
		NodeList children = n.getChildNodes();

	    LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    view = (RadioGroup) mInflater.inflate(AmlBuilder.localResources.getInt("aml_radiogroup"), null);

	    // parse through all defined child nodes for radio buttons
		for (int j = 0; j < children.getLength(); j++) {
			Node child = children.item(j);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().toLowerCase().equals("input")) {
				View v = new AmlInputRadioButton(context, child).getView();
				if (v != null) {
					LinearLayout.LayoutParams layout = AmlBuilder.getLayout(child);
					view.addView(v, layout);
				}
				else Log.d(TAG, "AmlInputRadioButton() returned null! That's probably not good.");
			}
		}

		Log.d(TAG, "New RadioGroup construction complete from XML node " + n.toString());
	}
	
	/**
	 * Get the View created from this object
	 * @return View to be inserted into parent layout
	 */
	public RadioGroup getView() {
		return view;
	}

}
