package org.mechdancer.nano.serial.data.parser

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.data.arduino.EncoderValuePacket
import org.mechdancer.nano.serial.data.arduino.MotorSpeedPacket
import org.mechdancer.nano.serial.data.arduino.MotorStatePacket
import org.mechdancer.nano.serial.data.arduino.RobotResetPacket
import org.mechdancer.nano.serial.data.stm.EncoderDataPacket

private val serializers =
    listOf(
        MotorSpeedPacket,
        MotorStatePacket,
        EncoderValuePacket,
        RobotResetPacket,
        EncoderDataPacket
    )

private val typeWithSize =
    serializers.associate {
        it.type to it.size
    }
private val typeWithSerializer =
    serializers.associateBy { it.type }

sealed class ParsedPacket<T>(val core: T) {

    //region Arduino
    class MotorSpeed(core: MotorSpeedPacket) : ParsedPacket<MotorSpeedPacket>(core)

    class MotorState(core: MotorStatePacket) : ParsedPacket<MotorStatePacket>(core)
    @Deprecated("STM")
    class EncoderValue(core: EncoderValuePacket) : ParsedPacket<EncoderValuePacket>(core)

    object RobotReset : ParsedPacket<RobotResetPacket>(RobotResetPacket)
    //endregion

    //region STM
    class EncoderData(core: EncoderDataPacket) : ParsedPacket<EncoderDataPacket>(core)
    //endregion


    object Nothing : ParsedPacket<Unit>(Unit)
    object Failed : ParsedPacket<Unit>(Unit)

    fun hasValue() = this !is Nothing && this !is Failed

    companion object {
        fun wrap(it: Any?): ParsedPacket<*> =
            when (it) {
                is MotorSpeedPacket   -> MotorSpeed(it)
                is MotorStatePacket   -> MotorState(it)
                is EncoderValuePacket -> EncoderValue(it)
                is RobotResetPacket   -> RobotReset
                is EncoderDataPacket  -> EncoderData(it)
                else                  -> Failed
            }
    }

    override fun toString(): String = "${javaClass.simpleName}: ${core.toString()}"
}

fun buildEngine() =
    ParseEngine<Byte, ParsedPacket<*>> { buffer ->
        val size = buffer.size
        val begin =
            buffer
                .indexOfFirst { it == RidiculousConstants.PACKET_HEAD }
                .takeIf { it >= 0 }
                ?: return@ParseEngine ParseEngine.ParseInfo(size, size, ParsedPacket.Nothing)
        val packet = (begin + 1)
            .let { buffer.getOrNull(it) }
            ?.let { typeWithSize[it] }
            ?.takeIf { size >= begin + it }
            ?.let { buffer.slice(begin until it + begin) }
            ?: return@ParseEngine ParseEngine.ParseInfo(begin, size, ParsedPacket.Nothing)

        return@ParseEngine typeWithSerializer[packet[1]]
            ?.fromByteArray(packet.toByteArray())
            .let {
                ParseEngine.ParseInfo(
                    begin + packet.size,
                    begin + packet.size,
                    ParsedPacket.wrap(it)
                )
            }
    }