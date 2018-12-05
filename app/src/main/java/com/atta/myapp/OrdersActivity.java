package com.atta.myapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.myapp.model.Order;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class OrdersActivity extends AppCompatActivity {


    private static final String TAG = OrdersActivity.class.getSimpleName();

    private RecyclerView mOrdersRV;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Order, OrderViewHolder> mOrdersRVAdapter;

    private FirebaseAuth auth;

    String userEmail, imageUrl;

    StorageReference imagePath;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userEmail = auth.getCurrentUser().getEmail();

        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("orders");
        mDatabase.keepSynced(true);

        mOrdersRV = (RecyclerView) findViewById(R.id.myRecycleView);

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
        Query ordersQuery = ordersRef.orderByChild("email").equalTo(userEmail);


        mOrdersRV.hasFixedSize();
        mOrdersRV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions ordersOptions = new FirebaseRecyclerOptions.Builder<Order>().setQuery(ordersQuery, Order.class).build();

        mOrdersRVAdapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(ordersOptions) {
            @Override
            protected void onBindViewHolder(OrderViewHolder holder, final int position, final Order model) {
                holder.setTitle(model.getOrderTitle());
                holder.setDesc(model.getOrderDescription());
                holder.setImage(getBaseContext(), model.getOrderImage());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String key = mOrdersRVAdapter.getRef(position).getKey();

                        updateOrder(model, key);

                    }
                });

                holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String key = mOrdersRVAdapter.getRef(position).getKey();

                        deleteOrder(key);

                    }
                });
            }

            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                return new OrdersActivity.OrderViewHolder(view);
            }
        };

        mOrdersRV.setAdapter(mOrdersRVAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        mOrdersRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mOrdersRVAdapter.stopListening();


    }



    public static class OrderViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView deleteImage;
        public OrderViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            deleteImage = mView.findViewById(R.id.delete_image);


        }
        public void setTitle(String title){
            TextView orderTitle = (TextView)mView.findViewById(R.id.title_tv);
            orderTitle.setText(title);
        }
        public void setDesc(String desc){
            TextView orderDesc = (TextView)mView.findViewById(R.id.desc_tv);
            orderDesc.setText(desc);
        }
        public void setImage(Context ctx, String image){
            ImageView orderImage = (ImageView) mView.findViewById(R.id.image_view);
            Picasso.get().load(image).into(orderImage);
        }

    }

    public void updateOrder(Order order, final String key){

        final Dialog myDialog = new Dialog(this);


        Button addOrder;
        RelativeLayout uploadImage;
        final EditText orderTitle, orderDescription;
        myDialog.setContentView(R.layout.custom_popup);

        orderTitle = myDialog.findViewById(R.id.order_title);
        orderDescription = myDialog.findViewById(R.id.order_dis);
        uploadImage = myDialog.findViewById(R.id.img_layout);
        uploadImage.setVisibility(View.GONE);

        orderTitle.setText(order.getOrderTitle());
        orderDescription.setText(order.getOrderDescription());


        addOrder = myDialog.findViewById(R.id.add);
        addOrder.setText("Update Order");
        addOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //updateOrder(orderTitle.getText().toString(), orderDescription.getText().toString(), myDialog);
                DatabaseReference dR = FirebaseDatabase.getInstance().getReference("orders").child(key);

                dR.child("orderTitle").setValue(orderTitle.getText().toString());
                dR.child("orderDescription").setValue(orderDescription.getText().toString());
                myDialog.dismiss();

            }
        });


        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private boolean deleteOrder(String id) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("orders").child(id);

        //removing artist
        dR.removeValue();


        Toast.makeText(getApplicationContext(), "Order Deleted", Toast.LENGTH_LONG).show();

        return true;
    }
}
