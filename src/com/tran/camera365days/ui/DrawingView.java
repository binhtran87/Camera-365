package com.tran.camera365days.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera.Face;
import android.view.OrientationEventListener;
import android.util.AttributeSet;
import android.view.View;

import com.tran.camera365days.util.FaceDetectionUtil;

/**
* Drawing the rectangle for Face detection and auto focus
*
**/

public class DrawingView extends View {

	private boolean haveTouch = false;
	private Rect touchArea;
	private Paint paintFocus, paintFace;
	private Face[] mFaces;
	
    private Paint mTextPaint;
    private int mDisplayOrientation;
    private int mOrientation;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paintFocus = new Paint();
		paintFocus.setColor(Color.YELLOW);
		paintFocus.setStyle(Paint.Style.STROKE);
		paintFocus.setStrokeWidth(1);
		haveTouch = false;
		
		paintFace = new Paint();
		paintFace.setColor(Color.RED);
		paintFace.setStyle(Paint.Style.STROKE);
		paintFace.setStrokeWidth(3);
		paintFace.setAntiAlias(true);
		paintFace.setDither(true);
		paintFace.setAlpha(128);
		
		mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(23);
        mTextPaint.setColor(Color.RED);
        mTextPaint.setStyle(Paint.Style.FILL);
	}
	
    public void setFaces(Face[] faces) {
        mFaces = faces;
        invalidate();
    }
	
    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        mDisplayOrientation = displayOrientation;
        invalidate();
    }
    
	public void setHaveTouch(boolean val, Rect rect) {
		haveTouch = val;
		touchArea = rect;
	}

	@Override
	public void onDraw(Canvas canvas) {

		if(haveTouch){
			canvas.drawRect(
					touchArea.left, touchArea.top, touchArea.right, touchArea.bottom, paintFocus);
		}
		
		if (mFaces != null && mFaces.length > 0) {
            Matrix matrix = new Matrix();
            FaceDetectionUtil.prepareMatrix(matrix, false, mDisplayOrientation, getWidth(), getHeight());
            canvas.save();
            matrix.postRotate(mOrientation);
            canvas.rotate(-mOrientation);
            RectF rectF = new RectF();
            for (Face face : mFaces) {
                rectF.set(face.rect);
                matrix.mapRect(rectF);
                canvas.drawRect(rectF, paintFace);
                canvas.drawText("Score " + face.score, rectF.right, rectF.top, mTextPaint);
            }
            canvas.restore();
        }

	}
}