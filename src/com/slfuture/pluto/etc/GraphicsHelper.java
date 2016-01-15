package com.slfuture.pluto.etc;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	 * 解码图像文件
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
}
