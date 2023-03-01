package hu.unideb.inf.barcodescannernew;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class CodeAnalyzer {

    private Context context;

    private InputImage inputImage;
    private BarcodeScannerOptions options;
    private BarcodeScanner barcodeScanner;
    private TrackedCodeView trackedCodeView;

    public ImageAnalysis.Analyzer getAnalizer(){
        return this::analyze;
    }

    @SuppressLint({"UnsafeOptInUsageError", "UseCompatLoadingForDrawables"})
    private void analyze(@NonNull ImageProxy image) {
        if (image.getImage() == null) return;

        inputImage = InputImage.fromMediaImage(
                image.getImage(),
                image.getImageInfo().getRotationDegrees()
        );

        options = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
        barcodeScanner = BarcodeScanning.getClient(options);

        barcodeScanner.process(inputImage)
                .addOnSuccessListener((List<Barcode> barcodes)->{
                    if (barcodes.size() > 0) {
                        trackedCodeView = new TrackedCodeView(context, barcodes.get(0).getBoundingBox());
                        //drawCL.addView(trackedCodeView);


                        //innen fogjuk visszairányítani a kapott eredményt

//                        codeTextView.setText(barcodes.get(0).getDisplayValue());
//                        trackCL.setBackground(ResourcesCompat.getDrawable(getResources() ,R.drawable.green_background, null));
                    }
                    else {
//                        trackCL.setBackground(getResources().getDrawable(R.drawable.red_background));
//                        drawCL.removeAllViews();
                    }
                })
                .addOnFailureListener(e -> Log.e("TAG", "A kód feldolgozása sikertelen!", e))
                .addOnCompleteListener(task -> image.close());
    }
}
