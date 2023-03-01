package hu.unideb.inf.barcodescannernew;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class TrackedCodeView extends View {

    private Rect boundingBox;

    public TrackedCodeView(Context context, Rect boundingBox){
        super(context);
        this.boundingBox = boundingBox;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint= new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);
        paint.setColor(Color.parseColor("#FF0000"));
        paint.setStrokeWidth(5);

        canvas.drawRect(boundingBox, paint);
    }
}
