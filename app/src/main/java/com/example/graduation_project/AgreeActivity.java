/*

(LoginActivity                        -> (FounderMenu) -> (BlockMenu) -> (MakeVA->KA->VP->KR->KP->CS->CR->CH->CC)
LoginRequest,                                          -> (ShowBlock, ShowBlockRequest, BlockChangeRequest)
FouderLogin.php)                                       -> (ShowConsultant, userListAdapter, cousultant)
 ↓ 회원가입 버튼 클릭시                                 -> (Board, BoardAdapter, BoardInfo) -> (WriteBoard, BoardRegisterRequest),(ModifyBoard, BoardUpdateRequest)
(AgreeActivity(여기!))                                                                            -> (BoardComment, CommentRegister, CommentDeleteRequest, BoardDeleteRequest, CommentAdapter, CommentInfo)
 ↓ 라디오버튼 체크 후 확인 버튼 클릭시
(RegisterActivity
RegisterRequst, FoundRegister.php
ValidateRequest
MakeIdRequest, MakeId.php)

이 클래스는 본격적인 회원가입에 앞서 약관에 동의하느냐를 묻는 이용약관 클래스입니다.
약관의 내용은 res/values/strings.xml 에 있으며, 이는 실재 법에 의거하지 않아 그럴듯해 보일 뿐이지 만약 정식으로 앱을 배포한다면 수정해야합니다.
이 클래스는 보시다시피 약관에 동의하느냐라는 라디오버튼을 '예'로 전환하면 그 다음 본격적인 회원가입이 이루어지는 RegisterActivity로 이동하는 클래스이므로
굳이 일일히 설명을 남기지 않겠습니다.

 */

package com.example.graduation_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

/*
    작성자 : 김재성
    작성일 : 2018-11-21

    설명 : 이용약관을 띄움
 */

public class AgreeActivity extends AppCompatActivity {
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree);

        RadioButton noButton = (RadioButton)findViewById(R.id.no);
        next=(Button)findViewById(R.id.next);
        next.setEnabled(false);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next.setEnabled(false);
            }
        });
        RadioButton yesButton = (RadioButton)findViewById(R.id.yes);
        next=(Button)findViewById(R.id.next);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next.setEnabled(true);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AgreeActivity.this,RegisterActivity.class);
                        AgreeActivity.this.startActivity(intent);
                        finish();
                    }
                });
            }
        });


    }
}
