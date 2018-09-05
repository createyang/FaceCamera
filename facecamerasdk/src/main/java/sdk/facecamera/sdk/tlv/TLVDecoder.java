package sdk.facecamera.sdk.tlv;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.ByteOrder;

public final class TLVDecoder extends CumulativeProtocolDecoder {
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (in.remaining() >= 8) {
            in.mark();

            in.order(ByteOrder.LITTLE_ENDIAN);
            @SuppressWarnings("unused")
            int type = in.getInt();
            int len = in.getInt();

            if (in.remaining() < len) {
                // 拆包的情况
                in.reset();
                return false;
            } else {
                in.reset();
                int totalLen = (int) (8 + len);

                byte[] packArr = new byte[totalLen];
                in.get(packArr, 0, totalLen);

                IoBuffer buffer = IoBuffer.allocate(totalLen);
                buffer.put(packArr);
                buffer.flip();
                out.write(buffer);
                buffer.free();

                if (in.remaining() > 0) {
                    // 粘包了
                    return true;
                }
            }
        }
        return false;
    }
}
