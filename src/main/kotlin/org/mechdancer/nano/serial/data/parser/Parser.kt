@file:Suppress("DEPRECATION")

package org.mechdancer.nano.serial.data.parser

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.data.arduino.EncoderDataPacket
import org.mechdancer.nano.serial.data.arduino.MotorSpeedPacket
import org.mechdancer.nano.serial.data.arduino.MotorStatePacket
import org.mechdancer.nano.serial.data.arduino.RobotResetPacket
import org.mechdancer.nano.serial.data.stm.EncoderDataPacketSTM
import org.mechdancer.nano.serial.data.stm.EncoderResetPacketSTM

private val serializers =
    listOf(
        MotorSpeedPacket,
        MotorStatePacket,
        EncoderDataPacket,
        RobotResetPacket.serializer,
        EncoderDataPacketSTM,
        EncoderResetPacketSTM.serializer
    )

private val typeWithSize =
    serializers.associate {
        it.type to it.size
    }
private val typeWithSerializer =
    serializers.associateBy { it.type }

sealed class ParsedPacketWrapper<T>(val core: T) {

    //region Arduino

    class MotorSpeed(core: MotorSpeedPacket) : ParsedPacketWrapper<MotorSpeedPacket>(core)
    class MotorState(core: MotorStatePacket) : ParsedPacketWrapper<MotorStatePacket>(core)
    class EncoderData(core: EncoderDataPacket) : ParsedPacketWrapper<EncoderDataPacket>(core)

    object RobotReset : ParsedPacketWrapper<RobotResetPacket>(RobotResetPacket)

    //endregion

    //region STM

    @Deprecated("STM")
    class EncoderDataSTM(core: EncoderDataPacketSTM) : ParsedPacketWrapper<EncoderDataPacketSTM>(core)

    @Deprecated("STM")
    object EncoderReset : ParsedPacketWrapper<EncoderResetPacketSTM>(EncoderResetPacketSTM)

    //endregion


    object Nothing : ParsedPacketWrapper<Unit>(Unit)
    object Failed : ParsedPacketWrapper<Unit>(Unit)

    fun hasValue() = this !is Nothing && this !is Failed

    companion object {
        fun wrap(it: Any?): ParsedPacketWrapper<*> =
            when (it) {
                is MotorSpeedPacket      -> MotorSpeed(it)
                is MotorStatePacket      -> MotorState(it)
                is EncoderDataPacket     -> EncoderData(it)
                is RobotResetPacket      -> RobotReset
                is EncoderDataPacketSTM  -> EncoderDataSTM(it)
                is EncoderResetPacketSTM -> EncoderReset
                else                     -> Failed
            }
    }

    override fun toString(): String = "${javaClass.simpleName}: ${core.toString()}"
}

fun buildEngine() =
    ParseEngine<Byte, ParsedPacketWrapper<*>> { buffer ->
        val size = buffer.size
        val begin =
            buffer
                .indexOfFirst { it == RidiculousConstants.PACKET_HEAD }
                .takeIf { it >= 0 }
                ?: return@ParseEngine ParseEngine.ParseInfo(size, size, ParsedPacketWrapper.Nothing)
        val packet = (begin + 1)
            .let { buffer.getOrNull(it) }
            ?.let { typeWithSize[it] }
            ?.takeIf { size >= begin + it }
            ?.let { buffer.slice(begin until it + begin) }
            ?: return@ParseEngine ParseEngine.ParseInfo(begin, size, ParsedPacketWrapper.Nothing)

        return@ParseEngine typeWithSerializer[packet[1]]
            ?.fromByteArray(packet.toByteArray())
            .let {
                ParseEngine.ParseInfo(
                    begin + packet.size,
                    begin + packet.size,
                    ParsedPacketWrapper.wrap(it)
                )
            }
    }