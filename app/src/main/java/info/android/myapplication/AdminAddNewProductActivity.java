package info.android.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
    private String CategoryName,Description,Price ,Pname,savecuurentdata,savecurrenttime;

    private Button addproduct;
    private EditText inputproductname, inputproductdescription,inputproductprice;
    private ImageView inputproductimage;
    public static final int  gallerypick=1;
    private Uri imageuri;
    private String productrandomkey, downloadimageurl;
    private StorageReference productimageref;
    private DatabaseReference productsref;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

       CategoryName = getIntent().getExtras().get("category").toString();
       productimageref = FirebaseStorage.getInstance().getReference().child("Product Images");
       productsref = FirebaseDatabase.getInstance().getReference().child("Products");


       addproduct = (Button) findViewById(R.id.add_new_product);
       inputproductname =(EditText) findViewById(R.id.product_name);
       inputproductdescription=(EditText) findViewById(R.id.product_description);
       inputproductprice= (EditText) findViewById(R.id.product_price);
       inputproductimage=(ImageView) findViewById(R.id.select_product_image);
        loadingbar = new ProgressDialog(this);


       inputproductimage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               openGallery();
           }
       });
       addproduct.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Validateproductdata();
           }
       });
    }



    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,gallerypick);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallerypick && resultCode==RESULT_OK && data!=null){
               imageuri = data.getData();
               inputproductimage.setImageURI(imageuri);
        }
    }



    private void Validateproductdata() {

        Description= inputproductdescription.getText().toString();
        Price= inputproductprice.getText().toString();
        Pname = inputproductname.getText().toString();

        if(imageuri == null){

            Toast.makeText(this,"Product image is obligatory..",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(Description)){
            Toast.makeText(this,"Product description is obligatory..",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(Price)){
            Toast.makeText(this,"Product price is obligatory..",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(Pname)){
            Toast.makeText(this,"Product name is obligatory..",Toast.LENGTH_SHORT).show();

        }else{
            StoreProductInformation();
        }

    }

    private void StoreProductInformation() {

        loadingbar.setTitle("Add new Product");
        loadingbar.setMessage("Dear Admin, Please wait, while we are adding the new product.");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentdata = new SimpleDateFormat("MMM dd ,yyyy");
        savecuurentdata = currentdata.format(calendar.getTime());

        SimpleDateFormat currenttime = new SimpleDateFormat("HH: mm :ss  a");
        savecurrenttime = currenttime.format(calendar.getTime());

        productrandomkey = savecuurentdata+savecurrenttime;



        final StorageReference filepath= productimageref.child(imageuri.getLastPathSegment()+ productrandomkey + ".jpg");
        final UploadTask uploadTask = filepath.putFile(imageuri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this,"Error :"+ message ,Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AdminAddNewProductActivity.this,"Image Uploaded Successfully" ,Toast.LENGTH_SHORT).show();

                Task<Uri> urltask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {


                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadimageurl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();


                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){

                            downloadimageurl= task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this,"Product image url save to database successfully..." ,Toast.LENGTH_SHORT).show();
                              saveproductinfotodatabase();
                        }

                    }
                });

            }
        });
    }




    private void saveproductinfotodatabase() {
        HashMap<String,Object> productmap =  new HashMap<>();
        productmap.put("pid",productrandomkey);
        productmap.put("date",savecuurentdata);
        productmap.put("time",savecurrenttime);
        productmap.put("description",Description);
        productmap.put("image",downloadimageurl);
        productmap.put("category",CategoryName);
        productmap.put("price",Price);
        productmap.put("pname",Pname);

        productsref.child(productrandomkey).updateChildren(productmap)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Intent intent = new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                    startActivity(intent);

                    loadingbar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();

                }else{
                    loadingbar.dismiss();
                    String message= task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this," Error:" + message,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
