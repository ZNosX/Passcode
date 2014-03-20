package com.dcr.Passcode;

import java.util.ArrayList;
import java.util.LinkedList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PasscodeActivity extends Activity {
	Handler sleepHndler = new Handler();

	private final int PSC_SIZE = 1;
	private final int PSC_T_SIZE = 4;
	private int dotCursor;

	/**
	 * Determine if the passcode is unlocked
	 */
	private boolean unlocked = false;

	private String[] pscTemp = new String[PSC_T_SIZE];
	//private TextView psc1, psc2, psc3, psc4;
	private ImageView psc1, psc2, psc3, psc4;
	private ImageView[] psc;
	private RelativeLayout FOCUS;
	private final String DEBUG_PSW = "1234";
	private String passcode, f_psc_set, c_psc_set;
	private TextView Error_Msg, Page_Title;
	private int Failed_try = 0;
	private Context CONTEXT;
	private int mWindowWidth, width, height;
	private boolean NO_PSC = false, CON_PSC = false;
	private Vibrator VIB;

	private final String MSG = "MSG", PIN = "PIN";
	private Button[] btnNum;
	private Button psc_1, psc_2, psc_3, psc_4, psc_5, psc_6, psc_7, psc_8, psc_9, psc_0, psc_d, psc_n;

	private OnKeyListener button= new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// If the event is a key-down event on the "enter" button
			if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
					(keyCode == KeyEvent.KEYCODE_DEL)) {
				// Perform action on key press
				//psc2.setText("");
				//psc2.setEnabled(true);
				//psc2.requestFocus();
				//psc3.setEnabled(false);
				return true;
			}
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passcode);

		VIB = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

		controlLink();

		getScreenSize();

		Log.d("RESOLUTION", "Width: "+width +" Height:"+ height);

		setBtnShape();

		dotLink();

		if(PscInfo.getPIN(PIN, CONTEXT) == null)
		{
			setPasscode();
		}

		if(PscInfo.getPIN(MSG, CONTEXT) != null)
		{
			if(!PscInfo.getPIN(MSG, CONTEXT).equals("0"))
			{
				Failed_try = Integer.parseInt(PscInfo.getPIN(MSG, CONTEXT));
				Display_attempt_fail_msg();
			}
		}

		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		//psc1.setShadowLayer(10, 2, 3, Color.BLACK);
		//psc2.setShadowLayer(10, 2, 3, Color.BLACK);
		//psc3.setShadowLayer(10, 2, 3, Color.BLACK);
		//psc4.setShadowLayer(10, 2, 3, Color.BLACK);

		//inputBoxLink();
	}

	private void verifyPsc()
	{
		if(NO_PSC)
		{
			clearInput();
			clearErrorMsg();
			CON_PSC = true;
			NO_PSC = false;
			f_psc_set = passcode;
			Page_Title.setText("Confirm your passcode:");

		}
		else if(CON_PSC)
		{
			VIB.vibrate(200);
			sleepHndler.postDelayed(new Runnable() {
				public void run() {
					//doStuff();
					VIB.vibrate(190);
					clearInput();
					c_psc_set = passcode;
					if(c_psc_set.equals(f_psc_set)) PscSet();
					else
					{
						CON_PSC = false;
						NO_PSC = true;
						Page_Title.setText("Setup passcode below:");
						Error_Msg.setText("Passcode did not match, code is not saved.");
						Error_Msg.setVisibility(View.VISIBLE);
						//clearInput();
					}
				}
			}, 280);
		}
		else if(passcode.equals(PscInfo.getPIN(PIN, CONTEXT)))
		{
			psc4.setEnabled(false);
			FOCUS.requestFocus();

			PscInfo.savePIN(MSG, "0", CONTEXT);
			clearErrorMsg();
			
			VIB.vibrate(100);
			new Handler().postDelayed(new Runnable() {
				  @Override
				  public void run() {

				    //Create an intent that will start the main activity.
				    Intent intent = new Intent(PasscodeActivity.this, Registration.class);
				    PasscodeActivity.this.startActivity(intent);

				    //Finish splash activity so user cant go back to it.
				    PasscodeActivity.this.finish();

				    //Apply splash exit (fade out) and main entry (fade in) animation transitions.
				    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				  }
				}, 60);

//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setMessage("Passcode matched")
//			.setCancelable(false)
//			.setIcon(android.R.drawable.ic_secure)
//			.setTitle("Success")
//			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int id) {
//					//do things
//					unlocked = true;
//					dialog.dismiss();
//				}
//			});
//			AlertDialog dialog = builder.create();
//			dialog.setCancelable(false);
//			dialog.setCanceledOnTouchOutside(false);
//			if (!dialog.isShowing())
//				dialog.show();
		}
		else
		{
			Failed_try += 1;
			PscInfo.savePIN(MSG, Failed_try+"", CONTEXT);
			VIB.vibrate(200);
			sleepHndler.postDelayed(new Runnable() {
				public void run() {
					//doStuff();
					VIB.vibrate(190);
					Display_attempt_fail_msg();
				}
			}, 280);
		}
	}

	private void Display_attempt_fail_msg()
	{
		Error_Msg.setText(Failed_try+" attempts failed");
		Error_Msg.setVisibility(View.VISIBLE);
		clearInput();
	}

	private void getScreenSize()
	{
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
	}

	private void setPasscode()
	{
		NO_PSC = true;
		Page_Title.setText("Setup passcode below:");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(" Please setup your Passcode now.")
		.setCancelable(false)
		.setIcon(android.R.drawable.ic_secure)
		.setTitle("Passcode is not setup")
		.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//do things
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		if (!dialog.isShowing())
			dialog.show();
	}

	private void controlLink()
	{
		CONTEXT = this.getApplicationContext();

		FOCUS = (RelativeLayout) findViewById(R.id.root_focus);

		Page_Title = (TextView)findViewById(R.id.page_title);

		psc_1 = (Button) findViewById(R.id.psc_1);
		psc_2 = (Button) findViewById(R.id.psc_2);
		psc_3 = (Button) findViewById(R.id.psc_3);
		psc_4 = (Button) findViewById(R.id.psc_4);
		psc_5 = (Button) findViewById(R.id.psc_5);
		psc_6 = (Button) findViewById(R.id.psc_6);
		psc_7 = (Button) findViewById(R.id.psc_7);
		psc_8 = (Button) findViewById(R.id.psc_8);
		psc_9 = (Button) findViewById(R.id.psc_9);
		psc_0 = (Button) findViewById(R.id.psc_0);
		psc_d = (Button) findViewById(R.id.psc_d);
		psc_n = (Button) findViewById(R.id.psc_n);

		btnNum = new Button[]{psc_1, psc_2, psc_3, psc_4, psc_5, psc_6, psc_7, psc_8, psc_9, psc_0, psc_d, psc_n};

		psc1 = (ImageView) findViewById(R.id.psc1);
		psc2 = (ImageView) findViewById(R.id.psc2);
		psc3 = (ImageView) findViewById(R.id.psc3);
		psc4 = (ImageView) findViewById(R.id.psc4);

		psc = new ImageView[]{psc1, psc2, psc3, psc4};

		//TextView
		//psc1 = (TextView) findViewById(R.id.psc1);
		//psc2 = (TextView) findViewById(R.id.psc2);
		//psc3 = (TextView) findViewById(R.id.psc3);
		//psc4 = (TextView) findViewById(R.id.psc4);


		Error_Msg = (TextView)findViewById(R.id.msg_view);
	}

	private void setBtnShape()
	{
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(22, 8, 22, 8);

		for(Button btn : btnNum)
		{
			if(width == 1080)
			{
				params.setMargins(28, 12, 28, 12);
				btn.setWidth((width-315)/3);
				btn.setHeight((width-315)/3);

				btn.setLayoutParams(params);
			}
			else
			{
				btn.setWidth((width-200)/3);
				btn.setHeight((width-200)/3);

				btn.setLayoutParams(params);
			}
		}
	}

	private void PscSet()
	{
		CON_PSC = false;
		PscInfo.savePIN(PIN, c_psc_set, CONTEXT);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your Passcode has been set.")
		.setCancelable(false)
		.setIcon(android.R.drawable.ic_secure)
		.setTitle("Passcode set successful")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//do things
				dialog.dismiss();

				clearInput();
				Page_Title.setText("Enter your passcode:");
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		if (!dialog.isShowing())
			dialog.show();
	}

	private void clearErrorMsg()
	{
		Error_Msg.setVisibility(View.INVISIBLE);
	}

	private void clearInput()
	{
		//TextView
		//psc1.setText("");
		//psc2.setText("");
		//psc3.setText("");
		//psc4.setText("");

		dotCursor = 0;
		for(ImageView i : psc)
		{
			i.setBackgroundResource(R.drawable.dot_white);
		}

		//		psc1.setEnabled(true);
		//		psc1.requestFocus();
		//		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		//
		//		psc2.setEnabled(false);
		//		psc3.setEnabled(false);
		//		psc4.setEnabled(false);
	}

	public void onResume()
	{
		super.onResume();
		clearInput();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.passcode, menu);
		return true;
	}

	private void dotLink()
	{
		for(Button btn : btnNum)
		{
			//			btn.setOnTouchListener(new View.OnTouchListener() 
			//			{
			//				@Override
			//				public boolean onTouch(View v, MotionEvent event) {
			//					// TODO Auto-generated method stub
			//					
			//					VIB.vibrate(40);
			//					return false;
			//				}
			//			});

			//btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

			btn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					// Vibrate for 500 milliseconds
					VIB.vibrate(40);
					if(!unlocked)
					{
						String btn = v.getTag().toString();
						if(btn.equals("delete"))
						{
							if(dotCursor > 0)
							{
								psc[--dotCursor].setBackgroundResource(R.drawable.dot_white);
							}
						}
						else if(btn.equals("next"))
						{
							// Now do nothing
						}
						else if(dotCursor < PSC_T_SIZE)
						{
							try{
								pscTemp[dotCursor] = btn;
								psc[dotCursor++].setBackgroundResource(R.drawable.dot_dark);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
							if(dotCursor == PSC_T_SIZE)
							{
								sleepHndler.postDelayed(new Runnable() {
									public void run() {
										//doStuff();
										getCurPsc();
										verifyPsc();
									}
								}, 195);
							}
						}
					}
				}
			});
		}
	}

	private void getCurPsc()
	{
		passcode = "";
		for(String i : pscTemp)
		{
			passcode += i;
		}
	}

	//	private void inputBoxLink()
	//	{
	//		psc1.addTextChangedListener(new TextWatcher() {
	//	
	//			public void onTextChanged(CharSequence s, int start,int before, int count) 
	//			{
	//				// TODO Auto-generated method stub
	//				if(psc1.getText().toString().length() == PSC_SIZE)     //size as per your requirement
	//				{
	//					psc2.setEnabled(true);
	//					psc2.requestFocus();
	//					psc1.setEnabled(false);
	//				}
	//			}
	//			public void beforeTextChanged(CharSequence s, int start,
	//					int count, int after) {
	//				// TODO Auto-generated method stub
	//			}
	//	
	//			@Override
	//			public void afterTextChanged(Editable arg0) {
	//				// TODO Auto-generated method stub
	//			}
	//	
	//		});
	//	
	//		psc2.addTextChangedListener(new TextWatcher() {
	//	
	//			public void onTextChanged(CharSequence s, int start,int before, int count) 
	//			{
	//				// TODO Auto-generated method stub
	//				if(psc1.getText().toString().length() == PSC_SIZE)     //size as per your requirement
	//				{
	//					psc3.setEnabled(true);
	//					psc3.requestFocus();
	//					psc2.setEnabled(false);
	//				}
	//			}
	//			public void beforeTextChanged(CharSequence s, int start,
	//					int count, int after) {
	//				// TODO Auto-generated method stub
	//			}
	//	
	//			@Override
	//			public void afterTextChanged(Editable arg0) {
	//				// TODO Auto-generated method stub
	//			}
	//	
	//		});
	//	
	//		psc2.setOnKeyListener(new OnKeyListener() {
	//			@Override
	//			public boolean onKey(View v, int keyCode, KeyEvent event) {
	//				// If the event is a key-down event on the "enter" button
	//				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
	//						(keyCode == KeyEvent.KEYCODE_DEL)) {
	//					// Perform action on key press
	//					psc1.setText("");
	//					psc1.setEnabled(true);
	//					psc1.requestFocus();
	//					psc2.setEnabled(false);
	//					return true;
	//				}
	//				return false;
	//			}
	//		});
	//	
	//		psc3.addTextChangedListener(new TextWatcher() {
	//	
	//			public void onTextChanged(CharSequence s, int start,int before, int count) 
	//			{
	//				// TODO Auto-generated method stub
	//				if(psc3.getText().toString().length() == PSC_SIZE)     //size as per your requirement
	//				{
	//					psc4.setEnabled(true);
	//					psc4.requestFocus();
	//					psc3.setEnabled(false);
	//				}
	//			}
	//			public void beforeTextChanged(CharSequence s, int start,
	//					int count, int after) {
	//				// TODO Auto-generated method stub
	//			}
	//	
	//			@Override
	//			public void afterTextChanged(Editable arg0) {
	//				// TODO Auto-generated method stub
	//			}
	//	
	//		});
	//	
	//		psc3.setOnKeyListener(new OnKeyListener() {
	//			@Override
	//			public boolean onKey(View v, int keyCode, KeyEvent event) {
	//				// If the event is a key-down event on the "enter" button
	//				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
	//						(keyCode == KeyEvent.KEYCODE_DEL)) {
	//					// Perform action on key press
	//					psc2.setText("");
	//					psc2.setEnabled(true);
	//					psc2.requestFocus();
	//					psc3.setEnabled(false);
	//					return true;
	//				}
	//				return false;
	//			}
	//		});
	//	
	//		psc4.addTextChangedListener(new TextWatcher() {
	//	
	//			public void onTextChanged(CharSequence s, int start,int before, int count) 
	//			{
	//				// TODO Auto-generated method stub
	//				if(psc4.getText().toString().length() == PSC_SIZE)     //size as per your requirement
	//				{
	//					//psc4.setEnabled(false);
	//					//FOCUS.requestFocus();
	//	
	//					//Do the verify
	//					passcode = psc1.getText().toString()+psc2.getText().toString()+psc3.getText().toString()+psc4.getText().toString();
	//					verifyPsc();
	//				}
	//			}
	//			public void beforeTextChanged(CharSequence s, int start,
	//					int count, int after) {
	//				// TODO Auto-generated method stub
	//			}
	//	
	//			@Override
	//			public void afterTextChanged(Editable arg0) {
	//				// TODO Auto-generated method stub
	//			}
	//	
	//		});
	//	
	//		psc4.setOnKeyListener(new OnKeyListener() {
	//			@Override
	//			public boolean onKey(View v, int keyCode, KeyEvent event) {
	//				// If the event is a key-down event on the "enter" button
	//				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
	//						(keyCode == KeyEvent.KEYCODE_DEL)) {
	//					// Perform action on key press
	//					psc3.setText("");
	//					psc3.setEnabled(true);
	//					psc3.requestFocus();
	//					psc4.setEnabled(false);
	//					return true;
	//				}
	//				return false;
	//			}
	//		});
	//	
	//	}

}
