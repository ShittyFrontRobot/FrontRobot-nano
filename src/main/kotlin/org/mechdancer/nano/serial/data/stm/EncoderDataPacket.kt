package org.mechdancer.nano.serial.data.stm

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.data.DataSerializer
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class EncoderDataPacket(val values: IntArray) {
    init {
        require(values.size == RidiculousConstants.MOTOR_SIZE / 2)
    }

    companion object : DataSerializer<EncoderDataPacket>(13,
        RidiculousConstants.MOTOR_SIZE / 2 * 4 + RidiculousConstants.PACKET_INFO_SIZE) {
        override fun toByteArray(data: EncoderDataPacket): ByteArray =
            ByteArrayOutputStream(size).use {
                it.writeHead()
                it.writeType()
                ByteBuffer
                    .allocate(size - RidiculousConstants.PACKET_INFO_SIZE)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .apply {
                        data.values.forEach { d ->
                            putInt(d)
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
                val nioBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
                val result = IntArray(RidiculousConstants.MOTOR_SIZE / 2)
                (0 until RidiculousConstants.MOTOR_SIZE/2).forEach { i ->
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