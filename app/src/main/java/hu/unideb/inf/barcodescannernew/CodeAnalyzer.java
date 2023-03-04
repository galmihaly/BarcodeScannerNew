package hu.unideb.inf.barcodescannernew;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.lang.ref.WeakReference;
import java.util.List;

public class CodeAnalyzer implements ImageAnalysis.Analyzer {

    private WeakReference<SetupCamera> ctpmw;

    private Context context;
    private InputImage inputImage;
    private BarcodeScannerOptions options;
    private BarcodeScanner barcodeScanner;
//    private TrackedCodeView trackedCodeView;
    private Message message;

    public ImageAnalysis.Analyzer getAnalizer(){
        return this::analyze;
    }

    public CodeAnalyzer(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setSetupCameraWeakReference(SetupCamera setupCamera) {
        this.ctpmw = new WeakReference<>(setupCamera);
    }

    @SuppressLint({"UnsafeOptInUsageError", "UseCompatLoadingForDrawables"})
    public void analyze(@NonNull ImageProxy image) {
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
//                        trackedCodeView = new TrackedCodeView(context, barcodes.get(0).getBoundingBox());
                        //drawCL.addView(trackedCodeView);

                        message = Util.createMessage(Util.IS_CREATED, "A trackedCodeView elkészült!");
                        message.obj = barcodes.get(0).getBoundingBox();

                        if(ctpmw != null && ctpmw.get() != null && message != null) {
                            ctpmw.get().sendResultToPresenter(message);
                        }


                        //innen fogjuk visszairányítani a kapott eredményt

//                        codeTextView.setText(barcodes.get(0).getDisplayValue());
//                        trackCL.setBackground(ResourcesCompat.getDrawable(getResources() ,R.drawable.green_background, null));
                    }
                    else {
//                        trackCL.setBackground(getResources().getDrawable(R.drawable.red_background));
//                        drawCL.removeAllViews();

                        message = Util.createMessage(Util.IS_NOT_CREATED, "A trackedCodeView nem készült el!");
                        message.obj = null;

                        if(ctpmw != null && ctpmw.get() != null && message != null) {
                            ctpmw.get().sendResultToPresenter(message);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("TAG", "A kód feldolgozása sikertelen!", e))
                .addOnCompleteListener(task -> image.close());
    }
}
