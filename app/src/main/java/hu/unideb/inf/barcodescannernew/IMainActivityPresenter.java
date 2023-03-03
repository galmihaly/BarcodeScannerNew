package hu.unideb.inf.barcodescannernew;

import android.graphics.Point;
import android.os.Message;
import android.widget.ImageButton;

import androidx.camera.core.Preview;

public interface IMainActivityPresenter {
    void initTaskManager();
    void initCamera();
    void sendPointsToView(Point[] points);
}
