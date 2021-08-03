package com.example.myrestaurant.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrestaurant.Common.Common;
import com.example.myrestaurant.EventBus.FoodListEvent;
import com.example.myrestaurant.FoodListActivity;
import com.example.myrestaurant.Interface.IOnRecyclerViewClickListener;
import com.example.myrestaurant.Model.Category;
import com.example.myrestaurant.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<Category> categoryList;
    List<Category> categoryListOld;
    private MyCategoryAdapter.SelectedCategory selectedCategory;

    public MyCategoryAdapter(Context context, List<Category> categoryList, SelectedCategory selectedCategory){
        this.context = context;
        this.categoryList = categoryList;
        this.categoryListOld = categoryList;
        this.selectedCategory = selectedCategory;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Picasso.get().load(categoryList.get(position).getImage()).into(holder.img_category);
        holder.txt_category.setText(categoryList.get(position).getName());
        holder.setListener(new IOnRecyclerViewClickListener() {
            @Override
            public void onCLick(View view, int position) {
                EventBus.getDefault().postSticky(new FoodListEvent(true, categoryList.get(position)));
                context.startActivity(new Intent(context, FoodListActivity.class));
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (categoryList.size() == 1) {
            return Common.DEFAULT_COLUMN_COUNT;
        } else {
            if (categoryList.size() % 2 == 0) {
                return Common.DEFAULT_COLUMN_COUNT;
            } else {
                return (position > 1 && position == categoryList.size() - 1) ? Common.FULL_WIDTH_COLUMN : Common.DEFAULT_COLUMN_COUNT;
            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    categoryList = categoryListOld;
                } else {
                    List<Category> list = new ArrayList<>();
                    for (Category category : categoryListOld) {
                        if (category.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(category);
                        }
                    }
                    categoryList = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = categoryList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                categoryList = (List<Category>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_category)
        ImageView img_category;

        @BindView(R.id.txt_category)
        TextView txt_category;

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        Unbinder unbinder;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.onCLick(v, getAdapterPosition());
            selectedCategory.selectedCategory(categoryList.get(getAdapterPosition()));
        }

    }

    public interface SelectedCategory {
        void selectedCategory(Category categoryModel);

    }
}
