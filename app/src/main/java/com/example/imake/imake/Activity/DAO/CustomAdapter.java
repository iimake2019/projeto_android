package com.example.imake.imake.Activity.DAO;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imake.R;
import com.example.imake.imake.Activity.Entidades.Foto;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class CustomAdapter extends BaseAdapter{
    private Context mContext;
    private List<Foto> uploads;

    public CustomAdapter(Context c, List<Foto> uploads ) {
        mContext = c;
        this.uploads = uploads;
    }

    @Override
    public int getCount() {
        return uploads.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.image_item, null);
            Foto upload = uploads.get(position);

            TextView textAutor = (TextView) grid.findViewById(R.id.txtAutor);
            textAutor.setText(upload.getNomeAutor());

            TextView textCategoria = grid.findViewById(R.id.txtCategoria);
            textCategoria.setText(upload.getCategoria());

            ImageView imageView = (ImageView) grid.findViewById(R.id.image_view_upload);
            Picasso.get()
                    .load(upload.getUrl())
                    .fit()
                    .centerCrop()
                    .into(imageView);
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}