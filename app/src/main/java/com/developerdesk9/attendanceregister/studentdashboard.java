package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class studentdashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String sid;
    private TextView nav_name;
    private TextView nav_username;
    Button proceed;
    DatabaseReference atrecord;
    DatabaseReference student;
    DatabaseReference studentsubdetails;
    View headerViw;
    ProgressDialog mDialog;
    AlertDialog dialog;
    String mPass,mConPass,oldpass;
    public TextView date_time,notify;
    Spinner spinnerstu_en_sublist;
    String item_sublist;
    private static final String FILE_NAME = "state.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentdashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        sid=intent.getStringExtra("sid");

        proceed=findViewById(R.id.proceed_btn);

        mDialog=new ProgressDialog(this);
        mDialog.setTitle("Please Wait");
        mDialog.setCanceledOnTouchOutside(false);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aa");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        date_time =findViewById(R.id.date_time);
        date_time.setText(formattedDate);






        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        */
        student= FirebaseDatabase.getInstance().getReference("Student");
        atrecord= FirebaseDatabase.getInstance().getReference("AttendanceRecord");
        studentsubdetails= FirebaseDatabase.getInstance().getReference("StudentSubDeatails");



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerViw=navigationView.getHeaderView(0);
        nav_name=(TextView)headerViw.findViewById(R.id.nav_name_stu);
        nav_username=(TextView)headerViw.findViewById(R.id.nav_username_stu);
        navigationView.setNavigationItemSelectedListener(this);

        student.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String musernam,mname;
                mname=dataSnapshot.child(sid).child("sname").getValue(String.class);
                nav_name.setText(mname);

                musernam=dataSnapshot.child(sid).child("sid").getValue(String.class);
                nav_username.setText(musernam);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        //spinner for sub enrolled
        spinnerstu_en_sublist=findViewById(R.id.spinnerselsub);
        final List<String> lstfacsub=new ArrayList<String>();
        lstfacsub.add("Select Subject");

        ArrayAdapter<String> facultysubarrayadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstfacsub);
        facultysubarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerstu_en_sublist.setAdapter(facultysubarrayadapter);

        spinnerstu_en_sublist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item_sublist=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        studentsubdetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDialog.show();
                for(DataSnapshot dsp :dataSnapshot.child(sid).getChildren()){
                    String name;
                    name=dsp.getKey();
                    lstfacsub.add(name);

                }
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mDialog.dismiss();

            }
        });


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calltonextpage();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert!");
            builder.setMessage("Do you want to Exit?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                    finish();
                    ActivityCompat.finishAffinity(studentdashboard.this);
                    System.exit(0);
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();  // Show the Alert Dialog box

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.studentdashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.app_dev) {
            startActivity(new Intent(getApplicationContext(),about_app.class));
            return true;
        }
        else if (id == R.id.logout_title_btn) {
            signout("9999");
            Intent logout=new Intent(getApplicationContext(),MainActivity.class);
            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logout);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile1) {
            Intent intenttt=new Intent(studentdashboard.this,profile.class);
            intenttt.putExtra("sid",sid);
            startActivity(intenttt);
        }
        else if (id == R.id.nav_fac_chnagepass) {
            forPassCustomDialog();
        } else if (id == R.id.nav_faclogout) {
            signout("9999");
            Intent logout=new Intent(getApplicationContext(),MainActivity.class);
            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logout);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void calltonextpage(){

        if (item_sublist=="Select Subject"){
            Toast.makeText(getApplicationContext(),"Please Select Valid Subject",Toast.LENGTH_SHORT).show();
        }

        else {

            Intent intent=new Intent(getApplicationContext(),viewattendancepage.class);
            intent.putExtra("subid",item_sublist);
            intent.putExtra("sid",sid);
            startActivity(intent);

        }

    }

    public void signout(String statevar){

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


    private void forPassCustomDialog()
    {
        final AlertDialog.Builder myDialog =new AlertDialog.Builder(studentdashboard.this);
        LayoutInflater inflater =LayoutInflater.from(studentdashboard.this);

        final View myview=inflater.inflate(R.layout.forgot_pass,null);

        final AlertDialog dialog=myDialog.create();
        dialog.setView(myview);

        final EditText forpass=myview.findViewById(R.id.forpass_et);
        final EditText forpass_confirm=myview.findViewById(R.id.forpass_confirm_et);
        final EditText old_psss=myview.findViewById(R.id.oldpass_et);
        Button resetpass_btn=myview.findViewById(R.id.forpass_bt);

        resetpass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldpass=old_psss.getText().toString();
                mPass=forpass.getText().toString().trim();
                mConPass=forpass_confirm.getText().toString().trim();
                if(TextUtils.isEmpty(mPass))
                {
                    forpass.setError("Please Enter Password..!");
                    return;
                }
                else if (mPass.length()<6){
                    forpass.setError("Password is too short..");
                    return;
                }
                else if (mPass.equals(mConPass)){
                    mDialog.setMessage("Please Wait...");
                    mDialog.setTitle("Password Updating..");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    checkoldcredential();
                }

                else {
                    forpass_confirm.setError("Password didn't Match..");
                }

            }
        });

        dialog.show();
    }

    public  void checkoldcredential(){

        student.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dbpassword=dataSnapshot.child(sid).child("spass").getValue(String.class);

                if (dbpassword.equals(oldpass)){
                    mDialog.dismiss();
                    finalchange();
                }
                else {
                    mDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(studentdashboard.this);
                    builder.setTitle("Alert !");
                    builder.setMessage("Wrong old Password..");
                    builder.setCancelable(false);

                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void finalchange(){

        student.child(sid).child("spass").setValue(mConPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Password Change Successfully.",Toast.LENGTH_SHORT).show();
                    signout("9999");
                    Toast.makeText(getApplicationContext(),"Please Login Again",Toast.LENGTH_SHORT).show();
                    Intent logout=new Intent(getApplicationContext(),MainActivity.class);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logout);
                }

                else {
                    mDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(studentdashboard.this);
                    builder.setTitle("Failed..");
                    builder.setMessage("Either you don't have Access to change password or Something went wrong contact Admin. ");
                    builder.setCancelable(false);

                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }
        });
    }
}
