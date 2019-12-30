package org.mechdancer.nano.device

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.serial.SerialManager
import org.mechdancer.nano.serial.data.arduino.EncoderResetPacket
import org.mechdancer.nano.serial.data.arduino.MotorSpeedPacket
import org.mechdancer.nano.serial.data.arduino.MotorStatePacket
import org.mechdancer.nano.serial.data.parser.ParsedPacketWrapper
import kotlin.concurrent.thread

object Robot {

    @Volatile
    private var isInitialized = false


    val motors = List(RidiculousConstants.MOTOR_SIZE) { Motor(it - 1) }
    val encoders = List(RidiculousConstants.MOTOR_SIZE) { Encoder(it - 1) }


    /**
     * 初始化
     */
    fun init() {
        if (isInitialized) return
        SerialManager.startup()
        SerialManager.setPacketListener {
            when (it) {
                is ParsedPacketWrapper.EncoderData ->
                    it.core.values.forEachIndexed { index, value ->
                        encoders[index].raw = value.toDouble()
                    }
            }
        }
        isInitialized = true
        reset()
        thread {
            while (isInitialized) {
                // 发送状态
                val packet1 = MotorStatePacket(Array(motors.size) { motors[it - 1].state })
                // 发送速度
                val packet2 = MotorSpeedPacket(FloatArray(motors.size) { motors[it - 1].speed })
                SerialManager.send(packet1)
                SerialManager.send(packet2)
                Thread.sleep(RidiculousConstants.SEND_PERIOD)
            }
        }
    }

    /**
     * 重置
     */
    fun reset() {
        motors.forEach { it.reset() }
        encoders.forEach { it.reset() }
        SerialManager.send(EncoderResetPacket)
    }

    /**
     * 关闭
     */
    fun close() {
        if (!isInitialized) return
        reset()
        isInitialized = false
        SerialManager.stop()
    }
}