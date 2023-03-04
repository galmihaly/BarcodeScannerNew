package hu.unideb.inf.barcodescannernew;

import android.content.Intent;
import android.graphics.Point;
import android.widget.ImageButton;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;


public interface IMainActivityView {
    int getRotationPreviewView();
    Preview.SurfaceProvider getSurfaceProvider();
    void getPointsFromPresenter(Point[] points);
    LifecycleOwner getLifeCycle();
}
