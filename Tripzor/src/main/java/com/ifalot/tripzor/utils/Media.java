package com.ifalot.tripzor.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FilenameFilter;

public class Media {

    public static String getImagePath(Context context, final String name, String ext){
        if(ext == null){
            String[] files = context.getFilesDir().list(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return s.startsWith(name + ".");
                }
            });
            if(files.length > 0) return context.getFilesDir().getAbsolutePath() + "/" + files[0];
            else return null;
        }else {
            return context.getFilesDir().getAbsolutePath() + "/" + name + "." + ext;
        }
    }

    public static String getFilePath(Context context, String name){
        return context.getFilesDir().getAbsolutePath() + "/" + name;
    }

    public static Drawable getRoundedImage(Context context, FileDescriptor fd){
        Bitmap bm = BitmapFactory.decodeFileDescriptor(fd);
        int squaresize = Math.min(bm.getWidth(), bm.getHeight());
        int x = bm.getWidth() > bm.getHeight() ? (bm.getWidth() - squaresize)/2 : 0;
        int y = bm.getWidth() > bm.getHeight() ? 0 : (bm.getHeight() - squaresize)/2;
        bm = Bitmap.createBitmap(bm, x, y, squaresize, squaresize);
        RoundedBitmapDrawable rd = RoundedBitmapDrawableFactory.create(context.getResources(), bm);
        rd.setCornerRadius(Math.max(bm.getWidth(), bm.getHeight()) / 2.0f);
        return rd;
    }

    public static Drawable getRoundedImage(Context context, String filename){
        String path = getImagePath(context, filename, null);
        if(path == null) return null;
        Bitmap bm = BitmapFactory.decodeFile(path);
        int squaresize = Math.min(bm.getWidth(), bm.getHeight());
        int x = bm.getWidth() > bm.getHeight() ? (bm.getWidth() - squaresize)/2 : 0;
        int y = bm.getWidth() > bm.getHeight() ? 0 : (bm.getHeight() - squaresize)/2;
        bm = Bitmap.createBitmap(bm, x, y, squaresize, squaresize);
        RoundedBitmapDrawable rd = RoundedBitmapDrawableFactory.create(context.getResources(), bm);
        rd.setCornerRadius(Math.max(bm.getWidth(), bm.getHeight()) / 2.0f);
        return rd;
    }

}
