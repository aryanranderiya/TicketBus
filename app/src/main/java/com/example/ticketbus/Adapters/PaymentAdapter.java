package com.example.ticketbus.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbus.Model.CardModel;
import com.example.ticketbus.R;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    Context context;
    List<CardModel> cardModels;

    boolean edtEnabled;

    public PaymentAdapter(Context context, List<CardModel> cardModels) {
        this.context = context;
        this.cardModels = cardModels;
    }

    @NonNull
    @Override
    public PaymentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.payment_method_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentAdapter.ViewHolder viewHolder, int i) {

        viewHolder.txt_cardNumber.setText(cardModels.get(i).getCardNumber().toString());
        viewHolder.txt_cardHolder.setText(cardModels.get(i).getCardHolder());

        boolean isVisible = cardModels.get(i).isVisibility();

        viewHolder.expanded_layout.setVisibility(isVisible ? View.VISIBLE : View.GONE);


    }

    @Override
    public int getItemCount() {
        return cardModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView txt_cardNumber, txt_cardHolder;
        EditText edt_paymentCardPin;
        LinearLayout expanded_layout;

        ImageButton btn_pinEntered;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_pinEntered = itemView.findViewById(R.id.btn_pinEntered);

            cardView = itemView.findViewById(R.id.payment_card);

            edt_paymentCardPin = itemView.findViewById(R.id.edt_paymentCardPin);

            edt_paymentCardPin.setTransformationMethod(PasswordTransformationMethod.getInstance());

            expanded_layout = itemView.findViewById(R.id.expanded_layout);

            txt_cardNumber = itemView.findViewById(R.id.payment_cardNumber);
            txt_cardHolder = itemView.findViewById(R.id.payment_cardHolder);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CardModel cardModel = cardModels.get(getAdapterPosition());
                    cardModel.setVisibility(!cardModel.isVisibility());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            btn_pinEntered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isLocked;

                    Intent intentIsLocked = new Intent("isLocked");

                    String paymentCardPin = edt_paymentCardPin.getText().toString();

                    Intent intent = new Intent("payment_card_pin");
                    intent.putExtra("paymentPin",paymentCardPin);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    if (edtEnabled){
                        isLocked = false;
                        intentIsLocked.putExtra("isLocked",isLocked);
                        btn_pinEntered.setImageResource(R.drawable.icon_open_lock);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intentIsLocked);
                    }
                    else{
                        isLocked = true;
                        intentIsLocked.putExtra("isLocked",isLocked);
                        btn_pinEntered.setImageResource(R.drawable.icon_lock_fill);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intentIsLocked);
                    }

                    edt_paymentCardPin.setEnabled(edtEnabled);
                    edtEnabled = !edtEnabled;
                }
            });
        }
    }
}
