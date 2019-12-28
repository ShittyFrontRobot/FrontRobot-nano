package org.mechdancer.nano.serial.data.arduino

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.data.DataSerializer
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Suppress("DEPRECATION")
@Deprecated("STM")
data class EncoderValuePacket(
    val values: FloatArray
) {

    init {
        require(values.size == RidiculousConstants.MOTOR_SIZE)
    }

    companion object : DataSerializer<EncoderValuePacket>(0xA2.toByte(),
        4 * RidiculousConstants.MOTOR_SIZE + RidiculousConstants.PACKET_INFO_SIZE) {
        override fun toByteArray(data: EncoderValuePacket): ByteArray =
            ByteArrayOutputStream(size).use {
                it.writeHead()
                it.writeType()
                ByteBuffer
                    .allocate(size - RidiculousConstants.PACKET_INFO_SIZE)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .apply {
                        data.values.forEach { d ->
                            putFloat(d)
                        }
                    }
                    .array()
                    .let { array ->
                        it.write(array)
                    }
                it.write(it.toByteArray().xorTail())
                it.toByteArray()
            }


        override fun fromByteArray(array: ByteArray): EncoderValuePacket? =
            array.splitPacket { _, bytes, check ->
                if (!array.checkPacket(check)) return@splitPacket null
                val nioBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
                val result = FloatArray(RidiculousConstants.MOTOR_SIZE)
                (0 until RidiculousConstants.MOTOR_SIZE).forEach { i ->
                    result[i] = nioBuffer.float
                }
                EncoderValuePacket(result)
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncoderValuePacket

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}