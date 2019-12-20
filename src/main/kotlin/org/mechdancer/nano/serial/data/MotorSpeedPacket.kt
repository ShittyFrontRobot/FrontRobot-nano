package org.mechdancer.nano.serial.data

import org.mechdancer.nano.RidiculousConstants
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class MotorSpeedPacket(
    val speeds: FloatArray
) {

    init {
        require(speeds.size == RidiculousConstants.MOTOR_SIZE)
    }

    companion object : DataSerializer<MotorSpeedPacket>(0xA1.toByte(),
        4 * RidiculousConstants.MOTOR_SIZE + RidiculousConstants.PACKET_INFO_SIZE) {
        override fun toByteArray(data: MotorSpeedPacket): ByteArray =
            ByteArrayOutputStream(size).use {
                it.writeHead()
                it.writeType()
                ByteBuffer
                    .allocate(size - RidiculousConstants.PACKET_INFO_SIZE)
                    // 大端
                    .order(ByteOrder.BIG_ENDIAN)
                    .apply {
                        data.speeds.forEach { d ->
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

        override fun fromByteArray(array: ByteArray): MotorSpeedPacket? =
            array.splitPacket { _, bytes, check ->
                if (!array.checkPacket(check)) return@splitPacket null
                val nioBuffer = ByteBuffer.wrap(bytes)
                // 每个电机一个 float
                val result = FloatArray(RidiculousConstants.MOTOR_SIZE)
                (0 until RidiculousConstants.MOTOR_SIZE).forEach { i ->
                    result[i] = nioBuffer.float
                }
                MotorSpeedPacket(result)
            }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MotorSpeedPacket

        if (!speeds.contentEquals(other.speeds)) return false

        return true
    }

    override fun hashCode(): Int {
        return speeds.contentHashCode()
    }
}