package com.example.my_boss.questrip;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.ArrayList;


/**
 * Created by takayayuuki on 2016/10/24.
 */

public final class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private ArrayList<Bitmap> mItemBitmap = new ArrayList();

    public RecyclerAdapter(final Context context, final ArrayList<Bitmap> itemList) {
        mContext = context;
        mItemBitmap = itemList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ImageView imageItem = (ImageView) holder.itemView.findViewById(R.id.image_View);
        imageItem.setImageBitmap(mItemBitmap.get(position));
    }


    @Override
    public int getItemCount() {
        return mItemBitmap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImageView;
        public Bitmap mItem;

        private ViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.image_View);
        }
    }
}
