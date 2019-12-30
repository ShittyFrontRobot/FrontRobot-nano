package org.mechdancer.nano.serial.data.stm

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.data.DataSerializer
import org.mechdancer.nano.serial.data.Packet

/**
 * 来自 STM 的三编码器脉冲
 */
@Suppress("DEPRECATION")
@Deprecated("STM")
data class EncoderDataPacketSTM(val values: IntArray) : Packet<EncoderDataPacketSTM>(EncoderDataPacketSTM) {
    init {
        require(values.size == RidiculousConstants.MOTOR_SIZE / 2)
    }

    companion object : DataSerializer<EncoderDataPacketSTM>(13,
        RidiculousConstants.MOTOR_SIZE / 2 * 4 + RidiculousConstants.PACKET_INFO_SIZE) {
        override fun toByteArray(data: EncoderDataPacketSTM): ByteArray =
            newStreamedByteArray(size) {
                writeHead()
                writeType()
                newLEArray(size - RidiculousConstants.PACKET_INFO_SIZE) {
                    data.values.forEach { d ->
                        putInt(d)
                    }
                }.let { write(it) }
                write(toByteArray().xorTail())
            }


        override fun fromByteArray(array: ByteArray): EncoderDataPacketSTM? =
            array.splitPacket { _, bytes, check ->
                if (!array.checkPacket(check)) return@splitPacket null
                val nioBuffer = bytes.toLEArray()
                val result = IntArray(RidiculousConstants.MOTOR_SIZE / 2)
                (0 until RidiculousConstants.MOTOR_SIZE / 2).forEach { i ->
                    result[i] = nioBuffer.int
                }
                EncoderDataPacketSTM(result)
            }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncoderDataPacketSTM

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }

}