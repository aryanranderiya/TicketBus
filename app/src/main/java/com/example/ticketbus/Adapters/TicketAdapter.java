package com.example.ticketbus.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketbus.Model.TicketModel;
import com.example.ticketbus.R;
import com.example.ticketbus.TicketBooked;

import org.w3c.dom.Text;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;

    private List<TicketModel> ticketList;

    private LayoutInflater layoutInflater;

    public TicketAdapter(Context context, List<TicketModel> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.ticket_item_layout, parent, false);

        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {

        int pos = position;

        holder.layout_ticketID.setText(ticketList.get(position).getTicketID());
        holder.layout_adultCount.setText(Integer.toString(ticketList.get(position).getAdultCount()));
        holder.layout_childCount.setText(Integer.toString(ticketList.get(position).getChildCount()));
        holder.layout_fromLocation.setText(ticketList.get(position).getFromLocation());
        holder.layout_toLocation.setText(ticketList.get(position).getToLocation());
        holder.layout_totalPassenger.setText(Integer.toString(ticketList.get(position).getTotalPassenger()));
        holder.layout_ticketPrice.setText(Integer.toString(ticketList.get(position).getTicketPrice()));

        holder.ticketItemHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, TicketBooked.class);
                i.putExtra("TicketID",ticketList.get(pos).getTicketID());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public class TicketViewHolder extends RecyclerView.ViewHolder {

        public TextView layout_ticketID, layout_fromLocation, layout_toLocation, layout_adultCount, layout_childCount, layout_ticketPrice, layout_totalPassenger;

        public LinearLayout ticketItemHolder;
        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);

            layout_ticketID = itemView.findViewById(R.id.layout_ticketID);
            layout_fromLocation = itemView.findViewById(R.id.layout_fromLocation);
            layout_toLocation = itemView.findViewById(R.id.layout_toLocation);
            layout_adultCount = itemView.findViewById(R.id.layout_adultCount);
            layout_childCount = itemView.findViewById(R.id.layout_childCount);
            layout_totalPassenger = itemView.findViewById(R.id.layout_passengerCount);
            layout_ticketPrice = itemView.findViewById(R.id.layout_ticketPrice);

            ticketItemHolder = itemView.findViewById(R.id.ticketItemHolder);
        }
    }
}
