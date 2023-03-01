package hu.unideb.inf.barcodescannernew;

import android.content.Intent;
import android.widget.ImageButton;

import androidx.camera.core.Preview;


public interface IMainActivityView {
    int getRotationPreviewView();
    Preview.SurfaceProvider getSurfaceProvider();

}
