package com.hkm.innoscan.interfaces;
import android.support.v4.app.DialogFragment;

public interface DialogCB {
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
    public void onDialogNeutral(DialogFragment dialog);
}
