package com.firebase.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.firebaseapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class homeActivity extends AppCompatActivity {

    FloatingActionButton fabAddButt;
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private String post_key;
    private String name;
    private String description;

    //FIREBASE
    private FirebaseAuth Auth;
    private DatabaseReference dataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Firebase App");

        Auth = FirebaseAuth.getInstance();
        FirebaseUser User = Auth.getCurrentUser();
        String uid = User.getUid();
        dataBase = FirebaseDatabase.getInstance().getReference().child("All Data").child(uid);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        fabAddButt = findViewById(R.id.fabAddButt);
        fabAddButt.setOnClickListener(view -> {
            AddData();
        });
    }

    private void AddData(){
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View inputView = inflater.inflate(R.layout.inputlayout, null);

        inputDialog.setView(inputView);
        AlertDialog dialog = inputDialog.create();
        dialog.setCancelable(false);

        EditText nameIn = inputView.findViewById(R.id.nameIn);
        EditText descIn = inputView.findViewById(R.id.descIn);
        Button saveBut = inputView.findViewById(R.id.saveBut);
        saveBut.setOnClickListener(view -> {
            String name = nameIn.getText().toString().trim();
            String desc = descIn.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {nameIn.setError("Required Field...");}
            if (TextUtils.isEmpty(desc)) {descIn.setError("Required Field...");}
            String id = dataBase.push().getKey();
            String date = DateFormat.getDateInstance().format(new Date());
            Data data = new Data(name, desc, id, date);
            dataBase.child(id).setValue(data);
            Toast.makeText(this, "Data Saved to Firebase", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });
        Button canBut = inputView.findViewById(R.id.canBut);
        canBut.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(dataBase,Data.class)
                .build();
        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i, @NonNull Data data) {
                myViewHolder.setName(data.getName());
                myViewHolder.setDate(data.getDate());
                myViewHolder.setDescription(data.getDescription());
                myViewHolder.myView.setOnClickListener(view -> {
                    post_key = getRef(i).getKey();
                    name = data.getName();
                    description = data.getDescription();
                    updateData();
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.itemlayoutdesign,parent,false);
                return new MyViewHolder(view);
            }
        };
            adapter.startListening();
            recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }
        public void setId(String id){
            TextView Vid = myView.findViewById(R.id.idOut);
            Vid.setText(id);
        }
        public void setDescription(String description){
            TextView Vdesc = myView.findViewById(R.id.descOut);
            Vdesc.setText(description);
        }
        public void setName(String name){
            TextView Vname = myView.findViewById(R.id.nameOut);
            Vname.setText(name);
        }
        public void setDate(String date){
            TextView Vdate = myView.findViewById(R.id.dateout);
            Vdate.setText(date);
        }

    }

    public void updateData(){
            AlertDialog.Builder updateDialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View updateView = inflater.inflate(R.layout.updatelayout, null);
            updateDialog.setView(updateView);

            AlertDialog dialog = updateDialog.create();
            EditText mName = updateView.findViewById(R.id.nameUp);
            EditText mDesc = updateView.findViewById(R.id.descUp);
            mName.setText(name);
            mName.setSelection(name.length());
            mDesc.setText(description);
            mDesc.setSelection(description.length());
            Button delete = updateView.findViewById(R.id.delBut);
            Button update = updateView.findViewById(R.id.updateBut);
            update.setOnClickListener(view -> {
                name = mName.getText().toString().trim();
                description = mDesc.getText().toString().trim();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(name, description,post_key,mDate);
                dataBase.child(post_key).setValue(data);
                dialog.dismiss();
            });
            delete.setOnClickListener(view -> {
                dataBase.child(post_key).removeValue();
                dialog.dismiss();
            });

            dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.logout: Auth.signOut();
            startActivity(new Intent(homeActivity.this, MainActivity.class));
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}