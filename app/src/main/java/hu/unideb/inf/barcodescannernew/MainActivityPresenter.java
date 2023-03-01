package hu.unideb.inf.barcodescannernew;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageButton;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import hu.unideb.inf.barcodescannernew.tasksmanager.CustomThreadPoolManager;
import hu.unideb.inf.barcodescannernew.tasksmanager.PresenterThreadCallback;

public class MainActivityPresenter implements IMainActivityPresenter, PresenterThreadCallback {

    private LifecycleOwner lifecycleOwner;
    private Context context;
    private IMainActivityView iMainActivityView;
    private CustomThreadPoolManager mCustomThreadPoolManager;
    private MainActivityHandler mMainActivityHandler;

    private List<Integer> typesOfLoginButton = new ArrayList<>();

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

        // Ui-ra szánt üzenetet kezelejük itt
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case Util.BUTTON_USER_PASS:{

                    if(msg.obj instanceof ImageButton){
                        ImageButton button = (ImageButton) msg.obj;
                        iMainActivityPresenterWeakReference.get().sendButtonToPresenter(button);
                    }

                    break;
                }
            }
        }
    }
}
