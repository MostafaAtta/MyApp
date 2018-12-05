package com.atta.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.myapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private EditText inputName, inputEmail, inputPassword;
    private Button btnRegister;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;

    ImageView profileImage;

    private TextView selectImage, cameraText, loginText;

    Bitmap bitmap;

    StorageReference imagePath;

    static final int REQUEST_IMAGE_CAPTURE = 2;

    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int EXTERNAL_STORAGE_REQUEST_CODE = 102;

    private String userId, imageUrl;

    boolean imageSelected;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setTitle(R.string.register_activity_title);

        inputName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);


        profileImage = (ImageView) findViewById(R.id.userImage);

        selectImage = (TextView) findViewById(R.id.upload_txt);
        cameraText = (TextView) findViewById(R.id.camera_txt);
        loginText = (TextView) findViewById(R.id.btnLoginScreen);

        selectImage.setOnClickListener(this);
        cameraText.setOnClickListener(this);
        loginText.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CAMERA,
                    CAMERA_REQUEST_CODE);
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    EXTERNAL_STORAGE_REQUEST_CODE);
            return;
        }

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

    }


    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }

    @Override
    public void onClick(View view) {

        if (view == btnRegister){

            if (validate()){

                imagePath.putFile(uri). addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(getApplicationContext(),"profile photo uploaded",Toast.LENGTH_SHORT).show();
                        imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "onSuccess: uri= "+ uri.toString());

                                imageUrl = uri.toString();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                        Log.e("Image upload", e.getMessage());


                    }
                });

                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                userId = mFirebaseDatabase.push().getKey();

                User user = new User(name, email, imageUrl);

                mFirebaseDatabase.child(userId).setValue(user);

                addUserChangeListener();

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });


            }
        }else if (view == selectImage){
            Intent intent = new Intent();

            intent.setType("image/*");

            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);


        }else if (view == cameraText){

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }


        }else if (view == loginText){


            Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();


        }
    }

    private boolean validate() {

        boolean valid = true;


        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (!imageSelected){

            Toast.makeText(getApplicationContext(),"Select a profile photo",Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (name.isEmpty() || name.equals(""))
        {
            inputName.setError("Enter your name");
            valid = false;

        }

        if (email.isEmpty() || email.equals(""))
        {
            inputEmail.setError("Enter your Email");
            valid = false;

        }


        if (password.isEmpty() || password.equals(""))
        {
            inputPassword.setError("Enter your Password");
            valid = false;

        }
        return valid;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                profileImage.setBackground(null);
                profileImage.setImageURI(uri);
                imagePath = FirebaseStorage.getInstance().getReference().child("profile").child(uri.getLastPathSegment());
                imageSelected = true;

            } catch (IOException e) {

                e.printStackTrace();
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            uri = data.getData();
            profileImage.setBackground(null);
            profileImage.setImageURI(uri);
            imagePath = FirebaseStorage.getInstance().getReference().child("profile").child(uri.getLastPathSegment());
            imageSelected = true;

        }
    }


    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.getUserName() + ", " + user.getEmail());


                // clear edit text
                inputEmail.setText("");
                inputName.setText("");
                inputPassword.setText("");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }
}
