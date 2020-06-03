package info.android.myapplication.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import info.android.myapplication.Interface.itemClickListener;
import info.android.myapplication.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView cartproductname ,cartproductprice,cartproductquantity;
    public itemClickListener listener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        cartproductname = (TextView) itemView.findViewById(R.id.cart_product_name);
        cartproductprice = (TextView) itemView.findViewById(R.id.cart_product_price);
        cartproductquantity = (TextView) itemView.findViewById(R.id.cart_product_quantity);
    }

    public void setListener(itemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
            listener.onClick(v,getAdapterPosition(),false);
    }
}
