package hu.unideb.inf.barcodescannernew;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import hu.unideb.inf.barcodescannernew.tasksmanager.PresenterThreadCallback;

public class SetupCamera {

    private WeakReference<PresenterThreadCallback> presenterThreadCallbackWeakReference;

    private Context context;
    private LifecycleOwner lifecycleOwner;
    private int lensFacing;
    private ProcessCameraProvider cameraProvider;
    private Preview.Builder previewBuilder;
    private int rotation;
    private Preview.SurfaceProvider surfaceProvider;
    private Executor executor;
    private ImageAnalysis.Builder imageAnalysisBuilder;
    private int imageAnalysisType;

    public SetupCamera(Context context, int lensFacing, Preview.SurfaceProvider surfaceProvider, Executor executor, int imageAnalysisType) {
        this.context = context;
        this.lensFacing = lensFacing;
        this.surfaceProvider = surfaceProvider;
        this.executor = executor;
        this.imageAnalysisType = imageAnalysisType;
    }

    private CodeAnalyzer codeAnalyzer;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private CameraSelector cameraSelector;
    private Preview previewUseCase;
    private ImageAnalysis analysisUseCase;


    public void initCamera(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void initFutureMethodForCameraProvider(){
        cameraProviderFuture.addListener(() -> {
            try {
                this.cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                Log.e("Hiba", e.getMessage());
            }
        }, ContextCompat.getMainExecutor(context));
    }

    public void bindUsesCases(){
        try {
            this.cameraProvider = cameraProviderFuture.get();

            if (this.cameraProvider == null) { return; }
            if(this.previewBuilder == null) { return; }
            if(this.imageAnalysisBuilder == null) { return; }

            if (previewUseCase != null) { this.cameraProvider.unbind(previewUseCase); }
            if (analysisUseCase != null) { this.cameraProvider.unbind(analysisUseCase); }

            // PreviewUseCase
            this.previewBuilder = new Preview.Builder();
            this.previewBuilder.setTargetRotation(this.rotation);
            previewUseCase = this.previewBuilder.build();
            previewUseCase.setSurfaceProvider(this.surfaceProvider);

            // ImageAnalysisUseCase
            this.imageAnalysisBuilder = new ImageAnalysis.Builder();
            this.imageAnalysisBuilder.setBackpressureStrategy(imageAnalysisType);
            this.imageAnalysisBuilder.setTargetRotation(rotation);
            analysisUseCase = this.imageAnalysisBuilder.build();

            codeAnalyzer = new CodeAnalyzer(this.context);
            analysisUseCase.setAnalyzer(this.executor, codeAnalyzer.getAnalizer());

            this.cameraProvider.bindToLifecycle(this.lifecycleOwner, cameraSelector, analysisUseCase);
            this.cameraProvider.bindToLifecycle(this.lifecycleOwner, cameraSelector, previewUseCase);

        } catch (ExecutionException | InterruptedException e) {
            Log.e("Hiba", e.getMessage());
        }
    }

    public void setPresenterCallback(PresenterThreadCallback presenterThreadCallback) {
        this.presenterThreadCallbackWeakReference = new WeakReference<>(presenterThreadCallback);
    }

    public void sendResultToPresenter(Message message){
        if(presenterThreadCallbackWeakReference!= null && presenterThreadCallbackWeakReference.get() != null) {
            presenterThreadCallbackWeakReference.get().sendResultToPresenter(message);
        }
    }
}
