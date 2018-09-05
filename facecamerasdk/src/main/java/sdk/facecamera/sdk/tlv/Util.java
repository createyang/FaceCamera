package sdk.facecamera.sdk.tlv;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class Util {
    private static final int MESSAGE_ID_HEARTBEAT = 2; // 心跳包
    private static final int MESSAGE_ID_ACK = 4; // 统一应答包

    // 从MessageID创建TYPE
    public static int createType(int messageID) {
        return createType(12, 0, 10, messageID);
    }
    // 发送心跳包
    public static void sendHeartBeat(IoSession session) {
        IoBuffer buffer = IoBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int type = createType(MESSAGE_ID_HEARTBEAT);
        buffer.putInt(type);
        buffer.putInt(0);
        buffer.flip();
        session.write(buffer);
    }
    // 从请求包TYPE字段创建回应包请求字段
    private static int createType(int originType, int messageID) {
        return (originType & -1024) | messageID;
    }
    // 发送响应包
    public static void sendAck(IoSession session, int typeOrigin, int messageID, int ackCode) {
        IoBuffer buffer = IoBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(createType(typeOrigin, MESSAGE_ID_ACK));
        buffer.putInt(8);
        buffer.putInt(messageID);
        buffer.putInt(ackCode);
        buffer.flip();
        session.write(buffer);
    }
    /**
     * 从系统类型、主版本号、次版本号以及消息类型创建Type字段
     * @param sysType 系统类型
     * @param majorProtocol 主版本号
     * @param minorProtocol 次版本号
     * @param msgType 消息类型
     * @return Type
     */
    public static int createType(int sysType, int majorProtocol, int minorProtocol, int msgType) {
        int type = 0;

        type |= (sysType << 24);
        type |= (majorProtocol << 14);
        type |= (minorProtocol << 10);
        type |= msgType;

        return type;
    }

    public static Date resolveUTCTime(long secs, long usecs) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-8:00"));
        cal.set(1970, 0, 1, 0, 0, 0);
        cal.add(Calendar.SECOND, (int)secs);
        cal.add(Calendar.MILLISECOND, (int)(usecs / 1000));
        return cal.getTime();
    }
}
