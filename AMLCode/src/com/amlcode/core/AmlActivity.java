/*
AmlActivity.java
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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Jeff Rowberg
 *
 */
public class AmlActivity extends Activity {

	private static final String TAG = "amlcode";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Pushing new activity");
		AmlBuilder.pushActivity(this);
		Bundle extras = getIntent().getExtras();
		Log.d(TAG, "Activity id='" + extras.getString("com.amlcode.core.NewAmlId") + "'");
		View root = AmlBuilder.getView(extras.getString("com.amlcode.core.NewAmlId"));
		if (root != null) {
			// remove this view from its parent, if already associated
			ViewGroup vgCheck = (ViewGroup) root.getParent();
			if (vgCheck != null) vgCheck.removeView(root);
			
			// set main content to this view
			setContentView(root);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Popping this activity on destroy");
		AmlBuilder.popActivity();
	}
}
