package org.mechdancer.nano.device

import org.mechdancer.nano.RidiculousConstants
import org.mechdancer.nano.device.motor.Motor
import org.mechdancer.nano.device.motor.MotorState
import org.mechdancer.nano.serial.SerialDataListener
import org.mechdancer.nano.serial.SerialManager
import org.mechdancer.nano.serial.data.EncoderDataPacket
import org.mechdancer.nano.serial.data.MotorSpeedPacket
import org.mechdancer.nano.serial.data.MotorStatePacket
import org.mechdancer.nano.serial.data.RobotResetPacket
import kotlin.concurrent.thread

object Robot {

    @Volatile
    private var isInitialized = false


    val motors = List(RidiculousConstants.MOTOR_SIZE - 1) { Motor(it) }

    private val encoderDataListener =
        SerialDataListener(EncoderDataPacket) {
            if (it == null) return@SerialDataListener
            it.ticks.forEachIndexed { i, t ->
                motors[i].encoderValue = t
            }
        }

    /**
     * 初始化
     */
    fun init() {
        if (isInitialized) return
        SerialManager.addListener(encoderDataListener)
        SerialManager.startup()
        isInitialized = true
        reset()
        thread {
            while (isInitialized) {
                // 发送状态
                val packet1 = MotorStatePacket.toByteArray(MotorStatePacket(motors.map { it.state }.toTypedArray()))
                // 发送速度
                val packet2 = MotorSpeedPacket.toByteArray(MotorSpeedPacket(motors.map { it.speed }.toFloatArray()))
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
        motors.forEach {
            it.state = MotorState.Stop
            it.speed = 0f
            it.encoderValue = 0f
        }
        SerialManager.send(RobotResetPacket, RobotResetPacket)
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