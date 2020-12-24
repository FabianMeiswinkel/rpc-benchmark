package rntbd;


import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

import static com.google.common.base.Preconditions.checkNotNull;

final class RntbdFramer {

    private RntbdFramer() {
    }

    static boolean canDecodeHead(final ByteBuf in) throws CorruptedFrameException {

        checkNotNull(in, "in");

        if (in.readableBytes() < RntbdResponseStatus.LENGTH) {
            return false;
        }

        final int start = in.readerIndex();
        final long length = in.getUnsignedIntLE(start);

        if (length > Integer.MAX_VALUE) {
            final String reason = Strings.lenientFormat("Head frame length exceeds Integer.MAX_VALUE, %s: %s",
                    Integer.MAX_VALUE, length
            );
            throw new CorruptedFrameException(reason);
        }

        if (length < Integer.BYTES) {
            final String reason = Strings.lenientFormat("Head frame length is less than size of length field, %s: %s",
                    Integer.BYTES, length
            );
            throw new CorruptedFrameException(reason);
        }

        return length <= in.readableBytes();
    }

    static boolean canDecodePayload(final ByteBuf in, final int start) {

        checkNotNull(in, "in");

        final int readerIndex = in.readerIndex();

        if (start < readerIndex) {
            throw new IllegalArgumentException("start < in.readerIndex()");
        }

        final int offset = start - readerIndex;

        if (in.readableBytes() - offset < Integer.BYTES) {
            return false;
        }

        final long length = in.getUnsignedIntLE(start);

        if (length > Integer.MAX_VALUE) {
            final String reason = Strings.lenientFormat("Payload frame length exceeds Integer.MAX_VALUE, %s: %s",
                    Integer.MAX_VALUE, length
            );
            throw new CorruptedFrameException(reason);
        }

        return offset + Integer.BYTES + length <= in.readableBytes();
    }

    static boolean canDecodePayload(final ByteBuf in) {
        return canDecodePayload(in, in.readerIndex());
    }
}
