package com.roket.shmtzh.uchat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roket.shmtzh.uchat.R;
import com.roket.shmtzh.uchat.model.Message;
import com.roket.shmtzh.uchat.utils.DateUtils;
import com.roket.shmtzh.uchat.utils.UiUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shmtzh on 7/2/16.
 */
public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> items;
    private Context context;
    private final int MESSAGE = 0, IMAGE = 1, LOCATION = 2;

    public FeedRecyclerViewAdapter(List<Message> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {

        switch (items.get(position).getType()) {
            case "message":
                return MESSAGE;
            case "location":
                return LOCATION;
            case "image":
                return IMAGE;
            default:
                return MESSAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//
//
//
        switch (viewType) {
            case MESSAGE:
                View v1 = inflater.inflate(R.layout.viewholder_text, viewGroup, false);
                viewHolder = new TextViewHolder(v1);
                break;
            case IMAGE:
                View v2 = inflater.inflate(R.layout.viewholder_image, viewGroup, false);
                viewHolder = new ImageViewHolder(v2);
                break;
            case LOCATION:
                View v3 = inflater.inflate(R.layout.viewholder_image, viewGroup, false);
                viewHolder = new LocationViewHolder(v3);
                break;
            default:
                View v4 = inflater.inflate(R.layout.viewholder_image, viewGroup, false);
                viewHolder = new LocationViewHolder(v4);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case MESSAGE:
                TextViewHolder vh1 = (TextViewHolder) viewHolder;
                configureTextViewHolder(vh1, position);
                break;
            case IMAGE:
                ImageViewHolder vh2 = (ImageViewHolder) viewHolder;
                configureImageViewHolder(vh2, position);
                break;
            case LOCATION:
                LocationViewHolder vh3 = (LocationViewHolder) viewHolder;
                configureLocationViewHolder(vh3, position);
                break;
            default:
                ImageViewHolder vh4 = (ImageViewHolder) viewHolder;
                configureImageViewHolder(vh4, position);
                break;
        }
    }

    private void configureTextViewHolder(TextViewHolder vh1, int position) {
        Message message = items.get(position);
        if (message != null) {
            vh1.setText(message.getMessage());
            vh1.setDate(message.getDate());
        }
    }

    private void configureImageViewHolder(ImageViewHolder vh2, int position) {
        Message image =  items.get(position);
        Picasso.with(context).load("file://" + Uri.parse(image.getMessage())).config(Bitmap.Config.RGB_565).fit().into(vh2.getImageView());
        vh2.setDate(image.getDate());
    }


    private void configureLocationViewHolder(LocationViewHolder vh2, int position) {
        Message image =  items.get(position);
        Picasso.with(context).load(image.getMessage()).into(vh2.getImageView());
        vh2.setDate(image.getDate());
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView date;

        public ImageViewHolder(View v) {
            super(v);
            image = UiUtils.findView(v, R.id.image);
            date = UiUtils.findView(v, R.id.date);
        }

        public ImageView getImageView() {
            return image;
        }

        public void setImageView(Bitmap im) {
            image.setImageBitmap(im);
        }

        public void setDate(long dt) {
            date.setText(DateUtils.getFormattedDate(dt));
        }

    }


    public class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        TextView date;

        public TextViewHolder(View v) {
            super(v);
            message = UiUtils.findView(v, R.id.message);
            date = UiUtils.findView(v, R.id.date);
        }

        public void setText(String msg) {
            message.setText(msg);
        }

        public void setDate(long dt) {
            date.setText(DateUtils.getFormattedDate(dt));
        }


    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        ImageView image;

        public LocationViewHolder(View v) {
            super(v);
            date = UiUtils.findView(v, R.id.date);
            image = UiUtils.findView(v, R.id.image);
        }

        public ImageView getImageView() {
            return image;
        }


        public void setDate(long dt) {
            date.setText(DateUtils.getFormattedDate(dt));
        }
    }


}