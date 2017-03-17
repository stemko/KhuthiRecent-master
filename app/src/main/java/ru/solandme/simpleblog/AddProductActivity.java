package ru.solandme.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddProductActivity extends AppCompatActivity {

    private EditText ProductName;
    private EditText ProductDescription;
    private EditText ProductPrice;
    private ImageButton selectImage;
    private EditText companyName;

    private Button submitBtn;

    private Uri imageUri = null;
    private StorageReference storage;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRefUser;
    private DatabaseReference databaseRefCompany;

    private ProgressDialog progress;

    private static final int GALLERY_REQUEST = 1;
    String company_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        if (getIntent().getExtras() != null ) {
            company_id = getIntent().getStringExtra("company_key");
        }

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        storage = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseRefUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        databaseRefCompany = FirebaseDatabase.getInstance().getReference().child("Company");


        selectImage = (ImageButton) findViewById(R.id.imageSelect);
        ProductName = (EditText) findViewById(R.id.etName);
        ProductDescription= (EditText) findViewById(R.id.etDescription);
        ProductPrice= (EditText) findViewById(R.id.etPrice);
        submitBtn = (Button) findViewById(R.id.btnAddItem);

        progress = new ProgressDialog(this);



        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });


    }


    private void startPosting() {
        progress.setMessage("Adding New Product ...");

        final String ProductName_val = ProductName.getText().toString().trim();
        final String ProductDescription_val = ProductDescription.getText().toString().trim();


        final String ProductPrice_val = ProductPrice.getText().toString().trim();
        if (!TextUtils.isEmpty(ProductName_val) && !TextUtils.isEmpty(ProductDescription_val) && imageUri != null&&!TextUtils.isEmpty(ProductPrice_val)) {

            progress.show();

            StorageReference filePath = storage.child("Product_Images").child(imageUri.getLastPathSegment());

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    String uid = auth.getCurrentUser().getUid();


                    databaseRefCompany.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                             Toast.makeText(AddProductActivity.this, company_id, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    final DatabaseReference newPostRef = databaseReference.child(company_id).push();


                    databaseRefUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPostRef.child("ProductName").setValue(ProductName_val);
                            newPostRef.child("ProductDescription").setValue(ProductDescription_val);
                            newPostRef.child("company_id").setValue(company_id);
                            newPostRef.child("ProductPrice").setValue(ProductPrice_val);
                            newPostRef.child("imageURL").setValue(downloadUrl.toString());
                            newPostRef.child("uid").setValue(currentUser.getUid());
                            newPostRef.child("username").setValue(dataSnapshot.child("name")
                                    .getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        ;//now replace the argument fragment/startActivity(new Intent(AddProductActivity.this, BlankFragment1.class));
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    progress.dismiss();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            selectImage.setImageURI(imageUri);




        }
    }

}
