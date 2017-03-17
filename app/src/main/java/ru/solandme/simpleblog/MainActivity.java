package ru.solandme.simpleblog;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {





    private RecyclerView blogList;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRefUsers;
    private DatabaseReference databaseRefCurrentUser;
    private DatabaseReference databaseRefLike;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Query queryCurrentUser;
    private String user_id;

    private Boolean processLike;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                } else {
                    user_id = auth.getCurrentUser().getUid();
                }
            }
        };

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Company");
        databaseRefUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseRefLike = FirebaseDatabase.getInstance().getReference().child("Likes");


        databaseRefCurrentUser = FirebaseDatabase.getInstance().getReference().child("Company");
        queryCurrentUser = databaseRefCurrentUser.orderByChild("uid").equalTo(user_id);


        databaseRefUsers.keepSynced(true);
        databaseRef.keepSynced(true);
        databaseRefLike.keepSynced(true);

        blogList = (RecyclerView) findViewById(R.id.blogList);

        /**
         * RecyclerView
         */
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        blogList.setLayoutManager(mLayoutManager);
        blogList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(0), true));
        blogList.setItemAnimator(new DefaultItemAnimator());
//        blogList.setAdapter(adapter);
        /**
         * RecyclerView
         */

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        blogList.setHasFixedSize(true);
        //  blogList.setLayoutManager(layoutManager);
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
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
        checkUserExist();
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                        Blog.class,
                        R.layout.blog_row,
                        BlogViewHolder.class,
//                        queryCurrentUser
                        databaseRef
                ) {
                    @Override
                    protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                        final String postKey = getRef(position).getKey();

                        viewHolder.setTitle(model.getTitle());
//                        viewHolder.setDesc(model.getDescription());
                        viewHolder.setImage(getApplicationContext(), model.getImageURL());
                        viewHolder.setUsername(model.getUsername());


                        viewHolder.setLikeBtn(postKey);

                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent singleBlogIntent = new Intent(MainActivity.this, ShopActivity.class);
                                singleBlogIntent.putExtra("postKey", postKey);
                                startActivity(singleBlogIntent);
//                                Toast.makeText(MainActivity.this, postKey, Toast.LENGTH_SHORT).show();
                            }
                        });

                        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                processLike = true;
                                databaseRefLike.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (processLike) {
                                            if (dataSnapshot.child(postKey).hasChild(auth.getCurrentUser().getUid())) {

                                                databaseRefLike.child(postKey).child(auth.getCurrentUser().getUid()).removeValue();
                                                processLike = false;

                                            } else {

                                                databaseRefLike.child(postKey).child(auth.getCurrentUser().getUid()).setValue("RandomValue");
                                                processLike = false;

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                };

        blogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageButton likeBtn;
        DatabaseReference databaseRefLike;
        FirebaseAuth auth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            likeBtn = (ImageButton) view.findViewById(R.id.likeBtn);
            databaseRefLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            auth = FirebaseAuth.getInstance();

            databaseRefLike.keepSynced(true);
        }

        void setTitle(String title) {
            TextView postCompanyName = (TextView) view.findViewById(R.id.postComName);
            postCompanyName.setText(title);
        }

        void setLikeBtn(final String postKey) {
            databaseRefLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postKey).hasChild(auth.getCurrentUser().getUid())) {
                        likeBtn.setImageResource(R.drawable.ic_like);
                    } else {
                        likeBtn.setImageResource(R.drawable.ic_like_grey);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

//        void setDesc(String desc) {
//            TextView CompanyName = (TextView) view.findViewById(R.id.postDesc);
//            CompanyName.setText(desc);
//        }

        void setImage(Context context, String imageUrl) {
            ImageView postImage = (ImageView) view.findViewById(R.id.postImage);
            Picasso.with(context).load(imageUrl).into(postImage);
        }

        void setUsername(String username) {
            TextView postUsername = (TextView) view.findViewById(R.id.postUsername);
            postUsername.setText(username);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        ADD BUTTON
         */

//        if (item.getItemId() == R.id.action_add) {
//            startActivity(new Intent(MainActivity.this, PostActivity.class));
//        }

        if (item.getItemId() == R.id.action_logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        auth.signOut();
    }


    private void checkUserExist() {

        databaseRefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user_id)) {
                    Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
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
