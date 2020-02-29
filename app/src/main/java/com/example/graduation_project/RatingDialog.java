package com.example.graduation_project;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class RatingDialog {

    private Context context;
    int i = 0;

    public RatingDialog(Context context) { this.context = context;}

    public int create() {


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_rating_dialog);
        dialog.show();

        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        final TextView textView = (TextView) dialog.findViewById(R.id.rating_title);
        final Button btn_ok = (Button) dialog.findViewById(R.id.okButton);
        final Button btn_cancle = (Button) dialog.findViewById(R.id.cancelButton);


        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            switch (Math.round(rating))
            {
                case 1:
                    i = 1;
                    textView.setText("1");
                    break;
                case 2:
                    i = 2;
                    textView.setText("2");
                    break;
                case 3:
                    i = 3;
                    textView.setText("3");
                    break;
                case 4:
                    i = 4;
                    textView.setText("4");
                    break;
                case 5:
                    i = 5;
                    textView.setText("5");
                    break;
                case 0:
                    i = 0;
                    textView.setText("0");
                    break;
            }
        });


        btn_ok.setOnClickListener(v -> {
            Toast.makeText(context, "int i = " + i, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btn_cancle.setOnClickListener(v -> {i=6; dialog.dismiss();});


        return i;
    }

}
