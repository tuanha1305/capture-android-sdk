package co.hyperverge.hypersnapsample;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

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
import java.util.Locale;

import co.hyperverge.hypersnapsdk.R;
import co.hyperverge.hypersnapsdk.activities.HVDocsActivity;
import co.hyperverge.hypersnapsdk.activities.HVFaceActivity;
import co.hyperverge.hypersnapsdk.listeners.APICompletionCallback;
import co.hyperverge.hypersnapsdk.listeners.DocCaptureCompletionHandler;
import co.hyperverge.hypersnapsdk.listeners.FaceCaptureCompletionHandler;
import co.hyperverge.hypersnapsdk.network.HVNetworkHelper;
import co.hyperverge.hypersnapsdk.objects.HVDocConfig;
import co.hyperverge.hypersnapsdk.objects.HVError;
import co.hyperverge.hypersnapsdk.objects.HVFaceConfig;



public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    /**
     * Document and Liveness have been moved to their respective parent configuration classes.
     */
    private HVDocConfig.Document selectedDocument;
    HVFaceConfig.LivenessMode mode = HVFaceConfig.LivenessMode.TEXTURELIVENESS;
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
    }


    public void startAppropriateDocumentActivity(final HVDocConfig docConfig) {

        HVDocsActivity.start(MainActivity.this, docConfig, new DocCaptureCompletionHandler() {
            @Override
            public void onResult(HVError error, JSONObject result) {

                if (error != null) {
                    resultView.setText("ERROR: " + error.getErrorCode() + " Msg: " + error.getErrorMessage());
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

    @Override
    protected void onResume() {
        super.onResume();
        setLocale();

    }

    public void setLocale() {
        /**
         * To set a locale for country, the contry code is set in Locale constructor
         * Locale locale = new Locale("vi") - Vietnam
         */
        Locale locale = new Locale("");
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        Resources res = getBaseContext().getResources();
        res.updateConfiguration(config, res.getDisplayMetrics());

    }

    public void startFaceCaptureActivity() {
        /**
         * HVFaceConfig is the configuration class to set parameters for HVFaceActivity
         */
        HVFaceConfig config = new HVFaceConfig();
        // config.setLivenessMode(mode);
        config.setFaceCaptureTitle(" Face capture  ");
        config.setShouldShowInstructionPage(true);
        HVFaceActivity.start(MainActivity.this, config, new FaceCaptureCompletionHandler() {
            @Override
            public void onResult(HVError error, final JSONObject result, JSONObject headers) {
                if (error != null) {
                    resultView.setText(" Msg: " + error.getErrorMessage() + "ERROR: " + error.getErrorCode());
                } else {
                    try {
                        resultView.setText(result.toString());
                        Toast.makeText(MainActivity.this, result.getString("imageUri"), Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Glide.with(MainActivity.this).load(result.getString("imageUri")).dontAnimate().into((ImageView) findViewById(R.id.iv_result));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 100);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void onClick(View view) {
        /**
         * HVDocConfig is the configuration class to set parameters for HVDocsActivity
         */

        HVDocConfig docConfig = new HVDocConfig();
        docConfig.setShouldShowFlashIcon(false);
        docConfig.setDocReviewDescription("Is your document fully visible, glare free and not blurred ?");
        docConfig.setDocReviewTitle("Review your photo");
        docConfig.setDocCaptureTitle("Docs Capture");
        docConfig.setDocCaptureDescription("Make sure your document is without any glare and is fully inside");
        docConfig.setDocCaptureSubText("Front side");
        docConfig.setShouldShowReviewScreen(true);
        docConfig.setShouldShowInstructionPage(true);
        if (view.getId() == R.id.tv_a4) {
            selectedDocument = HVDocConfig.Document.A4;

            docConfig.setDocumentType(selectedDocument);
            startAppropriateDocumentActivity(docConfig);
        }
        if (view.getId() == R.id.tv_card) {
            selectedDocument = HVDocConfig.Document.CARD;
            docConfig.setDocumentType(selectedDocument);
            startAppropriateDocumentActivity(docConfig);
        }
        if (view.getId() == R.id.tv_other) {
            selectedDocument = HVDocConfig.Document.OTHER;
            docConfig.setDocumentType(selectedDocument);
            startAppropriateDocumentActivity(docConfig);
        }
        if (view.getId() == R.id.tv_passport) {
            selectedDocument = HVDocConfig.Document.PASSPORT;
            docConfig.setDocumentType(selectedDocument);
            startAppropriateDocumentActivity(docConfig);
        }

        if (view.getId() == R.id.tv_face) {
            startFaceCaptureActivity();
        }
    }



    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        if (id == R.id.texture_liveness) {
            mode = HVFaceConfig.LivenessMode.TEXTURELIVENESS;
        } else if (id == R.id.none_liveness) {
            mode = HVFaceConfig.LivenessMode.NONE;
        }
    }
}