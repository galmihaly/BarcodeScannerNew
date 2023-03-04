package hu.unideb.inf.barcodescannernew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityView extends AppCompatActivity implements IMainActivityView {

    private static final String TAG = "MLKit Barcode";
    private static final int PERMISSION_CODE = 1001;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private PreviewView previewView;
    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private Preview previewUseCase;
    private ImageAnalysis analysisUseCase;
    private TextView codeTextView;
    private ConstraintLayout trackCL;
    private LinearLayout drawCL;

    private MainActivityPresenter mainActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        codeTextView = findViewById(R.id.codeTextView);
        trackCL = findViewById(R.id.trackCL);
        drawCL = findViewById(R.id.drawCL);

        previewView = findViewById(R.id.previewView);
        mainActivityPresenter = new MainActivityPresenter(this, getApplicationContext(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainActivityPresenter.initCamera();
    }

    @Override
    public int getRotationPreviewView() {
        return previewView.getDisplay().getRotation();
    }

    @Override
    public Preview.SurfaceProvider getSurfaceProvider() {
        return previewView.getSurfaceProvider();
    }

    @Override
    public LifecycleOwner getLifeCycle() {
        return this;
    }

    public void startCamera() {
        if(ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            if(mainActivityPresenter != null) mainActivityPresenter.initCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (requestCode == PERMISSION_CODE) {
            if(mainActivityPresenter != null) mainActivityPresenter.initCamera();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void getPointsFromPresenter(Point[] points) {

        Canvas canvas = new Canvas();

        Paint paint= new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);
        paint.setColor(Color.parseColor("#FF0000"));
        paint.setStrokeWidth(5);

        canvas.drawLine(points[0].x, points[0].y, points[1].x, points[1].y, paint);
        canvas.drawLine(points[1].x, points[1].y, points[2].x, points[2].y, paint);
        canvas.drawLine(points[2].x, points[2].y, points[3].x, points[3].y, paint);
        canvas.drawLine(points[3].x, points[3].y, points[0].x, points[0].y, paint);

        View view = new View(getApplicationContext());

        if(drawCL != null){
            drawCL.addView(view);
        }
    }
}