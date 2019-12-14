package org.mechdancer.nano.serial.data

object RobotResetPacket : DataSerializer<RobotResetPacket>(0xA8.toByte(), 1) {

    override fun toByteArray(data: RobotResetPacket): ByteArray =
        byteArrayOf(head)

    override fun fromByteArray(array: ByteArray): RobotResetPacket? {
        return if (array.size != size || array.first() != head) null
        else RobotResetPacket
    }

}