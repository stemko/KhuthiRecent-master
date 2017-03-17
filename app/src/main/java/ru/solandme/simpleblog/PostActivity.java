package ru.solandme.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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

public class PostActivity extends AppCompatActivity {

    private ImageButton selectImage;
    private EditText companyName;
    private EditText companyLatitude;
    private EditText companyLongitude;
    private Button submitBtn;
    private Button selectlocationBtn;
    private Uri imageUri = null;

    private StorageReference storage;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRefUser;
    private final int REQUEST_CODE_PLACEPICKER = 2;


    private ProgressDialog progress;

    private static final int GALLERY_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        storage = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Company");
        databaseRefUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        selectImage = (ImageButton) findViewById(R.id.imageSelect);
        companyName = (EditText) findViewById(R.id.company_name);
        companyLatitude = (EditText) findViewById(R.id.latitude);
        companyLongitude = (EditText) findViewById(R.id.longitude);
        selectlocationBtn = (Button) findViewById(R.id.btnselectLocation);
        submitBtn = (Button) findViewById(R.id.registerBtn);

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

        selectlocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startPlacePickerActivity();
            }

            private void startPlacePickerActivity() {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                // this would only work if you have your Google Places API working

                try {
                    Intent intent;
                    intent = intentBuilder.build(PostActivity.this);
                    startActivityForResult(intent, REQUEST_CODE_PLACEPICKER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void displaySelectedPlaceFromPlacePicker(Intent data) {
        Place placeSelected = PlacePicker.getPlace(data, this);

        String name = placeSelected.getName().toString();
        Double latitude=placeSelected.getLatLng().latitude;
        Double longitude=placeSelected.getLatLng().longitude;
        companyName = (EditText) findViewById(R.id.company_name);
        companyLatitude = (EditText) findViewById(R.id.latitude);
        companyLongitude = (EditText) findViewById(R.id.longitude);
        companyName.setText(name.toString());
        companyLatitude.setText(latitude.toString());
        companyLongitude.setText(longitude.toString());

    }


    private void startPosting() {
        progress.setMessage("Adding Company ...");

        final String CompanyName_val = companyName.getText().toString().trim();
        final String CompanyLatitude_val = companyLatitude.getText().toString().trim();
        final String CompanyLongitude_val = companyLongitude.getText().toString().trim();

        if (!TextUtils.isEmpty(CompanyName_val) && !TextUtils.isEmpty(CompanyLatitude_val) && imageUri != null&&!TextUtils.isEmpty(CompanyLongitude_val)) {

            progress.show();

            StorageReference filePath = storage.child("Company_Images").child(imageUri.getLastPathSegment());

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPostRef = databaseReference.push();

                    databaseRefUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPostRef.child("CompanyName").setValue(CompanyName_val);
                            newPostRef.child("CompanyLatitude").setValue(CompanyLatitude_val);
                            newPostRef.child("CompanyLongitude").setValue(CompanyLongitude_val);
                            newPostRef.child("imageURL").setValue(downloadUrl.toString());
                            newPostRef.child("uid").setValue(currentUser.getUid());
                            newPostRef.child("Cid").setValue(databaseList());
                            newPostRef.child("username").setValue(dataSnapshot.child("name")
                                    .getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(PostActivity.this, MainActivity.class));
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
       else if (requestCode == REQUEST_CODE_PLACEPICKER && resultCode == RESULT_OK) {
            displaySelectedPlaceFromPlacePicker(data);

        }
    }

}
