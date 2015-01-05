/**
 * 
 */
package com.hkm.innoscan.interfaces;

import android.support.v4.app.DialogFragment;

/**
 * @author hesk
 * 
 */
public interface DialogN {
	public void onDialogPositiveClick(int Tag_id);

	public void onDialogNegativeClick(int Tag_id);

	public void onDialogNeutral(int Tag_id);
}
