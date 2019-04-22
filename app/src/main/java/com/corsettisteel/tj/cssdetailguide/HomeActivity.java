package com.corsettisteel.tj.cssdetailguide;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private Button startButton;
    private ImageView ivMap, ivPhone, ivWebsite;
    private TextView tvAddress1, tvAddress2, tvPhone, tvWebsite;
    private String address1 = "2515 New Lenox Road", address2 = "Joliet, Illinois 60433", website = "http://www.corsettisteel.com", phoneNumber = "8157264083";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        startButton = (Button) findViewById(R.id.startButton);
        ivMap = (ImageView) findViewById(R.id.ivMapIcon);
        ivPhone = (ImageView) findViewById(R.id.ivPhoneIcon);
        ivWebsite = (ImageView) findViewById(R.id.ivWebIcon);
        tvAddress1 = (TextView) findViewById(R.id.tvAddress2);
        tvAddress2 = (TextView) findViewById(R.id.tvAddress3);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvWebsite = (TextView) findViewById(R.id.tvWebsite);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                HomeActivity.this.startActivity(intent);
            }
        });

        ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                if (dialIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(dialIntent);
                }

            }
        });

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("geo:0,0?q=" + address1 + " " + address2);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        ivWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                if (webIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(webIntent);
                }

            }
        });

        tvAddress1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Corsetti Steel Address", address1 + " " + address2);
                cbm.setPrimaryClip(clip);
                longClickVibrate();
                Toast.makeText(getBaseContext(), "Address copied.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        tvAddress2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Corsetti Steel Address", address1 + " " + address2);
                cbm.setPrimaryClip(clip);
                longClickVibrate();
                Toast.makeText(getBaseContext(), "Address copied.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        tvPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Corsetti Steel Phone", phoneNumber);
                cbm.setPrimaryClip(clip);
                longClickVibrate();
                Toast.makeText(getBaseContext(), "Phone number copied.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        tvWebsite.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Corsetti Steel Website", website);
                cbm.setPrimaryClip(clip);
                longClickVibrate();
                Toast.makeText(getBaseContext(), "Website copied.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void longClickVibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }
}
