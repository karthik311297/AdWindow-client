package com.example.adwindow.adwindow_client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adwindow.adwindow_client.adapter.ScreenTitleAdapter;
import com.example.adwindow.adwindow_client.model.Content;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenLocationAdUpload extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    RecyclerView screenLocations;
    List<String> locationTitles;
    String city;
    ScreenTitleAdapter screenTitleAdapter;
    private static final int FILE_CHOOSE_CODE = 999;
    Uri filePath;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_location_ad_upload);
        locationTitles = getIntent().getStringArrayListExtra("LOCS");
        firebaseAuth = FirebaseAuth.getInstance();
        city = getIntent().getStringExtra("CITY");
        populateRecyclerView();
        ImageButton selectAllLocs = findViewById(R.id.selectAllLocs);
        ImageButton uploadContent = findViewById(R.id.uploadContent);
        Button chooseContent = findViewById(R.id.chooseContent);
        selectAllLocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(screenTitleAdapter!=null) {
                    if (!screenTitleAdapter.areAllChecked()) {
                        screenTitleAdapter.checkAllLocations();
                        screenTitleAdapter.notifyDataSetChanged();
                    } else {
                        screenTitleAdapter.uncheckAllLocations();
                        screenTitleAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        uploadContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> screensToUploadIn = screenTitleAdapter.getAllCheckedLocations();
                uploadFile(screensToUploadIn);
            }
        });
        chooseContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try{
                    startActivityForResult(Intent.createChooser(intent, "Select Content"), FILE_CHOOSE_CODE);
                }
                catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(ScreenLocationAdUpload.this,"Please install a file manager",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == FILE_CHOOSE_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                if (data != null && data.getData()!=null) {
                    filePath = data.getData();
                    ImageView imageView = findViewById(R.id.fileChooseContent);
                    imageView.setImageDrawable(ContextCompat.getDrawable(ScreenLocationAdUpload.this, R.drawable.ic_folder_pink_24dp));
                    Toast.makeText(ScreenLocationAdUpload.this,"File Chosen!",Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateRecyclerView()
    {
        screenLocations = findViewById(R.id.screen_loc_list);
        if(locationTitles!=null) {
            screenTitleAdapter = new ScreenTitleAdapter(locationTitles);
            screenLocations.setAdapter(screenTitleAdapter);
            screenLocations.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void uploadFile(final List<String> screensToUploadIn)
    {
        EditText editText = findViewById(R.id.uploadContentName);
        final String  adName = editText.getText().toString();
        if(filePath == null)
        {
            Toast.makeText(ScreenLocationAdUpload.this,"Please choose a file first",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(adName.equals(""))
        {
            Toast.makeText(ScreenLocationAdUpload.this,"Please Enter an Ad Name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(filePath!=null)
        {
            final String userUid = firebaseAuth.getCurrentUser().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(ScreenLocationAdUpload.this);
            progressDialog.setTitle("Uploading Your Ad Please Wait..");
            progressDialog.show();
            final StorageReference advertisementReference = storageReference.child("Advertisements/"+userUid+"/"+adName+"."+getFileExtension(filePath));
            advertisementReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    progressDialog.dismiss();
                    Toast.makeText(ScreenLocationAdUpload.this, "You already have an Ad with that name, please change the Ad name", Toast.LENGTH_LONG).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    advertisementReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Completed 100%. Please Wait..");
                            advertisementReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String, Object> multiUpdates = new HashMap<>();
                                    String contentId = databaseReference.child("Advertisements").child(userUid).push().getKey();
                                    Map<String, Boolean> adStatus = new HashMap<>();
                                    for(String scr : screensToUploadIn)
                                    {
                                        adStatus.put(scr, false);
                                    }
                                    multiUpdates.put("Advertisements/"+userUid+"/"+contentId, new Content(contentId,uri.toString(),userUid,adStatus,adName));
                                    for(String title : screensToUploadIn)
                                    {
                                        multiUpdates.put("Screens/"+city+"/"+title+"/advertisementsStatus/"+contentId, false);
                                    }

                                    databaseReference.updateChildren(multiUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(ScreenLocationAdUpload.this,"Ad Uploaded",Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            advertisementReference.delete();
                                            Toast.makeText(ScreenLocationAdUpload.this,"Ad upload failed due to: "+e.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ScreenLocationAdUpload.this,"Ad upload failed due to: "+e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                    progressDialog.setMessage("Completed "+((int) progress) + "%...");
                                }
                            });
                }
            })
            ;
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
