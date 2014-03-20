package com.dcr.Passcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Registration extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	public void toLoginPage(View view)
	{
		Intent intent = new Intent(Registration.this, Login.class);
		Registration.this.startActivity(intent);

	    //Finish splash activity so user cant go back to it.
		Registration.this.finish();

	    //Apply splash exit (fade out) and main entry (fade in) animation transitions.
	    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	

}
