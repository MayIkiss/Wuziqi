package com.example.wangguanghong.wuziqi;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguanghong on 2017/2/6.
 */

public class Wuziqi extends View {
    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE=10;
    private int WIN_COUNT=5;
    private Paint mPaint=new Paint();
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private float ratioPieceOfLineHeight=3*1.0f/4;
    private boolean mIsWhite=true;
    private ArrayList<Point> mWhiteArray=new ArrayList<>();
    private ArrayList<Point> mBlackArray=new ArrayList<>();
    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;
    private static final String INSTANCE="instance";
    private static final String INSTANCE_GAME_OVER="game_over";
    private static final String INSTANCE_WHITEARRAY="white_array";
    private static final String INSTANCE_BLACKARRAY="black_array";
    private static final String INSTANCE_ISWHITE="is_white";

    public Wuziqi(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece= BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2);
        mBlackPiece=BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);

        int width=Math.min(widthSize,heightSize);
        if(widthMode==MeasureSpec.UNSPECIFIED){
            width=heightSize;
        }else if(heightMode==MeasureSpec.UNSPECIFIED){
            width=widthSize;
        }
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth=w;
        mLineHeight=mPanelWidth*1.0f/MAX_LINE;
        int pieceWidth= (int) (mLineHeight*ratioPieceOfLineHeight);
        mWhitePiece=Bitmap.createScaledBitmap(mWhitePiece,pieceWidth,pieceWidth,false);
        mBlackPiece=Bitmap.createScaledBitmap(mBlackPiece,pieceWidth,pieceWidth,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mIsGameOver){
            return false;
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            int x= (int) event.getX();
            int y= (int) event.getY();
            Point p =getValidPoint(x,y);
            if(mWhiteArray.contains(p)||mBlackArray.contains(p)){
                return false;
            }
            if(mIsWhite){
                mWhiteArray.add(p);
            }else{
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite=!mIsWhite;
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int)(x/mLineHeight), (int) (y/mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPeices(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin=checkFiveInLine(mWhiteArray);
        boolean blackWin=checkFiveInLine(mBlackArray);

        if(whiteWin||blackWin){
            mIsGameOver=true;
            mIsWhiteWinner=whiteWin;
            String text=mIsWhiteWinner?"白棋胜利":"黑棋胜利";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkFiveInLine(List<Point> points) {
        for(Point p:points){
            int x=p.x;
            int y=p.y;
            boolean win=checkHorizontal(x,y,points);
            if(win)return true;
            win=checkVertical(x,y,points);
            if(win)return true;
            win=checkLeftDiagonal(x,y,points);
            if(win)return true;
            win=checkRightDiagonal(x,y,points);
            if(win)return true;
        }

        return false;
    }

    //判断xy位置的棋子是否横向胜利
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        for(int i=1;i<WIN_COUNT;i++){
            if(points.contains(new Point(x-i,y))){
                count++;
            }else {
                break;
            }
        }
        if(count==WIN_COUNT){
            return true;
        }
        for(int i=1;i<WIN_COUNT;i++){
            if(points.contains(new Point(x+i,y))){
                count++;
            }else {
                break;
            }
        }
        if(count==WIN_COUNT){
            return true;
        }

        return false;
    }

    //判断xy位置的棋子是否竖向胜利
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        for(int i=1;i<WIN_COUNT;i++){
            if(points.contains(new Point(x,y-i))){
                count++;
            }else {
                break;
            }
        }
        if(count==WIN_COUNT){
            return true;
        }
        for(int i=1;i<WIN_COUNT;i++){
            if(points.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }
        if(count==WIN_COUNT){
            return true;
        }

        return false;
    }

    //判断xy位置的棋子是否左斜向胜利
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for(int i=1;i<WIN_COUNT;i++){
            if(points.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if(count==WIN_COUNT){
            return true;
        }
        for(int i=1;i<WIN_COUNT;i++){
            if(points.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if(count==WIN_COUNT){
            return true;
        }

        return false;
    }

    //判断xy位置的棋子是否右斜向胜利
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for(int i=1;i<WIN_COUNT;i++){
            if(points.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if(count==WIN_COUNT){
            return true;
        }
        for(int i=1;i<WIN_COUNT;i++){
            if(points.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if(count==WIN_COUNT){
            return true;
        }

        return false;
    }

    private void drawPeices(Canvas canvas) {
        for(int i=0,n=mWhiteArray.size();i<n;i++){
            Point whitePoint=mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,(whitePoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    (whitePoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null);
        }
        for(int i=0,n=mBlackArray.size();i<n;i++){
            Point blackPoint=mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,(blackPoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    (blackPoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null);
        }
    }

    private void drawBoard(Canvas canvas) {
        int w=mPanelWidth;
        float lineHeight=mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int start= (int) (mLineHeight/2);
            int end= (int) (mPanelWidth-mLineHeight/2);
            int point= (int) ((0.5+i)*lineHeight);
            canvas.drawLine(start,point,end,point,mPaint);
            canvas.drawLine(point,start,point,end,mPaint);
        }

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle=new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putBoolean(INSTANCE_ISWHITE,mIsWhite);
        bundle.putParcelableArrayList(INSTANCE_WHITEARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACKARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle bundle= (Bundle) state;
            mIsGameOver=bundle.getBoolean(INSTANCE_GAME_OVER);
            mIsWhite=bundle.getBoolean(INSTANCE_ISWHITE);
            mWhiteArray=bundle.getParcelableArrayList(INSTANCE_WHITEARRAY);
            mBlackArray=bundle.getParcelableArrayList(INSTANCE_BLACKARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public void restart(){
        mBlackArray.clear();
        mWhiteArray.clear();
        mIsGameOver=false;
        mIsWhite=true;
        mIsWhiteWinner=false;
        invalidate();
    }
}
