package info.android.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.android.myapplication.model.Users;
import info.android.myapplication.prevelant.Prevelant;
import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {

    private EditText inputnumber,inputpassword;
    private Button loginbutton;
    private ProgressDialog loadingbar;
    private String parentDbName = "Users";
    private CheckBox checkBoxRememberme;

    private TextView Admin, NotAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputnumber = (EditText) findViewById(R.id.login_phone_number_input);
        inputpassword =(EditText) findViewById(R.id.login_password_input);
        loginbutton = (Button) findViewById(R.id.main_login_btn);
        loadingbar = new ProgressDialog(this);
        checkBoxRememberme= (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        Admin =(TextView) findViewById(R.id.admin_panel_link);
        NotAdmin=(TextView) findViewById(R.id.not_admin_panel_link);


        Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbutton.setText("Login Admin");
                Admin.setVisibility(View.INVISIBLE);
                NotAdmin.setVisibility(View.VISIBLE);
                parentDbName= "Admins";
            }
        });
        NotAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbutton.setText("Login User");
                Admin.setVisibility(View.VISIBLE);
                NotAdmin.setVisibility(View.INVISIBLE);
                parentDbName="Users";
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });



    }

    private void loginUser() {
        String phone = inputnumber.getText().toString();
        String password = inputpassword.getText().toString();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please write your phone...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your password...",Toast.LENGTH_SHORT).show();
        }else{
            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Please wait, while we are checking the credentials.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();


            AccesToAccount(phone,password);
        }
            }


    private void AccesToAccount (final String phone, final String password){

        if(checkBoxRememberme.isChecked()){
            Paper.book().write(Prevelant.UserPhoneKey,phone);
            Paper.book().write(Prevelant.UserPasswordKey,password);
        }



              final DatabaseReference RootRef;
              RootRef= FirebaseDatabase.getInstance().getReference();

              RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                  {
                      if (dataSnapshot.child(parentDbName).child(phone).exists())
                      {
                          Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                          if (usersData.getPhone().equals(phone))
                          {
                              if (usersData.getPassword().equals(password))
                              {


                                  if(parentDbName.equals("Admins")) {
                                      Toast.makeText(loginActivity.this, "Welcome Admin , You are logged in Successfully...", Toast.LENGTH_SHORT).show();
                                      loadingbar.dismiss();

                                      Intent intent = new Intent(loginActivity.this, AdminCategoryActivity.class);
                                      startActivity(intent);

                                  }else if(parentDbName.equals("Users")){
                                      Toast.makeText(loginActivity.this, "logged in Successfully...", Toast.LENGTH_SHORT).show();
                                      loadingbar.dismiss();

                                      Intent intent = new Intent(loginActivity.this, HomeActivity.class);
                                      Prevelant.currentOnlineUser = usersData;
                                      startActivity(intent);
                                  }

                              }
                              else
                              {
                                  loadingbar.dismiss();
                                  Toast.makeText(loginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                              }
                          }
                      }
                      else
                      {
                          Toast.makeText(loginActivity.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                          loadingbar.dismiss();
                      }
                  }
                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });


    }
}
