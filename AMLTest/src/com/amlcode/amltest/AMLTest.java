package com.amlcode.amltest;

import com.amlcode.core.AmlBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class AMLTest extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AmlBuilder.setActivity(this);
		AmlBuilder.parse(R.raw.application);
		View root = AmlBuilder.getView("amlTestInput");
		if (root != null) setContentView(root);
    }
}