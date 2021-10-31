package com.jaymar.localcloudapp.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jaymar.localcloudapp.GUI.FileOpenActivity;
import com.jaymar.localcloudapp.R;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RViewHolder> {

    String[] title;
    int[] icons;
    Context context;
    public RecyclerViewAdapter(Context context, String[] title, int[] icons ){
        this.title = title;
        this.icons = icons;
        this.context = context;
    }


    @NonNull
    @Override
    public RViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_row, parent, false);

        return new RViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.filename.setText(title[position]);
        holder.icon.setImageResource(icons[position]);
        if(!title[position].toLowerCase().contains("empty"))
            holder.layout.setOnClickListener(new View.OnClickListener() {
                final int pos = position;
                @Override
                public void onClick(View v) {
                    Intent fileOpen = new Intent(context, FileOpenActivity.class);
                    fileOpen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    fileOpen.putExtra("image",icons[position]);
                    fileOpen.putExtra("filename",title[pos]);
                    fileOpen.putExtra("directory",DataParser.path);
                    context.startActivity(fileOpen);
                }
            });
    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    public class RViewHolder extends RecyclerView.ViewHolder{

        TextView filename;
        ImageView icon;
        ConstraintLayout layout;
        public RViewHolder(@NonNull View itemView){
            super(itemView);
            filename = itemView.findViewById(R.id.filename_title);
            icon = itemView.findViewById(R.id.icon_image);
            layout = itemView.findViewById(R.id.row_layout);
        }
    }
}
