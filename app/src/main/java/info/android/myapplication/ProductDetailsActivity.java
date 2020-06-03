package info.android.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import info.android.myapplication.model.Products;
import info.android.myapplication.prevelant.Prevelant;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCartButton;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice, productDescription, productName;
    private String productID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);
        addToCartButton = (Button) findViewById(R.id.pd_add_to_cart_button);
        
        getProductDetails(productID);
        
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingproductlist();
            }
        });


    }

    private void addingproductlist() {

        String savecurrentdata,savecurrenttime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentdata = new SimpleDateFormat("MMM dd,yyyy");
        savecurrentdata= currentdata.format(calendar.getTime());
        SimpleDateFormat currenttime = new SimpleDateFormat("HH :mm :ss a");
        savecurrenttime = currenttime.format(calendar.getTime());


        final DatabaseReference cartlisrref = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final  HashMap<String,Object> cartmap = new HashMap<>();
        cartmap.put("pid",productID);
        cartmap.put("pname",productName.getText().toString());
        cartmap.put("price",productPrice.getText().toString());
        cartmap.put("date",savecurrentdata);
        cartmap.put("time",savecurrenttime);
        cartmap.put("quantity",numberButton.getNumber());
        cartmap.put("discount","");

        cartlisrref.child("User View").child(Prevelant.currentOnlineUser.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartmap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            cartlisrref.child("Admin View").child(Prevelant.currentOnlineUser.getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cartmap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ProductDetailsActivity.this,"Add to Cart List",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                        }
                    }
                });

    }


    private void getProductDetails(String productID)
    {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    }

