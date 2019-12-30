package org.mechdancer.nano.serial.data.stm

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.data.DataSerializer
import org.mechdancer.nano.serial.data.Packet

/**
 * 发给 STM 的重置编码器
 */
@Suppress("DEPRECATION")
@Deprecated("STM")
object EncoderResetPacketSTM : Packet<EncoderResetPacketSTM>(
    object : DataSerializer<EncoderResetPacketSTM>(3, RidiculousConstants.PACKET_INFO_SIZE + 2) {
        override fun toByteArray(data: EncoderResetPacketSTM): ByteArray =
            newStreamedByteArray(size) {
                writeHead()
                writeType()
                write(0x5A)
                write(0x69)
                write(0x30)
            }

        override fun fromByteArray(array: ByteArray): EncoderResetPacketSTM? {
            return if (
                array.size != size ||
                array.first() != RidiculousConstants.PACKET_HEAD ||
                array[1] != type) null
            else EncoderResetPacketSTM

        }

    }
) {
    override fun toString(): String = javaClass.simpleName
}
