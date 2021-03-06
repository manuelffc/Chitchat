package com.gardenvillagers.chitchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DisplayUser extends AppCompatActivity {
    TextView registerUser;
    TextView username, nickname;
    Button loginButton;
    me.grantland.widget.AutofitTextView logout,accountText;
    ImageView  img;
    UserDetails phoneUser;
    SharedPreferences mPrefs;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user);
        registerUser = (TextView)findViewById(R.id.updateInfo);
        username = findViewById(R.id.username);
        nickname = findViewById(R.id.nickname);
        loginButton = (Button)findViewById(R.id.loginButton);
        img = findViewById(R.id.gif1);
        img.setOnClickListener(clickListener);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(clickListener);
        accountText = findViewById(R.id.accountText);
        accountText.setOnClickListener(clickListener);
        mPrefs= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String json = mPrefs.getString("phoneUser","");

        if (json.isEmpty()) {
            phoneUser = new UserDetails();
        }
        else {

            phoneUser = new Gson().fromJson(json, UserDetails.class);
            username.setText(currentUser.getEmail());
            registerUser.setText("Hello "+ currentUser.getDisplayName());

            if (!phoneUser.getPhotoURL().equals("")) {
                loadImageFromStorage(phoneUser.getPhotoURL());
                img.setBackgroundResource(0);
            }
            username.setText(currentUser.getEmail());
            registerUser.setText("Hello "+ currentUser.getDisplayName());
        }

    }

    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
    private View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){

                case R.id.gif1:
                    Intent photoSelect = new Intent(Intent.ACTION_PICK);
                    photoSelect.setType("image/*");
                    startActivityForResult(photoSelect,1);

                    break;
                case R.id.logout:
                    mAuth.signOut();
                    Intent i = new Intent(DisplayUser.this, MainActivity.class);
                    startActivity(i);
                    break;
                case R.id.accountText:
                    Intent j = new Intent(DisplayUser.this, DisplayCost.class);
                    startActivity(j);
                    break;

            }
        }
    };


    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                img.setBackgroundResource(0);
                img.setImageBitmap(selectedImage);
                phoneUser.setPhotoURL(saveToInternalStorage(selectedImage));
                com.google.gson.Gson gson = new GsonBuilder().registerTypeAdapter(UserDetails.class, new UserDetails.PersonSerializer())
                        .create();
                SharedPreferences.Editor prefsEditor = mPrefs.edit();

                String json = gson.toJson(phoneUser);
                Toast.makeText(getApplicationContext(),json + gson.toJson(phoneUser), Toast.LENGTH_LONG).show();
                prefsEditor.putString("phoneUser", json);
                prefsEditor.commit();
                prefsEditor.apply();
                Toast.makeText(this, imageUri.getPath(), Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}