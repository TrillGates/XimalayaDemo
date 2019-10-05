package com.sunofbeaches.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.sunofbeaches.himalaya.R;

public class ConfirmCheckBoxDialog extends Dialog {

    private View mCanacel;
    private View mConfirm;
    private OnDialogActionClickListener mClickListener = null;
    private CheckBox mCheckBox;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context,int themeResId) {
        this(context,true,null);
    }

    protected ConfirmCheckBoxDialog(@NonNull Context context,boolean cancelable,@Nullable OnCancelListener cancelListener) {
        super(context,cancelable,cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_box_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClickListener != null) {
                    boolean checked = mCheckBox.isChecked();
                    mClickListener.onConfirmClick(checked);
                    dismiss();
                }
            }
        });
        mCanacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClickListener != null) {
                    mClickListener.onCancelClick();
                    dismiss();
                }
            }
        });
    }

    private void initView() {
        mCanacel = this.findViewById(R.id.dialog_check_box_cancel);
        mConfirm = this.findViewById(R.id.dialog_check_box_confirm);
        mCheckBox = this.findViewById(R.id.dialog_check_box);
    }


    public void setOnDialogActionClickListener(OnDialogActionClickListener listener) {
        this.mClickListener = listener;
    }

    public interface OnDialogActionClickListener {
        void onCancelClick();

        void onConfirmClick(boolean isCheck);
    }
}
