package com.slfuture.pluto.etc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.Log;

/**
 * 图像帮助类
 */
public class GraphicsHelper {
	/**
	 * 隐藏构造函数
	 */
	private GraphicsHelper() { }
	
	/**
	 * 生成圆形的图片
	 * 
	 * @param bitmap 图片对象
	 * @param width 目标宽度
	 * @param height 目标高度
	 * @return 打圆的图片
	 */
	public static Bitmap makeCycleImage(Bitmap bitmap, int width, int height) {
		float radius = 0;
		if(width > height) {
			radius = height;
		}
		else {
			radius = width;
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		RectF rect = new RectF(0, 0, radius, radius);
		canvas.drawRoundRect(rect, radius/2, radius/2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, rect, paint);
		return result;
	}

	/**
	 * 给位图增加环
	 * 
	 * @param bitmap 位图对象
	 * @param color 边框颜色
	 * @param stroke 位图对象
	 */
	public static Bitmap makeImageRing(Bitmap bitmap, int color, int strokeWidth) {
		float radius = bitmap.getWidth();
		if(radius > bitmap.getHeight()) {
			radius = bitmap.getHeight();
		}
		radius = radius / 2 - strokeWidth + 1;
		Bitmap result = bitmap;
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, radius, paint);
		return result;
	}

	public static Bitmap makeCornerImage(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888); 
		Canvas canvas = new Canvas(output); 
		final int color = 0xff424242; 
		final Paint paint = new Paint(); 
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
		final RectF rectF = new RectF(rect); 
		final float roundPx = pixels; 
		paint.setAntiAlias(true); 
		canvas.drawARGB(0, 0, 0, 0); 
		paint.setColor(color); 
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
		canvas.drawBitmap(bitmap, rect, rect, paint); 
		return output; 
	}

	/**
	 * 解析资源
	 * 
	 * @param context 上下文
	 * @param resourceId 资源ID
	 * @return 位图对象
	 */
	public static Bitmap decodeResource(Context context, int resourceId) {
		return BitmapFactory.decodeResource(context.getResources(), resourceId);
	}

	/**
	 * 解码图像文件
	 * 
	 * @param file 文件
	 */
	public static Bitmap decodeFile(File file) {
		if(file.exists()) {
			return BitmapFactory.decodeFile(file.getAbsolutePath());
		}
		return null;
	}

	/**
	 * 根据DIP解码图像文件
	 * 
	 * @param context 上下文
	 * @param file 文件
	 * @param width 目标宽度
	 * @param height 目标高度
	 */
	public static Bitmap decodeFile(Context context, File file, int width, int height) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return decodeFile(file, (int)(width * scale + 0.5f), (int)(height * scale + 0.5f));
	}

	/**
	 * 根据像素解码图像文件
	 * 
	 * @param file 文件
	 * @param width 目标宽度
	 * @param height 目标高度
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap decodeFile(File file, int width, int height) {
		if(null == file || !file.exists()) {
			return null;
		}
		BitmapFactory.Options opts = null;
		if (width > 0 && height > 0) {
			opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(file.getPath(), opts);
			// 计算图片缩放比例
			final int minSideLength = Math.min(width, height);
			opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
			opts.inJustDecodeBounds = false;
			opts.inInputShareable = true;
			opts.inPurgeable = true;
		}
		try {
			return BitmapFactory.decodeFile(file.getPath(), opts);
		}
		catch (OutOfMemoryError e) {
			Log.e("pluto", "decodeFile failed", e);
		}
		return null;
	}

	/**
	 * 保存位图
	 * 
	 * @param bitmap 位图对象
	 * @param target 保存位置
	 * @param width 宽度
	 * @param height 高度
	 */
	public static void saveFile(Bitmap bitmap, File target, int width, int height) throws IOException {
        FileOutputStream stream = null;  
        try {  
        	stream = new FileOutputStream(target);
        	if(width > 0 && height > 0) {
        		int quality = width * 100 / bitmap.getWidth();
        		if(quality < height * 100 / bitmap.getHeight()) {
        			quality = height * 100 / bitmap.getHeight();
        		}
        		if(quality > 100) {
        			quality = 100;
        		}
        		else if(quality <= 0) {
        			quality = 1;
        		}
        		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        	}
        	else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        	}
            stream.flush();
        }
        catch (IOException e) {
                throw e;
        }
        finally {
        	if(null != stream) {
        		stream.close();
        	}
        	stream = null;
        }
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize = 0;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} 
		else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		}
		else if (minSideLength == -1) {
			return lowerBound;
		}
		else {
			return upperBound;
		}
	}

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px
     * 
     * @param context 上下文
     * @param dpValue DP值
     * @return 像素值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * 
     * @param context 上下文
     * @param pxValue 像素值
     * @return DP值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
