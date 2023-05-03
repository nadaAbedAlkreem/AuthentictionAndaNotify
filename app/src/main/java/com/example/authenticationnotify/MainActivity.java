package com.example.authenticationnotify ;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.authenticationnotify.LogInActivity;
import com.example.authenticationnotify.R;
import com.example.authenticationnotify.model.Users;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity  {
    public StorageReference storageReference = null;
    public DatabaseReference databaseReference = null;
    public EditText username;
    public EditText password;
    public EditText birthday;
    public EditText mobile;
    public EditText address;
    public EditText email;

    private FirebaseAuth mAuth;
    private Uri fileURI = null;
    public ImageView imageView;
    public String path;
    private final int PICK_IMAGE_GALLERY_CODE = 78;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 12345;
    private final int CAMERA_PICTURE_REQUEST_CODE = 56789;

    public ProgressBar progressBar;
    FirebaseFirestore db;
    TextView login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = database.getReference().child("user_image");
        storageReference = firebaseStorage.getReference();

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        birthday = findViewById(R.id.brithday);
        password = findViewById(R.id.password);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        login = findViewById(R.id.LoginMove);
         progressBar = findViewById(R.id.progressbar);
        imageView = findViewById(R.id.imageProfileUserSignUp);

         Button regsisterBtn = findViewById(R.id.regsister);
        login.setOnClickListener(view -> {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation( this).toBundle());


        });


    }

    public void regsisterbtn(View view) {

         String textUserName = username.getText().toString();
        String textBirthday = birthday.getText().toString();
        String textPassword = password.getText().toString();
        String textEmail = email.getText().toString();
        String textAddress = address.getText().toString();
        String textMobile = mobile.getText().toString();


        if (TextUtils.isEmpty(textUserName)) {
            Log.e("testnama", String.valueOf(fileURI));
            Log.e("testnama", " String.valueOf(fileURI)");

            Toast.makeText(this, "please enter your full name ", Toast.LENGTH_SHORT).show();
            username.setError(" user name is required");
            username.requestFocus();
        } else if (TextUtils.isEmpty(textPassword)) {
            Toast.makeText(this, "please enter your password   ", Toast.LENGTH_SHORT).show();
            password.setError(" password is required");
            password.requestFocus();
        } else if (textPassword.length() < 6) {
            Toast.makeText(this, "please enter your password  more then 6  ", Toast.LENGTH_SHORT).show();
            password.setError(" password is required more the 6 ");
            password.requestFocus();
        } else if (TextUtils.isEmpty(textEmail)) {
            Toast.makeText(this, "please enter your email   ", Toast.LENGTH_SHORT).show();
            email.setError(" email is required  ");
            email.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
            Toast.makeText(this, "please enter your email true    ", Toast.LENGTH_SHORT).show();
            email.setError(" email is vaild true   ");
            email.requestFocus();
        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(this, "please enter your mobile      ", Toast.LENGTH_SHORT).show();
            mobile.setError(" mobile is reqirud   ");
            mobile.requestFocus();
        } else if (textMobile.length() != 10) {
            Toast.makeText(this, "please enter your mobile  and vaild     ", Toast.LENGTH_SHORT).show();
            mobile.setError(" mobile is vaild true   ");
            mobile.requestFocus();
        } else if (TextUtils.isEmpty(textAddress)) {
            Toast.makeText(this, "please enter your  address  ", Toast.LENGTH_SHORT).show();
            address.setError(" address id required   ");
            address.requestFocus();
        } else if (TextUtils.isEmpty(textBirthday)) {
            Toast.makeText(this, "please enter your  Birthday  ", Toast.LENGTH_SHORT).show();
            birthday.setError(" Birthday id required   ");
            birthday.requestFocus();
        }   else if (fileURI == null) {
            Toast.makeText(this, "image not exist   ", Toast.LENGTH_SHORT).show();

        } else {

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
                            Toast.makeText(MainActivity.this, "Image  up", Toast.LENGTH_SHORT).show();
                            path = String.valueOf(uri);
                            regsisterUserFirebase(   textUserName, path, textBirthday, textPassword
                                    , textEmail, textAddress, textMobile );

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Image  faild ", Toast.LENGTH_SHORT).show();

                                        }
                                    }
            );

        }
    }


    private void regsisterUserFirebase( String textUserName, String path ,  String textBirthday
            , String textPassword, String textEmail
            , String textAddress, String textMobile ) {

        mAuth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "regisiter Successfully ", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification();
                            /// create collection datauser
                            Users users = new Users(  mAuth.getCurrentUser().getUid(),  textUserName, path, textMobile,
                                    textAddress, textBirthday  , textEmail  );
                            db.collection("Users")
                                    .add(users)

                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Intent intent =new Intent(MainActivity.this , LogInActivity.class) ;
                                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation( MainActivity.this).toBundle());Log.e("TAG", "Data added successfully to database");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });


                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthWeakPasswordException e) {
                                password.setError("your password is too weak kindly use a mix of alphabets ");
                                password.requestFocus();


                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                password.setError("your email is invalid or already i use Kindly re-enter");
                                password.requestFocus();

                                e.printStackTrace();

                            } catch (FirebaseAuthUserCollisionException e) {
                                password.setError("User is already registerd with this email   , use another email ");
                                password.requestFocus();

                                e.printStackTrace();

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                e.printStackTrace();
                            }

                        }
                    }

                });
    }




    public void selectImageProfileUser(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public void checkPermisionImge() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);

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
//        else if (requestCode == CAMERA_PICTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK)
//        {
//            Bundle extras = data.getExtras() ;
//            Bitmap bitmap = (Bitmap)extras.get("data") ;
//            imageView.setImageBitmap(bitmap);
//            fileURI =getImgURI(getApplicationContext() , bitmap) ;
//            Log.e("nada" , String.valueOf(fileURI));
//
//
//        }
    }


//    private  Uri getImgURI(Context context , Bitmap bitmap)
//    {
//        ByteArrayOutputStream bytes  = new ByteArrayOutputStream() ;
//        bitmap.compress(Bitmap.CompressFormat.PNG  , 100 , bytes) ;
//        String path = MediaStore.Images.Media.insertImage(getContentResolver() , bitmap , "title" , null);
//        Log.e("nada" , String.valueOf(Uri.parse(path)));
//
//    }
}


