package com.example.ticketbus.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbus.Model.BusItem;
import com.example.ticketbus.R;
import com.example.ticketbus.TicketOptions;

import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    private final List<BusItem> busList;

    private LayoutInflater layoutInflater;

    private final Context context;

    public BusAdapter(List<BusItem> busList, Context context) {
        this.busList = busList;
        this.context = context;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.bus_list_item, parent, false);

        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {

        final BusItem selectedBus = busList.get(position);

        holder.busNo.setText(busList.get(position).getBusNo());
        holder.price.setText("₹"+busList.get(position).getTicketPrice());
        holder.fromLoc.setText(busList.get(position).getFromLocation());
        holder.toLoc.setText(busList.get(position).getToLocation());
        holder.startTime.setText(busList.get(position).getStartTime());
        holder.endTime.setText(busList.get(position).getEndTime());

        holder.cardBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iselectedBus = new Intent(context, TicketOptions.class);
                iselectedBus.putExtra("busNo",selectedBus.getBusNo());
                iselectedBus.putExtra("price",selectedBus.getTicketPrice());
                iselectedBus.putExtra("fromLoc",selectedBus.getFromLocation());
                iselectedBus.putExtra("toLoc",selectedBus.getToLocation());
                iselectedBus.putExtra("startTime",selectedBus.getStartTime());
                iselectedBus.putExtra("endTime",selectedBus.getEndTime());

                iselectedBus.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(iselectedBus);
            }
        });


    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public class BusViewHolder extends RecyclerView.ViewHolder {

        public TextView busNo, price, fromLoc, toLoc, startTime, endTime;
        public CardView cardBus;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);

            busNo = itemView.findViewById(R.id.delete_busNumber);
            price = itemView.findViewById(R.id.delete_ticketPrice);
            fromLoc = itemView.findViewById(R.id.delete_fromLoci);
            toLoc = itemView.findViewById(R.id.delete_toLoci);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);

            cardBus = itemView.findViewById(R.id.cardBus);
        }
    }
    public void removeItem(int position){
        busList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(BusItem item, int position){
        busList.add(position, item);
        notifyItemInserted(position);
    }
}
