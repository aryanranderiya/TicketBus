package com.example.ticketbus.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbus.BusPassInformation;
import com.example.ticketbus.Model.BusPassModel;
import com.example.ticketbus.R;

import java.util.List;

public class BusPassAdapter extends RecyclerView.Adapter<BusPassAdapter.ViewHoler> {

    List<BusPassModel> list;
    Context context;

    public BusPassAdapter(List<BusPassModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.bus_pass_bg_layout, parent, false);

        return new ViewHoler(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler viewHoler, int i) {
        viewHoler.txtTitle.setText(list.get(i).getTitle());
        viewHoler.relativeLayout.setBackgroundResource(list.get(i).getImage());

        viewHoler.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BusPassInformation.class);
                intent.putExtra("pass_type", list.get(viewHoler.getAdapterPosition()).getType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder {

        TextView txtTitle;
        RelativeLayout relativeLayout;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            relativeLayout = itemView.findViewById(R.id.rL1);

        }
    }
}