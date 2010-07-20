/*
AmlTable.java
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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * @author Jeff Rowberg
 *
 */
public class AmlTable {

	private static final String TAG = "amlcode";

	private TableLayout view;
	
	public AmlTable(Context context, Node tableNode) {
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (TableLayout) mInflater.inflate(AmlBuilder.localResources.getInt("aml_table"), null);
		Node expandNode = tableNode.getAttributes().getNamedItem("expand");
		if (expandNode != null) {
			String expand = expandNode.getNodeValue().toLowerCase();
			if (expand.equals("all") || expand.equals("*")) view.setStretchAllColumns(true);
			else {
				try {
					String[] indexes = expand.split(",");
					for (int i = 0; i < indexes.length; i++) {
						int columnIndex = Integer.parseInt(indexes[i].trim()) - 1;
						view.setColumnStretchable(columnIndex, true);
					}
				} catch (Exception e) {
					// bad number format
				}
			}
		}
		Log.d(TAG, "New table created from XML node " + tableNode.toString());
		Log.d(TAG, "Table has " + tableNode.getChildNodes().getLength() + " child node(s)");
		for (int i = 0; i < tableNode.getChildNodes().getLength(); i++) {
			Node child = tableNode.getChildNodes().item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue; // skip non-element nodes
			String name = child.getNodeName().toLowerCase();
			if (name.equals("tr")) {
				// table row
				Log.d(TAG, "Adding row to table");
				TableRow row = (TableRow) mInflater.inflate(AmlBuilder.localResources.getInt("aml_table_row"), null);
				for (int j = 0; j < child.getChildNodes().getLength(); j++) {
					Node child2 = child.getChildNodes().item(j);
					if (child2.getNodeType() != Node.ELEMENT_NODE) continue; // skip non-element nodes
					String name2 = child2.getNodeName().toLowerCase();
					if (name2.equals("td")) {
						// table cell
						Log.d(TAG, "Adding cell to table");
						LinearLayout cellView = (LinearLayout) mInflater.inflate(AmlBuilder.localResources.getInt("aml_table_cell"), null);
						for (int k = 0; k < child2.getChildNodes().getLength(); k++) {
							View v = AmlBuilder.parseNode(context, child2.getChildNodes().item(k));
							if (v != null) {
								LinearLayout.LayoutParams layout = AmlBuilder.getLayout(child2.getChildNodes().item(k));
								cellView.addView(v, layout);
							}
						}
						TableRow.LayoutParams cellLayout = new TableRow.LayoutParams();
						LinearLayout.LayoutParams layout = AmlBuilder.getLayout(child2);
						cellLayout.width = layout.width;
						cellLayout.height = layout.height;
						cellView.setLayoutParams(cellLayout);
						AmlBuilder.applyFormatAttribute(cellView, "fontsize", child2.getAttributes().getNamedItem("fontsize"));
						AmlBuilder.applyFormatAttribute(cellView, "colspan", child2.getAttributes().getNamedItem("colspan"));
						AmlBuilder.applyFormatAttribute(cellView, "align", child2.getAttributes().getNamedItem("align"));
						AmlBuilder.applyFormatAttribute(cellView, "valign", child2.getAttributes().getNamedItem("valign"));						
						AmlBuilder.applyFormatAttribute(cellView, "color", child2.getAttributes().getNamedItem("color"));						
						AmlBuilder.applyFormatAttribute(cellView, "bgcolor", child2.getAttributes().getNamedItem("bgcolor"));						
						row.addView(cellView, cellView.getLayoutParams());
						Log.d(TAG, "Cell added to table successfully");
					}
				}
				LinearLayout.LayoutParams layout = AmlBuilder.getLayout(child);
				view.addView(row, layout);
			}
		}
		Log.d(TAG, "New table construction complete from XML node " + tableNode.toString());
	}
	
	/**
	 * Get the View created from this object
	 * @return View to be inserted into parent layout
	 */
	public TableLayout getView() {
		return view;
	}

}
