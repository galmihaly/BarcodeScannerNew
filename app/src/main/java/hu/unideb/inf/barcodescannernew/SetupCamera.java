package hu.unideb.inf.barcodescannernew;

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

    public SetupCamera(Context context, LifecycleOwner lifecycleOwner, int lensFacing, ProcessCameraProvider cameraProvider, Preview.Builder previewBuilder, int rotation, Preview.SurfaceProvider surfaceProvider, Executor executor, ImageAnalysis.Builder imageAnalysisBuilder, int imageAnalysisType) {
        this.context = context.getApplicationContext();
        this.lifecycleOwner = lifecycleOwner;
        this.lensFacing = lensFacing;
        this.cameraProvider = cameraProvider;
        this.previewBuilder = previewBuilder;
        this.rotation = rotation;
        this.surfaceProvider = surfaceProvider;
        this.executor = executor;
        this.imageAnalysisBuilder = imageAnalysisBuilder;
        this.imageAnalysisType = imageAnalysisType;
    }

    private CodeAnalyzer codeAnalyzer;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private CameraSelector cameraSelector;
    private Preview previewUseCase;
    private ImageAnalysis analysisUseCase;


    public void initCamera(){
        this.cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        this.cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
    }

    public void initFutureMethodForCameraProvider(Runnable runnable){
        this.cameraProviderFuture.addListener(runnable, ContextCompat.getMainExecutor(context));
    }

    public void bindUsesCases(){
        if (cameraProvider == null) { return; }
        if(previewBuilder == null) { return; }
        if(imageAnalysisBuilder == null) { return; }

        if (previewUseCase != null) { cameraProvider.unbind(previewUseCase); }
        if (analysisUseCase != null) { cameraProvider.unbind(analysisUseCase); }

        // PreviewUseCase
        previewBuilder.setTargetRotation(rotation);
        previewUseCase = previewBuilder.build();
        previewUseCase.setSurfaceProvider(surfaceProvider);

        // ImageAnalysisUseCase
        imageAnalysisBuilder.setBackpressureStrategy(imageAnalysisType);
        imageAnalysisBuilder.setTargetRotation(rotation);
        analysisUseCase = imageAnalysisBuilder.build();

        codeAnalyzer = new CodeAnalyzer(context);
        analysisUseCase.setAnalyzer(executor, codeAnalyzer.getAnalizer());

        try {
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, analysisUseCase);
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase);
        } catch (Exception e) {
            Log.e("TAG", "Hiba az analizer csatlakoztatásakor!", e);
        }
    }

    public ListenableFuture<ProcessCameraProvider> getCameraProvider(){
        return this.cameraProviderFuture;
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
