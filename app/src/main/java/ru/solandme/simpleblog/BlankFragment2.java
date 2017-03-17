package ru.solandme.simpleblog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;




public class BlankFragment2 extends Fragment {

    private RecyclerView CartList;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRefUsers;
    private DatabaseReference databaseRefCurrentUser;
    private DatabaseReference databaseRefLike;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String postKey = null;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Query queryCurrentUser;
    private String user_id;
    private static final String COMPANY_ID = "company_id";

    //private Button AddToCart;
    private String mCompanyId;
    private String mDataSlug;
    private Boolean processLike;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Right", "onCreate()");



    }
    public static BlankFragment2 newInstance(String CompanyId) {
        Bundle bundle = new Bundle();
        bundle.putString(COMPANY_ID, CompanyId);
        BlankFragment2 BlankFragment2 = new BlankFragment2();
        BlankFragment2.setArguments(bundle);
        return BlankFragment2;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String link = bundle.getString(COMPANY_ID);
//            Toast.makeText(getContext(),link, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_blank_fragment2, container, false);



        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (getArguments() != null) {
            mCompanyId = getArguments().getString(COMPANY_ID);

        }


        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                } else {
                    user_id = auth.getCurrentUser().getUid();
                }

            }
        };



        databaseRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(mCompanyId);
        databaseRefUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseRefLike = FirebaseDatabase.getInstance().getReference().child("Likes");

        databaseRefCurrentUser = FirebaseDatabase.getInstance().getReference().child("Cart");
        queryCurrentUser = databaseRefCurrentUser.orderByChild("uid").equalTo(user_id);
        Toast.makeText(getContext(), mCompanyId, Toast.LENGTH_SHORT).show();

        databaseRefUsers.keepSynced(true);
        databaseRef.keepSynced(true);
        databaseRefLike.keepSynced(true);



        CartList = (RecyclerView) v.findViewById(
                R.id.CartList);



//


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        CartList.setHasFixedSize(true);

        CartList.setLayoutManager(layoutManager);

        return v;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
    }
    @Override
    public void onStart() {

        super.onStart();
        auth.addAuthStateListener(authStateListener);
        checkUserExist();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                        Product.class,
                        R.layout.cart_row,
                        ProductViewHolder.class,
//                        queryCurrentUser, v
                        databaseRef.orderByChild("uid").equalTo(auth.getCurrentUser().getUid())

                ) {
                    @Override
                    protected void populateViewHolder(ProductViewHolder viewHolder, final Product model, int position) {

                        final DatabaseReference databaseAddToCart = FirebaseDatabase.getInstance().getReference().child("Cart").child(mCompanyId).push();
                        final String postKey = getRef(position).getKey();
                        viewHolder.setProductName(model.getProductName());
                        viewHolder.setProductDescription(model.getProductDescription());
                        viewHolder.setProductPrice(" "+model.getProductPrice());
                        viewHolder.setImage(getContext(), model.getImageURL());


                        viewHolder.CheckoutButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentPayment = new Intent();
                                intentPayment.setClass(getContext(),PaymentActivity.class);
                                intentPayment.putExtra("company_key", postKey);
                                startActivity(intentPayment);
                            }
                        });
//                        viewHolder.setUsername(model.getUsername());
                        viewHolder.singleRemoveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog diaBox = AskOption();
                                diaBox.show();


                            }
                            private AlertDialog AskOption()
                            {
                                AlertDialog myQuittingDialogBox =new AlertDialog.Builder(getContext(), R.style.MyDialogTheme)
                                        //set message, title, and icon
                                        .setTitle("Delete")
                                        .setMessage("Do you want to Delete this item?")


                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                //your deleting code

                                                databaseRef.child(postKey).removeValue();
                                                Toast.makeText(getContext(), "Item Deleted from Cart", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }

                                        })



                                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();

                                            }
                                        })
                                        .create();
                                return myQuittingDialogBox;

                            }

                        });

//                        viewHolder.AddToCart.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                databaseAddToCart.child("product_name").setValue(model.getProductName());
//                                databaseAddToCart.child("product_desc").setValue(model.getProductDescription());
//                                databaseAddToCart.child("product_price").setValue(model.getProductPrice());
//                                databaseAddToCart.child("image").setValue(model.getImageURL());
//                                databaseAddToCart.child("uid").setValue(currentUser.getUid());
//                                Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
//                            }
//                        });


//                        viewHolder.setLikeBtn(postKey);

//                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent singleBlogIntent = new Intent(getContext(), ShopActivity.class);
//                                singleBlogIntent.putExtra("postKey", postKey);
////                                startActivity(singleBlogIntent);
//                            }
//                        });

//                        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                processLike = true;
//                                databaseRefLike.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                        if (processLike) {
//                                            if (dataSnapshot.child(postKey).hasChild(auth.getCurrentUser().getUid())) {
//
//                                                databaseRefLike.child(postKey).child(auth.getCurrentUser().getUid()).removeValue();
//                                                processLike = false;
//
//                                            } else {
//
//                                                databaseRefLike.child(postKey).child(auth.getCurrentUser().getUid()).setValue("RandomValue");
//                                                processLike = false;
//
////                                            }
////                                        }
//                                    }

//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
////                            }
//                        });
                    }
                };

        CartList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageButton likeBtn;
        private Button AddToCart;
        private Button singleRemoveButton;
        private Button CheckoutButton;
        DatabaseReference databaseRefLike;
        FirebaseAuth auth;

        public ProductViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            likeBtn = (ImageButton) view.findViewById(R.id.likeBtn);
            AddToCart = (Button) view.findViewById(R.id.b_product_add);
            singleRemoveButton = (Button) view.findViewById(R.id.b_product_cancel);
            CheckoutButton = (Button) view.findViewById(R.id.b_product_check_out);
            databaseRefLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            auth = FirebaseAuth.getInstance();

            databaseRefLike.keepSynced(true);
        }

        void setProductName(String pro_Name) {
            TextView productName = (TextView) view.findViewById(R.id.tv_product_name);
            productName.setText(pro_Name);
        }

//        void setLikeBtn(final String postKey) {
//            databaseRefLike.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.child(postKey).hasChild(auth.getCurrentUser().getUid())) {
//                        likeBtn.setImageResource(R.drawable.ic_like);
//                    } else {
////                        likeBtn.setImageResource(R.drawable.ic_like_grey);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
////            });
//        }

        void setProductDescription(String desc) {
            TextView ProductDescription = (TextView) view.findViewById(R.id.product_description);
            ProductDescription.setText(desc);
        }

        void setImage(Context context, String imageUrl) {
            ImageView ProductImage = (ImageView) view.findViewById(R.id.product_pic);
            Picasso.with(context).load(imageUrl).into(ProductImage);
        }

        void setProductPrice(String price) {
            TextView ProductPrice = (TextView) view.findViewById(R.id.product_price);
            ProductPrice.setText(price);
        }

//        void setUsername(String username) {
//            TextView postUsername = (TextView) view.findViewById(R.id.postUsername);
//            postUsername.setText(username);
////        }
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        ADD BUTTON
         */

//        if (item.getItemId() == R.id.action_add) {
//            startActivity(new Intent(MainActivity.this, PostActivity.class));
//        }
//
//        if (item.getItemId() == R.id.action_logout) {
//            logout();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void logout() {
        auth.signOut();
    }


    private void checkUserExist() {

        databaseRefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user_id)) {
                    Intent setupIntent = new Intent(getContext(), SetupActivity.class);
                    setupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));

    }

}



