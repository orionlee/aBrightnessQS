package net.oldev.aBrightnessQS;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static class BrightnessSettingsModel {

        private final Context mContext;

        private ChangeListener mListener = null;

        public BrightnessSettingsModel(Context context) {
            mContext = context;
        }

        // TODO: real persistence
        private static String msSettings = "1,5,10,35,50,100";
    
        public String getSettings() {
            return msSettings;
        }

        public void setSettings(String settings) {
            // TODO: validation
            msSettings = settings;
            fireChangeEvent();
        }

        private void fireChangeEvent() {
            if (mListener != null) {
                mListener.onChange(msSettings);
            }
        }

        public void setOnChangeListener(ChangeListener listener) {
            mListener = listener;
            fireChangeEvent();
        }

        public static interface ChangeListener {
            void onChange(String settings);
        }
    }
    private BrightnessSettingsModel mModel;

    private void dbgMsg(String msg) {
        android.widget.Toast.makeText(getApplicationContext(), msg, 
            android.widget.Toast.LENGTH_LONG).show();      
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Now setup the UI
        setContentView(R.layout.activity_main);

        // Useful for issues below
        final TextView brightnessPctsOutput = (TextView)findViewById(R.id.brightnessPctsOutput);

        // Connect brightnessPctsOutput UI to the model
        mModel = new BrightnessSettingsModel(this);
        BrightnessSettingsModel.ChangeListener changeListener = new BrightnessSettingsModel.ChangeListener() {            
            @Override
            public void onChange(String settings) {
                brightnessPctsOutput.setText(settings);
            }
        };

        mModel.setOnChangeListener(changeListener);


        // Hook a dialog to change brightness levels
        final View brightnessPctsSection = findViewById(R.id.brightnessPctsSection);
        brightnessPctsSection.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 final CharSequence brightnessPctsStr = brightnessPctsOutput.getText();
                 showBrightnessPctsDialog(brightnessPctsStr);
             }
         });


    }

    private CharSequence getTextOfViewById(int id) {
        final TextView tView = (TextView)findViewById(id);
        return tView.getText();
    }
    private void showBrightnessPctsDialog(CharSequence curValue) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle(getTextOfViewById(R.id.brightnessPctsLabel));
        builder.setMessage(getTextOfViewById(R.id.brightnessPctsLabelDesc));

        final EditText editText = new EditText(this);
        editText.setText(curValue);

        builder.setView(editText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newVal = editText.getText().toString();
                mModel.setSettings(newVal);
                dbgMsg("After OK: " + newVal);
            }
        });
        
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Nothing to do
            }
        });

        builder.show();

    }
}

