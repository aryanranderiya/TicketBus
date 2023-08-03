package com.example.ticketbus;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements TextWatcher {


    // UI components
    TextInputEditText edit_name, edit_phone;
    TextInputLayout layout_edit_name,layout_edit_email,layout_edit_phone;
    TextView text_view_email;
    Button btn_profile_picture, btn_upload_profile_picture, btn_remove_profile_picture, btn_cancel;
    ImageButton btn_back_to_profile, btn_update_profile;
    CircleImageView users_profile_image;
    ImageView edit_profile;
    AlertDialog.Builder builder;
    Dialog alertDialog;
    ScrollView scrollView;
    Button cancelButton, removeButton;

    DialogPlus dialogPlus;
    ProgressDialog progressDialog;

    // Other profile variables
    Uri imagePath;
    private Uri croppedImagePath;
    ActivityResultLauncher<Intent> launcherSelectImage, launcherCrop, launcher;

    // String and Boolean variables
    static String new_name, new_phone, old_name, old_phone;
    static boolean isEmptyTextFieldFlag, hasFetchedDataFlag=true, isProfilePictureNull=false, selected, isProfilePictureRemoved =false, disableButtonFlag;

    // Database variables
    DatabaseReference reference_user;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    String UserID="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initialize();
        fetchData();
        buttons();
        matchText();
        launcherSelectImage();
        launcherCropSetup();
    }

    private void launcherCropSetup() {

        launcherCrop = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    croppedImagePath = UCrop.getOutput(result.getData());
                    Bitmap bitmap = null;
                    alertDialog.dismiss();
                    btn_remove_profile_picture.setEnabled(true);
                    btn_cancel.setEnabled(true);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), croppedImagePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    users_profile_image.setImageBitmap(bitmap);
                    btn_profile_picture.setEnabled(true);
                    btn_profile_picture.setTextColor(getResources().getColor(R.color.buspass_blue));
                    setSubmitFlag(false);
                }
            }
        );
    }

    private void launcherSelectImage() {

        launcherSelectImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selected = true;
                    imagePath = result.getData().getData();
                    launcherCrop(imagePath);
                }
            }
        );
    }

    private void launcherCrop(Uri imagePath) {

        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle("Crop Profile Picture");
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.buspass_blue));

        UCrop uCrop = UCrop.of(imagePath, Uri.fromFile(new File(getCacheDir(), "cropped_image")))
            .withAspectRatio(1, 1)
            .withMaxResultSize(512, 512)
            .withOptions(options);
        launcherCrop.launch(uCrop.getIntent(this));
    }

    private void matchText() {

        DatabaseReference referenceMatchText = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
        referenceMatchText.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    old_name = Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                    old_phone = Objects.requireNonNull(snapshot.child("Phone").getValue()).toString();
                }
                checkFlagAndEnableButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        TextWatcher nameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new_name = s.toString().trim();
                checkFlagAndEnableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        TextWatcher phoneTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new_phone = s.toString();
                checkFlagAndEnableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        Objects.requireNonNull(layout_edit_name.getEditText()).addTextChangedListener(nameTextWatcher);
        Objects.requireNonNull(layout_edit_phone.getEditText()).addTextChangedListener(phoneTextWatcher);
    }

    private void checkFlagAndEnableButton() {

        boolean flagValue = TextUtils.equals(new_name, old_name) || TextUtils.equals(new_phone, old_phone);

        // both values are same

        if(!TextUtils.equals(new_name, old_name) || !TextUtils.equals(new_phone, old_phone))
            flagValue=false;

        Log.d("TAG", "OLD_NAME: " + old_name );
        Log.d("TAG", "NEW_NAME: " + new_name );
        Log.d("TAG", "OLD_PHONE: " + old_phone);
        Log.d("TAG", "NEW_PHONE: " + new_phone);
        Log.d("TAG", "FLAG: " + flagValue);

        setSubmitFlag(!flagValue);
    }

    private void buttons() {

        btn_update_profile.setOnClickListener(view -> {

            updateData();
        });

        users_profile_image.setOnClickListener(view -> profilePictureProcess());
        edit_profile.setOnClickListener(view -> users_profile_image.callOnClick());

        btn_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isProfilePictureRemoved){
                    users_profile_image.setImageResource(R.drawable.icon_user_account_circle);
                    updateProfilePicture("null");
                    Toast.makeText(EditProfile.this, "Profile picture is removed!", Toast.LENGTH_SHORT).show();
                    setSubmitFlag(false);
                    btn_profile_picture.setText(R.string.update_profile_picture);
                    btn_profile_picture.setTextColor(getResources().getColor(R.color.grey));
                    isProfilePictureRemoved = false;
                    btn_remove_profile_picture.setEnabled(true);
                    btn_cancel.setEnabled(true);
                }
                else if(selected){
                    uploadProfilePicture();
                    btn_remove_profile_picture.setEnabled(true);
                    btn_cancel.setEnabled(true);
                }
            }
        });

        btn_back_to_profile.setOnClickListener(view -> finish());
    }

    private void uploadProfilePicture() {

        progressDialog.setTitle("Uploading Profile Picture...");
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        storage.getReference("Profile/"+ "ProfilePicture"+UserID).putFile(croppedImagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {

                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                updateProfilePicture(task.getResult().toString());
                                Toast.makeText(getApplicationContext(), "Profile photo successfully updated!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    isProfilePictureNull=false;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Profile photo not updated.", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
                btn_profile_picture.setTextColor(getResources().getColor(R.color.grey));
                btn_profile_picture.setEnabled(false);
                setSubmitFlag(false);

            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress=100.0*snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                progressDialog.setProgress((int)progress);
                progressDialog.setMessage((int)progress+"% Uploaded...");
            }
        });
    }

    private void updateProfilePicture(String url) {

        FirebaseDatabase.getInstance().getReference("Users/"+ Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()+"/Profile").setValue(url);

        if (Objects.equals(url, "null")){
            isProfilePictureNull = true;
        }
    }

    private void profilePictureProcess() {

        View dialog_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_profile_picture_dialog,null);
        builder= new AlertDialog.Builder(EditProfile.this);
        builder.setView(dialog_view);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        btn_upload_profile_picture = dialog_view.findViewById(R.id.btnUploadProfilePic);
        btn_remove_profile_picture = dialog_view.findViewById(R.id.btnRemoveProfilePicture);
        btn_cancel = dialog_view.findViewById(R.id.btnCancel);

        btn_upload_profile_picture.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               selectImage();
           }
        });

        btn_cancel.setOnClickListener(view -> alertDialog.dismiss());

        btn_remove_profile_picture.setOnClickListener(view -> {

            btn_remove_profile_picture.setEnabled(false);
            btn_cancel.setEnabled(false);
            if(isProfilePictureNull){
                Toast.makeText(getApplicationContext(), "Profile picture is not set!", Toast.LENGTH_SHORT).show();
                btn_update_profile.setEnabled(false);
            }
            else {
                showBottomSheetDialog();
                isProfilePictureRemoved = true;
                btn_profile_picture.setText(R.string.remove_profile_picture);
                btn_profile_picture.setTextColor(ContextCompat.getColor(this, R.color.buspass_blue));
                btn_profile_picture.setEnabled(true);
                btn_update_profile.setEnabled(false);
                alertDialog.dismiss();
            }
        });
    }

    private void selectImage() {

        Intent iSelectImage = new Intent(Intent.ACTION_PICK);
        iSelectImage.setType("image/*");
        launcherSelectImage.launch(iSelectImage);
    }

    private void showBottomSheetDialog() {

        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_profile_layout, null);
        cancelButton = bottomSheetView.findViewById(R.id.cancel_button);
        removeButton = bottomSheetView.findViewById(R.id.remove_button);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetDialog.setCancelable(true);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_profile_picture.setTextColor(getResources().getColor(R.color.grey));
                btn_profile_picture.setEnabled(false);
                btn_profile_picture.setText(R.string.update_profile_picture);
                bottomSheetDialog.dismiss();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isProfilePictureRemoved = true;
                btn_profile_picture.callOnClick();
                bottomSheetDialog.dismiss();
                isProfilePictureNull=true;
            }
        });
        bottomSheetDialog.show();
    }

    private void updateData() {

        DatabaseReference referenceUpdate = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
        if (!isEmptyTextFieldFlag) {
            Toast.makeText(getApplicationContext(), " Profile couldn't be updated.", Toast.LENGTH_SHORT).show();
        } else {
            referenceUpdate.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String str_name = Objects.requireNonNull(layout_edit_name.getEditText()).getText().toString();
                    String str_phone = Objects.requireNonNull(layout_edit_phone.getEditText()).getText().toString();

                    referenceUpdate.child("Name").setValue(str_name);
                    referenceUpdate.child("Phone").setValue(str_phone);

                    layout_edit_name.getEditText().setText(str_name);
                    layout_edit_phone.getEditText().setText(str_phone);

                    hasFetchedDataFlag = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            String str_name = Objects.requireNonNull(layout_edit_name.getEditText()).getText().toString();
            Toast.makeText(getApplicationContext(), str_name + "'s Profile Updated!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchData() {

        dialogPlus.show();
        try {
            reference_user = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
            reference_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email_result = Objects.requireNonNull(snapshot.child("Email").getValue()).toString();
                    String name_result = Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                    String phone_result = Objects.requireNonNull(snapshot.child("Phone").getValue()).toString();

                    Objects.requireNonNull(layout_edit_name.getEditText()).setText(name_result);
                    Objects.requireNonNull(layout_edit_phone.getEditText()).setText(phone_result);
                    text_view_email.setText(email_result);

                    if (snapshot.hasChild("Profile")) {

                        if (Objects.requireNonNull(snapshot.child("Profile").getValue()).equals("null")) {
                            users_profile_image.setImageResource(R.drawable.icon_user_account_circle);
                            isProfilePictureNull=true;
                        } else {
                            Glide.with(getApplicationContext()).load(Objects.requireNonNull(snapshot.child("Profile").getValue()).toString()).into(users_profile_image);
                        }

                    }
                    hasFetchedDataFlag = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditProfile.this, "Database Error!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(EditProfile.this, "Oops!! Some Error Occurred!", Toast.LENGTH_SHORT).show();
        }
        dialogPlus.dismiss();
    }

    private void initialize() {

        // Database Hooks
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        UserID = currentUser.getUid();

        // Creating DialogPlus
        dialogPlus = DialogPlus.newDialog(EditProfile.this)
            .setContentHolder(new ViewHolder(R.layout.custom_loading_dialog))
            .setContentBackgroundResource(Color.TRANSPARENT)
            .setGravity(Gravity.CENTER)
            .create();

        // EditText and TextView Hooks
        edit_name = findViewById(R.id.edit_name);
        edit_phone = findViewById(R.id.edit_phone);
        text_view_email = findViewById(R.id.textView_email);
        // ImageButton and ImageView Hooks
        btn_update_profile = findViewById(R.id.btn_update_profile);
        btn_profile_picture = findViewById(R.id.btn_profile_picture);
        edit_profile = findViewById(R.id.edit_profile);
        // Circle Image View Hook
        users_profile_image = findViewById(R.id.users_profile_image);
        // ScrollView Hook
        scrollView = findViewById(R.id.scrollView);
        // Back To User Profile Hook
        btn_back_to_profile = findViewById(R.id.btn_back_to_profile);
        // EditText Layout Hooks
        layout_edit_name = findViewById(R.id.layout_edit_name);
        layout_edit_phone = findViewById(R.id.layout_edit_phone);

        btn_profile_picture.setEnabled(false);
        btn_profile_picture.setTextColor(getResources().getColor(R.color.grey));

        setSubmitFlag(false);

        Objects.requireNonNull(layout_edit_name.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(layout_edit_phone.getEditText()).addTextChangedListener(this);

        progressDialog = new ProgressDialog(EditProfile.this);
    }

    public void setSubmitFlag(boolean flagValue) {
        disableButtonFlag = flagValue;

        if (disableButtonFlag) {
            btn_update_profile.setEnabled(true);
            btn_update_profile.setColorFilter(getResources().getColor(R.color.buspass_blue));
        }
        else{
            btn_update_profile.setEnabled(false);
            btn_update_profile.setColorFilter(getResources().getColor(R.color.grey));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        setCustomError();
        checkFlagAndEnableButton();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setCustomError();
        checkFlagAndEnableButton();
    }

    @Override
    public void afterTextChanged(Editable editable) {

        setCustomError();
        checkFlagAndEnableButton();
    }

    private void setCustomError() {

        String name = Objects.requireNonNull(layout_edit_name.getEditText()).getText().toString();
        String phone = Objects.requireNonNull(layout_edit_phone.getEditText()).getText().toString();

        if (layout_edit_name.hasFocus()) {
            if (name.isEmpty() || name.matches(".*\\d.*")) {
                layout_edit_name.setError("Please enter name!");
                isEmptyTextFieldFlag = false;
                setSubmitFlag(false);
            }
            else {
                layout_edit_name.setError(null);
                isEmptyTextFieldFlag = true;
            }
        }
        if (layout_edit_phone.hasFocus()) {
            if (phone.length() != 10 || !(phone.matches("[0-9]+")) ) {
                layout_edit_phone.setError("Please enter valid phone number!");
                isEmptyTextFieldFlag = false;
                setSubmitFlag(false);
            }
            else {
                layout_edit_phone.setError(null);
                isEmptyTextFieldFlag=true;
            }
        }
    }
}