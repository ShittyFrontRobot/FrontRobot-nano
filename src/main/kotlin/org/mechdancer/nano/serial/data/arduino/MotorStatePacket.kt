package org.mechdancer.nano.serial.data.arduino

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.device.Motor.State
import org.mechdancer.nano.serial.data.DataSerializer
import org.mechdancer.nano.serial.data.Packet

/**
 * 发给 Arduino 的六电机状态
 */
data class MotorStatePacket(
    val states: Array<State>
) : Packet<MotorStatePacket>(MotorStatePacket) {

    init {
        require(states.size == RidiculousConstants.MOTOR_SIZE)
    }

    companion object : DataSerializer<MotorStatePacket>(0xA3.toByte(),
        RidiculousConstants.MOTOR_SIZE + RidiculousConstants.PACKET_INFO_SIZE) {

        private fun State.toByte() = when (this) {
            State.Stop  -> 0x0
            State.Break -> 0x1
            State.Speed -> 0x2
        }.toByte()

        private fun fromByte(byte: Byte) = when (byte.toInt()) {
            0x0  -> State.Stop
            0x1  -> State.Break
            0x2  -> State.Speed
            else -> throw IllegalArgumentException("Unknown motor state: $byte")
        }

        override fun toByteArray(data: MotorStatePacket): ByteArray =
            newStreamedByteArray(size) {
                writeHead()
                writeType()
                data.states
                    .map { s -> s.toByte() }
                    .toByteArray()
                    .let {
                        write(it)
                    }
                write(toByteArray().xorTail())
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