package com.example.graduation_project;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/*
    작성자 : 김평기
    작성일 : 2019-09-03
    제공 기능 : AR
    관련 액티비티 : CompareActivity, MapCompareActivity, TresureNode, TresureSetting, RatingDialog, DemoUtils, Explain_Dialog
    외부 라이브러리 및 플러그인 : Sceneform, ARCore

    기능 설명 : 카메라를 이용해 바닥을 인식한 후, 구글 ARCore을 활용하여 3D 이미지를 증강현실로 띄움.

    AR 단계 :
        1. 바닥 인식.
        2. ModelRenderable에 전 액티비티에서 받아온 contentid를 토대로 리소스 할당
        3. 베이스 노드 생성 이후, 그 위치를 기준으로 ModelRenderable을 적용한 노드를 생성함.
        이후 분기
        4-1. 설명 버튼 : 화면 중앙에 openAPI로 받아온 overview 정보를 가진 TextView를 띄움.
        4-2. 평점 버튼 : 그 문화재에 대한 평점을 매김.
        4-3. 회전 버튼 : ObjectAnimator를 이용해 노드에 회전 애니매이션을 적용함. boolean으로 온오프.
 */

public class ARActivity extends AppCompatActivity {

    private static final int RC_PERMISSIONS = 0x123;
    private boolean installRequested;

    private GestureDetector gestureDetector;
    private Snackbar loadingMessageSnackbar = null;

    private ArSceneView arSceneView;
    private ModelRenderable tresureRenderable;
//    private ViewRenderable overviewRenderable;
//    // 0528 - 폐기했으나 나중에 쓸데있으면 재활용(2019-09-03)
//    private ViewRenderable buttonRenderable;
//    // 0528 - 폐기했으나 나중에 쓸데있으면 재활용(2019-09-03)
    private final TresureSetting tresureSettings = new TresureSetting();

    private boolean hasFinishedLoading = false;

    private boolean hasPlacedSystem = false;

    CompletableFuture<ModelRenderable> tresureStage;

    String contentid;
    String overview_content;

    int get_rating;

    boolean turn_able = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        contentid = intent.getStringExtra("id");
        overview_content = intent.getStringExtra("overview");


        if (!DemoUtils.checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_ar);
        arSceneView = findViewById(R.id.ar_scene_view);




        if (contentid.equals("126207"))
        {
            tresureStage =
                    ModelRenderable.builder().setSource(this, Uri.parse("content_star.sfb")).build();
        }
        else if (contentid.equals("125983")) {
            tresureStage =
                    ModelRenderable.builder().setSource(this, Uri.parse("content_seven.sfb")).build();
        }
        else if (contentid.equals("126510")){
            tresureStage =
                    ModelRenderable.builder().setSource(this, Uri.parse("content_jongmyo.sfb")).build();
        }
        else if (contentid.equals("127425")){
            tresureStage =
                    ModelRenderable.builder().setSource(this, Uri.parse("content_Dokrip.sfb")).build();
        }
        else if (contentid.equals("126512")){
            tresureStage =
                    ModelRenderable.builder().setSource(this, Uri.parse("content_Gwanghwa.sfb")).build();
        }
        else if (contentid.equals("126216")){
            tresureStage =
                    ModelRenderable.builder().setSource(this, Uri.parse("content_seokgul.sfb")).build();
        }

        FloatingActionButton explain_btn = (FloatingActionButton) findViewById(R.id.explan_btn);
        FloatingActionButton rating_btn = (FloatingActionButton) findViewById(R.id.rating_btn);
        FloatingActionButton turning_btn = (FloatingActionButton) findViewById(R.id.turning_btn);

        explain_btn.setOnClickListener(v -> {
            Explain_Dialog explain_dialog = new Explain_Dialog(ARActivity.this);
            explain_dialog.create(overview_content);
        });

        rating_btn.setOnClickListener(v -> {
            RatingDialog ratingDialog = new RatingDialog(this);
            get_rating = ratingDialog.create();
            if (get_rating > 5) {
                // 값 입력 안 되게.
                Toast.makeText(getApplicationContext(), "걸러야함", Toast.LENGTH_SHORT).show();
            }
            else if (get_rating <= 5) {Toast.makeText(getApplicationContext(), "get_rating == " + get_rating, Toast.LENGTH_LONG).show(); }
        });

        turning_btn.setOnClickListener(v -> {
            if (turn_able == false) {
                tresureSettings.setTurn_able(true);
                turn_able = true;
                Toast.makeText(getApplicationContext(), "turn Y", Toast.LENGTH_LONG).show();
            }
            else {
                tresureSettings.setTurn_able(false);
                turn_able = false;
                Toast.makeText(getApplicationContext(), "turn N", Toast.LENGTH_LONG).show();
            }
        });

        // 버튼 변경
        Locale locale = this.getResources().getConfiguration().locale;
        if (locale.getLanguage() == "en")
        {
            explain_btn.setBackgroundResource(R.drawable.explan_eng);
            rating_btn.setBackgroundResource(R.drawable.rating_eng);
            turning_btn.setBackgroundResource(R.drawable.turning_eng);
        }
        else if (locale.getLanguage() == "ko") {
            explain_btn.setBackgroundResource(R.drawable.explan_kr);
            rating_btn.setBackgroundResource(R.drawable.rating_kr);
            turning_btn.setBackgroundResource(R.drawable.turning_kr);
        }
        else {
            explain_btn.setBackgroundResource(R.drawable.explan_hanza);
            rating_btn.setBackgroundResource(R.drawable.rating_hanza);
            turning_btn.setBackgroundResource(R.drawable.turning_hanza);
        }


        CompletableFuture<ViewRenderable> overviewStage =
                ViewRenderable.builder().setView(this, R.layout.showinfo).build();
        // 0528
        CompletableFuture<ViewRenderable> buttonStage =
                ViewRenderable.builder().setView(this, R.layout.arbutton).build();
        // 0528

        CompletableFuture.allOf(
                tresureStage,
                overviewStage,
                buttonStage)
                .handle(
                        (notUsed, throwable) -> {
                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderable", throwable);
                                return null;
                            }

                            try {
                                tresureRenderable = tresureStage.get();
//                                overviewRenderable = overviewStage.get();
//                                // 0528
//                                buttonRenderable = buttonStage.get();
//                                // 0528
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(this, "Unable to load renderable", ex);
                            }

                            return null;
                        });

        // Set up a tap gesture detector.
        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                onSingleTap(e);
                                return true;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) {
                                return true;
                            }
                        });

        // Set a touch listener on the Scene to listen for taps.
        arSceneView
                .getScene()
                .setOnTouchListener(
                        (HitTestResult hitTestResult, MotionEvent event) -> {

                            if (!hasPlacedSystem) {
                                return gestureDetector.onTouchEvent(event);
                            }


                            return false;
                        });


        arSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (loadingMessageSnackbar == null) {
                                return;
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                if (plane.getTrackingState() == TrackingState.TRACKING) {
                                    hideLoadingMessage();
                                }
                            }
                        });

        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (arSceneView == null) {
            return;
        }

        if (arSceneView.getSession() == null) {

            try {
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = DemoUtils.hasCameraPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (arSceneView != null) {
            arSceneView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (arSceneView != null) {
            arSceneView.destroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!DemoUtils.hasCameraPermission(this)) {
            if (!DemoUtils.shouldShowRequestPermissionRationale(this)) {

                DemoUtils.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void onSingleTap(MotionEvent tap) {
        if (!hasFinishedLoading) {
            return;
        }

        Frame frame = arSceneView.getArFrame();
        if (frame != null) {
            if (!hasPlacedSystem && tryPlaceSystem(tap, frame)) {
                hasPlacedSystem = true;
            }
        }
    }

    private boolean tryPlaceSystem(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    Anchor anchor = hit.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arSceneView.getScene());
                    Node System = createSystem();

                    anchorNode.addChild(System);
                    return true;
                }
            }
        }

        return false;
    }

    private Node createSystem() {
        Node base = new Node();

        Node tresure = new Node();
        tresure.setParent(base);
        tresure.setLocalPosition(new Vector3(0.0f, 0.5f, 0.0f));

        TresureNode tresureVisual = new TresureNode(tresureSettings);
        tresureVisual.setParent(tresure);
        tresureVisual.setLocalScale(new Vector3(1.0f, 1.0f, 1.0f));
        tresureVisual.setRenderable(tresureRenderable);

        return base;
    }


    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ARActivity.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();


        final ContentFrameLayout lm = (ContentFrameLayout) findViewById(android.R.id.content);
        ContentFrameLayout.LayoutParams params = new ContentFrameLayout.
                LayoutParams(ContentFrameLayout.LayoutParams.WRAP_CONTENT,
                ContentFrameLayout.LayoutParams.WRAP_CONTENT);


    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }


}
