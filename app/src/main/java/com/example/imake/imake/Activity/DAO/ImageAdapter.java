package com.example.imake.imake.Activity.DAO;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imake.R;
import com.example.imake.imake.Activity.Entidades.Foto;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Foto> mUploads;
    private OnItemClickListener mListener;

    public ImageAdapter(Context context, List<Foto> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Foto uploadCurrent = mUploads.get(position);
        holder.txtAutor.setText("Make Up:  "+uploadCurrent.getNomeAutor());
        holder.txtCategoria.setText("Categoria:  "+uploadCurrent.getCategoria());
        Picasso.get()
                .load(uploadCurrent.getUrl())
                .fit()
                .centerCrop()
                .into(holder.imgageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView txtCategoria;
        public TextView txtAutor;
        public ImageView imgageView;
        public ImageView imgFavorito;

        public ImageViewHolder(@NonNull final View itemView) {
            super(itemView);

            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtAutor = itemView.findViewById(R.id.txtAutor);
            imgageView = itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mListener!=null){
                int position = getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Selecione a opção desejada:");
            MenuItem favoritar = contextMenu.add(Menu.NONE, 1, 1, "Adicionar aos favoritos");
            MenuItem desejos = contextMenu.add(Menu.NONE, 2,2, "Adicionar à lista de desejos");
            MenuItem perfil = contextMenu.add(Menu.NONE, 3, 3, "Visitar perfil do autor");

            favoritar.setOnMenuItemClickListener(this);
            desejos.setOnMenuItemClickListener(this);
            perfil.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(mListener!=null){
                int position = getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION){
                    switch (menuItem.getItemId()){
                        case 1:
                            mListener.onFavoritarClick(position);
                            return true;
                        case 2:
                            mListener.onDesejoClick(position);
                            return true;

                        case 3:
                            mListener.onPerfilClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);

        void onFavoritarClick(int position);

        void onDesejoClick(int position);

        void onPerfilClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
