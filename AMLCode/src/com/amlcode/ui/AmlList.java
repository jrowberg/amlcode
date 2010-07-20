/*
AmlList.java
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

import java.util.ArrayList;

import org.w3c.dom.Node;

import com.amlcode.core.AmlActionException;
import com.amlcode.core.AmlBuilder;
import com.amlcode.core.AmlResourceException;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * @author Jeff Rowberg
 *
 */
public class AmlList {
	
	private static final String TAG = "amlcode";
	
	private AmlListAdapter adapter;
	private ListView view;
	private ArrayList<Node> list;
	private Node fontsizeNode;
	private Node alignNode;
	private Node paddingNode;
	private Node colorNode;
	private Node bgcolorNode;
	
	public AmlList(Context context, Node listNode) {
		adapter = new AmlListAdapter(context);
		list = new ArrayList<Node>();
		
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (ListView) mInflater.inflate(AmlBuilder.localResources.getInt("aml_list"), null);

		if (view.getId() == View.NO_ID) {
			int newId = (int) Math.round(Math.random() * 2147483647);
			Log.d(TAG, "AmlList() generated new ID " + newId);
			view.setId(newId);  
		}

		view.setOnItemClickListener(new ListView.OnItemClickListener() {
		    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		    	try {
					AmlBuilder.onItemClickListener(arg0, (AmlListItemView) view, position, id);
				} catch (AmlActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AmlResourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
		view.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
		    public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
	    		try {
					return AmlBuilder.onItemLongClickListener(arg0, (AmlListItemView) view, position, id);
				} catch (AmlActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AmlResourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
		    }
		});

		Log.d(TAG, "New list created from XML node " + listNode.toString());
		Log.d(TAG, "Original list has " + listNode.getChildNodes().getLength() + " child node(s)");
		fontsizeNode = listNode.getAttributes().getNamedItem("fontsize");
		alignNode = listNode.getAttributes().getNamedItem("align");
		paddingNode = listNode.getAttributes().getNamedItem("padding");
		colorNode = listNode.getAttributes().getNamedItem("color");
		bgcolorNode = listNode.getAttributes().getNamedItem("bgcolor");
		for (int i = 0; i < listNode.getChildNodes().getLength(); i++) {
			Node child = listNode.getChildNodes().item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue; // skip non-element nodes
			String name = child.getNodeName().toLowerCase();
			if (name.equals("item")) {
				// list item
				list.add(child);
			} else if (name.equals("header")) {
				// list header item
				Log.d(TAG, "Adding header to list");
				View v = new AmlListItemView(context, child, AmlBuilder.localResources.getInt("aml_list_header"), null);
				AmlBuilder.applyFormatAttribute(v, "fontsize", fontsizeNode);
				AmlBuilder.applyFormatAttribute(v, "align", alignNode);
				AmlBuilder.applyFormatAttribute(v, "padding", paddingNode);
				AmlBuilder.applyFormatAttribute(v, "color", colorNode);						
				AmlBuilder.applyFormatAttribute(v, "bgcolor", bgcolorNode);						
				view.addHeaderView(v);
			} else if (name.equals("footer")) {
				// list footer item
				Log.d(TAG, "Adding footer to list");
				View v = new AmlListItemView(context, child, AmlBuilder.localResources.getInt("aml_list_footer"), null);
				AmlBuilder.applyFormatAttribute(v, "fontsize", fontsizeNode);
				AmlBuilder.applyFormatAttribute(v, "align", alignNode);
				AmlBuilder.applyFormatAttribute(v, "padding", paddingNode);
				AmlBuilder.applyFormatAttribute(v, "color", colorNode);						
				AmlBuilder.applyFormatAttribute(v, "bgcolor", bgcolorNode);						
				view.addFooterView(v);
			}
		}
		Log.d(TAG, "Formatted list has " + list.size() + " data item(s)");
		view.setAdapter(adapter);
		view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		Log.d(TAG, "New list construction complete from XML node " + listNode.toString());
	}
	
	/**
	 * Get the View created from this object
	 * @return View to be inserted into parent layout
	 */
	public ListView getView() {
		return view;
	}

	/**
	 * A ListAdapter that presents content from an ArrayList of Nodes.
	 * 
	 */
	public class AmlListAdapter extends BaseAdapter {
		
		public AmlListAdapter(Context context) {
			mContext = context;
		}

		/**
		 * Get the number of items in the list.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			return list.size();
		}

		/**
		 * Get a single list item by position.
		 * 
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position) {
			return list.get(position);
		}

		/**
		 * Get an item's ID by position (in this case, we're just
		 * using the ArrayList index).
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Build a custom view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			AmlListItemView v;
			Node item = (Node) getItem(position);
			Node iFontsize = item.getAttributes().getNamedItem("fontsize");
			Node iAlign = item.getAttributes().getNamedItem("align");
			Node iPadding = item.getAttributes().getNamedItem("padding");
			Node iColor = item.getAttributes().getNamedItem("color");
			Node iBgcolor = item.getAttributes().getNamedItem("bgcolor");
			
			// store tap/hold data for event listener, if present
			Bundle viewData = new Bundle();
			Node tapNode = item.getAttributes().getNamedItem("tap");
			Node holdNode = item.getAttributes().getNamedItem("hold");
			if (tapNode != null) {
				viewData.putString("tap", tapNode.getNodeValue());
			}
			if (holdNode != null) {
				viewData.putString("hold", holdNode.getNodeValue());
			}
			
			if (convertView == null) {
				v = new AmlListItemView(mContext, item, AmlBuilder.localResources.getInt("aml_list_item"), viewData);
			} else {
				v = (AmlListItemView) convertView;
				// TODO: how do we reset defaults for specially formatted list items? Is a puzzlement!
				v.setBackgroundColor(0); // set default
				v.recreate(item, viewData);
			}
			AmlBuilder.applyFormatAttribute(v, "fontsize", (iFontsize != null) ? iFontsize : fontsizeNode);
			AmlBuilder.applyFormatAttribute(v, "align", (iAlign != null) ? iAlign : alignNode);
			AmlBuilder.applyFormatAttribute(v, "padding", (iPadding != null) ? iPadding : paddingNode);
			AmlBuilder.applyFormatAttribute(v, "color", (iColor != null) ? iColor : colorNode);						
			AmlBuilder.applyFormatAttribute(v, "bgcolor", (iBgcolor != null) ? iBgcolor : bgcolorNode);						
			return v;
		}
		
		/**
		 * Remember our context so we can use it when constructing views.
		 */
		private Context mContext;
		
	}

	/**
	 * AmlListItemView defines the view for each list item. This is dynamic based on
	 * the supplied structure of the list, though the default is a single full-width
	 * TextView.
	 * 
	 */
	public class AmlListItemView extends LinearLayout {
		
		private int textTemplateId;
		private Context context;
		private Bundle viewData;
		
		public AmlListItemView(Context context, Node n, int inflateId, Bundle viewData) {
			super(context);
			this.textTemplateId = inflateId;
			this.context = context;
			recreate(n, viewData);
		}
		public void recreate(Node n, Bundle viewData) {
			removeAllViews();
			this.viewData = viewData;
			AmlBuilder.setDefaultLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			for (int i = 0; i < n.getChildNodes().getLength(); i++) {
				Node child = n.getChildNodes().item(i);
				View v = AmlBuilder.parseNode(context, child, textTemplateId);
				if (v != null) {
					LinearLayout.LayoutParams layout = AmlBuilder.getLayout(child, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					addView(v, layout);
				}
			}
			AmlBuilder.setDefaultLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		}
		
		public Bundle getViewData() {
			return viewData;
		}
		
	}

}
