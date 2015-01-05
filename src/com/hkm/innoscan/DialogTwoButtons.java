package com.hkm.innoscan;

import com.hkm.innoscan.interfaces.DialogN;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

@SuppressLint("ValidFragment")
public class DialogTwoButtons extends DialogFragment {

	// Use this instance of the interface to deliver action events
	protected DialogN mListener;
	protected Context ctx;

	// Override the Fragment.onAttach() method to instantiate the DialogCB
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			ctx = getActivity();
			// Instantiate the DialogCB so we can send events to the host
			mListener = (DialogN) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement DialogCB");
		}
	}

	protected String notice;

	public DialogTwoButtons() {
		notice = "";
	}

	public DialogTwoButtons(String txt) {
		notice = txt;
		positive_id = R.string.ok;
		negative_id = R.string.cancel;
		tag_id = 0;
	}

	public DialogTwoButtons(int resString) {
		if (isAdded()) {
			notice = getActivity().getResources().getString(resString);
		}
	}

	protected int positive_id, negative_id, tag_id;

	public DialogTwoButtons(int resString, int position_string_id,
			int negative_string_id, int TAG) {
		if (isAdded()) {
			notice = getActivity().getResources().getString(resString);
			positive_id = position_string_id;
			negative_id = negative_string_id;
			tag_id = TAG;
		}
	}

	public DialogTwoButtons(String txt, int position_string_id,
			int negative_string_id, int TAG) {
		notice = txt;
		positive_id = position_string_id;
		negative_id = negative_string_id;
		tag_id = TAG;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(notice)

				/*
				 * .setNeutralButton(R.string.ok, new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface
				 * dialogInterface, int i) { mListener
				 * .o(DialogTwoButtons.this); } })
				 */

				.setPositiveButton(positive_id,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// FIRE ZE MISSILES!
								mListener.onDialogPositiveClick(tag_id);
							}
						})
				.setNegativeButton(negative_id,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
								mListener.onDialogNegativeClick(tag_id);
							}
						})

				.setCancelable(false)

		;

		return builder.create();
	}
}
