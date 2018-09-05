package sdk.facecamera.sdk;

import android.content.Context;
import android.graphics.Bitmap;

import com.sun.jna.Native;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * 特征值提取工具类
 */
public final class FeatureGetter {

	static{
		System.loadLibrary("hafeaturesdk");
		System.loadLibrary("jnidispatch");
	}
	
	private static volatile boolean g_bInited = false;
	private static Object g_oLocker = new Object();

	private static void init() {
		synchronized (g_oLocker) {
			if (g_bInited) return;
			int ret = FeatureSdkLibrary.INSTANCE.HA_FeatureSdkInit(512, 40, 1);
			if(ret == 0) g_bInited = true;
		}
	}
	private static byte[] getPixelsBGR(Bitmap image) {
		int bytes = image.getByteCount();

		ByteBuffer buffer = ByteBuffer.allocate(bytes);
		image.copyPixelsToBuffer(buffer);

		byte[] temp = buffer.array();

		byte[] pixels = new byte[(temp.length/4) * 3];

		for (int i = 0; i < temp.length/4; i++) {

			pixels[i * 3] = temp[i * 4 + 2];		//B
			pixels[i * 3 + 1] = temp[i * 4 + 1]; 	//G
			pixels[i * 3 + 2] = temp[i * 4 ];		//R

		}

		return pixels;
	}

	/**
	 * 从图片检测人脸并归一化
	 *
	 * @param img 要归一化的图片
	 * @return 元素个数为2的数组，如果提取失败则两个都为null；如果提取成功，则第一个元素为byte[150*150*3]，是归一化之后的bgr矩阵数据，第二个元素为Bitmap，是人脸特写（缩放过的），可存入相机
	 */
	public static Object[] getFeatureWithThumbnail(Bitmap img) {
		init();
		synchronized (g_oLocker) {
			if(!g_bInited) return null;
			if(img == null) return null;
			ByteBuffer pbgr_out =  ByteBuffer.allocateDirect(200 * 200 * 3);
	    	IntBuffer outwidth = IntBuffer.allocate(1);
	    	IntBuffer outheight = IntBuffer.allocate(1);
	    	Object[] ret_ = new Object[2];
	    	byte[] bgr = getPixelsBGR(img);
	    	ByteBuffer pbgr_in = ByteBuffer.wrap(bgr);
			ByteBuffer twisBuf = ByteBuffer.allocateDirect(150 * 150 * 3);
			IntBuffer _twisW = IntBuffer.allocate(1);
			IntBuffer _twisH = IntBuffer.allocate(1);
	    	int ret = FeatureSdkLibrary.INSTANCE.HA_TwistFaceImage(pbgr_in, img.getWidth(), img.getHeight(), pbgr_out, outwidth, outheight, twisBuf, _twisW, _twisH);
			byte[] twisBgr = null;
			if(ret != 0) return ret_;
			int __twisW = _twisW.get();
			int __twisH = _twisH.get();
			twisBgr = new byte[__twisW * __twisH * 3];
			twisBuf.get(twisBgr);
	    	ret_[0] = twisBgr;
			int intWidth = outwidth.get();
			int intHeight = outheight.get();
			int intByteCount = intWidth * intHeight * 3;
			byte[] bytesImage = new byte[intByteCount];
			pbgr_out.get(bytesImage);
			byte[] byteColors = new byte[intWidth * intHeight * 4];
			for(int i = 0; i < intWidth * intHeight; ++i) {
				byteColors[i * 4 + 0] = bytesImage[i * 3 + 2];
				byteColors[i * 4 + 1] = bytesImage[i * 3 + 1];
				byteColors[i * 4 + 2] = bytesImage[i * 3 + 0];
                byteColors[i * 4 + 3] = (byte)255;
			}
			Bitmap bmpImage = Bitmap.createBitmap(intWidth, intHeight, Bitmap.Config.ARGB_8888);
			bmpImage.copyPixelsFromBuffer(ByteBuffer.wrap(byteColors));
			ret_[1] = bmpImage;
	    	return ret_;
		}
	}
}
