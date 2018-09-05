package sdk.facecamera.sdk;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;

interface FeatureSdkLibrary extends Library {
	public static final String JNA_LIBRARY_NAME = "hafeaturesdk";
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(FeatureSdkLibrary.JNA_LIBRARY_NAME);
	public static final FeatureSdkLibrary INSTANCE = (FeatureSdkLibrary)Native.loadLibrary(FeatureSdkLibrary.JNA_LIBRARY_NAME, FeatureSdkLibrary.class);
	public static final int ERR_GET_FACE = (int)-1;
	public static final int ERR_NONE = (int)0;
	public static final int ERR_GET_FEATURE = (int)-3;
	public static final int ERR_FACE_NUM = (int)-2;
	public static final int ERR_TWIST_FACE = (int)-4;
	public static final int ERR_FACE_SIZE = (int)-5;
	int HA_FeatureSdkInit(int feature_size, int minface, int fastmode);
	int HA_TwistFaceImage(ByteBuffer inbgr, int inwidth, int inheight, ByteBuffer outbgr, IntBuffer outwidth, IntBuffer outheight, ByteBuffer twistbgr, IntBuffer twistw, IntBuffer twisth);
	int HA_FeatureSdkDeinit();
}
