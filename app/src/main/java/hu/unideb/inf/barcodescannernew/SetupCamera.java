package hu.unideb.inf.barcodescannernew;

import android.content.Context;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SetupCamera {

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

        previewBuilder.setTargetRotation(rotation);
        previewUseCase = previewBuilder.build();
        previewUseCase.setSurfaceProvider(surfaceProvider);

        imageAnalysisBuilder.setBackpressureStrategy(imageAnalysisType);
        imageAnalysisBuilder.setTargetRotation(rotation);
        analysisUseCase = imageAnalysisBuilder.build();
        CodeAnalyzer codeAnalyzer = new CodeAnalyzer();
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
}
