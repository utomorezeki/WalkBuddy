package com.mobileapps.walkbuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mobileapps.walkbuddy.walkbuddy.R;

/**
 * Fragment for managing a user's account.
 */
public class AccountFragment extends Fragment {
    private static final String TAG = "AccountFragment";

    private EditText editNameText, editEmailText, editPasswordText, confirmPasswordText;

    private OnAccountFragmentInteractionListener mListener;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called");
        // Inflate the layout for this fragment
        final FrameLayout mFrameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_account, container, false);

        Button btnSignOut = mFrameLayout.findViewById(R.id.sign_out);
        Button btnDeleteAccount = mFrameLayout.findViewById(R.id.delete_account);
        Button btnSaveChanges = mFrameLayout.findViewById(R.id.save_changes);

        editNameText = mFrameLayout.findViewById(R.id.edit_name);
        editEmailText = mFrameLayout.findViewById(R.id.edit_email);
        editPasswordText = mFrameLayout.findViewById(R.id.edit_password);
        confirmPasswordText = mFrameLayout.findViewById(R.id.confirm_password);

        // Sign Out button click
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build alert dialog for sign out confirmation
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                alertDialogBuilder.setTitle("Sign Out");

                alertDialogBuilder
                        .setMessage("Are you sure you want to sign out?")
                        .setCancelable(false)
                        .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mListener.onSignOut();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        // Delete Account button click
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build alert dialog for sign out confirmation
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                alertDialogBuilder.setTitle("Delete Account");

                alertDialogBuilder
                        .setMessage("Are you sure you want to delete your account?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mListener.onDeleteAccount();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        // Save Changes button click
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = editNameText.getText().toString().trim();
                String newEmail = editEmailText.getText().toString().trim();
                String newPassword = editPasswordText.getText().toString().trim();
                String confirmPassword = confirmPasswordText.getText().toString().trim();

                // Confirm password confirmation matches and is at least 6 characters long
                if (!TextUtils.isEmpty(newPassword)) {
                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (newPassword.length() < 6) {
                        Toast.makeText(getContext(), R.string.minimum_password, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                mListener.onEditAccount(newName, newEmail, newPassword);
            }
        });

        return mFrameLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach called");
        if (context instanceof OnAccountFragmentInteractionListener) {
            mListener = (OnAccountFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAccountFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach called");
        mListener = null;
    }

    public interface OnAccountFragmentInteractionListener {
        /**
         * Signs a user out.
         */
        void onSignOut();

        /**
         * Deletes a user's account.
         */
        void onDeleteAccount();

        /**
         * Edits the fields of a user's account.
         * @param newName new name for user
         * @param newEmail new email for user
         * @param newPassword new, confirmed password for user.
         */
        void onEditAccount(String newName, String newEmail, String newPassword);
    }
}
