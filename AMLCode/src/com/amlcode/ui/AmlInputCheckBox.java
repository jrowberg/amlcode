/*
AmlInputCheckBox.java
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
import android.widget.CheckBox;

/**
 * @author Jeff Rowberg
 *
 */
public class AmlInputCheckBox {

	private static final String TAG = "amlcode";

	private CheckBox view;

	public AmlInputCheckBox(Context context, Node n) {
		Log.d(TAG, "New CheckBox input from XML node " + n.toString());
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (CheckBox) mInflater.inflate(AmlBuilder.localResources.getInt("aml_input_checkbox"), null);
		Node label = n.getChildNodes().item(0);
		Node checkedNode = n.getAttributes().getNamedItem("checked");
		if (label != null) view.setText(label.getNodeValue());
		String checked = "";
		if (checkedNode != null) checked = checkedNode.getNodeValue().toLowerCase();
		view.setChecked(checked.equals("yes"));
		AmlBuilder.applyActionAttribute(view, "tap", n.getAttributes().getNamedItem("tap"));
		Log.d(TAG, "New CheckBox input construction complete from XML node " + n.toString());
	}

	/**
	 * Get the View created from this object
	 * @return View to be inserted into parent layout
	 */
	public CheckBox getView() {
		return view;
	}

}
