package ru.solandme.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShopActivity extends AppCompatActivity {
    private DatabaseReference databaseRef;
    private FirebaseAuth auth;
    private String postKey = null;
    private Button submitBtn;
    private ImageButton selectImage;
    private ImageView singleImageSelect;
    private ProgressDialog progress;

    private static final int GALLERY_REQUEST = 1;
    private int pos;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static final int REQUEST_CODE_ADD = 100;
    public static final String KEY_PRODUCT_TO_EDIT = "KEY_PRODUCT_TO_EDIT";
    public static final int REQUEST_CODE_EDIT = 101;
    private EditText ProductName;
    private EditText ProductDescription;
    private EditText ProductPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        postKey = getIntent().getExtras().getString("postKey");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Blog");
        auth = FirebaseAuth.getInstance();


        selectImage = (ImageButton) findViewById(R.id.singleImageSelect);
        ProductName = (EditText) findViewById(R.id.etName);
        ProductDescription = (EditText) findViewById(R.id.etDescription);
        ProductPrice = (EditText) findViewById(R.id.etPrice);
        submitBtn = (Button) findViewById(R.id.btnAddItem);

        progress = new ProgressDialog(this);

//        Toast.makeText(this, postKey, Toast.LENGTH_SHORT).show();


        //////fragment
        setupFAB();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Products"));
        tabLayout.addTab(tabLayout.newTab().setText("Cart"));
        tabLayout.addTab(tabLayout.newTab().setText("my Orders"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //fragment

        //Fragment argumentFragment = new BlankFragment1();//Get Fragment Instance



        databaseRef = FirebaseDatabase.getInstance().getReference().child("Company");
        auth = FirebaseAuth.getInstance();

        singleImageSelect = (ImageView) findViewById(R.id.singleImageSelect);
//  =
        databaseRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("Ygritte", dataSnapshot.toString());
                String imageURL = (String) dataSnapshot.child("imageURL").getValue();

                String postUid = (String) dataSnapshot.child("uid").getValue();
//
//                singleTitleField.setText(postTitle);
//                singleTitleField.setEnabled(false);
//                singleDescField.setText(postDesc);
//                singleDescField.setEnabled(false);
                singleImageSelect.setClickable(false);

                Picasso.with(ShopActivity.this).load(imageURL).into(singleImageSelect);

                if (auth.getCurrentUser().getUid().equals(postUid)) {
                    singleImageSelect.setClickable(true);
//                    singleTitleField.setEnabled(true);
//                    singleDescField.setEnabled(true);
//                    singleRemoveButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    }
    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAddProduct = new Intent();
                intentAddProduct.setClass(ShopActivity.this, AddProductActivity.class);
                intentAddProduct.putExtra("company_key", postKey);
                startActivityForResult(intentAddProduct, REQUEST_CODE_ADD);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    BlankFragment1 tab1 = new BlankFragment1();
                    Bundle data = new Bundle();//Use bundle to pass data
                    //put string, int, etc in bundle with a key value
                    data.putString("company_id", postKey);//put string, int, etc in bundle with a key value
                    tab1.setArguments(data);//Finally set argument bundle to fragment
                    return tab1;
                case 1:
                    BlankFragment2 tab2 = new BlankFragment2();
                    data = new Bundle();
                    //put string, int, etc in bundle with a key value
                    data.putString("company_id", postKey);//put string, int, etc in bundle with a key value
                    tab2.setArguments(data);//Finally set argument bundle to fragment
                    return tab2;
                case 2:
                    BlankFragment3 tab3 = new BlankFragment3();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD) {
//                Product newProduct = (Product) data.getSerializableExtra(AddProductActivity.KEY_PRODUCT);
//                productRecyclerAdapter.addProduct(newProduct);
//                recyclerShoppingCart.scrollToPosition(0);
//            }
//            else if( requestCode == REQUEST_CODE_EDIT) {
//                Product changedProduct = (Product) data.getSerializableExtra(
//                        AddProductActivity.KEY_PRODUCT);
//                changedProduct.setId(idToEdit);

//                productRecyclerAdapter.edit(changedProduct, positionToEdit);
            }

        }

//        updateTotalPrice();
    }


}





