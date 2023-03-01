package hu.unideb.inf.barcodescannernew;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

public class CodeAnalyzer {

    private ImageAnalysis.Analyzer analyzer;

    public ImageAnalysis.Analyzer getAnalizer(){
        return this::analyze;
    }

    private void analyze(@NonNull ImageProxy image) {

    }
}
