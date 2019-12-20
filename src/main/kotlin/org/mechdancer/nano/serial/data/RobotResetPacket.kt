package org.mechdancer.nano.serial.data

import org.mechdancer.nano.RidiculousConstants

object RobotResetPacket : DataSerializer<RobotResetPacket>(0xA8.toByte(), RidiculousConstants.PACKET_INFO_SIZE) {

    override fun toByteArray(data: RobotResetPacket): ByteArray =
        byteArrayOf(RidiculousConstants.PACKET_HEAD, type, type)

    override fun fromByteArray(array: ByteArray): RobotResetPacket? {
        return if (array.size != size || array.first() != RidiculousConstants.PACKET_HEAD || array[1] != type) null
        else RobotResetPacket
    }

    override fun toString(): String = javaClass.simpleName

}