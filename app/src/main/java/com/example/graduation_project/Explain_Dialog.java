package com.example.graduation_project;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;


/*
    작성자 : 김평기
    작성일 : 2019-08-10
    제공 기능 : 설명 다이얼로그 띄우는 용.
    관련 액티비티 : ARActivity
    외부 라이브러리 및 플러그인 :


    기능 설명 : ARActivity에 다이얼로그 띄우는 용.

    상세정보 단계 :
            커스터마이징한 설명 다이얼로그를 생성함.
 */

public class Explain_Dialog {

    private Context context;

    public Explain_Dialog(Context context) { this.context = context; }

    public void create(final String overview){
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.showinfo);
        WindowManager.LayoutParams layoutParams = dlg.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        dlg.show();

        final TextView explain_textView = (TextView) dlg.findViewById(R.id.ar_textView);
        explain_textView.setText(overview);
    }
}
