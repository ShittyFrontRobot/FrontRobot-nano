package org.mechdancer.nano.serial.data

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.device.motor.MotorState
import java.io.ByteArrayOutputStream

/**
 * 电机状态包
 */
data class MotorStatePacket(
    val states: Array<MotorState>
) {

    init {
        require(states.size == RidiculousConstants.MOTOR_SIZE)
    }

    companion object : DataSerializer<MotorStatePacket>(0xA3.toByte(), RidiculousConstants.MOTOR_SIZE + 2) {

        private fun MotorState.toByte() = when (this) {
            MotorState.Stop  -> 0x0
            MotorState.Break -> 0x1
            MotorState.Speed -> 0x2
        }.toByte()

        private fun fromByte(byte: Byte) = when (byte.toInt()) {
            0x0  -> MotorState.Stop
            0x1  -> MotorState.Break
            0x2  -> MotorState.Speed
            else -> throw IllegalArgumentException("Unknown motor state: $byte")
        }

        override fun toByteArray(data: MotorStatePacket): ByteArray =
            ByteArrayOutputStream(size).use {
                it.writeHead()
                data.states
                    .map { s -> s.toByte() }
                    .toByteArray()
                    .run {
                        it.write(this)
                        // 写入帧头后再计算校验位
                        it.write(it.toByteArray().xorAll())
                    }
                it.toByteArray()
            }

        override fun fromByteArray(array: ByteArray): MotorStatePacket? =
            array.splitPacket { _, bytes, check ->
                if (!array.checkPacket(check)) return@splitPacket null
                bytes
                    .map { fromByte(it) }
                    .toTypedArray()
                    .let(::MotorStatePacket)
            }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MotorStatePacket

        if (!states.contentEquals(other.states)) return false

        return true
    }

    override fun hashCode(): Int {
        return states.contentHashCode()
    }
}