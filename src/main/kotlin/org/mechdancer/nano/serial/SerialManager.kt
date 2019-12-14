package org.mechdancer.nano.serial

import com.fazecast.jSerialComm.SerialPort
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.concurrent.thread

/**
 * 串口管理器
 */
object SerialManager {

    @Volatile
    private var isStarted = false

    private val comPort: SerialPort by lazy {
        SerialPort.getCommPorts().first()
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

    /**
     * 启动
     */
    fun startup() {
        if (isStarted) return
        comPort.openPort()
        comPort.setComPortParameters(115200, 8, 1, 0)
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0)
        isStarted = true
        thread {
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
        comPort.closePort()
        isStarted = false
    }

}