package org.mechdancer.nano.serial

import com.fazecast.jSerialComm.SerialPort
import org.mechdancer.nano.globalLogger
import org.mechdancer.nano.info
import org.mechdancer.nano.serial.data.Packet
import org.mechdancer.nano.serial.data.parser.ParsedPacketWrapper
import org.mechdancer.nano.serial.data.parser.buildEngine
import kotlin.concurrent.thread

/**
 * 串口管理器
 */
object SerialManager {

    @Volatile
    private var isStarted = false

    private val comPort: SerialPort by lazy {
        SerialPort.getCommPorts().find { "USB" in it.systemPortName }?.also {
            globalLogger.info { "Open serial port: ${it.systemPortName}" }
        } ?: SerialPort.getCommPorts()[0]
    }

    private var packetListener = { _: ParsedPacketWrapper<*> -> }

    private val engine = buildEngine()

    fun <R : Packet<R>> send(data: R) =
        send(data.serialize())

    fun send(data: ByteArray) =
        comPort.writeBytes(data, data.size.toLong())

    fun setPacketListener(block: (ParsedPacketWrapper<*>) -> Unit) {
        packetListener = block
    }

    /**
     * 启动
     */
    fun startup() {
        if (isStarted) return
        globalLogger.info { "Manager startup" }
        comPort.openPort()
        comPort.setComPortParameters(115200, 8, 1, 0)
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 50, 0)
        isStarted = true
        thread {
            while (isStarted) {
                val buffer = ByteArray(64)
                comPort.readBytes(buffer, buffer.size.toLong())
                engine(buffer.toList(), packetListener)
            }
        }
    }

    /**
     * 关闭
     */
    fun stop() {
        if (!isStarted) return
        globalLogger.info { "Manager stop" }
        comPort.closePort()
        isStarted = false
    }

}