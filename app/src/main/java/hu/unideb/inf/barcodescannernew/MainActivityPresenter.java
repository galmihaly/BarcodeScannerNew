package hu.unideb.inf.barcodescannernew;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageButton;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import hu.unideb.inf.barcodescannernew.tasksmanager.CustomThreadPoolManager;
import hu.unideb.inf.barcodescannernew.tasksmanager.PresenterThreadCallback;

public class MainActivityPresenter implements IMainActivityPresenter, PresenterThreadCallback {

    private LifecycleOwner lifecycleOwner;
    private Context context;
    private IMainActivityView iMainActivityView;
    private CustomThreadPoolManager mCustomThreadPoolManager;
    private MainActivityHandler mMainActivityHandler;

    public MainActivityPresenter(LifecycleOwner lifecycleOwner, Context context, IMainActivityView iMainActivityView) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
        this.iMainActivityView = iMainActivityView;
    }

    @Override
    public void initTaskManager() {
        try {

            mMainActivityHandler = new MainActivityHandler(Looper.myLooper(), this);
            mCustomThreadPoolManager = CustomThreadPoolManager.getsInstance();
            mCustomThreadPoolManager.setPresenterCallback(this);

        }
        catch (Exception e){
            Log.e("", e.getMessage());
        }
    }

    @Override
    public void initCamera() {

        int lensFacing = CameraSelector.LENS_FACING_BACK;
        Preview.SurfaceProvider surfaceProvider = iMainActivityView.getSurfaceProvider();
        Executor executor = Executors.newSingleThreadExecutor();
        int imageAnalysisType = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST;

        SetupCamera setupCamera = new SetupCamera(
                context,
                lensFacing,
                surfaceProvider,
                executor,
                imageAnalysisType
        );

        CodeAnalyzer codeAnalyzer = new CodeAnalyzer(context);
        codeAnalyzer.setSetupCameraWeakReference(setupCamera);

        setupCamera.setLifecycleOwner(iMainActivityView.getLifeCycle());
        setupCamera.initCamera();
        setupCamera.initFutureMethodForCameraProvider();
        setupCamera.setRotation(iMainActivityView.getRotationPreviewView());
        setupCamera.bindUsesCases();
        setupCamera.setPresenterCallback(this);
    }

    @Override
    public void sendPointsToView(Point[] points) {
        if(iMainActivityView == null) return;
        iMainActivityView.getPointsFromPresenter(points);
    }

    @Override
    public void sendResultToPresenter(Message message) {
        if(mMainActivityHandler == null) return;
        mMainActivityHandler.sendMessage(message);
    }


    private static class MainActivityHandler extends Handler {

        private WeakReference<IMainActivityPresenter> iMainActivityPresenterWeakReference;

        public MainActivityHandler(Looper looper, IMainActivityPresenter iMainActivityPresenter) {
            super(looper);
            this.iMainActivityPresenterWeakReference = new WeakReference<>(iMainActivityPresenter);
        }

        // Ui-ra sz??nt ??zenetet kezelej??k itt
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case Util.IS_CREATED:{

                    if(msg.obj instanceof Point[]){
                        Point[] points = (Point[]) msg.obj;
                        iMainActivityPresenterWeakReference.get().sendPointsToView(points);
                    }
                    break;
                }
            }
        }
    }
}
