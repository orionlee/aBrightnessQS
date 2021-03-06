package net.oldev.aBrightnessQS;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BrightnessSettingsModel mModel;
    private BrightnessManager mBrightnessManager;
    private BrightnessManager.BrightnessContentObserver mBrightnessContentObserver;

    private void dbgMsg(String msg) {
        android.widget.Toast.makeText(getApplicationContext(), msg, 
            android.widget.Toast.LENGTH_LONG).show();      
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Now setup the UI
        setContentView(R.layout.activity_main);

        // init member variables 
        mModel = new BrightnessSettingsModel(this);

        mBrightnessManager = new BrightnessManager(this);
        mBrightnessContentObserver = mBrightnessManager.new BrightnessContentObserver(new Handler(), 
            new BrightnessManager.ChangeListener() {
                @Override 
                public void onChange(int newBrightnessPct) {
                    PLog.d("mBrightnessContentObserver.onChange(): " + newBrightnessPct);
                    doShowCurBrightnessPct(newBrightnessPct);
                }
            });
        
        // Useful for issues below
        final TextView brightnessPctsOutput = (TextView)findViewById(R.id.brightnessPctsOutput);

        // Connect brightnessPctsOutput UI to the model
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
                 new BrightnessPctsDialogBuilder().show(brightnessPctsStr);
             }
         });

        //
        // Non-UI related
        //

        // see BrightnessTileUpdateService for the service's starting points
        BrightnessTileUpdateService.start(this);
    }

    
    private void doShowCurBrightnessPct(int curBrightnessPct) {
        final TextView curBrightnessPctOutput = (TextView)findViewById(R.id.curBrightnessPctOutput);
        final String curBrightnessPctStr = ( curBrightnessPct != BrightnessManager.BRIGHTNESS_AUTO ? 
                                                curBrightnessPct + "%" :
                                                 getResources().getString(R.string.brightness_auto_label) );
        curBrightnessPctOutput.setText(curBrightnessPctStr);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // UI to show current brightness percentage
        // It's updated upon re-entering the screen
        // 
        final int curBrightnessPct = mBrightnessManager.getPct();
        doShowCurBrightnessPct(curBrightnessPct);

        // Use case: After the activity is run, when changes brightness on quick settings
        // and comes back here, the UI will not updated by onResume()
        // Using this change listener to compensate for it.
        mBrightnessManager.registerOnChange(mBrightnessContentObserver); 
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Activity no longer visible: no need to listen to brightness change anymore.
        mBrightnessManager.unregisterOnChange(mBrightnessContentObserver); 
    }

    // Encapsulates the logic of the dialog UI and actions
    // It is primarily stateless, except it needs parent's mModel and Context
    private class BrightnessPctsDialogBuilder {
        public void show(CharSequence curValue) {
            show(curValue, null);
        }

        public void show(CharSequence curValue, CharSequence errMsg) {
            //@see https://stackoverflow.com/questions/10903754/input-text-dialog-android

            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AppTheme_Dialog_Alert);

            builder.setTitle(getTextOfViewById(R.id.brightnessPctsLabel));
            builder.setMessage(getTextOfViewById(R.id.brightnessPctsLabelDesc));

            final EditText editText = new EditText(MainActivity.this);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            //@see https://developer.android.com/training/keyboard-input/style.html#Action
            //@see https://stackoverflow.com/a/5941620
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editText.setId(R.id.brightnessPctsInput); // for androidTest
            // TODO: style EditText to be consistent with the colors of the app

            builder.setView(editText);

            editText.setText(curValue);
            if (errMsg != null && errMsg.length() > 0) {
                editText.setError(errMsg);
            }

            builder.setPositiveButton(R.string.ok_btn_label, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    updateModelSettingsWithEditText(editText);
                }
            });

            builder.setNegativeButton(R.string.cancel_btn_label, new DialogInterface.OnClickListener() {
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
                        updateModelSettingsWithEditText(editText);
                        dialog.dismiss();
                        handled = true;
                    }
                    return handled;
                }
            });

        }
        private void updateModelSettingsWithEditText(final EditText editText) {
            String newVal = editText.getText().toString();
            try {
                MainActivity.this.mModel.setSettings(newVal);
            } catch (BrightnessSettingsModel.IllegalSettingsException ise) {
                String errMsg = MainActivity.this.getString(R.string.err_msgf_invalid_settings, ise.getInvalidLevel());
                show(newVal, errMsg);
            }
        }

        private CharSequence getTextOfViewById(int id) {
            final TextView tView = (TextView)MainActivity.this.findViewById(id);
            return tView.getText();
        }
    }
}

