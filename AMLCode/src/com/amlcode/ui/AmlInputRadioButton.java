/*
AmlInputRadioButton.java
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
import android.widget.RadioButton;

/**
 * @author Jeff Rowberg
 *
 */
public class AmlInputRadioButton {

	private static final String TAG = "amlcode";

	private RadioButton view;

	public AmlInputRadioButton(Context context, Node n) {
		Log.d(TAG, "New RadioButton input from XML node " + n.toString());
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (RadioButton) mInflater.inflate(AmlBuilder.localResources.getInt("aml_input_radiobutton"), null);
		Node label = n.getChildNodes().item(0);
		Node checkedNode = n.getAttributes().getNamedItem("checked");
		if (label != null) view.setText(label.getNodeValue());
		String checked = "";
		if (checkedNode != null) checked = checkedNode.getNodeValue().toLowerCase();
		view.setChecked(checked.equals("yes"));
		AmlBuilder.applyActionAttribute(view, "tap", n.getAttributes().getNamedItem("tap"));
		AmlBuilder.applyActionAttribute(view, "hold", n.getAttributes().getNamedItem("hold"));
		Log.d(TAG, "New RadioButton input construction complete from XML node " + n.toString());
	}

	/**
	 * Get the View created from this object
	 * @return View to be inserted into parent layout
	 */
	public RadioButton getView() {
		return view;
	}

}
