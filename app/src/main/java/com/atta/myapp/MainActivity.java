package com.atta.myapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.atta.myapp.model.Order;
import com.atta.myapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = MainActivity.class.getSimpleName();


    Button profileBtn, addOrderBtn, viewOrdersBtn;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    StorageReference imagePath;
    Uri uri;

    String userEmail, imageUrl, orderId;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle(R.string.main_activity_title);

        profileBtn = findViewById(R.id.profile_btn);
        profileBtn.setOnClickListener(this);
        addOrderBtn = findViewById(R.id.add_order);
        addOrderBtn.setOnClickListener(this);
        viewOrdersBtn = findViewById(R.id.view_orders);
        viewOrdersBtn.setOnClickListener(this);


        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("orders");

        getUser();

    }

    private void getUser() {

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userEmail = auth.getCurrentUser().getEmail();

        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){

                    user = data.getValue(User.class);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_logout) {

            auth.signOut();
            //Auth.GoogleSignInApi.signOut(apiClient);

            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == profileBtn){

            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);

        }else if (view == addOrderBtn){


            final Dialog myDialog = new Dialog(this);


            Button addOrder;
            ImageView uploadImage;
            final EditText orderTitle, orderDescription;
            myDialog.setContentView(R.layout.custom_popup);

            orderTitle = myDialog.findViewById(R.id.order_title);
            orderDescription = myDialog.findViewById(R.id.order_dis);
            uploadImage = myDialog.findViewById(R.id.uploadImage);


            addOrder = myDialog.findViewById(R.id.add);
            addOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addOrder(orderTitle.getText().toString(), orderDescription.getText().toString(), myDialog);
                }
            });

            uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();

                    intent.setType("image/*");

                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);
                }
            });

            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
        }if (view == viewOrdersBtn){

            Intent intent = new Intent(MainActivity.this,OrdersActivity.class);
            startActivity(intent);

        }
    }

    private void addOrder(final String orderTitleText, final String orderDes, final Dialog myDialog) {

        imagePath.putFile(uri). addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getApplicationContext(),"order photo uploaded",Toast.LENGTH_SHORT).show();
                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: uri= "+ uri.toString());

                        imageUrl = uri.toString();


                        Order order = new Order(user.getUserName(),user.getEmail(), imageUrl, orderTitleText, orderDes);


                        orderId = mFirebaseDatabase.push().getKey();


                        mFirebaseDatabase.child(orderId).setValue(order);

                        addOrderChangeListener();

                        myDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(),e.getMessage() + " Try again",Toast.LENGTH_SHORT).show();

                Log.e("Image upload failed", e.getMessage());


            }
        });

    }

    private void addOrderChangeListener() {
        mFirebaseDatabase.child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);

                // Check for null
                if (order == null) {
                    Log.e(TAG, "Order data is null!");
                    return;
                }



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read order", error.toException());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();


            imagePath = FirebaseStorage.getInstance().getReference().child("orders").child(uri.getLastPathSegment());

        }
    }
}
