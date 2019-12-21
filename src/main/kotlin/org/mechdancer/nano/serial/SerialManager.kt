package org.mechdancer.nano.serial

import com.fazecast.jSerialComm.SerialPort
import org.mechdancer.nano.globalLogger
import org.mechdancer.nano.info
import org.mechdancer.nano.serial.data.DataSerializer
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.concurrent.thread

/**
 * 串口管理器
 */
object SerialManager {

    @Volatile
    private var isStarted = false

    private val comPort: SerialPort by lazy {
        SerialPort.getCommPorts()[0].also {
            globalLogger.info { "Open serial port: ${it.systemPortName}" }
        }
    }

    private val listeners =
        ConcurrentSkipListSet<SerialDataListener<*, *>> { o1, o2 ->
            o1.hashCode().compareTo(o2.hashCode())
        }


    /**
     * 添加数据监听器
     */
    fun addListener(listener: SerialDataListener<*, *>) =
        listeners.add(listener)

    /**
     * 移除数据监听器
     */
    fun removeListener(listener: SerialDataListener<*, *>) =
        listeners.remove(listener)

    fun <R, T : DataSerializer<R>> send(data: R, serializer: T) =
        send(serializer.toByteArray(data))

    fun send(data: ByteArray) =
        comPort.writeBytes(data, data.size.toLong())

    /**
     * 启动
     */
    fun startup() {
        if (isStarted) return
        globalLogger.info { "Manager startup" }
        comPort.openPort()
        comPort.setComPortParameters(115200, 8, 1, 0)
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0)
        isStarted = true
        thread(isDaemon = true) {
            while (isStarted) {
                val buffer = ByteArray(1024)
                comPort.readBytes(buffer, buffer.size.toLong())
                listeners.forEach {
                    it.update(buffer)
                }
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