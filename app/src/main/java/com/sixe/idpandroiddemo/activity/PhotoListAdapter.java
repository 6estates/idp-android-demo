package com.sixe.idpandroiddemo.activity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.sixe.idpandroiddemo.R;

import java.util.List;

/**
 * adapter of photo list
 */
public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.PhotoListViewHolder> {

    private List<String> mList;
    private Context mContext;

    public PhotoListAdapter(Context context, List<String> list) {
        mList = list;
        mContext = context;

    }

    @NonNull
    @Override
    public PhotoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_photo, parent, false);
        return new PhotoListViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoListViewHolder holder, int position) {
        holder.ivPhoto.setImageBitmap(BitmapFactory.decodeFile((mList.get(position))));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class PhotoListViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;

        public PhotoListViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
        }
    }

}
