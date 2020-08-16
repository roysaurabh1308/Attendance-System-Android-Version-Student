package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    EditText username;
    TextInputLayout password;
    public Button loginbtn;
    String userid,pass;
    DatabaseReference mreference;
    ProgressDialog mDialog;
    private static long back_pressed;
    private static final String FILE_NAME = "state.txt";
    String state;
    String falg=null,uuid=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        redirectstate();

        mDialog=new ProgressDialog(this);

        username = findViewById(R.id.username_et);
        password = findViewById(R.id.password_et);
        loginbtn= findViewById(R.id.loginButton);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userid = username.getText().toString().trim();
                pass = password.getEditText().getText().toString().trim();

                if(userid.isEmpty()){
                    username.setError("Please Enter Username");
                    return;
                }
                else if (pass.isEmpty()){
                    password.setError("Enter Password");
                    return;
                }

                mDialog.setTitle("Authenticating...");
                mDialog.setMessage(userid);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                verifyusercredential();
            }

        });
    }


    public void verifyusercredential(){

        mreference = FirebaseDatabase.getInstance().getReference("Student");

        mreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String  dbpassword;

                    dbpassword = dataSnapshot.child(userid).child("spass").getValue(String.class);
                    verify(dbpassword);
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void verify(String dbpassword){

        if (pass.equals(dbpassword)) {
            mDialog.dismiss();
            savests("5"+userid);
            Intent intent =new Intent(this,studentdashboard.class);
            intent.putExtra("sid",userid);
            startActivity(intent);
            finish();

        }
        else if(! pass.equals(dbpassword)){
            Toast.makeText(getApplicationContext(),"UserId or Password is Incorrect", Toast.LENGTH_LONG).show();
            mDialog.dismiss();

        }
    }

    public void savests(String statevar ) {
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(statevar.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void redirectstate(){

        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }


            state=sb.toString();

            onstsbasicredirectfinalcheck(state);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void onstsbasicredirectfinalcheck(String st){


        try {
            falg=st.substring(0,1);
            uuid=st.substring(1).trim().toLowerCase();
        }
        catch (Exception e){}

        if (falg.equals("5")){
            Intent intenttt=new Intent(MainActivity.this,studentdashboard.class);
            intenttt.putExtra("sid",uuid);
            startActivity(intenttt);
            finish();
        }


    }
}
