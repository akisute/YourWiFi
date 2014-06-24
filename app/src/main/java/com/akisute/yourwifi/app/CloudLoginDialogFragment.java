package com.akisute.yourwifi.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.akisute.android.daggered.DaggeredDialogFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CloudLoginDialogFragment extends DaggeredDialogFragment {

    @InjectView(R.id.username)
    TextView mUsernameView;
    @InjectView(R.id.password)
    TextView mPasswordView;

    @Inject
    LayoutInflater mLayoutInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, getTheme());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = mLayoutInflater.inflate(R.layout.dialog_cloud_login, null);
        ButterKnife.inject(this, view);
        builder.setView(view);

        builder.setTitle(R.string.login)
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
