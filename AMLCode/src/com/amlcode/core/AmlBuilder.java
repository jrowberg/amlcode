/*
AmlBuilder.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amlcode.ui.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Jeff Rowberg
 *
 */
public class AmlBuilder {
	
	private static final String TAG = "amlcode";

	private static float scale = 0;
	private static int defaultWidth = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static int defaultHeight = LinearLayout.LayoutParams.WRAP_CONTENT;

	private static Activity act;
	private static String packageName;
	
	public static Bundle localResources = new Bundle();
	public static HashMap<String, Node> viewXML = new HashMap<String, Node>();
	public static HashMap<String, ViewGroup> views = new HashMap<String, ViewGroup>();
	public static HashMap<Integer, Bundle> data = new HashMap<Integer, Bundle>();
	public static Stack<Activity> actList = new Stack<Activity>();
	
	private static void initializeResources() throws AmlStructureException {
		if (act == null) throw new AmlStructureException("Must call AmlBuilder.setActivity() before parsing/building anything");
		packageName = act.getPackageName();
		Log.d(TAG, "Initializing local resources");
		localResources.putInt("aml_activity_linear", act.getResources().getIdentifier("aml_activity_linear", "layout", packageName));
		localResources.putInt("aml_activity_relative", act.getResources().getIdentifier("aml_activity_relative", "layout", packageName));
		localResources.putInt("aml_input_button", act.getResources().getIdentifier("aml_input_button", "layout", packageName));
		localResources.putInt("aml_input_checkbox", act.getResources().getIdentifier("aml_input_checkbox", "layout", packageName));
		localResources.putInt("aml_input_edittext", act.getResources().getIdentifier("aml_input_edittext", "layout", packageName));
		localResources.putInt("aml_input_imagebutton", act.getResources().getIdentifier("aml_input_imagebutton", "layout", packageName));
		localResources.putInt("aml_input_radiobutton", act.getResources().getIdentifier("aml_input_radiobutton", "layout", packageName));
		localResources.putInt("aml_input_togglebutton", act.getResources().getIdentifier("aml_input_togglebutton", "layout", packageName));
		localResources.putInt("aml_list_footer", act.getResources().getIdentifier("aml_list_footer", "layout", packageName));
		localResources.putInt("aml_list_header", act.getResources().getIdentifier("aml_list_header", "layout", packageName));
		localResources.putInt("aml_list_item", act.getResources().getIdentifier("aml_list_item", "layout", packageName));
		localResources.putInt("aml_list", act.getResources().getIdentifier("aml_list", "layout", packageName));
		localResources.putInt("aml_radiogroup", act.getResources().getIdentifier("aml_radiogroup", "layout", packageName));
		localResources.putInt("aml_scrollview", act.getResources().getIdentifier("aml_scrollview", "layout", packageName));
		localResources.putInt("aml_table_cell", act.getResources().getIdentifier("aml_table_cell", "layout", packageName));
		localResources.putInt("aml_table_row", act.getResources().getIdentifier("aml_table_row", "layout", packageName));
		localResources.putInt("aml_table", act.getResources().getIdentifier("aml_table", "layout", packageName));
		localResources.putInt("aml_text", act.getResources().getIdentifier("aml_text", "layout", packageName));
	}
	
	public static int findResource(String name) {
		return act.getResources().getIdentifier(name, "drawable", packageName);
	}
	
	public static void setScale(float s) {
		if (s >= 1 || s == 0) scale = s;
	}
	
	public static void setActivity(Activity activity) {
		act = activity;
	}
	
	public static void pushActivity(Activity activity) {
		actList.push(act);
		act = activity;
	}
	
	public static void popActivity() {
		act = actList.pop();
	}
	
	public static void setDefaultLayout(int width, int height) {
		defaultWidth = width;
		defaultHeight = height;
	}
	
	public static void parse(int resourceId) {
		Log.d(TAG, "Parsing AML from local resource " + resourceId);
		try {
			if (act == null) throw new AmlStructureException("Must call AmlBuilder.setActivity() before parsing/building anything");
			InputStream in = act.getResources().openRawResource(resourceId);
			if (in == null) throw new AmlStructureException("Could not create input stream from supplied resourceId");
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				in.close();
				parse(sb.toString());
			} catch (UnsupportedEncodingException e) {
				// oops
				e.printStackTrace();
			} finally {
				in.close();
			}
		} catch (IOException e) {
			// oops
			e.printStackTrace();
		} catch (AmlStructureException e) {
			new AlertDialog.Builder(act)
				.setTitle("AML Structure Error").setMessage(e.getMessage()).setNeutralButton("OK", null).show();
		}
	}
	
	public static void parse(String amlString) throws AmlStructureException {
		Log.d(TAG, "Parsing AML from String resource");
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        
        try {
        	db = dbf.newDocumentBuilder();
        	StringReader reader = new StringReader(amlString);
        	InputSource inputSource = new InputSource(reader);
        	doc = db.parse(inputSource);
            doc.getDocumentElement().normalize();
    	    NodeList amlSet = doc.getElementsByTagName("amlset");
    	    NodeList amlNodes = doc.getElementsByTagName("aml");
    	    if (amlSet.getLength() == 0 && amlNodes.getLength() != 1) {
    	    	throw new AmlStructureException("AML markup must contain either a single 'amlset' root tag with multiple 'aml' children, or else a single 'aml' root tag");
    	    }
    	    for (int i = 0; i < amlNodes.getLength(); i++) {
    	    	Node idNode = amlNodes.item(i).getAttributes().getNamedItem("id");
    	    	if (idNode == null || idNode.getNodeValue().trim().length() == 0) {
    	    		throw new AmlStructureException("All parent 'aml' nodes must have 'id' attribute");
    	    	}
    	    	viewXML.put(idNode.getNodeValue().trim(), amlNodes.item(i));
    	    }
        } catch (ParserConfigurationException e) {
        	throw new AmlStructureException("Parser Configuration Exception: " + e.getMessage());
        } catch (SAXException e) {
        	throw new AmlStructureException("SAX Exception: " + e.getMessage());
		} catch (IOException e) {
        	throw new AmlStructureException("IO Exception: " + e.getMessage());
		}
	}

	public static void build(Node amlNode) throws AmlStructureException {
		if (amlNode == null) {
			Log.d(TAG, "Cannot create root view from null node");
			throw new AmlStructureException("Cannot create root view from null node");
		}
		
		Log.d(TAG, "Creating new root view from AML node " + amlNode.toString());
		if (localResources.isEmpty()) initializeResources();
        if (scale == 0) {
        	scale = act.getResources().getDisplayMetrics().density;
        	Log.d(TAG, "Screen density scale factor is " + scale);
        }
        
        // get the aml view ID, or assign if unspecified
        String id = "aml" + (views.size() + 1);
        Node idNode = amlNode.getAttributes().getNamedItem("id");
        if (idNode != null) id = idNode.getNodeValue();
        Log.d(TAG, "New view has id='" + id + "'");
        
	    NodeList amlNodes = amlNode.getChildNodes();
	    Log.d(TAG, "Main view node has " + amlNodes.getLength() + " child nodes");
		
	    // create main activity viewgroup
	    LayoutInflater mInflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    ViewGroup rootView = (ViewGroup) mInflater.inflate(localResources.getInt("aml_activity_linear"), null);
	    
	    // determine whether scrolling is enabled (it isn't by default, so pay attention!)
	    ScrollView scrollContainer = null;
	    Node scrollNode = amlNode.getAttributes().getNamedItem("scroll");
	    if (scrollNode != null && scrollNode.getNodeValue().toLowerCase().equals("yes")) {
	    	scrollContainer = (ScrollView) mInflater.inflate(localResources.getInt("aml_scrollview"), null);
	    }
	    
	    // parse through all defined aml nodes
		for (int j = 0; j < amlNodes.getLength(); j++) {
			Log.d(TAG, "build() creating new view from XML node " + amlNodes.item(j) + " (name=" + amlNodes.item(j).getNodeName() + ")");
			View v = parseNode(act, amlNodes.item(j));
			if (v != null) {
				ViewGroup.LayoutParams layout = AmlBuilder.getLayout(amlNodes.item(j), v.getLayoutParams());
				rootView.addView(v, layout);
			}
			else Log.d(TAG, "parseNode() returned null! That could be bad markup, or just an empty TEXT node.");
		}
		
		// return finished output (scrollview container or just the root viewgroup)
		if (scrollContainer == null) {
			views.put(id, rootView);
		} else {
			scrollContainer.addView(rootView);
			views.put(id, scrollContainer);
		}
	}
	
	public static View parseNode(Context context, Node n) {
		return parseNode(context, n, 0);
	}
	
	public static View parseNode(Context context, Node n, int textTemplateId) {
		if (n == null) return null;
		Log.d(TAG, "Parsing XML node " + n.toString() + " into view");
		int nodeType = n.getNodeType();
		if (nodeType == Node.ELEMENT_NODE) {
			String itemName = n.getNodeName().toLowerCase();
			if (itemName.equals("list")) {
				// unordered list
				Log.d(TAG, "parseNode() found new <list> node, building list");
				return new AmlList(context, n).getView();
			} else if (itemName.equals("table")) {
				// table
				Log.d(TAG, "parseNode() found new <table> node, building table");
				return new AmlTable(context, n).getView();
			} else if (itemName.equals("input")) {
				// input
				Log.d(TAG, "parseNode() found new <input> node, building input");
				return new AmlInput(context, n).getView();
			} else if (itemName.equals("radiogroup")) {
				// input
				Log.d(TAG, "parseNode() found new <radiogroup> node, building group");
				return new AmlRadioGroup(context, n).getView();
			} else {
				Log.d(TAG, "parseNode() doesn't know what to do with a <" + itemName + "> element");
				return null;
			}
		} else if (nodeType == Node.TEXT_NODE) {
			// only create a new view if it's not empty
			if (n.getNodeValue().trim().length() > 0) {
				Log.d(TAG, "parseNode() found new TEXT node, building text view");
				return new AmlText(context, n, textTemplateId).getView();
			} else {
				Log.d(TAG, "parseNode() skipping empty TEXT node");
				return null;
			}
		}
		
		// unable to parse
		Log.d(TAG, "parseNode() doesn't know how to deal with XML nodes of type " + nodeType);
		return null;
		
	}
	
	public static ViewGroup getView(String id) {
		try {
			ViewGroup v = (ViewGroup) views.get(id);
			if (v == null) {
				// current view hasn't been built yet, so build it now
				Node aml = viewXML.get(id);
				if (aml == null) {
					throw new AmlResourceException("Could not find view with id='" + id + "'");
				} else {
					build(aml);
					v = (ViewGroup) views.get(id);
					return v;
				}
			} else {
				return v;
			}
		} catch (AmlResourceException e) {
			new AlertDialog.Builder(act)
				.setTitle("AML Resource Error").setMessage(e.getMessage()).setNeutralButton("OK", null).show();
			return null;
		} catch (AmlStructureException e) {
			new AlertDialog.Builder(act)
				.setTitle("AML Structure Error").setMessage(e.getMessage()).setNeutralButton("OK", null).show();
			return null;
		}
	}
	
	public static LinearLayout.LayoutParams getLayout(Node node) {
		return getLayout(node, defaultWidth, defaultHeight);
	}
	
	public static LinearLayout.LayoutParams getLayout(Node node, ViewGroup.LayoutParams defaultLayout) {
		int useWidth = defaultWidth;
		int useHeight = defaultHeight;
		if (defaultLayout != null) {
			useWidth = defaultLayout.width;
			useHeight = defaultLayout.height;
			Log.d(TAG, "Got default width=" + useWidth + ", height=" + useHeight);
		}
		return getLayout(node, useWidth, useHeight);
	}
	
	public static LinearLayout.LayoutParams getLayout(Node node, int useWidth, int useHeight) {
		// determine width and height
		int width = useWidth;
		int height = useHeight;
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Node widthNode = node.getAttributes().getNamedItem("width");
			Node heightNode = node.getAttributes().getNamedItem("height");
			if (widthNode != null) {
				String widthString = widthNode.getNodeValue().toLowerCase();
				int testWidth;
				try {
					if (widthString.equals("fill")) width = LinearLayout.LayoutParams.FILL_PARENT;
					else if ((testWidth = Integer.parseInt(widthString)) > 0) width = Math.round(testWidth * scale); 
				} catch (Exception e) {
					// invalid format
				}
			}
			if (heightNode != null) {
				String heightString = heightNode.getNodeValue().toLowerCase();
				int testHeight;
				try {
					if (heightString.equals("fill")) height = LinearLayout.LayoutParams.FILL_PARENT;
					else if ((testHeight = Integer.parseInt(heightString)) > 0) height = Math.round(testHeight * scale); 
				} catch (Exception e) {
					// invalid format
				}
			}
		}
		Log.d(TAG, "Generated layout with width=" + width + ", height=" + height);
		return new LinearLayout.LayoutParams(width, height);
	}
	
	public static void applyFormatAttribute(View v, String name, Node node) {
		//if (node == null) Log.d(TAG, "applyFormatAttribute() for " + name + " given NULL node");
		if (v == null || node == null) return;
		applyFormat(v, name, node.getNodeValue());
	}
	
	public static void applyFormat(View v, String name, String value) {
		if (v == null || value == null) return;
		String[] fullName = v.getClass().getName().split("\\.");
		String[] realName = fullName[fullName.length - 1].split("\\$");
		String className = realName[realName.length - 1];
		Boolean applied = false;
		Boolean stop = false;
		if (name.equals("fontsize")) {
			// text size (small/medium/large)
			if (className.equals("TextView")) {
				Log.d(TAG, "applyFormat() applying " + name + "=" + value + " to '" + className + "' instance");
				TextView v2 = (TextView) v;
				if (value.equals("small")) v2.setTextAppearance(v.getContext(), android.R.style.TextAppearance_Small);
				else if (value.equals("medium")) v2.setTextAppearance(v.getContext(), android.R.style.TextAppearance_Medium);
				else if (value.equals("large")) v2.setTextAppearance(v.getContext(), android.R.style.TextAppearance_Large);
				applied = true;
			}
		} else if (name.equals("color")) {
			if (className.equals("TextView")) {
				Log.d(TAG, "applyFormat() applying " + name + "=" + value + " to '" + className + "' instance");
				TextView v2 = (TextView) v;
				v2.setTextColor(Color.parseColor(value));
				applied = true;
			}
		} else if (name.equals("bgcolor")) {
			// background color
			Log.d(TAG, "applyFormat() applying " + name + "=" + value + " to '" + className + "' instance");
			v.setBackgroundColor(Color.parseColor(value));
			applied = true;
			stop = true;
		} else if (name.equals("padding")) {
			// horizontal content alignment (left/center/right)
			Log.d(TAG, "applyFormat() applying " + name + "=" + value + " to '" + className + "' instance");
			try {
				int padding = Math.round(Integer.parseInt(value) * scale);
				v.setPadding(padding, padding, padding, padding);
			} catch (Exception e) {
				// invalid format, probably not an integer
			}
			applied = true;
			stop = true;
		} else if (name.equals("align")) {
			// horizontal content alignment (left/center/right)
			Log.d(TAG, "applyFormat() applying " + name + "=" + value + " to '" + className + "' instance");
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
			if (lp.gravity == -1) lp.gravity = 0;
			if (value.equals("left")) lp.gravity |= Gravity.LEFT;
			else if (value.equals("center") || value.equals("middle")) lp.gravity |= Gravity.CENTER_HORIZONTAL;
			else if (value.equals("right")) lp.gravity |= Gravity.RIGHT;
			v.setLayoutParams(lp);
			applied = true;
		} else if (name.equals("valign")) {
			// vertical content alignment (top/center/bottom)
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
			if (lp.gravity == -1) lp.gravity = 0;
			if (value.equals("top")) lp.gravity |= Gravity.TOP;
			else if (value.equals("center") || value.equals("middle")) lp.gravity |= Gravity.CENTER_VERTICAL;
			else if (value.equals("bottom")) lp.gravity |= Gravity.BOTTOM;
			v.setLayoutParams(lp);
			applied = true;
		} else if (name.equals("colspan")) {
			TableRow.LayoutParams lp = (TableRow.LayoutParams) v.getLayoutParams();
			try {
				int colspan = Integer.parseInt(value);
				lp.span = colspan;
				v.setLayoutParams(lp);
			} catch (Exception e) {
				// invalid format, probably not an integer
			}
			applied = true;
			stop = true;
		}
		if (stop) {
			Log.d(TAG, "applyFormat() instructed to stop applying " + name + "=" + value + " after this view");
		} else if (className.equals("LinearLayout") || className.equals("ListView") || className.equals("TableLayout") || className.equals("TableRow") || className.equals("AmlListItemView")) {
			Log.d(TAG, "applyFormat() applying " + name + "=" + value + " to all child views of '" + className + "' instance");
			ViewGroup v2 = (ViewGroup) v;
			for (int i = 0; i < v2.getChildCount(); i++) applyFormat(v2.getChildAt(i), name, value);
		} else if (!applied) {
			Log.d(TAG, "applyFormat() cannot apply " + name + "=" + value + " to class '" + className + "'");
		}
	}
	
	public static void applyActionAttribute(View v, String name, Node node) {
		//if (node == null) Log.d(TAG, "applyActionAttribute() for " + name + " given NULL node");
		if (v == null || node == null) return;
		applyAction(v, name, node.getNodeValue());
	}
	
	public static void applyAction(View v, String name, String value) {
		if (v == null || value == null) return;
		String[] fullName = v.getClass().getName().split("\\.");
		String[] realName = fullName[fullName.length - 1].split("\\$");
		String className = realName[realName.length - 1];
		Boolean applied = false;
		
		// assign a random ID if the view doesn't have one
		if (v.getId() == View.NO_ID) {
			int newId = (int) Math.round(Math.random() * 2147483647);
			Log.d(TAG, "applyAction() generated new ID " + newId);
			v.setId(newId);  
		}
		
		// get or create the view's current bundle of id-associated data
		Bundle viewData = data.get(v.getId());
		if (viewData == null) viewData = new Bundle();

		// add appropriate event listeners
		if (name.equals("tap")) {
			// tap/click
			Log.d(TAG, "applyAction() applying " + name + "=" + value + " to '" + className + "' instance");
			viewData.putString(name, value);
			v.setOnClickListener(new View.OnClickListener() {
			    public void onClick(View view) {
			    	try {
			    		AmlBuilder.onClickListener(view);
			    	} catch (AmlActionException e) {
			    		// TODO Auto-generated catch block
			    		e.printStackTrace();
			    	} catch (AmlResourceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			});
			applied = true;
		} else if (name.equals("hold")) {
			// tap and hold
			Log.d(TAG, "applyAction() applying " + name + "=" + value + " to '" + className + "' instance");
			viewData.putString(name, value);
			v.setOnLongClickListener(new View.OnLongClickListener() {
			    public boolean onLongClick(View view) {
			    	try {
			    		return AmlBuilder.onLongClickListener(view);
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
			applied = true;
		}
		
		// save updated view data
		data.put(v.getId(), viewData);
		
		if (!applied) {
			Log.d(TAG, "applyAction() cannot apply " + name + "=" + value + " to class '" + className + "'");
		}
	}
	
	public static void onClickListener(View view) throws AmlActionException, AmlResourceException {
		Log.d(TAG, "Click event on " + view.toString());
		Bundle viewData = data.get(view.getId());
		Log.d(TAG, "View data has tap=" + viewData.getString("tap"));
		runAction(viewData.getString("tap"), view);
	}
	
	public static void onItemClickListener(AdapterView<?> arg0, AmlList.AmlListItemView view, int position, long id) throws AmlActionException, AmlResourceException {
		Log.d(TAG, "Click event on item " + view.toString());
		Bundle viewData = view.getViewData();
		Log.d(TAG, "View data has tap=" + viewData.getString("tap"));
		runAction(viewData.getString("tap"), view);
	}
	
	public static boolean onLongClickListener(View view) throws AmlActionException, AmlResourceException {
		Log.d(TAG, "LongClick event on " + view.toString());
		Bundle viewData = data.get(view.getId());
		Log.d(TAG, "View data has tap=" + viewData.getString("hold"));
		runAction(viewData.getString("hold"), view);
		return true;
	}
	
	public static boolean onItemLongClickListener(AdapterView<?> arg0, AmlList.AmlListItemView view, int position, long id) throws AmlActionException, AmlResourceException {
		Log.d(TAG, "LongClick event on item " + view.toString());
		Bundle viewData = view.getViewData();
		Log.d(TAG, "View data has hold=" + viewData.getString("hold"));
		runAction(viewData.getString("hold"), view);
		return true;		
	}
	
	public static void runAction(String action, View sourceView) throws AmlActionException, AmlResourceException {
		if (action == null) return;
		if (action.charAt(0) == '.') {
			// AML built-in action, begins with '.'
			Pattern p = Pattern.compile("\\.([a-zA-Z0-9]+)\\(([a-zA-Z0-9\\ _,\\.]*)\\)");
			Matcher m = p.matcher(action);
			if (m.find()) {
				String func = m.group(1);
				String params = m.group(2);
				Log.d(TAG, "Running AML function '" + func + "', parameters '" + params + "'");
				
				// run the requested function
				if (func.equals("back")) {
					// go back one activity (potentially quit the whole app)
					Log.d(TAG, "Going back one view");
					act.finish();
				} else if (func.equals("go")) {
					// move to a new activity/view
					Log.d(TAG, "Switching to new view with id='" + params + "'");
					ViewGroup view = getView(params);
					if (view != null) {
						Intent intent = new Intent(act.getApplicationContext(), AmlActivity.class);
						intent.putExtra("com.amlcode.core.NewAmlId", params);
						Log.d(TAG, "Starting new activity...");
						act.startActivity(intent);
					} else {
						Log.d(TAG, "Could not locate view with id='" + params + "'");
					}
				}
			} else {
				throw new AmlActionException("AML action specified, but could not parse '" + action + "'");
			}
		} else {
			throw new AmlActionException("Custom action specified, but could not parse '" + action + "'");
		}
	}

}
