package hu.unideb.inf.barcodescannernew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivityView extends AppCompatActivity implements IMainActivityView {

//    private static final String TAG = "MLKit Barcode";
//    private static final int PERMISSION_CODE = 1001;
//    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private PreviewView previewView;
    private TextView codeTextView;
    private ConstraintLayout trackCL;
    private LinearLayout drawCL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        codeTextView = findViewById(R.id.codeTextView);
        trackCL = findViewById(R.id.trackCL);
        drawCL = findViewById(R.id.drawCL);


        MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this, getApplicationContext(), this);
        mainActivityPresenter.initCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public int getRotationPreviewView() {
        return previewView.getDisplay().getRotation();
    }

    @Override
    public Preview.SurfaceProvider getSurfaceProvider() {
        return previewView.getSurfaceProvider();
    }
}