package com.example.myrestaurant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrestaurant.Common.Common;
import com.example.myrestaurant.EventBus.AddonEventChange;
import com.example.myrestaurant.Model.Addon;
import com.example.myrestaurant.R;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyAddonAdapter extends RecyclerView.Adapter<MyAddonAdapter.MyViewHolder> {

    Context context;
    List<Addon> addonList;

    public MyAddonAdapter(Context context, List<Addon> addonList) {
        this.context = context;
        this.addonList = addonList;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_addon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.ckb_addon.setText(new StringBuilder(addonList.get(position).getName())
                .append(" +(" + context.getString(R.string.money_sign))
                .append(addonList.get(position).getExtraPrice())
                .append(")"));
        holder.ckb_addon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Common.addonList.add(addonList.get(position));
                    EventBus.getDefault().postSticky(new AddonEventChange(true, addonList.get(position)));
                }else{
                    Common.addonList.remove(addonList.get(position));
                    EventBus.getDefault().postSticky(new AddonEventChange(false, addonList.get(position)));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addonList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ckb_addon)
        CheckBox ckb_addon;

        Unbinder unbinder;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
