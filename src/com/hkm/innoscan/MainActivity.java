package com.hkm.innoscan;

import java.io.IOException;

import net.sourceforge.zbar.Symbol;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.hkm.innoscan.redeem.Redeem;
import com.hkm.innoscan.redeem.StoreStatus;
import com.hkm.innoscan.redeem.Tool;

public class MainActivity extends BackBoneSimple implements
		View.OnClickListener {
	public interface redeem_call_back {
		public void redeem_success(JSONObject return_data);

		public void redeem_error(String str);
	}

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST_STEP_1 = 1;
	private static final int ZBAR_QR_SCANNER_REQUEST_STEP_2 = 2;
	private TextView ete, status;
	private int verif_step;
	private static String verification_code, code_ver_2nd, method_msg,
			method_code, error_invalid_code;
	private Button reset_button, profile_check_button;
	private ImageButton scan_button;
	private ToggleButton auto_scan_button;
	private boolean autoscan = false;
	private MediaPlayer mPlayer;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.setting:

			return true;
		case R.id.about:
			try {
				PackageInfo pInfo = getPackageManager().getPackageInfo(
						getPackageName(), 0);
				final String version = pInfo.versionName;
				final String msg = Tool.get_mac_address(this) + "\n"
						+ "Developed by ImusicTech\nv" + version;
				MainActivity.this.single_dialog(msg);
			} catch (Exception e) {

			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.auto:
			autoscan = auto_scan_button.isChecked();
			if (autoscan)
				Tool.trace(MainActivity.this, R.string.scanauto_notice);
			break;
		case R.id.scan_button:
			if (verif_step == 1) {
				launchQRScanner(ZBAR_QR_SCANNER_REQUEST_STEP_1);
			} else if (verif_step == 2) {
				launchQRScanner(ZBAR_QR_SCANNER_REQUEST_STEP_2);
			}
			break;
		case R.id.profile:
			try {
				single_notice();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Tool.trace(MainActivity.this, e.toString());
			}
			// it was the second button
			break;
		case R.id.reset:
			init_step_1();
			break;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mPlayer = MediaPlayer.create(this, R.raw.notification_2);
		status = (TextView) this.findViewById(R.id.status_text);
		ete = (TextView) this.findViewById(R.id.textbox);
		auto_scan_button = (ToggleButton) this.findViewById(R.id.auto);
		// profile button
		profile_check_button = (Button) this.findViewById(R.id.profile);
		/**
		 * QR code switch
		 */

		reset_button = (Button) this.findViewById(R.id.reset);
		reset_button.setOnClickListener(this);
		auto_scan_button.setOnClickListener(this);
		/**
		 * this is the check the customer's ID
		 */
		profile_check_button.setOnClickListener(this);
		error_invalid_code = this.getResources().getString(
				R.string.emailcode_fail);
		init_step_1();
	}

	public boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	private void init_step_1() {
		profile_check_button.setVisibility(View.GONE);
		reset_button.setVisibility(View.GONE);
		scan_button = (ImageButton) this.findViewById(R.id.scan_button);
		/**
		 * 
		 * "code_verify1">Scan Code - Phone/Email "code_verify2">Scan Redeem QR
		 * "code from the Email now! "code_verify3">Scan Redeem QR code from the
		 * Phone now!
		 */
		scan_button.setImageResource(R.drawable.qr_scan);
		scan_button.setOnClickListener(this);
		code_ver_2nd = "";
		verification_code = "";
		verif_step = 1;
		method_code = "";
		Tool.trace(this, "step 1");
	}

	private void init_step_2() {
		profile_check_button.setVisibility(View.VISIBLE);
		reset_button.setVisibility(View.VISIBLE);
		scan_button = (ImageButton) this.findViewById(R.id.scan_button);
		scan_button.setOnClickListener(this);
		if (Integer.parseInt(method_code) == 4) {
			// scan_button.setText(R.string.code_verify3);
			scan_button.setImageResource(R.drawable.qrcode_phone);
		}
		if (Integer.parseInt(method_code) == 3) {
			// scan_button.setText(R.string.code_verify2);
			scan_button.setImageResource(R.drawable.qrcode_email);
		}
		verif_step = 2;
	}

	public void launchQRScanner(int z_step_id) {
		if (isCameraAvailable()) {

			final Intent intent = new Intent(this, ZBarScannerActivity.class);
			
			if (z_step_id == ZBAR_QR_SCANNER_REQUEST_STEP_1) {
				Tool.trace(this, R.string.code_verify1);
			} else if (z_step_id == ZBAR_QR_SCANNER_REQUEST_STEP_2) {
				if (Integer.parseInt(method_code) == 4) {
					Tool.trace(this, R.string.code_verify3);
				} else if (Integer.parseInt(method_code) == 3) {
					Tool.trace(this, R.string.code_verify2);
				}
			}
			
			intent.putExtra(ZBarConstants.SCAN_MODES,
					new int[] { Symbol.QRCODE });

			
			startActivityForResult(intent, z_step_id);
		} else {
			Tool.trace(this, "Rear Facing Camera Unavailable");
			status.setText("Rear Facing Camera Unavailable");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Tool.trace(this, "scan result callback");

		/*
		 * switch (requestCode) {
		 * 
		 * 
		 * case ZBAR_SCANNER_REQUEST: Tool.trace(this, "scan result callback");
		 * break; case ZBAR_QR_SCANNER_REQUEST_STEP_1:
		 * 
		 * mPlayer.start();
		 * 
		 * if (resultCode == RESULT_OK) { status.setText(R.string.loading);
		 * 
		 * verification_code = data .getStringExtra(ZBarConstants.SCAN_RESULT);
		 * submission_of_http_request_from_step_1();
		 * 
		 * } else if (resultCode == RESULT_CANCELED && data != null) { String
		 * error = data.getStringExtra(ZBarConstants.ERROR_INFO); if
		 * (!TextUtils.isEmpty(error)) { Tool.trace(this, error);
		 * status.setText(error); } } break; case
		 * ZBAR_QR_SCANNER_REQUEST_STEP_2:
		 * 
		 * mPlayer.start(); if (resultCode == RESULT_OK) {
		 * 
		 * String scan_result = data .getStringExtra(ZBarConstants.SCAN_RESULT);
		 * if (scan_result.equals(code_ver_2nd)) {
		 * submission_of_http_request_from_step_2(scan_result); } else {
		 * Tool.trace(this, R.string.emailcode_fail);
		 * status.setText("unabled to confirm this QR code");
		 * 
		 * try { final DialogSimpleNotification dialog = new
		 * DialogSimpleNotification( error_invalid_code);
		 * dialog.show(getSupportFragmentManager(), "onebutton"); } catch
		 * (Exception e) { status.setText(e.getMessage()); }
		 * 
		 * } } else if (resultCode == RESULT_CANCELED && data != null) { String
		 * error = data.getStringExtra(ZBarConstants.ERROR_INFO); if
		 * (!TextUtils.isEmpty(error)) { Tool.trace(this, error); } } break; }
		 */
	}

	private void submission_of_http_request_from_step_1() {
		final Redeem re = new Redeem(getApplicationContext(),
				new redeem_call_back() {

					@Override
					public void redeem_success(JSONObject data) {

						try {
							code_ver_2nd = data.getString("ver_on_2_step");
							method_msg = data.getString("instruction");
							method_code = data.getString("instruction_code");

							StoreStatus.CurrentRedeemProduct = data;

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Tool.trace(MainActivity.this, e.toString());
						}

						init_step_2();
						Tool.trace(MainActivity.this, R.string.ready);
						status.setText(method_msg);
						// ete.setText(StoreStatus.CurrentRedeemProduct.toString());

						if (autoscan) {
							Intent intent = new Intent(MainActivity.this,
									ZBarScannerActivity.class);
							intent.putExtra(ZBarConstants.SCAN_MODES,
									new int[] { Symbol.QRCODE });
							startActivityForResult(intent,
									ZBAR_QR_SCANNER_REQUEST_STEP_2);
						}
						/*
						 * init the single check ID dialog here
						 */
						try {
							single_notice();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Tool.trace(MainActivity.this, e.toString());
						}

					}

					@Override
					public void redeem_error(String str) {
						// TODO Auto-generated method stub
						Tool.trace(MainActivity.this, str);
						verification_code = "";
						// status.setText(str);
						try {
							MainActivity.this.single_dialog(str);
						} catch (Exception e) {
							status.setText(str);
							// TODO Auto-generated catch block
							// Tool.trace(MainActivity.this,
							// e.toString());
						}

					}

				});

		final String[] asyncdata = { verification_code, "verify" };
		re.execute(asyncdata);
	}

	private void submission_of_http_request_from_step_2(String scan_result) {

		final Redeem re = new Redeem(getApplicationContext(),
				new redeem_call_back() {

					@Override
					public void redeem_success(JSONObject data) {
						Tool.trace(MainActivity.this,
								R.string.emailcode_success);
						status.setText(R.string.emailcode_success);
						init_step_1();
					}

					@Override
					public void redeem_error(String str) {
						// TODO Auto-generated method stub
						// Tool.trace(MainActivity.this, str);
						// status.setText(str);

						try {
							MainActivity.this.single_dialog(str);
						} catch (Exception e) {
							status.setText(str);
							// TODO Auto-generated catch block
							Tool.trace(MainActivity.this, e.toString()
									+ " - failed to render dialog");
						}
					}

				});
		final String[] asyncdata = { scan_result, "redeem", method_code };
		verification_code = scan_result;
		re.execute(asyncdata);
	}

	private void submission_of_http_request_from_customer_ID() {
		final Redeem re = new Redeem(getApplicationContext(),
				new redeem_call_back() {

					@Override
					public void redeem_success(JSONObject data) {
						Tool.trace(MainActivity.this,
								R.string.emailcode_success);
						init_step_1();
						status.setText(R.string.emailcode_success);
					}

					@Override
					public void redeem_error(String str) {
						// TODO Auto-generated method stub
						try {
							MainActivity.this.single_dialog(str);
						} catch (Exception e) {
							status.setText(str);
							// TODO Auto-generated catch block
							Tool.trace(MainActivity.this, e.toString()
									+ " - failed to render dialog");
						}
					}

				});
		final String[] asyncdata = { verification_code, "redeem", "5" };

		// Tool.trace(MainActivity.this, verification_code);
		re.execute(asyncdata);
	}

	@Override
	public void onDialogNeutral(DialogFragment dialog) {
		// TODO Auto-generated method stub
		status.setText("");
	}

	@Override
	public void onDialogPositiveClick(int Tag_id) {
		super.onDialogPositiveClick(Tag_id);
		// TODO Auto-generated method stub
		if (Tag_id == 1) {
			// status.setText(R.string.loading);

			submission_of_http_request_from_customer_ID();
		}
	}

}
