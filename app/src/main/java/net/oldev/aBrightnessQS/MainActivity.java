package net.oldev.aBrightnessQS;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;

import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {

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
        //@see https://stackoverflow.com/questions/10903754/input-text-dialog-android

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle(getTextOfViewById(R.id.brightnessPctsLabel));
        builder.setMessage(getTextOfViewById(R.id.brightnessPctsLabelDesc));

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        //@see https://developer.android.com/training/keyboard-input/style.html#Action
        //@see https://stackoverflow.com/a/5941620
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        builder.setView(editText);

        editText.setText(curValue);
        
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newVal = editText.getText().toString();
                try {
                    mModel.setSettings(newVal);
                } catch (IllegalArgumentException iae) {
                    // TODO: proper error message display
                    dbgMsg(iae.getMessage());
                }
            }
        });
        
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Nothing to do
            }
        });

        final android.app.Dialog dialog = builder.show();

        // set it here as it requires a reference to dialog
        editText.setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO: Refactor with setPositiveButton
                    String newVal = editText.getText().toString();
                    try {
                        mModel.setSettings(newVal);
                    } catch (IllegalArgumentException iae) {
                        // TODO: proper error message display
                        dbgMsg(iae.getMessage());
                    }
                    dialog.dismiss();
                    handled = true;
                }
                return handled;
            }
        });

        /// TODO: return dialog;
    }
}

