package sdk.facecamera.sdk;

import android.graphics.Rect;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.view.SurfaceHolder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import sdk.facecamera.sdk.events.CaptureCompareDataReceivedEventHandler;
import sdk.facecamera.sdk.events.ConnectedStateChangedEventHandler;
import sdk.facecamera.sdk.events.StreamDataReceivedEventHandler;
import sdk.facecamera.sdk.pojos.CaptureCompareData;
import sdk.facecamera.sdk.pojos.Face;
import sdk.facecamera.sdk.pojos.ListFaceCriteria;
import sdk.facecamera.sdk.pojos.ListFaceResult;
import sdk.facecamera.sdk.tlv.TLVCodecFactory;
import sdk.facecamera.sdk.tlv.Util;

/**
 * 人脸相机对象
 * <br>
 * 通过此对象操作远程设备，
 */
public final class FaceCamera {

    /**
     * 设备状态
     */
    public enum State {
        /**
         * new了，还未Initialize
         */
        NotInitialize,
        /**
         * 未连接(或第一次连接未成功)
         */
        NotConnected,
        /**
         * 重连中(第一次连接成功后网络异常，正在尝试重连)
         */
        ReConnecting,
        /**
         * 已断开(第一次连接成功后网络异常，已经断开)
         */
        Disconnect,
        /**
         * 已连接
         */
        Connected,
        /**
         * 已断开(手动操作)
         */
        Disconnected,
        /**
         * 已卸载
         */
        UnInitialized
    }

    // region public properties
    private String ip;
    private String username;
    private String password;
    private volatile State m_state = State.NotInitialize;

    /**
     * 获取设备状态
     *
     * @return 设备当前状态
     */
    public State getState() {
        return m_state;
    }

    /**
     * 获取设备IP
     *
     * @return 设备IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * 获取设备登录用户名
     *
     * @return 设备登录用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 获取设备登录密码
     *
     * @return 设备登录密码
     */
    public String getPassword() {
        return password;
    }

    protected void setIp(String ip) {
        this.ip = ip;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected void setPassword(String password) {
        this.password = password;
    }
    // endregion public properties

    // region events
    private ConnectedStateChangedEventHandler connectedStateChangedEventHandler;
    private CaptureCompareDataReceivedEventHandler captureCompareDataReceivedEventHandler;
    private StreamDataReceivedEventHandler streamDataReceivedEventHandler;

    /**
     * 设置设备连接状态改变事件处理函数
     *
     * @param _connectedStateChangedEventHandler 连接状态改变事件处理函数
     * @return 当前对象，伪Builder
     */
    public FaceCamera onConnectedStateChanged(ConnectedStateChangedEventHandler _connectedStateChangedEventHandler) {
        this.connectedStateChangedEventHandler = _connectedStateChangedEventHandler;
        return this;
    }

    /**
     * 注册对比抓拍数据接收事件回调
     *
     * @param _captureCompareDataReceivedEventHandler 对比抓拍数据接收事件处理函数
     * @return 当前对象
     */
    public FaceCamera onCaptureCompareDataReceived(CaptureCompareDataReceivedEventHandler _captureCompareDataReceivedEventHandler) {
        this.captureCompareDataReceivedEventHandler = _captureCompareDataReceivedEventHandler;
        return this;
    }

    /**
     * 注册视频数据接收事件回调
     *
     * @param _streamDataReceivedEventHandler 视频数据接收事件处理函数
     * @return 当前对象
     */
    public FaceCamera onStreamDataReceived(StreamDataReceivedEventHandler _streamDataReceivedEventHandler) {
        this.streamDataReceivedEventHandler = _streamDataReceivedEventHandler;
        return this;
    }
    // endregion events

    // region event triggers
    protected void fireConnectedStateChangedEvent(boolean state) {
        if (connectedStateChangedEventHandler != null) {
            connectedStateChangedEventHandler.onConnectedStateChanged(state);
        }
    }
    // endregion event triggers

    // region private properties
    private volatile boolean _played = false;
    private SurfaceHolder _holder;
    private Object _locker = new Object();
    private NioSocketConnector configConnection, streamConnection;
    private IoSession configSession, streamSession;
    private volatile boolean configConnected, streamConnected;
    private MediaCodec _codec;
    // endregion private properties


    // region public methods
    public FaceCamera() {
        TLVCodecFactory codecFactory = new TLVCodecFactory();
        ProtocolCodecFilter codecFilter = new ProtocolCodecFilter(codecFactory);

        configConnection = new NioSocketConnector();
        configConnection.getFilterChain().addLast("tlv", codecFilter);
        configConnection.setHandler(new ConfigConnectorHandler());
        configConnection.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 5); // 10秒未发出数据的话要发心跳
        configConnection.addListener(new ConfigAutoReconnectHandler());
        configConnection.setConnectTimeoutMillis(3000);
        streamConnection = new NioSocketConnector();
        streamConnection.getFilterChain().addLast("tlv", codecFilter);
        streamConnection.setHandler(new StreamConnectorHandler());
        streamConnection.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 5); // 10秒未发出数据的话要发心跳
        streamConnection.addListener(new StreamAutoReconnectHandler());
        streamConnection.setConnectTimeoutMillis(3000);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        unInitialize();
    }

    /**
     * 初始化设备
     *
     * @param ip       设备IP
     * @param userName 登录用户名
     * @param passWord 登录密码
     * @return 是否初始化成功，如不成功，不可继续进行连接操作
     */
    public synchronized boolean initialize(String ip, String userName, String passWord) {
        if (m_state != State.NotInitialize && m_state != State.UnInitialized)
            return false; // 状态判定，只能是未初始化和已卸载的设备才能进行初始化操作
        this.ip = ip;
        this.username = userName;
        this.password = passWord;
        m_state = State.NotConnected;
        return true;
    }

    /**
     * 反初始化设备 <br>
     * 如果设备已连接，则立即断开 <br>
     * 如果不显式调用此函数，则此函数会在对象被回收时调用
     *
     * @return 是否反初始化成功<br>
     * 除非设备已反初始化或还未初始化<br>
     * 不然均会成功
     */
    public synchronized boolean unInitialize() {
        if (m_state == State.NotInitialize || m_state == State.UnInitialized)
            return false; // 状态判定，未初始化或已卸载不能够进行卸载操作
        if (m_state != State.NotConnected && m_state != State.Disconnected) // 处于正常连接和正在重连的设备需要断开连接
        {
            disConnect();
        }
        m_state = State.UnInitialized;
        _played = false;
        return true;
    }

    /**
     * 与设备建立连接
     *
     * @return 是否连接成功<br>
     * 如果未成功，则认为是网络不畅，我们会不停尝试重连<br>
     * 如果不希望自动重连，则请调用{@link #disConnect()}或{@link #unInitialize()}释放资源
     */
    public synchronized boolean connect() {
        switch (m_state) {
            case NotInitialize: // 未初始化，返回false
                return false;
            case ReConnecting: // 正在重连，返回false
                return false;
            case Connected: // 已经连上，返回true
                return true;
            case UnInitialized: // 已经卸载，返回false
                return false;
            default:
                break;
        }
        if (doConnect()) {
            if (m_state == State.NotConnected) // 如果连接立即成功，而且是第一次连接，则手动触发一次Connected事件
                fireConnectedStateChangedEvent(true);
            m_state = State.Connected;
            return true;
        }
        m_state = State.ReConnecting; // 如果连接未成功，则设备进入自动重连状态
        return false;
    }

    /**
     * 断开设备
     *
     * @return 是否操作成功<br>
     * 除非设备已经断开/反初始化或还未连接/初始化<br>
     * 不然均会成功
     */
    public synchronized boolean disConnect() {
        if (m_state != State.Connected && m_state != State.ReConnecting) // 只能在正常连接时和处于自动重连状态中才能断开
        {
            return false;
        }
        if (m_state == State.Connected) // 重连中的应该报过一次了，就不再上报
            fireConnectedStateChangedEvent(false); // 手动断开的sdk不触发回调，因此手动发布一个事件
        m_state = State.Disconnected;
        doDisconnect();
        return true;
    }

    /**
     * 播放实时视频画面
     * <br>
     * 不是函数成功返回后视频画面即刻展现，可能会有网络和解码的延迟，甚至可能根本没有收到设别的视频数据
     *
     * @param holder 要播放画面的控件<br>可以不传递此参数，表示只接收数据而不展示，如果这样，就需要通过回调获取h264数据
     * @return 是否播放成功（只要设备处于连接状态且本函数为第一次被调用，则返回成功）
     */
    public synchronized boolean startVideoPlay(SurfaceHolder holder) {
        if ((m_state != State.Connected && m_state != State.ReConnecting) || _played)
            return false;
        if (_holder != null)
            doStopVideoPlay();
        _holder = holder;
        _played = true;
        doStartVideoPlay();
        return true;
    }

    /**
     * 停止播放实时画面
     *
     * @return 是否关闭成功<br>
     * 除非设备未连接或并未开始播放<br>
     * 不然均返回成功
     */
    public synchronized boolean stopVideoPlay() {
        if ((m_state != State.Connected && m_state != State.ReConnecting) || !_played)
            return false;
        _played = false;
        doStopVideoPlay();
        return true;
    }

    private volatile int _ack_addface = -2;

    /**
     * 向相机下发人脸模板
     *
     * @param toAdd         待下发的人脸
     * @param timeOutInMili 超时时间（毫秒）
     * @return 一个代码，0表示成功 -1表示相机未连接 -2表示超时 -3表示参数不合法（函数内校验，设备端也可能返回参数不合法但不使用-3这个代码） 其它值请询问技术支持人员
     */
    public synchronized int addFace(Face toAdd, long timeOutInMili) {
        if (m_state != State.Connected) return -1;
        _ack_addface = -2;
        try {
            int sendRet = sendFaceData(toAdd, 402);
            if (sendRet != 0) return sendRet;
            synchronized (_locker) {
                _locker.wait(timeOutInMili);
                return _ack_addface;
            }
        } catch (Exception ex) {
            return -3;
        }
    }

    private volatile int _ack_delface = -2;

    /**
     * 删除人脸模板<br>根据指定编号
     *
     * @param id            要删除的编号
     * @param timeOutInMili 超时时间
     * @return 一个代码，0表示成功 -1 表示相机未连接 -2表示超时 -3表示参数不合法（函数内校验，设备端也可能返回参数不合法但不使用-3这个代码） 其它值请询问技术支持人员
     */
    public synchronized int deleteFace(String id, long timeOutInMili) {
        if (m_state != State.Connected) return -1;
        _ack_delface = -2;
        try {
            if (configSession == null || !configSession.isConnected() || id == null || id.isEmpty())
                return -3;
            return deleteFaceCore(id, -1, timeOutInMili);
        } catch (Exception ex) {
            return -3;
        }
    }

    /**
     * 删除人脸模板<br>删除指定角色下所有
     *
     * @param role          要删除的角色 0：普通人员 1：白名单 2：黑名单
     * @param timeOutInMili 超时时间
     * @return 一个代码，0表示成功 -1 表示相机未连接 -2表示超时 -3表示参数不合法（函数内校验，设备端也可能返回参数不合法但不使用-3这个代码） 其它值请询问技术支持人员
     */
    public synchronized int deleteFace(int role, long timeOutInMili) {
        if (m_state != State.Connected) return -1;
        _ack_delface = -2;
        try {
            if (configSession == null || !configSession.isConnected() || (role != 0 && role != 1 && role != 2))
                return -3;
            return deleteFaceCore(null, role, timeOutInMili);
        } catch (Exception ex) {
            return -3;
        }
    }

    private volatile int _ack_modface = -2;

    /**
     * 修改人脸模板<br>通过对象中编号进行修改，覆盖设备库中对应模板其余属性
     *
     * @param toModify      需要修改的模板数据
     * @param timeOutInMili 超时时间
     * @return 一个代码，0表示成功 -1 表示相机未连接 -2表示超时 -3表示参数不合法（函数内校验，设备端也可能返回参数不合法但不使用-3这个代码） 其它值请询问技术支持人员
     */
    public synchronized int modifyFace(Face toModify, long timeOutInMili) {
        if (m_state != State.Connected) return -1;
        _ack_modface = -2;
        try {
            int sendRet = sendFaceData(toModify, 402);
            if (sendRet != 0) return sendRet;
            synchronized (_locker) {
                _locker.wait(timeOutInMili);
                return _ack_modface;
            }
        } catch (Exception ex) {
            return -3;
        }
    }

    private volatile int _ack_listface = -2;
    private ArrayList<Face> _ret_listface_faces = new ArrayList<Face>();
    private int _ret_listface_total = 0;

    /**
     * 分页查询人脸模板数据
     *
     * @param criteria      查询条件（包括页码和条数）
     * @param result        接收符合条件的人脸模板数据容器
     * @param timeOutInMili 超时时间
     * @return 一个代码，0表示成功 -1 表示相机未连接 -2表示超时 -3表示参数不合法（函数内校验，设备端也可能返回参数不合法但不使用-3这个代码） 其它值请询问技术支持人员
     */
    public synchronized int listFace(ListFaceCriteria criteria, ListFaceResult result, long timeOutInMili) {
        if (m_state != State.Connected) return -1;
        _ack_listface = -2;
        _ret_listface_faces.clear();
        _ret_listface_total = 0;
        try {
            if (configSession == null || !configSession.isConnected() || result == null) return -3;
            if (criteria.getRole() != -1 && criteria.getRole() != 0 && criteria.getRole() != 1 && criteria.getRole() != 2)
                return -3;
            if (criteria.getPageNo() < 1) return -3;
            if (criteria.getPageSize() < 1 || criteria.getPageSize() > 100) return -3;
            IoBuffer buffer = IoBuffer.allocate(28);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt(Util.createType(401));
            buffer.putInt(20);
            buffer.putInt(criteria.getRole());
            buffer.putInt(criteria.getPageNo());
            buffer.putInt(criteria.getPageSize());
            buffer.put((byte) (criteria.isGetFeatureData() ? 1 : 0));
            buffer.put((byte) (criteria.isGetImageData() ? 1 : 0));
            buffer.put(new byte[6]);
            buffer.flip();
            configSession.write(buffer);
            synchronized (_locker) {
                _locker.wait(timeOutInMili);
                if (_ack_listface == 0) {
                    result.setFaces(_ret_listface_faces.toArray(new Face[0]));
                    result.setTotal(_ret_listface_total);
                }
                return _ack_listface;
            }
        } catch (Exception ex) {
            return -3;
        }
    }
    // endregion public

    // region private methods
    private byte[] lastBuf;
    private List<byte[]> nals = new ArrayList<>(30);
    private boolean csdSet = false; // sps和pps是否设置，在mediacodec里面可以不显式设置到mediaformat，但需要在buffer的前两帧给出
    private int lastWidth = 1080;
    private int lastHeight = 1920;

    private synchronized void handleH264(int width, int height, byte[] buffer) {
        if (_holder == null || _holder.getSurface() == null) return;
        if (_codec == null || (lastWidth != width || lastHeight != height)) {
            lastBuf = null;
            nals.clear();
            if (_codec != null) {
                _codec.stop();
                _codec.release();
            }
            lastWidth = width;
            lastHeight = height;
            try {
                _codec = MediaCodec.createDecoderByType("video/avc");
                MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", lastWidth, lastHeight);
                _codec.configure(mediaFormat, _holder.getSurface(), null, 0);
                _codec.start();
            } catch (IOException e) {
                if (_codec != null) {
                    _codec.stop();
                    _codec.release();
                    _codec = null;
                }
            }
        }
        if (_codec == null) return;
        byte[] typedAr = null;
        if (buffer == null || buffer.length < 1) return;
        if (lastBuf != null) {
            typedAr = new byte[buffer.length + lastBuf.length];
            System.arraycopy(lastBuf, 0, typedAr, 0, lastBuf.length);
            System.arraycopy(buffer, 0, typedAr, lastBuf.length, buffer.length);
        } else {
            typedAr = buffer;
        }
        int lastNalEndPos = 0;
        byte b1 = -1; // 前一个
        byte b2 = -2; // 前二个
        List<Integer> nalStartPos = new ArrayList<Integer>();
        for (int i = 0; i < typedAr.length - 1; i += 2) {
            byte b_0 = typedAr[i];
            byte b_1 = typedAr[i + 1];
            if (b1 == 0 && b_0 == 0 && b_1 == 0) {
                nalStartPos.add(i - 1);
            } else if (b_1 == 1 && b_0 == 0 && b1 == 0 && b2 == 0) {
                nalStartPos.add(i - 2);
            }
            b2 = b_0;
            b1 = b_1;
        }
        if (nalStartPos.size() > 1) {
            for (int i = 0; i < nalStartPos.size() - 1; ++i) {
                byte[] nal = new byte[nalStartPos.get(i + 1) - nalStartPos.get(i)];
                System.arraycopy(typedAr, nalStartPos.get(i), nal, 0, nal.length);
                nals.add(nal);
                lastNalEndPos = nalStartPos.get(i + 1);
            }
        } else {
            lastNalEndPos = nalStartPos.get(0);
        }
        if (lastNalEndPos != 0 && lastNalEndPos < typedAr.length) {
            lastBuf = new byte[typedAr.length - lastNalEndPos];
            System.arraycopy(typedAr, lastNalEndPos, lastBuf, 0, typedAr.length - lastNalEndPos);
        } else {
            if (lastBuf == null) {
                lastBuf = typedAr;
            }
            byte[] _newBuf = new byte[lastBuf.length + buffer.length];
            System.arraycopy(lastBuf, 0, _newBuf, 0, lastBuf.length);
            System.arraycopy(buffer, 0, _newBuf, lastBuf.length, buffer.length);
            lastBuf = _newBuf;
        }
        boolean sps = false;
        boolean pps = false;
        boolean lastSps = false;
        if (!csdSet) {
            if (nals.size() > 0) {
                Iterator<byte[]> it = nals.iterator();
                while (it.hasNext() && !csdSet) {
                    byte[] nal = it.next();
                    if ((nal[4] & 0x1f) == 7) {
                        sps = true;
                        lastSps = true;
                        continue;
                    }
                    if ((nal[4] & 0x1f) == 8 && lastSps) {
                        pps = true;
                        csdSet = true;
                        continue;
                    }
                    it.remove();
                }
            }
        }
        if (!csdSet) {
            nals.clear();
        }
        //LogUtils.i("csdSet");
        if (nals.size() > 0) {
            Iterator<byte[]> it = nals.iterator();
            while (it.hasNext()) {
                ByteBuffer inputBuffer;
                int inputBufferIndex = _codec.dequeueInputBuffer(10);
                if (inputBufferIndex >= 0) {
                    // 版本判断。当手机系统小于 5.0 时，用arras
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        inputBuffer = _codec.getInputBuffer(inputBufferIndex);
                    } else {
                        ByteBuffer[] inputBuffers = _codec.getInputBuffers();
                        inputBuffer = inputBuffers[inputBufferIndex];
                    }
                    byte[] nal = it.next();
                    inputBuffer.put(nal);
                    _codec.queueInputBuffer(inputBufferIndex, 0, nal.length, 0, 0);
                    it.remove();
                    continue;
                }
                break;
            }

        }
        /**
         *清理内存
         */
        while (nals.size() > 30) {
            nals.remove(0);
        }
        /*if (nals.size() >30){
            int idx = 0;
            while (idx++<30){
                nals.remove(0);
            }
            lastBuf = null;
        }*/
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = _codec.dequeueOutputBuffer(bufferInfo, 100);
        while (outputBufferIndex >= 0) {
            _codec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = _codec.dequeueOutputBuffer(bufferInfo, 0);
        }
    }

    private int sendFaceData(Face faceEntity, int messageID) throws UnsupportedEncodingException {
        if (configSession == null || !configSession.isConnected() || faceEntity == null)
            return -3;
        if (faceEntity.getId() == null || faceEntity.getId().isEmpty())
            return -3;
        if (faceEntity.getRole() != 0 && faceEntity.getRole() != 1 && faceEntity.getRole() != 2)
            return -3;
        /*if (faceEntity.getFeatureCount() < 1 || faceEntity.getFeatureCount() > 5)
            return -3;
        if (faceEntity.getFeatureSize() < 128)
            return -3;
        if (faceEntity.getFeatureData() == null || faceEntity.getFeatureData().length != faceEntity.getFeatureCount())
            return -3;
        for (int i = 0; i < faceEntity.getFeatureCount(); ++i)
            if (faceEntity.getFeatureData()[i] == null || faceEntity.getFeatureData()[i].length != faceEntity.getFeatureSize())
                return -3;*/
        if (faceEntity.getTwisBgrs() == null)
            return -3;
        if (faceEntity.getTwisBgrs().length < 1)
            return -3;
        for (int i = 0; i < faceEntity.getTwisBgrs().length; ++i) {
            if (faceEntity.getTwisBgrs()[i] == null || faceEntity.getTwisBgrs()[i].length != 150 * 150 * 3)
                return -3;
        }
        int imageDataLen = 4;
        if (faceEntity.getImageData() != null && faceEntity.getImageData().length > 0) {
            for (int i = 0; i < faceEntity.getImageData().length; ++i) {
                if (faceEntity.getImageData()[i] == null || faceEntity.getImageData()[i].length == 0)
                    return -3;
                imageDataLen += 4;
                imageDataLen += 4;
                imageDataLen += faceEntity.getImageData()[i].length;
            }
        }
        int twisDataLen = 0;
        if (faceEntity.getTwisBgrs() != null) {
            twisDataLen = faceEntity.getTwisBgrs().length * (150 * 150 * 3 + 8);
        }
        IoBuffer buffer = IoBuffer.allocate(80 + faceEntity.getFeatureCount() * faceEntity.getFeatureSize() * 4 + imageDataLen + twisDataLen);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(Util.createType(messageID));
        buffer.putInt(72 + faceEntity.getFeatureCount() * faceEntity.getFeatureSize() * 4 + imageDataLen + twisDataLen);
        byte[] temp = faceEntity.getId().getBytes("UTF-8");
        if (temp.length > 19)
            return -3;
        buffer.put(temp);
        buffer.put(new byte[20 - temp.length]);
        if (faceEntity.getName() == null || faceEntity.getName().isEmpty()) {
            buffer.put(new byte[16]);
        } else {
            temp = faceEntity.getName().getBytes("UTF-8");
            if (temp.length > 15)
                return -3;
            buffer.put(temp);
            buffer.put(new byte[16 - temp.length]);
        }
        buffer.putInt(faceEntity.getRole());
        buffer.putShort(faceEntity.getFeatureCount());
        buffer.putShort(faceEntity.getFeatureSize());
        for (int i = 0; i < faceEntity.getFeatureCount(); ++i)
            for (int j = 0; j < faceEntity.getFeatureSize(); ++j)
                buffer.putFloat(faceEntity.getFeatureData()[i][j]);
        buffer.put(new byte[16]);
        if (imageDataLen == 4) {
            buffer.putInt(0);
        } else {
            buffer.putInt(faceEntity.getImageData().length);
            for (int i = 0; i < faceEntity.getImageData().length; ++i) {
                byte[] imageData = faceEntity.getImageData()[i];
                buffer.putInt(imageData.length);
                buffer.put(new byte[]{'j', 'p', 'g', 0});
                buffer.put(imageData);
            }
        }
        buffer.putUnsignedInt(faceEntity.getWiegandNo());
        buffer.putUnsignedInt(faceEntity.getExpireDate());
        buffer.putInt(faceEntity.getTwisBgrs().length);
        if (faceEntity.getTwisBgrs() != null) {
            for (int i = 0; i < faceEntity.getTwisBgrs().length; ++i) {
                buffer.putInt(150);
                buffer.putInt(150);
                buffer.put(faceEntity.getTwisBgrs()[i]);
            }
        }
        buffer.flip();
        configSession.write(buffer);
        return 0;
    }

    private int deleteFaceCore(String id, int role, long timeOutInMili) throws InterruptedException, UnsupportedEncodingException {
        IoBuffer buffer = IoBuffer.allocate(32);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(Util.createType(404));
        buffer.putInt(24);
        buffer.putInt(role);
        if (id == null || id.isEmpty())
            buffer.put(new byte[20]);
        else {
            byte[] temp = id.getBytes("UTF-8");
            if (temp.length > 19) return -3;
            buffer.put(temp);
            buffer.put(new byte[20 - temp.length]);
        }
        buffer.flip();
        configSession.write(buffer);
        synchronized (_locker) {
            _locker.wait(timeOutInMili);
            return _ack_delface;
        }
    }

    private boolean doConnect() {
        try {
            if (configConnected) return true;
            configConnection.setDefaultRemoteAddress(new InetSocketAddress(ip, 9527));
            ConnectFuture future = configConnection.connect();
            future.awaitUninterruptibly();
            configSession = future.getSession();
            configConnected = configSession != null;
            return configConnected;
        } catch (Exception ex) {
            configConnected = false;
            return false;
        }
    }

    private void doDisconnect() {
        if (!configConnected) return;
        try {
            //configSession.getCloseFuture().awaitUninterruptibly();
            configSession.closeNow();
            configConnected = false;
        } catch (Exception ex) {
        }
        doStopVideoPlay();
    }

    private void doStartVideoPlay() {
        try {
            if (streamConnected) return;
            streamConnection.setDefaultRemoteAddress(new InetSocketAddress(ip, 20000));
            ConnectFuture future = streamConnection.connect();
            future.awaitUninterruptibly();
            streamSession = future.getSession();
            streamConnected = streamSession != null;
        } catch (Exception ex) {
            streamConnected = false;
        }
    }

    private void doStopVideoPlay() {
        try {
            if (_codec != null) {
                _codec.stop();
                //释放资源
                _codec.release();
                _codec = null;
            }
        } catch (Exception ex) {
        }
        if (!streamConnected) return;
        try {
            streamSession.closeNow();
            streamConnected = false;
        } catch (Exception ex) {
        }
    }
    // endregion private methods

    // region mina relevant

    /**
     * 用于配置(数据)连接的事件处理器
     */
    private class ConfigConnectorHandler extends IoHandlerAdapter {
        @Override
        public void sessionOpened(IoSession session) throws Exception {
            if (FaceCamera.this.connectedStateChangedEventHandler != null)
                FaceCamera.this.connectedStateChangedEventHandler.onConnectedStateChanged(true);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            IoBuffer buffer = (IoBuffer) message;
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            int type = buffer.getInt();
            int messageID = (type & 0x3FF);
            @SuppressWarnings("unused")
            int len = buffer.getInt();
            switch (messageID) {
                case 4:
                    if (buffer.remaining() < 8) return;
                    int replyId = buffer.getInt();
                    int ackCode = buffer.getInt();
                    switch (replyId) {
                        case 401:
                            if (ackCode != 0) {
                                synchronized (_locker) {
                                    _ack_listface = ackCode;
                                    _locker.notify();
                                }
                            }
                            if (buffer.remaining() < 8) return;
                            _ret_listface_total = buffer.getInt();
                            int index = buffer.getInt();
                            if (index == 0) {
                                synchronized (_locker) {
                                    _ack_listface = ackCode;
                                    _locker.notify();
                                }
                            }
                            if (buffer.remaining() < 48) return;
                            byte[] temp = new byte[20];
                            buffer.get(temp);
                            Face face = new Face();
                            face.setId(new String(temp, "UTF-8").trim());
                            temp = new byte[16];
                            buffer.get(temp);
                            face.setName(new String(temp, "UTF-8").trim());
                            face.setRole(buffer.getInt());
                            face.setFeatureCount(buffer.getShort());
                            face.setFeatureSize(buffer.getShort());
                            if (face.getFeatureCount() > 0) {
                                if (face.getFeatureSize() != 0) {
                                    if (buffer.remaining() < face.getFeatureCount() * face.getFeatureSize() * 4)
                                        return;
                                    face.setFeatureData(new float[face.getFeatureCount()][]);
                                    for (int i = 0; i < face.getFeatureCount(); ++i) {
                                        face.getFeatureData()[i] = new float[face.getFeatureSize()];
                                        for (int j = 0; j < face.getFeatureSize(); ++j)
                                            face.getFeatureData()[i][j] = buffer.getFloat();
                                    }
                                }
                            }
                            if (buffer.remaining() < 4) return;
                            int imageCount = buffer.getInt();
                            if (imageCount > 0) {
                                byte[][] imageData = new byte[imageCount][];
                                for (int i = 0; i < imageCount; ++i) {
                                    if (buffer.remaining() < 4) return;
                                    int imageDataLen = buffer.getInt();
                                    if (imageDataLen < 0) return;
                                    if (buffer.remaining() < imageDataLen) return;
                                    imageData[i] = new byte[imageDataLen];
                                    buffer.get(imageData[i]);
                                }
                                face.setImageData(imageData);
                            }
                            if (buffer.remaining() < 24) {
                                _ret_listface_faces.add(face);
                                return;
                            }
                            buffer.get(new byte[16]);
                            face.setWiegandNo(buffer.getUnsignedInt());
                            face.setExpireDate(buffer.getUnsignedInt());
                            _ret_listface_faces.add(face);
                            break;
                        case 402:
                            synchronized (_locker) {
                                _ack_addface = ackCode;
                                _locker.notify();
                            }
                            break;
                        case 403:
                            synchronized (_locker) {
                                _ack_modface = ackCode;
                                _locker.notify();
                            }
                            break;
                        case 404:
                            synchronized (_locker) {
                                _ack_delface = ackCode;
                                _locker.notify();
                            }
                            break;
                    }
                    break;
                case 5:
                    if (buffer.remaining() < 4) return;
                    CaptureCompareData _data = new CaptureCompareData();
                    _data.setSequenceID(buffer.getUnsignedInt());
                    if (buffer.remaining() < 32) return;
                    byte[] temp = new byte[32];
                    buffer.get(temp);
                    _data.setCameraID(new String(temp, "UTF-8").trim());
                    if (buffer.remaining() < 32) return;
                    buffer.get(temp);
                    _data.setAddrID(new String(temp, "UTF-8").trim());
                    if (buffer.remaining() < 96) return;
                    temp = new byte[96];
                    buffer.get(temp);
                    _data.setAddrName(new String(temp, "UTF-8").trim());
                    if (buffer.remaining() < 8) return;
                    long captimeSec = buffer.getUnsignedInt();
                    long captimeUSec = buffer.getUnsignedInt();
                    _data.setCaptureTime(Util.resolveUTCTime(captimeSec, captimeUSec));
                    if (buffer.remaining() < 2) return;
                    _data.setRealtime(buffer.getShort() != 0);
                    if (buffer.remaining() < 2) return;
                    short matchScore = buffer.getShort();
                    if (matchScore > 0) {
                        _data.setPersonMatched(true);
                        _data.setMatchScore(matchScore);
                        if (buffer.remaining() < 40) return;
                        temp = new byte[20];
                        buffer.get(temp);
                        _data.setPersonID(new String(temp, "UTF-8").trim());
                        temp = new byte[16];
                        buffer.get(temp);
                        _data.setPersonName(new String(temp, "UTF-8").trim());
                        _data.setPersonRole(buffer.getInt());
                    }
                    if (buffer.remaining() < 4) return;
                    int environmentImgFlag = buffer.getInt();
                    int environmentImgSize = 0;
                    if (environmentImgFlag != 0) {
                        if (buffer.remaining() < 16) return;
                        buffer.getInt(); // 跳过4个字节jpg
                        environmentImgSize = buffer.getInt();
                        int x = buffer.getUnsignedShort();
                        int y = buffer.getUnsignedShort();
                        int w = buffer.getUnsignedShort();
                        int h = buffer.getUnsignedShort();
                        _data.setFaceRegionInEnvironment(new Rect(x, y, x + w, y + h));
                    }
                    if (buffer.remaining() < 4) return;
                    int featureImgFlag = buffer.getInt();
                    int featureImgSize = 0;
                    if (featureImgFlag != 0) {
                        if (buffer.remaining() < 16) return;
                        buffer.getInt(); // 跳过4个字节jpg
                        featureImgSize = buffer.getInt();
                        int x = buffer.getUnsignedShort();
                        int y = buffer.getUnsignedShort();
                        int w = buffer.getUnsignedShort();
                        int h = buffer.getUnsignedShort();
                        _data.setFaceRegionInFeature(new Rect(x, y, x + w, y + h));
                    }
                    if (buffer.remaining() < 4) return;
                    int videoFlag = buffer.getInt();
                    int videoSize = 0;
                    if (videoFlag != 0) {
                        if (buffer.remaining() < 24) return;
                        long startTimeSec = buffer.getUnsignedInt();
                        long startTimeUSec = buffer.getUnsignedInt();
                        _data.setVideoStartTime(Util.resolveUTCTime(startTimeSec, startTimeUSec));
                        long endTimeSec = buffer.getUnsignedInt();
                        long endTimeUSec = buffer.getUnsignedInt();
                        _data.setVideoEndTime(Util.resolveUTCTime(endTimeSec, endTimeUSec));
                        buffer.getInt(); // 跳过4个字节mp4
                        videoSize = buffer.getInt();
                    }
                    if (buffer.remaining() < 128) return;
                    _data.setSex(buffer.get());
                    _data.setAge(buffer.get());
                    buffer.get(new byte[126]);
                    if (environmentImgFlag != 0) {
                        if (buffer.remaining() < environmentImgSize) return;
                        _data.setEnvironmentImageData(new byte[environmentImgSize]);
                        buffer.get(_data.getEnvironmentImageData());
                    }
                    if (featureImgFlag != 0) {
                        if (buffer.remaining() < featureImgSize) return;
                        _data.setFeatureImageData(new byte[featureImgSize]);
                        buffer.get(_data.getFeatureImageData());
                    }
                    if (videoFlag != 0) {
                        if (buffer.remaining() < videoSize) return;
                        _data.setVideoData(new byte[videoSize]);
                        buffer.get(_data.getVideoData());
                    }
                    if (buffer.remaining() < 4) { // 兼容旧协议，没有特征值回传
                        if (FaceCamera.this.captureCompareDataReceivedEventHandler != null) {
                            FaceCamera.this.captureCompareDataReceivedEventHandler.onCaptureCompareDataReceived(_data);
                        }
                        Util.sendAck(session, type, 5, 0);
                        return;
                    }
                    int featureCount = buffer.getInt();
                    if (featureCount > 0) {
                        if (buffer.remaining() < featureCount * 4) return;
                        _data.setFeatureData(new float[featureCount]);
                        for (int i = 0; i < featureCount; ++i) {
                            _data.getFeatureData()[i] = buffer.getFloat();
                        }
                    }
                    if (buffer.remaining() < 4) {
                        if (FaceCamera.this.captureCompareDataReceivedEventHandler != null) {
                            FaceCamera.this.captureCompareDataReceivedEventHandler.onCaptureCompareDataReceived(_data);
                        }
                        Util.sendAck(session, type, 5, 0);
                        return;
                    }
                    int modelImgSize = buffer.getInt();
                    if (modelImgSize > 0) {
                        if (buffer.remaining() < 4 + modelImgSize) {
                            return;
                        }
                        buffer.getInt(); // 跳过4字节jpg
                        _data.setModelImageData(new byte[modelImgSize]);
                        buffer.get(_data.getModelImageData());
                    }
                    if (FaceCamera.this.captureCompareDataReceivedEventHandler != null) {
                        FaceCamera.this.captureCompareDataReceivedEventHandler.onCaptureCompareDataReceived(_data);
                    }
                    Util.sendAck(session, type, 5, 0);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            if (status == IdleStatus.WRITER_IDLE) {
                if (System.currentTimeMillis() - session.getLastWriteTime() >= 9000)
                    Util.sendHeartBeat(session);
            } else if (status == IdleStatus.READER_IDLE) {
                long usecBetween = System.currentTimeMillis() - session.getLastReadTime();
                int secBetween = (int) (usecBetween / 1000);
                if (secBetween > 14) {
                    // 设备很长时间没发过来数据了
                    session.closeNow();
                }
            }
        }
    }

    /**
     * 用于视频连接的事件处理器
     */
    private class StreamConnectorHandler extends IoHandlerAdapter {

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            IoBuffer buffer = (IoBuffer) message;
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            int type = buffer.getInt();
            int messageID = (type & 0x3FF);
            int len = buffer.getInt();
            switch (messageID) {
                case 103:
                    if (buffer.remaining() < 4) return;
                    int type1 = buffer.getInt();
                    if (type1 != 2) return;
                    if (buffer.remaining() < 16) return;
                    int width = buffer.getUnsignedShort();
                    int height = buffer.getUnsignedShort();
                    @SuppressWarnings("unused")
                    int seq_no0 = buffer.getInt();
                    @SuppressWarnings("unused")
                    int seq_no1 = buffer.getInt();
                    int bufferSize = (int) buffer.getUnsignedInt();
                    if (buffer.remaining() < bufferSize) return;
                    // 做个最大判定，不能太大了！
                    if (bufferSize > 1024 * 1024) return;
                    byte[] h264Segment = new byte[bufferSize];
                    buffer.get(h264Segment);
                    if (FaceCamera.this.streamDataReceivedEventHandler != null) {
                        FaceCamera.this.streamDataReceivedEventHandler.onStreamDataReceived(width, height, h264Segment);
                    }
                    handleH264(width, height, h264Segment);
                    break;
            }
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            if (status == IdleStatus.WRITER_IDLE) {
                if (System.currentTimeMillis() - session.getLastWriteTime() >= 9000)
                    Util.sendHeartBeat(session);
            } else if (status == IdleStatus.READER_IDLE) {
                long usecBetween = System.currentTimeMillis() - session.getLastReadTime();
                int secBetween = (int) (usecBetween / 1000);
                if (secBetween > 14) {
                    // 设备很长时间没发过来数据了
                    session.closeNow();
                }
            }
        }
    }

    /**
     * 配置连接断线重连
     */
    private final class ConfigAutoReconnectHandler implements IoServiceListener {
        @Override
        public void serviceActivated(IoService ioService) throws Exception {

        }

        @Override
        public void serviceIdle(IoService ioService, IdleStatus idleStatus) throws Exception {

        }

        @Override
        public void serviceDeactivated(IoService ioService) throws Exception {

        }

        @Override
        public void sessionCreated(IoSession ioSession) throws Exception {

        }

        @Override
        public void sessionClosed(IoSession ioSession) throws Exception {

        }

        @Override
        public void sessionDestroyed(IoSession ioSession) throws Exception {
            if (m_state == State.Connected) {
                m_state = State.ReConnecting;
                if (FaceCamera.this.connectedStateChangedEventHandler != null)
                    FaceCamera.this.connectedStateChangedEventHandler.onConnectedStateChanged(false);
            }
            // 需要重连
            while (m_state == State.ReConnecting) {
                try {
                    ConnectFuture future = configConnection.connect();
                    future.awaitUninterruptibly();// 等待连接创建成功
                    configSession = future.getSession();// 获取会话
                    if (m_state != State.ReConnecting) {
                        configSession.closeNow();
                        break;
                    }
                    if (configSession != null) {
                        if (FaceCamera.this.connectedStateChangedEventHandler != null)
                            FaceCamera.this.connectedStateChangedEventHandler.onConnectedStateChanged(true);
                        configConnected = true;
                        m_state = State.Connected;
                        break;
                    }
                } catch (Exception ex) {

                }
            }
        }
    }

    /**
     * 视频连接断线重连
     */
    private final class StreamAutoReconnectHandler implements IoServiceListener {
        @Override
        public void serviceActivated(IoService ioService) throws Exception {

        }

        @Override
        public void serviceIdle(IoService ioService, IdleStatus idleStatus) throws Exception {

        }

        @Override
        public void serviceDeactivated(IoService ioService) throws Exception {

        }

        @Override
        public void sessionCreated(IoSession ioSession) throws Exception {

        }

        @Override
        public void sessionClosed(IoSession ioSession) throws Exception {

        }

        @Override
        public void sessionDestroyed(IoSession ioSession) throws Exception {
            while (m_state == State.Connected || m_state == State.ReConnecting && FaceCamera.this._played) {
                try {
                    ConnectFuture future = streamConnection.connect();
                    future.awaitUninterruptibly();// 等待连接创建成功
                    streamSession = future.getSession();// 获取会话
                    if (streamSession != null) {
                        if (m_state != State.Connected && m_state != State.ReConnecting && !FaceCamera.this._played) {
                            streamSession.closeNow();
                        } else {
                            streamConnected = true;
                        }
                        break;
                    }
                } catch (Exception ex) {

                }
            }
        }
    }
    // endregion mina relevant
}
