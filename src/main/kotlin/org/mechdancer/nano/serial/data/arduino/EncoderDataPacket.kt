package org.mechdancer.nano.serial.data.arduino

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.data.DataSerializer
import org.mechdancer.nano.serial.data.Packet

/**
 * 来自 Arduino 的六编码器脉冲
 */
data class EncoderDataPacket(
    val values: IntArray
) : Packet<EncoderDataPacket>(EncoderDataPacket) {

    init {
        require(values.size == RidiculousConstants.MOTOR_SIZE)
    }

    companion object : DataSerializer<EncoderDataPacket>(0xA2.toByte(),
        4 * RidiculousConstants.MOTOR_SIZE + RidiculousConstants.PACKET_INFO_SIZE) {
        override fun toByteArray(data: EncoderDataPacket): ByteArray =
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


        override fun fromByteArray(array: ByteArray): EncoderDataPacket? =
            array.splitPacket { _, bytes, check ->
                if (!array.checkPacket(check)) return@splitPacket null
                val nioBuffer = bytes.toLEArray()
                val result = IntArray(RidiculousConstants.MOTOR_SIZE)
                (0 until RidiculousConstants.MOTOR_SIZE).forEach { i ->
                    result[i] = nioBuffer.int
                }
                EncoderDataPacket(result)
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncoderDataPacket

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}