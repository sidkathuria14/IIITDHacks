package com.example.sidkathuria14.myapplication;

import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    public static final int rcCC = 33;
    boolean isCC = false;
    ImageView imgView;
    public static final int REQUEST_CODE_CREATOR = 111;
    Uri uri_path;
    public static final String TAG = "main";
    public static final int PICK_IMAGE_REQUEST = 123;
    private GoogleApiClient mGoogleApiClient;
    private Bitmap mBitmapToSave;
public static final int REQUEST_CODE_RESOLUTION = 1443;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.imgView);


        firebaseAuth = FirebaseAuth.getInstance();

        final Bundle b = getIntent().getExtras();
//        final TextView textView= (TextView) findViewById(R.id.textView);
//        final TextView textView2= (TextView) findViewById(R.id.textView2);
//        Button btn = (Button) findViewById(R.id.button2);

        authStateListener = new Main2Activity().authStateListener();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String k = user.getDisplayName().toString();
            String k1 = user.getEmail().toString();
            Toast.makeText(MainActivity.this, k + "\n" + k1, Toast.LENGTH_SHORT).show();
//            textView.setText(k);
//            textView2.setText(k1);
            Log.d(TAG, "onCreate: " + k);
            Log.d(TAG, "onCreate: " + k1);
        }

        ((Button) findViewById(R.id.upload)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFileToDrive();
            }
        });


        ((Button) findViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                firebaseAuth.removeAuthStateListener(authStateListener);
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        ((Button) findViewById(R.id.choose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
    }
//        });
//        Log.d(TAG, "onCreate: " + path);

//        Intent intent1 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent1, 0);

//        File fileMetadata = new File();
//        fileMetadata.setName("photo.jpg");
//        java.io.File filePath = new java.io.File("files/photo.jpg");
//        FileContent mediaContent = new FileContent("image/jpeg", filePath);
//        File file = driveService.files().create(fileMetadata, mediaContent)
//                .setFields("id")
//                .execute();


    //    public String getPathFromURI(Uri contentUri) {
//        String res = null;
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = this.getContentResolver().query(contentUri, proj, "", null, "");
//        if (cursor.moveToFirst()) {
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            res = cursor.getString(column_index);
//        }
//        cursor.close();
//        return res;
//    }
    private void saveFileToDrive() {
        Log.d(TAG, "saveFileToDrive: ");
        final Bitmap image = mBitmapToSave;

        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {

                        if (!result.getStatus().isSuccess()) {
                            Log.i("ERROR", "Failed to create new contents.");
                            return;
                        }


                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        // Write the bitmap data from it.
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.i("ERROR", "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle("Android Photo.png").build();
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("ERROR", "Failed to launch file chooser.");
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume: " + mGoogleApiClient.isConnected());
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
        Log.d(TAG, "onResume: " + mGoogleApiClient.isConnected());
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri_path = data.getData();
            Log.d(TAG, "onActivityResult: image chosen");
            Toast.makeText(this, "image chosen successfully", Toast.LENGTH_LONG);
            Log.d(TAG, "onActivityResult: " + String.valueOf(uri_path));
            imgView.setImageURI(uri_path);
            try {
                mBitmapToSave = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri_path);
            }
            catch (IOException ioe) {
                Log.d(TAG, "onActivityResult: ioe");

            }
//            try {
////                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                // Log.d(TAG, String.valueOf(bitmap));
//
////                ImageView imageView = (ImageView) findViewById(R.id.imageView);
////                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_RESOLUTION) { //succesfully saved!.
            Log.i(TAG, "Image successfully saved.");
            mBitmapToSave = null;
            Toast.makeText(this, "photo successfully saved to drive", Toast.LENGTH_SHORT).show();



        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//        if (resultCode == this.RESULT_OK) {
//            if (requestCode == 200) {
//                Uri selectedImageUri = imageReturnedIntent.getData();
//                if (null != selectedImageUri) {
//                    path = getPathFromURI(selectedImageUri);
//                    Log.d(TAG, "onActivityResult: " + path);
////                    return path;
////                    DrawerAdapter.imageViewPP.setImageURI(selectedImageUri);
//                }
//            }else if(requestCode==0){
//                Uri selectedImageUri = imageReturnedIntent.getData();
//                if (null != selectedImageUri) {
//                    path = getPathFromURI(selectedImageUri);
//                    //DrawerAdapter.imageViewPP.setImageURI(selectedImageUri);
//                    Log.d(TAG, "onActivityResult: " + path);
//
////                    return path;
//                }
//            }
//        }
//    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(contentUri, proj, "", null, "");
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: ");
        Log.d(TAG, "onConnectionFailed: " + result);
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, result.getErrorCode(), 0).show();
            return;
        }
        // Called typically when the app is not yet authorized, and authorization dialog is displayed to the user.
        try {
            result.startResolutionForResult(MainActivity.this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity. " + e.getMessage());
        }
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d(TAG, "onPointerCaptureChanged: ");
    }
}