package com.example.ticketbus;

// import classes

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorLong;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ticketbus.Databases.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment implements TextWatcher {

    // UI variables
    View view, password_view;
    ViewGroup parent;

    Button btn_change_password_submit, btn_go_to_edit_profile, btn_profile_bus_pass, btn_profile_wallet, btn_profile_change_password, btn_profile_logout;
    TextView user_profile_name;
    CircleImageView user_profile_pic;
    DialogPlus dialogPlus;
    TextInputEditText change_current_password, change_new_password, change_new_confirm_password;
    TextInputLayout layout_change_current_password, layout_change_new_password, layout_change_new_confirm_password;


    // Database variables
    DatabaseReference reference_user, reference_bus_pass, reference_wallet;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    FirebaseUser currentUser;

    // Other variables
    String UserID="";
    public static String user_name, fetch_user_email, current_password, new_password, confirm_new_password;
    SessionManager sessionManager;

    // All boolean
    private boolean hasCurrentPasswordError = false;
    private boolean hasNewPasswordError = false;
    private boolean hasConfirmNewPasswordError = false;
    private boolean doesUserHasBusPass=false;

    public Activity activity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        initialize();
        buttons();
        fetchData();

        return view;
    }

    // Fetching data from database
    private void fetchData() {

        dialogPlus.show();

        try {
            // Fetching data from database for "Users"
            reference_user = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
            reference_user.addValueEventListener(new ValueEventListener() {
                @SuppressLint("UseCompatTextViewDrawableApis")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {

                        btn_go_to_edit_profile.setEnabled(true);
                        btn_go_to_edit_profile.setTextColor(activity.getResources().getColor(R.color.white));
                        btn_go_to_edit_profile.setCompoundDrawableTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));

                        user_name = (String) Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                        fetch_user_email = Objects.requireNonNull(snapshot.child("Email").getValue()).toString();

                        user_profile_name.setText(user_name);
                        dialogPlus.dismiss();

                        if (snapshot.hasChild("Profile")) {

                            if (Objects.equals(snapshot.child("Profile").getValue(), "null")) {
                                user_profile_pic.setImageResource(R.drawable.icon_user_account_circle);
                            } else {
                                Glide.with(activity).load(Objects.requireNonNull(snapshot.child("Profile").getValue()).toString()).into(user_profile_pic);
                            }
                        }
                    } else {
                        Toast.makeText(activity, "No data found!", Toast.LENGTH_SHORT).show();
                        btn_go_to_edit_profile.setEnabled(false);
                        btn_go_to_edit_profile.setTextColor(getResources().getColor(R.color.black));
                        btn_go_to_edit_profile.setCompoundDrawableTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.black)));
                    }
                }
                @Override
                public void onCancelled (@NonNull DatabaseError error){
                }
            });

            // Fetching data from database for "Bus Pass"
            reference_bus_pass = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(UserID);

            reference_bus_pass.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("BusPass")){
                        doesUserHasBusPass = true;
                    }
                    else {
                        doesUserHasBusPass = false;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {

        }
        dialogPlus.dismiss();
    }

    // Event for all buttons in profile fragment
    private void buttons() {

        // Edit profile
        btn_go_to_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });

        // Bus Pass
        btn_profile_bus_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(doesUserHasBusPass) {
                    Intent i = new Intent(activity, UserBusPass.class);
                    Log.d("logging","true");
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(activity, CreateBusPassActivity.class);
                    Log.d("logging","false");
                    startActivity(i);
                }
            }
        });

        // Wallet
        btn_profile_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reference_wallet = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
                reference_wallet.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("Wallet")){
                            startActivity(new Intent(getActivity(), Wallet.class));
                        }
                        else{
                            startActivity(new Intent(getActivity(), CreateWallet.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        // Change Password
        btn_profile_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 changePassword();
//                Intent iChangePassword = new Intent(getActivity(), ChangePassword.class);
//                startActivity(iChangePassword);


            }
        });

        // Logout
        btn_profile_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                sessionManager.logoutUserFromSession(getActivity(), SessionManager.SESSION_REMEMBERME);
                sessionManager.logoutUserFromSession(getActivity(), SessionManager.SESSION_USERSESSION);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
    }

    private void changePassword() {
        change_current_password.addTextChangedListener(this);
        change_new_password.addTextChangedListener(this);
        change_new_confirm_password.addTextChangedListener(this);


        ViewGroup parent = (ViewGroup) password_view.getParent();
        if (parent != null) {
            parent.removeView(password_view);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(password_view);
        Dialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
        btn_change_password_submit.setEnabled(false);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                alertDialog.dismiss();

            }
        });


        btn_change_password_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                dialogPlus.show();
                AuthCredential authCredential = EmailAuthProvider.getCredential(currentUser.getEmail(), current_password);
                currentUser.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        currentUser.updatePassword(new_password).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                emptyChangePasswordField();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                emptyChangePasswordField();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        emptyChangePasswordField();
                    }
                });
            }
        });
        emptyChangePasswordField();
    }

    private void emptyChangePasswordField() {
        change_current_password.setText("");
        change_new_password.setText("");
        change_new_confirm_password.setText("");
        layout_change_current_password.setError(null);
        layout_change_new_password.setError(null);
        layout_change_new_confirm_password.setError(null);

        layout_change_current_password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout_change_new_password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout_change_new_confirm_password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    // Initializing all data when Profile Fragment class is loaded
    private void initialize() {

        // Button Hooks
        btn_profile_change_password = view.findViewById(R.id.btn_profile_change_password);
        btn_profile_bus_pass = view.findViewById(R.id.btn_profile_bus_pass);
        btn_go_to_edit_profile = view.findViewById(R.id.btn_go_to_edit_profile);
        btn_profile_wallet = view.findViewById(R.id.btn_profile_wallet);
        btn_profile_logout = view.findViewById(R.id.btn_profile_logout);
        user_profile_name = view.findViewById(R.id.user_profile_name);
        user_profile_pic = view.findViewById(R.id.user_profile_pic);

        btn_go_to_edit_profile.setEnabled(false);
        btn_go_to_edit_profile.setTextColor(getResources().getColor(R.color.black));
        btn_go_to_edit_profile.setCompoundDrawableTintList(ColorStateList.valueOf(activity.getColor(R.color.black)));

        // Database Hooks
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        UserID = currentUser.getUid();
        sessionManager = new SessionManager(getActivity(), SessionManager.SESSION_USERSESSION);

        dialogPlus = DialogPlus.newDialog(getActivity())
                .setContentHolder(new ViewHolder(R.layout.custom_loading_dialog))
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setGravity(Gravity.CENTER)
                .create();

        // Change Password Hooks

        password_view = getLayoutInflater().inflate(R.layout.layout_change_password,null);

//        changePasswordDialogView = changePasswordDialog.getHolderView();

        change_current_password = password_view.findViewById(R.id.change_current_password);
        change_new_password = password_view.findViewById(R.id.change_new_password);
        change_new_confirm_password = password_view.findViewById(R.id.change_new_confirm_password);

        btn_change_password_submit = password_view.findViewById(R.id.btn_change_password_submit);
        btn_change_password_submit.setEnabled(false);

        layout_change_current_password = password_view.findViewById(R.id.layout_change_current_password);
        layout_change_new_password = password_view.findViewById(R.id.layout_change_new_password);
        layout_change_new_confirm_password = password_view.findViewById(R.id.layout_change_new_confirm_password);

        layout_change_current_password.setError(null);
        layout_change_new_password.setError(null);
        layout_change_new_confirm_password.setError(null);

        change_current_password.setError(null);
        change_new_password.setError(null);
        change_new_confirm_password.setError(null);

        layout_change_current_password.setErrorIconDrawable(null);
        layout_change_new_password.setErrorIconDrawable(null);
        layout_change_new_confirm_password.setErrorIconDrawable(null);

        layout_change_current_password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout_change_new_password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout_change_new_confirm_password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setCustomError();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setCustomError();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        setCustomError();
    }


    private void setCustomError() {

        current_password = Objects.requireNonNull(layout_change_current_password.getEditText()).getText().toString();
        new_password = Objects.requireNonNull(layout_change_new_password.getEditText()).getText().toString();
        confirm_new_password = Objects.requireNonNull(layout_change_new_confirm_password.getEditText()).getText().toString();

        btn_change_password_submit.setEnabled(false);

        hasCurrentPasswordError = layout_change_current_password.getError() != null;
        hasNewPasswordError = layout_change_new_password.getError() != null;
        hasConfirmNewPasswordError = layout_change_new_confirm_password.getError() != null;

        // validation for Current Password field
        if (current_password.isEmpty()) {
            layout_change_current_password.setError("Please enter Password!");
            btn_change_password_submit.setEnabled(false);
            return;
        }
        else if (current_password.length() < 8){

//            layout_change_current_password.setError("Password length must be more than 8!");
            btn_change_password_submit.setEnabled(false);
            return;
        }
        else {
            layout_change_current_password.setError(null);
        }

        // validation for New Password field
        if (new_password.isEmpty()) {
            layout_change_new_password.setError("Please enter a Password!");
            btn_change_password_submit.setEnabled(false);
            return;
        }
        else if(new_password.equals(current_password)){
            layout_change_new_password.setError("New Password cannot be same as current Password!");
            btn_change_password_submit.setEnabled(false);
            return;
        }
        else if (new_password.length() < 8){
            layout_change_new_password.setError("Password length must be more than 8!");
            btn_change_password_submit.setEnabled(false);
            return;
        }
        else {
            layout_change_new_password.setError(null);
        }

        // validation for New Confirm Password field
        if (confirm_new_password.isEmpty()) {
            layout_change_new_confirm_password.setError("Please enter a Password!");
            btn_change_password_submit.setEnabled(false);
            return;
        }
        else if(!confirm_new_password.matches(new_password)){
            layout_change_new_confirm_password.setError("Please enter the same password!");
            btn_change_password_submit.setEnabled(false);
            return;
        }
        else {
            layout_change_new_confirm_password.setError(null);
        }

        btn_change_password_submit.setEnabled(!hasCurrentPasswordError && !hasNewPasswordError && !hasConfirmNewPasswordError);
    }
}