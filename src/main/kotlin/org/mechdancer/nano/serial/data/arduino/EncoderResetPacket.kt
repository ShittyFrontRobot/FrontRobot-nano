package org.mechdancer.nano.serial.data.arduino

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.data.DataSerializer
import org.mechdancer.nano.serial.data.Packet

/**
 * 发给 Arduino 的重置编码器
 */
object EncoderResetPacket : Packet<EncoderResetPacket>(
    object : DataSerializer<EncoderResetPacket>(0xA8.toByte(), RidiculousConstants.PACKET_INFO_SIZE) {

        override fun toByteArray(data: EncoderResetPacket): ByteArray =
            byteArrayOf(RidiculousConstants.PACKET_HEAD, type, type)

        override fun fromByteArray(array: ByteArray): EncoderResetPacket? {
            return if (array.size != size || array.first() != RidiculousConstants.PACKET_HEAD || array[1] != type) null
            else EncoderResetPacket
        }


    }
) {
    override fun toString(): String = javaClass.simpleName
}