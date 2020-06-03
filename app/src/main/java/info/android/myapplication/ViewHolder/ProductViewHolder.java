package info.android.myapplication.ViewHolder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import info.android.myapplication.Interface.itemClickListener;
import info.android.myapplication.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtproductname, txtproductdescription,txtproductprice;
    public ImageView imageView;
    public itemClickListener listener;


    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.products_image);
        txtproductname = (TextView) itemView.findViewById(R.id.products_name);
        txtproductdescription =(TextView) itemView.findViewById(R.id.products_description);
        txtproductprice =(TextView) itemView.findViewById(R.id.products_price);

    }

    public  void setItemClickListener(itemClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);
    }
}
