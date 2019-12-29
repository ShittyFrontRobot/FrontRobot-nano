package org.mechdancer.nano.serial.data.stm

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.data.DataSerializer
import org.mechdancer.nano.serial.data.arduino.RobotResetPacket
import java.io.ByteArrayOutputStream

/**
 * 发给 STM 的重置编码器
 */
object EncoderResetPacket :
    DataSerializer<EncoderResetPacket>(3, RidiculousConstants.PACKET_INFO_SIZE + 2) {
    override fun toByteArray(data: EncoderResetPacket): ByteArray =
        ByteArrayOutputStream(size).use {
            it.writeHead()
            it.writeType()
            it.write(0x5A)
            it.write(0x69)
            it.write(0x30)
            it.toByteArray()
        }

    override fun fromByteArray(array: ByteArray): EncoderResetPacket? {
        return if (array.size != size || array.first() != RidiculousConstants.PACKET_HEAD || array[1] != RobotResetPacket.type) null
        else EncoderResetPacket

    }

}