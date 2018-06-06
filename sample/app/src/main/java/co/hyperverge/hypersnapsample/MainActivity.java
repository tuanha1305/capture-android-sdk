package co.hyperverge.hypersnapsample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import co.hyperverge.hypersnapsdk.R;
import co.hyperverge.hypersnapsdk.activities.HVDocsActivity;
import co.hyperverge.hypersnapsdk.activities.HVFaceActivity;
import co.hyperverge.hypersnapsdk.listeners.CaptureCompletionHandler;
import co.hyperverge.hypersnapsdk.objects.Error;
import co.hyperverge.hypersnapsdk.objects.HyperSnapParams;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private final int MY_PERMISSIONS_REQUEST_CAMERA_ACTIVITY = 101;
    private ArrayList<String> runtimePermissions = new ArrayList<>(Arrays.asList(Manifest.permission.CAMERA));

    private HyperSnapParams.Document selectedDocument;
    HyperSnapParams.LivenessMode mode = HyperSnapParams.LivenessMode.TEXTURELIVENESS;
    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView = (TextView) findViewById(R.id.tv_result);
        findViewById(R.id.tv_a4).setOnClickListener(this);
        findViewById(R.id.tv_other).setOnClickListener(this);
        findViewById(R.id.tv_card).setOnClickListener(this);
        findViewById(R.id.tv_passport).setOnClickListener(this);
        findViewById(R.id.tv_face).setOnClickListener(this);
        ((RadioGroup) (findViewById(R.id.face_value))).setOnCheckedChangeListener(this);
        getPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA_ACTIVITY:

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Cannot start as all the permissions were not granted", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                return;
        }
    }

    private void getPermissions() {
        ArrayList<String> missingPermissions = checkForMissingPermissions();
        // Assume thisActivity is the current activity

        if (missingPermissions.size() == 0) {
            return;
        }

        ArrayList<String> toBeRequestedPermissions = new ArrayList<>();
        ArrayList<String> rationalePermissions = new ArrayList<>();
        for (String missingPermission : missingPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    missingPermission)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                rationalePermissions.add(missingPermission);

            } else {

                // No explanation needed, we can request the permission.
                toBeRequestedPermissions.add(missingPermission);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (toBeRequestedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    toBeRequestedPermissions.toArray(new String[0]),
                    MY_PERMISSIONS_REQUEST_CAMERA_ACTIVITY);
        }
        if (rationalePermissions.size() > 0) {
            String permissionsTxt = "";
            for (String perm : rationalePermissions) {
                String[] permSplit = perm.split("\\.");
                permissionsTxt += permSplit[permSplit.length - 1] + ", ";
            }

            permissionsTxt = permissionsTxt.substring(0, permissionsTxt.length() - 2);
            Toast.makeText(MainActivity.this, "Please give " + permissionsTxt + " permissions by going to Settings", Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<String> checkForMissingPermissions() {
        ArrayList<String> missingPermissions = new ArrayList<>();
        for (String permission : runtimePermissions) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }

    public void startAppropriateDocumentActivity(HyperSnapParams.Document document) {

        HVDocsActivity.start(MainActivity.this, selectedDocument, new CaptureCompletionHandler() {
            @Override
            public void onResult(Error error, JSONObject result) {

                if (error != null) {
                    resultView.setText("ERROR: " + error.getError().toString() + " Msg: " + error.getErrMsg());
                } else {
                    resultView.setText("RESULT: " + result.toString());
                    try {
                        Glide.with(MainActivity.this).load(result.getString("imageUri")).into((ImageView) findViewById(R.id.iv_result));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ScrollView sv = findViewById(R.id.sv_main);
                sv.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void startFaceCaptureActivity() {

        HVFaceActivity.start(MainActivity.this, mode, new CaptureCompletionHandler() {
            @Override
            public void onResult(Error error, JSONObject result) {
                if (error != null) {
                    resultView.setText( " Msg: " + error.getErrMsg() + "ERROR: " + error.getError().name() );
                } else {
                    resultView.setText(result.toString());
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_a4) {
            selectedDocument = HyperSnapParams.Document.A4;
            selectedDocument.setTopText("A4 Document");
            selectedDocument.setBottomText("Place your A4 document in the box");
            startAppropriateDocumentActivity(selectedDocument);
        }
        if (view.getId() == R.id.tv_card) {
            selectedDocument = HyperSnapParams.Document.CARD;
            selectedDocument.setTopText("Card Front Side");
            selectedDocument.setBottomText("Place your Card in the box");
            startAppropriateDocumentActivity(selectedDocument);
        }
        if (view.getId() == R.id.tv_other) {
            selectedDocument = HyperSnapParams.Document.OTHER;
            selectedDocument.setTopText("Custom Document");
            selectedDocument.setBottomText("Place your document in the box");
//            selectedDocument.setAspectRatio(0.25f);
            startAppropriateDocumentActivity(selectedDocument);
        }
        if (view.getId() == R.id.tv_passport) {
            selectedDocument = HyperSnapParams.Document.PASSPORT;
            selectedDocument.setTopText("Passport Front Side");
            selectedDocument.setBottomText("Place your Passport in the box");
            startAppropriateDocumentActivity(selectedDocument);
        }

        if (view.getId() == R.id.tv_face) {
            startFaceCaptureActivity();
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        if (id == R.id.texture_liveness) {
            mode = HyperSnapParams.LivenessMode.TEXTURELIVENESS;
        } else if (id == R.id.gesture_liveness) {
            mode = HyperSnapParams. LivenessMode.TEXTUREANDGESTURELIVENESS;
        } else if (id == R.id.none_liveness) {
            mode = HyperSnapParams.LivenessMode.NONE;
        }
    }
}