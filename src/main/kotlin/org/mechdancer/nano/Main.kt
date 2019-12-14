package org.mechdancer.nano

import org.mechdancer.nano.device.motor.MotorState
import org.mechdancer.nano.serial.data.MotorSpeedPacket
import org.mechdancer.nano.serial.data.MotorStatePacket
import org.mechdancer.nano.serial.data.EncoderDataPacket


fun main() {
    val data = MotorStatePacket(Array(6) { MotorState.Break })
    MotorStatePacket.toByteArray(data).let {
        println(it.joinToString())
        println(MotorStatePacket.fromByteArray(it))
    }
    val data2 = MotorSpeedPacket(doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0).map {
        it.toFloat()
    }.toFloatArray())
    MotorSpeedPacket.toByteArray(data2).let {
        println(it.joinToString())
        println(MotorSpeedPacket.fromByteArray(it))
    }
    val data3 = EncoderDataPacket(shortArrayOf(23, 233, 2333, 2333, 33, 333))
    EncoderDataPacket.toByteArray(data3).let {
        println(it.joinToString())
        println(EncoderDataPacket.fromByteArray(it))
    }
}

