package com.hkm.innoscan;

import org.json.JSONException;
import org.json.JSONObject;

import com.hkm.innoscan.interfaces.DialogCB;
import com.hkm.innoscan.interfaces.DialogN;
import com.hkm.innoscan.redeem.StoreStatus;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class BackBoneSimple  extends FragmentActivity implements DialogCB, DialogN{

	protected static final int BUTTON_ONE_REFERENCE = 0;
	protected static final int BUTTON_TWO_REFERENCE = 1;
	protected static final int BUTTON_THREE_REFERENCE = 2;
	protected static final int BUTTON_FOUR_REFERENCE = 3;

	@Override
	public void onDialogPositiveClick(int Tag_id) {
		// TODO Auto-generated method stub
		if (Tag_id == 1) {
			
		}
	}

	@Override
	public void onDialogNegativeClick(int Tag_id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDialogNeutral(int Tag_id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDialogNeutral(DialogFragment dialog) {
		// TODO Auto-generated method stub

	}
/*
	@Override
	public void onDestroy() {
		
		 * Fragment fragment =
		 * getFragmentManager().findFragmentById(R.id.innerfragment); if
		 * (fragment.isResumed()) {
		 * getFragmentManager().beginTransaction().remove(fragment).commit(); }
		 
		
	}*/

	protected void single_dialog(String return_result) throws Exception {
		final DialogSimpleNotification dialog = new DialogSimpleNotification(
				return_result);
		dialog.setCancelable(false);
		dialog.show(getSupportFragmentManager(), "onebutton");
	}

	protected void single_notice() throws JSONException {
		JSONObject j = StoreStatus.CurrentRedeemProduct;

		StringBuilder htmlContent = new StringBuilder();

		htmlContent.append("Name:");
		htmlContent.append(j.getString("user_name"));
		htmlContent.append("\nID: ");
		htmlContent.append(j.getString("id_code"));
		htmlContent.append("\nExpiry Date: ");
		htmlContent.append(j.getString("offer_expiry_date"));
		htmlContent.append("\nStock Title: ");
		htmlContent.append(j.getString("stock_name"));
		htmlContent.append("\nPayment needed?: ");
		htmlContent.append(j.getString("payment_done"));

		String result = htmlContent.toString();
		final DialogTwoButtons dialog = new DialogTwoButtons(result,
				R.string.redeem_id_card, R.string.close, 1);
		dialog.setCancelable(false);
		dialog.show(getSupportFragmentManager(), "twobuttons");
	}
}
