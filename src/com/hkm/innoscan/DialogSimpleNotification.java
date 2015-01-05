package com.hkm.innoscan;

import com.hkm.innoscan.interfaces.DialogCB;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

@SuppressLint("ValidFragment")
public class DialogSimpleNotification extends DialogFragment {

	// Use this instance of the interface to deliver action events
	protected DialogCB mListener;
	protected Context ctx;

	// Override the Fragment.onAttach() method to instantiate the DialogCB
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			ctx = getActivity();
			// Instantiate the DialogCB so we can send events to the host
			mListener = (DialogCB) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement DialogCB");
		}
	}

	protected String notice;

	public DialogSimpleNotification() {
		notice = "";
	}

	public DialogSimpleNotification(String txt) {
		notice = txt;
	}

	public DialogSimpleNotification(int resString, Context ctx) {
			notice = ctx.getResources().getString(resString);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(notice)
				.setCancelable(false)
				.setNeutralButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								mListener
										.onDialogNeutral(DialogSimpleNotification.this);
							}
						});

		return builder.create();
	}
}
