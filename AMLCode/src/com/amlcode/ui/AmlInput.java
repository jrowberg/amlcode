/*
AmlInput.java
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

import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * @author Jeff Rowberg
 *
 */
public class AmlInput {

	private static final String TAG = "amlcode";

	private View view;
	
	public AmlInput(Context context, Node n) {
		Log.d(TAG, "New input from XML node " + n.toString());
		Node typeNode = n.getAttributes().getNamedItem("type");
		if (typeNode == null) return;
		String type = typeNode.getNodeValue().toLowerCase();
		if (type.equals("button")) {
			view = new AmlInputButton(context, n).getView();
		} else if (type.equals("checkbox")) {
			view = new AmlInputCheckBox(context, n).getView();
		} else if (type.equals("text")) {
			view = new AmlInputEditText(context, n).getView();
		} else if (type.equals("password")) {
			EditText v2 = (EditText) new AmlInputEditText(context, n).getView();
			v2.setTransformationMethod(new PasswordTransformationMethod());
			view = v2;
		} else if (type.equals("radiobutton")) {
			view = new AmlInputRadioButton(context, n).getView();
		} else if (type.equals("togglebutton")) {
			view = new AmlInputToggleButton(context, n).getView();
		} else if (type.equals("imagebutton")) {
			view = new AmlInputImageButton(context, n).getView();
		}
		Log.d(TAG, "New input construction complete from XML node " + n.toString());
	}
	
	/**
	 * Get the View created from this object
	 * @return View to be inserted into parent layout
	 */
	public View getView() {
		return view;
	}

}
