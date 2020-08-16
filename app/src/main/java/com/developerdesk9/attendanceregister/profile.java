package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profile extends AppCompatActivity {

    String sid;
    Toolbar toolbar;
    DatabaseReference student,batchdetails;
    TextView name;
    TextView branch;
    TextView enrollmentno;
    String mmname;
    ProgressDialog mDialog;
    String branchs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent=getIntent();
        sid=intent.getStringExtra("sid");

        mDialog=new ProgressDialog(this);

        toolbar=findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");

        name=findViewById(R.id.profile_name_et);
        branch=findViewById(R.id.profile_branch_et);
        enrollmentno=findViewById(R.id.profile_enroll_et);
        enrollmentno.setText("Enrollment No: "+sid);

        student=FirebaseDatabase.getInstance().getReference("Student");
        batchdetails=FirebaseDatabase.getInstance().getReference("Batchdetails");
        student.keepSynced(true);

        student.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mmname=dataSnapshot.child(sid).child("sname").getValue(String.class);
                name.setText(mmname);

                branchs=dataSnapshot.child(sid).child("batch").getValue(String.class);
                branch.setText("Branch :"+branchs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                update();
                return false;
            }
        });

    }


    public void update(){

        final AlertDialog.Builder myDialog=new AlertDialog.Builder(profile.this);
        LayoutInflater inflater=LayoutInflater.from(profile.this);
        View mview=inflater.inflate(R.layout.update_name,null);
        final AlertDialog dialog=myDialog.create();
        dialog.setView(mview);
        dialog.show();

        final TextView updatename=mview.findViewById(R.id.update_name_et);
        final Button update=mview.findViewById(R.id.update_bt);
        updatename.setText(mmname);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mname=updatename.getText().toString().trim();

                if (mname.isEmpty()){
                    updatename.setError("Please enter name");
                    return;
                }

                else {

                    mDialog.setTitle("Updating..");
                    mDialog.setMessage(mname);

                    batchdetails.child(branchs).child(sid).child("sname").setValue(mname);
                    student.child(sid).child("sname").setValue(mname).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                mDialog.dismiss();
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
                            }

                            else {
                                mDialog.dismiss();
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Failed.",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

    }
}
