package com.example.ticketbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class TicketBooked extends AppCompatActivity {

    String ticketId, fromLocation, toLocation, validUpto, adultCount, childCount, totalPassenger, ticketPrice;
    Bitmap qrImage;

    TextView txt_ticketId, txt_fromLoc, txt_toLoc, txt_validUpto, txt_adultCount, txt_childCount, txt_totalPassenger, txt_ticketPrice;

    ImageView imgQr;

    Uri bookedQRUri;

    private FirebaseUser currentUser;
    String UserID="";
    DatabaseReference reference;

    Button btn_downloadTicket;
    DialogPlus dialogPlus;

    LinearLayout ticket_holder_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_booked);

        dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.custom_loading_dialog))
                .setContentBackgroundResource(Color.TRANSPARENT)
                .setGravity(Gravity.CENTER)
                .create();

        Intent i = getIntent();
        ticketId = i.getStringExtra("TicketID");
//        fromLocation = i.getStringExtra("fromLoc");
//        toLocation = i.getStringExtra("toLoc");
//        validUpto = i.getStringExtra("validUpto");
//        adultCount = i.getStringExtra("adultCount");
//        childCount = i.getStringExtra("childCount");
//        totalPassenger = i.getStringExtra("totalPassenger");
//        ticketPrice = i.getStringExtra("ticketPrice");
//
//        qrImage = i.getParcelableExtra("qr");
//
//        bookedQRUri = Uri.parse(i.getStringExtra("qrUri"));
//
        txt_ticketId = findViewById(R.id.txt_ticketId);
        txt_fromLoc = findViewById(R.id.txt_bookedFromLoc);
        txt_toLoc = findViewById(R.id.txt_bookedToLoc);
        txt_validUpto = findViewById(R.id.txt_entryValidUpto);
        txt_adultCount = findViewById(R.id.txt_bookedAdultCount);
        txt_childCount = findViewById(R.id.txt_bookedChildCount);
        txt_totalPassenger = findViewById(R.id.txt_bookedtotalPassengers);
        txt_ticketPrice = findViewById(R.id.txt_bookedTicketPrice);

        imgQr = findViewById(R.id.img_Qr);

//        btn_downloadTicket = findViewById(R.id.btn_downloadTicket);
//
//        ticket_holder_layout = findViewById(R.id.ticket_holder_layout);
//        ticket_holder_layout.setVisibility(View.INVISIBLE);
//
//
//        txt_ticketId.setText(ticketId);
//        txt_fromLoc.setText(fromLocation);
//        txt_toLoc.setText(toLocation);
//        txt_validUpto.setText(validUpto);
//        txt_adultCount.setText(adultCount);
//        txt_childCount.setText(childCount);
//        txt_totalPassenger.setText(totalPassenger);
//        txt_ticketPrice.setText(ticketPrice);
//
//        imgQr.setImageBitmap(qrImage);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        UserID = currentUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID).child("TicketsBooked").child(ticketId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                    Glide.with(getApplicationContext()).load(snapshot.child("QrCode").getValue().toString()).into(imgQr);
//                    dialogPlus.dismiss();

//                    btn_downloadTicket.setVisibility(View.INVISIBLE);

                txt_ticketId.setText(snapshot.child("TicketID").getValue().toString());
                txt_fromLoc.setText(snapshot.child("FromLocation").getValue().toString());
                txt_toLoc.setText(snapshot.child("ToLocation").getValue().toString());
                txt_validUpto.setText(snapshot.child("ValidUpto").getValue().toString());
                txt_adultCount.setText(snapshot.child("AdultCount").getValue().toString());
                txt_childCount.setText(snapshot.child("ChildCount").getValue().toString());
                txt_totalPassenger.setText(snapshot.child("TotalPassenger").getValue().toString());
                txt_ticketPrice.setText(snapshot.child("TicketPrice").getValue().toString());
//                Glide.with(TicketBooked.this).load(Objects.requireNonNull(snapshot.child("QrCode").getValue().toString())).into(imgQr);
                Picasso.get().load(snapshot.child("QrCode").getValue().toString()).into(imgQr);
//                Toast.makeText(TicketBooked.this, ""+snapshot.child("QrCode").getValue().toString(), Toast.LENGTH_SHORT).show();
//                imgQr.setImageBitmap(qrImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        ticket_holder_layout.setVisibility(View.VISIBLE);

//        btn_downloadTicket.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LinearLayout content = findViewById(R.id.ticketLayout);
//                content.setDrawingCacheEnabled(true);
//                Bitmap bitmap = content.getDrawingCache();
//                File file,f;
//                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
//                {
//                    file =new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
//                    if(!file.exists())
//                    {
//                        file.mkdirs();
//
//                    }
//                    f = new File(file.getAbsolutePath()+file.seperator+ "filename"+".png");
//                }
//                FileOutputStream ostream = new FileOutputStream(f);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
//                ostream.close();
//
//            }
// catch (Exception e){
//                e.printStackTrace();
//            }
//            }
//        });
    }
}