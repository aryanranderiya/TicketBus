package com.example.ticketbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ticketbus.Adapters.BusAdapter;
import com.example.ticketbus.Adapters.PaymentAdapter;
import com.example.ticketbus.Encryption.Decode;
import com.example.ticketbus.Encryption.Encode;
import com.example.ticketbus.Model.BusItem;
import com.example.ticketbus.Model.CardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import com.example.ticketbus.TicketOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class Payment extends AppCompatActivity {

    EditText edt_paymentCardPin;
    StorageReference QRstorageReference;
    int currentBalance, ticketPrice, totalPassenger, adultCount, childCount;
    boolean isLocked = false;
    String encodedUserCardPin;
    String enteredPaymentPin;
    Button btn_pay, btn_notHavingCard;
    DatabaseReference reference;
    private FirebaseUser currentUser;

    String UserID="";
    private String userName;

    private List<CardModel> cardModelList;

    private PaymentAdapter paymentAdapter;
    RecyclerView payment_recyclerView;

    LinearLayout notHavingCard;

    //TicketDetails
    String fromLoc, toLoc;

    String encryptedMergedDetails;

    String booked_ticketPrice, booked_AdultCount, bookedChildCount, booked_TotalPassenger;

    DialogPlus dialogPlus;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.custom_loading_dialog))
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setGravity(Gravity.CENTER)
                .create();

        QRstorageReference = FirebaseStorage.getInstance().getReference().child("QR");

        notHavingCard = findViewById(R.id.notHavingCard);
        btn_notHavingCard = findViewById(R.id.btn_notHavingCard);

        btn_pay = findViewById(R.id.btn_pay);
        btn_pay.setEnabled(false);

        LocalBroadcastManager.getInstance(this).registerReceiver(isLockedReciever,
                new IntentFilter("isLocked"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("payment_card_pin"));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        UserID = currentUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);

        payment_recyclerView = findViewById(R.id.payment_recyclerView);

        payment_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        cardModelList = new ArrayList<>();

        paymentAdapter = new PaymentAdapter(getApplicationContext() ,cardModelList);

        payment_recyclerView.setAdapter(paymentAdapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Wallet")){
                    fetchCard();
                }else{
                    createCard();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Getting values from Intent
        adultCount = getIntent().getIntExtra("adultCount",1);
        childCount = getIntent().getIntExtra("childCount",0);
        totalPassenger = getIntent().getIntExtra("totalPassenger",1);
        ticketPrice = getIntent().getIntExtra("ticketPrice",0);
        fromLoc = getIntent().getStringExtra("fromLoc");
        toLoc = getIntent().getStringExtra("toLoc");


        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userCardPin = Decode.decode(encodedUserCardPin);

                if (isLocked){
                    if (!enteredPaymentPin.equals(userCardPin)){
                        Toast.makeText(Payment.this, "Pin is not valid", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //pin valid
                        if (currentBalance > ticketPrice){

//                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
//                            String ticketId = ref.push().getKey();

                            dialogPlus.show();

                            Long generateTicketNo = generateRandom(5);
                            String ticketId = "TB"+generateTicketNo;

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy,hh:mm:ss a", Locale.getDefault());

                            String currentDateAndTime = sdf.format(new Date());

                            Calendar calendar, calenderExpire;
                            try {
                                Date date = sdf.parse(currentDateAndTime);
                                calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                calendar.add(Calendar.HOUR, 5);

                                calenderExpire = Calendar.getInstance();
                                calenderExpire.setTime(date);
                                calenderExpire.add(Calendar.HOUR, 12);

                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }


                            String validUpto = sdf.format(calendar.getTime());
                            String expireDate = sdf.format(calenderExpire.getTime());

                            HashMap<String,Object> ticketDetails = new HashMap<>();
                            ticketDetails.put("TicketID",ticketId);
                            ticketDetails.put("TotalPassenger",totalPassenger);
                            ticketDetails.put("AdultCount",adultCount);
                            ticketDetails.put("ChildCount",childCount);
                            ticketDetails.put("TicketPrice",ticketPrice);
                            ticketDetails.put("FromLocation",fromLoc);
                            ticketDetails.put("ToLocation",toLoc);
                            ticketDetails.put("BookingDate",currentDateAndTime);
                            ticketDetails.put("ValidUpto",validUpto);
                            ticketDetails.put("IsValidated",false);
                            ticketDetails.put("ExpireDate",expireDate);


                            booked_AdultCount = ""+adultCount;
                            bookedChildCount = ""+childCount;
                            booked_TotalPassenger = ""+totalPassenger;
                            booked_ticketPrice = "₹ "+ticketPrice;


                            List<String> list = new ArrayList<>();
                            list.add("TicketBus");
                            list.add(ticketId);
                            list.add(UserID);
                            list.add(""+totalPassenger);
                            list.add(""+adultCount);
                            list.add(""+ticketPrice);
                            list.add(fromLoc);
                            list.add(toLoc);


                            String mergedDetails = TextUtils.join(",", list);


                            encryptedMergedDetails = Encode.encode(mergedDetails);

                            Bitmap qrBitmap = generateQR(mergedDetails);

                            Uri qrUri = getUriFromBitmap(getApplicationContext(), qrBitmap);

                            //Uploading QR to Storage
                            final StorageReference uploader = QRstorageReference.child("QR-"+ticketId);
                            uploader.putFile(qrUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                  uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                      @Override
                                      public void onSuccess(Uri uri) {

                                          ticketDetails.put("QrCode", uri.toString());

                                          reference.child("TicketsBooked").child(ticketId).setValue(ticketDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  int newBalance = currentBalance - ticketPrice;

                                                  final Map<String, Object> updateBalance = new HashMap<>();
                                                  updateBalance.put("CardBalance", newBalance);

                                                  reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);
                                                  reference.child("Wallet").updateChildren(updateBalance);

                                                  dialogPlus.dismiss();

                                                  //passing booked ticket details to show
                                                  Intent bookedTicket = new Intent(Payment.this, TicketBooked.class);

                                                  bookedTicket.putExtra("TicketID",ticketId);
                                                  bookedTicket.putExtra("fromLoc",fromLoc);
                                                  bookedTicket.putExtra("toLoc",toLoc);
                                                  bookedTicket.putExtra("validUpto",validUpto);
                                                  bookedTicket.putExtra("adultCount",booked_AdultCount);
                                                  bookedTicket.putExtra("childCount",bookedChildCount);
                                                  bookedTicket.putExtra("totalPassenger",booked_TotalPassenger);
                                                  bookedTicket.putExtra("ticketPrice",booked_ticketPrice);

                                                  bookedTicket.putExtra("qrUri",qrUri.toString());

//                                                  bookedTicket.putExtra("qr",qrBitmap);

                                                  startActivity(bookedTicket);
                                                  finish();
                                              }
                                          });


                                      }
                                  });
                              }

                          });
                        }
                        else{
                            Toast.makeText(Payment.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(Payment.this, "Lock the pin", Toast.LENGTH_SHORT).show();
                }



            }
        });

    }

    private Bitmap generateQR(String encryptedMergedDetails) {
        MultiFormatWriter writer = new MultiFormatWriter();
        Bitmap bitmap;
        try {
            BitMatrix matrix = writer.encode(encryptedMergedDetails, BarcodeFormat.QR_CODE,
                    500, 500);

            BarcodeEncoder encoder = new BarcodeEncoder();

            bitmap = encoder.createBitmap(matrix);



            InputMethodManager manager = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE
            );

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        return bitmap;
    }

    private void createCard() {
        notHavingCard.setVisibility(View.VISIBLE);
        btn_notHavingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateWallet.class));
                finish();
            }
        });
    }

    public Uri getUriFromBitmap(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        // Generate a unique file name with timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "QRCode_" + timeStamp + ".jpeg";

        // Insert the image into MediaStore and get the content Uri
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri uri = null;
        try {
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri == null) {
                throw new IOException("Failed to insert image into MediaStore");
            }

            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                throw new IOException("Failed to open OutputStream for Uri: " + uri.toString());
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return uri;
    }

    private void fetchCard() {

        reference.child("Wallet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardModelList.clear();
                CardModel cardModel = snapshot.getValue(CardModel.class);
                cardModelList.add(cardModel);
                paymentAdapter.notifyDataSetChanged();

                encodedUserCardPin = snapshot.child("CardPin").getValue().toString();
                currentBalance = Integer.parseInt(snapshot.child("CardBalance").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            enteredPaymentPin = intent.getStringExtra("paymentPin");

            btn_pay.setEnabled(true);
        }
    };

    public BroadcastReceiver isLockedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isLocked = intent.getBooleanExtra("isLocked", false);
        }
    };

    public static long generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return Long.parseLong(new String(digits));
    }
}