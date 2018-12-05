package com.atta.myapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.atta.myapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    String userEmail;

    User user;

    EditText nameTxt, emailTxt;

    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        nameTxt = findViewById(R.id.user_name);
        emailTxt = findViewById(R.id.user_email);
        profileImage = findViewById(R.id.profile_image);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userEmail = auth.getCurrentUser().getEmail();

        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){

                    user = datas.getValue(User.class);

                    nameTxt.setText(user.getUserName());
                    emailTxt.setText(user.getEmail());

                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference storageReference = storage.getReferenceFromUrl(user.getImage());


                    Picasso.get().load(user.getImage()).into(profileImage);

                    Toast.makeText(ProfileActivity.this, user.getUserName(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
