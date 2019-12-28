package org.mechdancer.nano.serial.data.parser

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.data.EncoderDataPacket
import org.mechdancer.nano.serial.data.MotorSpeedPacket
import org.mechdancer.nano.serial.data.MotorStatePacket
import org.mechdancer.nano.serial.data.RobotResetPacket

private val serializers =
    listOf(
        MotorSpeedPacket,
        MotorStatePacket,
        EncoderDataPacket,
        RobotResetPacket
    )

private val typeWithSize =
    serializers.associate {
        it.type to it.size
    }
private val typeWithSerializer =
    serializers.associateBy { it.type }

sealed class ParsedPacket<T>(val core: T) {
    class MotorSpeed(core: MotorSpeedPacket) : ParsedPacket<MotorSpeedPacket>(core)
    class MotorState(core: MotorStatePacket) : ParsedPacket<MotorStatePacket>(core)
    class EncoderData(core: EncoderDataPacket) : ParsedPacket<EncoderDataPacket>(core)
    object RobotReset : ParsedPacket<RobotResetPacket>(RobotResetPacket)
    object Nothing : ParsedPacket<Unit>(Unit)
    object Failed : ParsedPacket<Unit>(Unit)

    fun hasValue() = this !is Nothing && this !is Failed

    companion object {
        fun wrap(it: Any?): ParsedPacket<*> =
            when (it) {
                is MotorSpeedPacket  -> MotorSpeed(it)
                is MotorStatePacket  -> MotorState(it)
                is EncoderDataPacket -> EncoderData(it)
                is RobotResetPacket  -> RobotReset
                else                 -> Failed
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