package com.example.trackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference GroupDB_ref;
    private DatabaseReference myUserDB_ref;

    private Spinner groupSpinner;
    private Button cancelButton;

    private Button newgroupButton;
    private EditText newgroupnameText;

    private String mygroupname;;
    private int mygroup_index;

    private ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        GroupDB_ref=FirebaseDatabase.getInstance().getReference("Groups");
        myUserDB_ref=FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("group");

        cancelButton = (Button) findViewById(R.id.grp_backbutton);
        cancelButton.setOnClickListener(this);

        logout= (ImageView) findViewById(R.id.logoutbtn) ;
        logout.setOnClickListener(this);

        newgroupButton = (Button) findViewById(R.id.grp_createnew);
        newgroupButton.setOnClickListener(this);
        newgroupnameText = (EditText) findViewById(R.id.grp_newname);

        groupSpinner = (Spinner) findViewById(R.id.grp_spinner);

        groupSpinner_populate();


        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                String selected_group_name=item.toString();
                if (item != null) {
                    Toast.makeText(GroupActivity.this, "You Have been joined to "+selected_group_name+" Successfully",
                            Toast.LENGTH_SHORT).show();
                    myUserDB_ref.setValue(selected_group_name);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void groupSpinner_populate() {

        myUserDB_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mygroupname=snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        GroupDB_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final List<String> GroupList = new ArrayList<String>();
                for (DataSnapshot groupSnapshot: snapshot.getChildren()){
                    String group = groupSnapshot.getValue(String.class);
                    if (group!=null){
                        GroupList.add(group);
                        if(group.equals( mygroupname)){
                            mygroup_index=GroupList.size();
                        }
                    }
                    ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(GroupActivity.this, android.R.layout.simple_spinner_item, GroupList);
                    groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    groupSpinner.setAdapter(groupAdapter);
                    if(mygroup_index>0){
                        groupSpinner.setSelection(mygroup_index-1);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.grp_backbutton:
                finish();
                break;


            case R.id.logoutbtn:
                finish();
                startActivity(new Intent(GroupActivity.this,MainActivity.class));
                break;


            case R.id.grp_createnew:
                String groupname= newgroupnameText.getText().toString().trim();
                String key = GroupDB_ref.push().getKey();
                Map<String, Object> map = new HashMap<>();
                map.put(key, groupname);
                GroupDB_ref.updateChildren(map);
                groupSpinner_populate();
                break;
        }

    }
}