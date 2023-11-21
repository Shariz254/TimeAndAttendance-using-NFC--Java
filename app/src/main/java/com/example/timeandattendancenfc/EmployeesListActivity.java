package com.example.timeandattendancenfc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class EmployeesListActivity extends AppCompatActivity {

    Activity act;
    Context ctx;
    public LinearLayout emp1, emp2;

    /**-------NFC----------**/
    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "successfull!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    public static String nfc_uid = null;

    String textcontent = "";
    String name;
    String idno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_list);

        act = this;
        ctx = this;

        emp1 = (LinearLayout) findViewById(R.id.emp1);
        emp2 = (LinearLayout) findViewById(R.id.emp2);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };

        emp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _dialogTapNfc();

            }
        });

        emp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _dialogTapNfc();

            }
        });

    }

    public void _dialogTapNfc() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EmployeesListActivity.this);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view_ = layoutInflaterAndroid.inflate(R.layout.tap_nfc_dialog, null);
        builder.setView(view_);
        builder.setCancelable(true);

        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        name = "TEST JANE DOE";//here load name of tapped employee
        idno = "909078";//here load id of tapped employee

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null){

            //stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{ tagDetected };


        final Button btnCancel = view_.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }


    /**Read from NFC Tag**/
    private void readFromIntent(Intent intent){
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null; //https://developer.android.com/reference/android/nfc/NdefMessage

            if (rawMsgs != null){
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++){
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }

            buildTagViews(msgs);
        }
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0)
            return;

        String text = "";

        byte[] payload = msgs[0].getRecords()[0].getPayload();

        if (payload.length > 0){
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
            int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
            try {
                //get the text
                text = new String(payload, languageCodeLength +1, payload.length - languageCodeLength - 1, textEncoding);
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.toString());
            }
            Log.v("APA SAKI", "+++++++++++++++++++++"+ text);

        } else {
            Toast.makeText(getApplicationContext(), "Clocked In Successfully!", Toast.LENGTH_LONG).show();
            EmployeesListActivity.this.finish();
        }

    }

    /**Write to NFC Tag**/
    private void write(String text, Tag tag) throws IOException, FormatException{
        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);

        //get an instance of Ndef for the tag
        Ndef ndef = Ndef.get(tag);

        if (ndef == null){

            //lets try to format the tag in ndef
            NdefFormatable nForm = NdefFormatable.get(tag);

            if (nForm != null){
                nForm.connect();
                nForm.format(message);
                nForm.close();
            }
        } else {
            ndef.connect();
            ndef.writeNdefMessage(message);
            ndef.close();
        }
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes(StandardCharsets.US_ASCII);
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Tag tag = myTag;
            byte[] id = tag.getId();

            Log.v("YYYYYYYYYYY", getHex(id));
            Log.v("YYYYYYYYYYYYYY", " " + getDec(id));
            Log.v("YYYYYYYYYYYYYYYY", " " + getReversed(id));

//            Toast.makeText(this, "NFC ID. " + getHex(id), Toast.LENGTH_LONG).show();

            nfc_uid = getHex(id);

            try {
                if (myTag == null){
                    Toast.makeText(ctx, ERROR_DETECTED, Toast.LENGTH_LONG).show();
                } else {
                    String message = name+"!"+idno;
                    write(message,myTag);
                    Toast.makeText(ctx, WRITE_SUCCESS, Toast.LENGTH_LONG).show();
//                    alertDialog.dismiss();
                    EmployeesListActivity.this.finish();
                }
            }catch (IOException e) {
                Toast.makeText(ctx, WRITE_ERROR, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }catch (FormatException e) {
                Toast.makeText(ctx, WRITE_ERROR, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }else {
            readFromIntent(getIntent());
        }
    }



    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }


    /******************************************************************************
     **********************************Enable Write********************************
     ******************************************************************************/
    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    /******************************************************************************
     **********************************Disable Write*******************************
     ******************************************************************************/
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append("");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }


}