package com.onepass.reception.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onepass.reception.R;
import com.onepass.reception.activities.DashBoardActivity;
import com.onepass.reception.models.response.PendingGuests;
import com.onepass.reception.utils.AppUtils;

import java.util.List;

public class PendingGuestAdapter extends RecyclerView.Adapter<PendingGuestAdapter.ViewHolder> {

    public PendingGuestAdapter(Context context, List<PendingGuests> pendingGuests) {
        this.context = context;
        this.pendingGuests = pendingGuests;
    }

    Context context;
    List<PendingGuests> pendingGuests;



    @NonNull
    @Override
    public PendingGuestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pending_guests_row,parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PendingGuestAdapter.ViewHolder holder, int position) {

        holder.tvName.setText(pendingGuests.get(position).getName());
        holder.tvPhone.setText(pendingGuests.get(position).getPhoneCountryCode()+"-"+pendingGuests.get(position).getPhoneNumber());
        holder.itemView.setOnClickListener(v->{
            if(AppUtils.isGpsEnabled(context)) {
                ((DashBoardActivity) context).captureUserPicture(position);
            }else{
                AppUtils.showGpsDialog(context);
            }
        });
    }


    @Override
    public int getItemCount() {
        return pendingGuests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName,tvPhone;
        LinearLayout llCapture,llMain;

        ImageView ivUser, ivCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            llCapture = itemView.findViewById(R.id.ll_capture);
            ivUser = itemView.findViewById(R.id.iv_user);
            ivCheck = itemView.findViewById(R.id.iv_check);
            llMain = itemView.findViewById(R.id.main);
        }
    }
}
