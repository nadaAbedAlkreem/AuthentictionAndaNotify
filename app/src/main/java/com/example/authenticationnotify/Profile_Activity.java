package com.example.authenticationnotify;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authenticationnotify.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Profile_Activity extends AppCompatActivity {
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    ImageView imageUserCurrent ;
    TextView nameUserCurrent ;
    TextView mobile ;
    TextView Birthday ;
    TextView email ;
    TextView Address ;
    String name_  = "";
    String address_ = "" ;
    String mobile_ = "" ;
     String brithday_ = "" ;
    private Uri fileURI = null;
    public ImageView imageView;
    public String path ;
    private final int PICK_IMAGE_GALLERY_CODE = 78;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 12345;
    private final int CAMERA_PICTURE_REQUEST_CODE = 56789;
    String id = "" ;
    public StorageReference storageReference = null;
    public DatabaseReference databaseReference = null;
    public      boolean imageUpload = false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageUserCurrent = findViewById(R.id.imageProfileUser) ;
        nameUserCurrent = findViewById(R.id.txtUserName) ;
        mobile = findViewById(R.id.txtMobile) ;
        Birthday = findViewById(R.id.txtBirthday) ;
        email = findViewById(R.id.txtEmail) ;
        Address = findViewById(R.id.txtAddress) ;
        Button updatebtn = findViewById(R.id.updatebtn) ;



        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = database.getReference().child("user_image");
        storageReference = firebaseStorage.getReference();


        db.collection("Users").whereEqualTo("id" ,   mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                        {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                List<DocumentSnapshot> list  = queryDocumentSnapshots.getDocuments() ;
                                if(!list.isEmpty()){
                                    for (DocumentSnapshot d : list) {
                          nameUserCurrent.setText(d.getString("userName"));
                        Picasso.get().load(d.getString("userImage"))
                                .into(imageUserCurrent);
                        mobile.setText(d.getString("mobile"));
                        Birthday.setText(d.getString("birthday"));
                        name_ = d.getString("userName") ;
                        brithday_ = d.getString("birthday")  ;
                        mobile_ = d.getString("mobile") ;
                        id = d.getId()  ;
                        Address.setText(d.getString("address"));
                        address_  = d.getString("address")   ;
                    }
                }else{
                    Log.e("ttttt" , "empty") ;
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ttttttttttttttt" , "FAILD") ;
            }
        });
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(Profile_Activity.this);

                dialog.setContentView(R.layout.dialog_crud);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                ProgressBar progressBar = dialog.findViewById(R.id.progressBar2);
                progressBar.setVisibility(View.GONE);
                EditText update_name = dialog.findViewById(R.id.update_user_name);
                EditText update_address = dialog.findViewById(R.id.update_address);
                EditText update_mobile = dialog.findViewById(R.id.update_phone);
                EditText update_birthday= dialog.findViewById(R.id.update_birth);
                Button update_btn= dialog.findViewById(R.id.updatebtn_);
                Button cansel = dialog.findViewById(R.id.consel);
                Button update_image_btn = dialog.findViewById(R.id.update_image);

                update_address.setText(address_) ;
                update_mobile.setText(mobile_) ;
                update_name.setText(name_);
                update_birthday.setText(brithday_);
                update_image_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder( Profile_Activity.this);
                        builder.setTitle("select image");
                        builder.setMessage("Please select on option ");
                        builder.setPositiveButton("camera", (dialogInterface, i) -> {
                            checkPermisionImge();
                            dialogInterface.dismiss();

                        });
                        builder.setNeutralButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                        builder.setNegativeButton("Gallery", (dialogInterface, i) -> {
                            selectFormGallery();
                            dialogInterface.dismiss();


                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });


                 update_btn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       progressBar.setVisibility(View.VISIBLE);

                       ////////////
                       if(fileURI != null) {
                           StorageReference ref = storageReference.child("image/" + UUID.randomUUID().toString());
                           ref.putFile(fileURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   Log.e("testtnn", String.valueOf(fileURI));
                                   Log.e("testtnn", " String.valueOf(fileURI)");

                                   ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                       @Override
                                       public void onSuccess(Uri uri) {
                                           databaseReference.push().setValue(uri.toString());
                                           Toast.makeText(Profile_Activity.this, "Image  up", Toast.LENGTH_SHORT).show();
                                           update_image_btn.setText("up load image");
                                           progressBar.setVisibility(View.GONE);

                                           path = String.valueOf(uri);
                                           Map<String, Object> user = new HashMap<>();

                                           user.put("userName", update_name.getText().toString());
                                           Log.e("nada", update_name.getText().toString());
                                           user.put("address", update_address.getText().toString());
                                           user.put("birthday", update_birthday.getText().toString());
                                           user.put("mobile", update_mobile.getText().toString());

                                               user.put("userImage", path);

                                           Toast.makeText(Profile_Activity.this, id, Toast.LENGTH_SHORT).show();
                                           db.collection("Users").document(id).update(user).addOnSuccessListener(documentReference -> {
                                               Toast.makeText(Profile_Activity.this, "update successfully", Toast.LENGTH_SHORT).show();
                                               dialog.dismiss();
                                               Intent intent = new Intent(Profile_Activity.this, Profile_Activity.class);
                                               startActivity(intent);

                                           }).addOnFailureListener(e -> {
                                               Log.e("tag", e.getMessage());
                                               Toast.makeText(Profile_Activity.this, "Added Failed", Toast.LENGTH_SHORT).show();
                                           });
                                       }
                                   });

                               }
                           }).addOnFailureListener(new OnFailureListener() {
                                                       @Override
                                                       public void onFailure(@NonNull Exception e) {
                                                           Toast.makeText(Profile_Activity.this, "Image  faild ", Toast.LENGTH_SHORT).show();

                                                       }
                                                   }
                           );
                       }
                       ///////
                       if(fileURI == null)
                       {
                           Map<String, Object> user = new HashMap<>();

                           user.put("userName", update_name.getText().toString());
                           Log.e("nada", update_name.getText().toString());
                           user.put("address", update_address.getText().toString());
                           user.put("birthday", update_birthday.getText().toString());
                           user.put("mobile", update_mobile.getText().toString());


                           Toast.makeText(Profile_Activity.this, id, Toast.LENGTH_SHORT).show();
                           db.collection("Users").document(id).update(user).addOnSuccessListener(documentReference -> {
                               Toast.makeText(Profile_Activity.this, "update successfully", Toast.LENGTH_SHORT).show();
                               dialog.dismiss();
                               Intent intent = new Intent(Profile_Activity.this, Profile_Activity.class);
                               startActivity(intent);

                           }).addOnFailureListener(e -> {
                               Log.e("tag", e.getMessage());
                               Toast.makeText(Profile_Activity.this, "Added Failed", Toast.LENGTH_SHORT).show();
                           });


                       }

                       }

               });
               cansel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       dialog.dismiss();
                   }
               });






                dialog.show();

            }
        });



        FirebaseMessaging.getInstance().subscribeToTopic("dareen")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.e("d" , "success") ;

                        }else {
                            Log.e("d", "faild");
                        }

                    }
                });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                         Log.e("TAG", token);
                        Toast.makeText(Profile_Activity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });



    }












    public void checkPermisionImge() {
        if (ContextCompat.checkSelfPermission(this
                , android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( Profile_Activity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);

        } else {
            openImage();
        }

    }

    private void openImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_PICTURE_REQUEST_CODE);
        }

    }

    private void selectFormGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "selectImage"), PICK_IMAGE_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) return;
            try {
                fileURI = data.getData();
                Log.e("nada", String.valueOf(data.getData()));
                Log.e("naffda", String.valueOf(fileURI));

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileURI);
                imageView.setImageBitmap(bitmap);

            } catch (Exception e) {

            }
        }


    }



}