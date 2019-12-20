package org.mechdancer.nano.serial.data

import org.mechdancer.nano.RidiculousConstants
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class EncoderDataPacket(
    val ticks: ShortArray
) {

    init {
        require(ticks.size == RidiculousConstants.MOTOR_SIZE)
    }

    companion object : DataSerializer<EncoderDataPacket>(0xA2.toByte(),
        4 * RidiculousConstants.MOTOR_SIZE + RidiculousConstants.PACKET_INFO_SIZE) {
        override fun toByteArray(data: EncoderDataPacket): ByteArray =
            ByteArrayOutputStream(size).use {
                it.writeHead()
                it.writeType()
                ByteBuffer
                    .allocate(MotorSpeedPacket.size - RidiculousConstants.PACKET_INFO_SIZE)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .apply {
                        data.ticks.forEach { d ->
                            putShort(d)
                        }
                    }
                    .array()
                    .let { array ->
                        it.write(array)
                    }
                it.write(it.toByteArray().xorTail())
                it.toByteArray()
            }


        override fun fromByteArray(array: ByteArray): EncoderDataPacket? =
            array.splitPacket { _, bytes, check ->
                if (!array.checkPacket(check)) return@splitPacket null
                val nioBuffer = ByteBuffer.wrap(bytes)
                // 每个编码器一个 short
                val result = ShortArray(RidiculousConstants.MOTOR_SIZE)
                (0 until RidiculousConstants.MOTOR_SIZE).forEach { i ->
                    result[i] = nioBuffer.short
                }
                EncoderDataPacket(result)
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncoderDataPacket

        if (!ticks.contentEquals(other.ticks)) return false

        return true
    }

    override fun hashCode(): Int {
        return ticks.contentHashCode()
    }
}