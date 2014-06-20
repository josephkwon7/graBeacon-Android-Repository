package com.dwf.tastyroad;

import com.example.wizturnbeacon.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Tutorial extends Activity implements OnClickListener {

	private ImageView btn_cancle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tutorial);

		btn_cancle = (ImageView) findViewById(R.id.btn_cancel);
		btn_cancle.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		finish();
	}
}
