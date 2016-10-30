package com.partymaker.cinaitaren;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vladimir on 29.10.16.
 */

public class OrganizationsView extends View {
    private final Paint textPaint;
    private Map<String, Integer> icons = new HashMap<>();

    private Integer ANGLE = 40;

    Paint paint;
    Bitmap bitmap;
    Matrix matrix;
    int displayHeight;
    int displayWidth;

    Double currentDegrees;
    List<Pair<String,Map<String,Object>>>  organizations;
    private int framesPerSecond = 60;

    public OrganizationsView(Context context, AttributeSet attrs) {
        super(context,attrs);

        icons.put("dental", R.mipmap.dental);
        icons.put("hotels", R.mipmap.hotels);
        icons.put("beauty", R.mipmap.beauty);
        icons.put("auto", R.mipmap.auto);
        icons.put("beauty shops", R.mipmap.beauty_shops);
        icons.put("restaurants", R.mipmap.restaurants);
        icons.put("supermarket", R.mipmap.supermarket);
        icons.put("banks", R.mipmap.banks);
        icons.put("gasstation", R.mipmap.gasstation);
        icons.put("fitness", R.mipmap.fitness);
        icons.put("drugstores", R.mipmap.drugstores);
        icons.put("services", R.mipmap.services);
        icons.put("medicine", R.mipmap.medicine);
        icons.put("bars", R.mipmap.bars);
        icons.put("atms", R.mipmap.atms);
        icons.put("malls", R.mipmap.malls);
        icons.put("mass media", R.mipmap.mass_media);
        icons.put("IT", R.mipmap.it);
        icons.put("airports", R.mipmap.airports);
        icons.put("entertainments", R.mipmap.entertainments);
        icons.put("travel", R.mipmap.travel);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(50);
        textPaint.setColor(Color.WHITE);

        //bitmapSource = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        displayHeight = displaymetrics.heightPixels;
        displayWidth = displaymetrics.widthPixels;

        //bitmap = Bitmap.createBitmap(bitmapSource, 0, 0, 256, 256, matrix, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(0, 0, displayWidth, displayHeight);


        if(organizations != null && currentDegrees != null){
            Double lastPosX = 0d;
            Double lastPosY = 0d;
            try {
                for(Pair<String,Map<String,Object>> organization:organizations){

                    Double posX = ((double)organization.second.get("angle") - currentDegrees);
                    if((double)organization.second.get("angle") < ANGLE){
                        if(posX < - 360 + ANGLE ){
                            posX = 360 - Math.abs(posX);
                        }
                    }

                    if((double)organization.second.get("angle") > 360 - ANGLE){
                        if(posX > 360 - ANGLE){
                            posX = posX - 360;
                        }
                    }

                    L.d("organization.first = "+organization.first+" currentDegree = " + currentDegrees + " posX = " + posX + " organization angle = " + organization.second.get("angle") + " organization distanse = " + organization.second.get("distance"));

                    Double finalPosX = posX * (displayWidth/ANGLE) + displayWidth/2 - 128;

                    Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), icons.get(organization.first));
                    canvas.drawBitmap(bitmapSource, finalPosX.intValue(), 150, paint);

                    canvas.drawText(organization.second.get("companyName") + "", finalPosX.intValue() - 100, 510, textPaint);
                    canvas.drawText(organization.second.get("name") + "", finalPosX.intValue() - 100, 590, textPaint);
                    canvas.drawText(organization.second.get("distance") + " м.", finalPosX.intValue(), 670, textPaint);
                }
            }catch (Exception e){

            }

        }
        this.postInvalidateDelayed( 1000 / framesPerSecond);
    }

    public void setOrganizations(List<Pair<String,Map<String,Object>>> organizations){
        this.organizations = organizations;
    }
    public void setCurrentDegrees(Double currentDegrees){
        this.currentDegrees = currentDegrees;
    }
}