package org.mechdancer.nano

import org.mechdancer.nano.device.motor.MotorState
import org.mechdancer.nano.serial.SerialManager
import org.mechdancer.nano.serial.data.EncoderDataPacket
import org.mechdancer.nano.serial.data.MotorSpeedPacket
import org.mechdancer.nano.serial.data.MotorStatePacket
import org.mechdancer.nano.serial.data.RobotResetPacket
import kotlin.concurrent.thread
import kotlin.random.Random


fun test() {
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
    val data4 = RobotResetPacket
    RobotResetPacket.toByteArray(data4).let {
        println(it.joinToString())
        println(RobotResetPacket.fromByteArray(it))
    }
}


fun main(args: Array<String>) {

//    val data1 = MotorStatePacket(Array(6) { MotorState.Break })
//    val data2 = MotorStatePacket(Array(6) { MotorState.Stop })
//    val data3 = MotorStatePacket(Array(6) { MotorState.Speed })
//    val bytes1 = MotorStatePacket.toByteArray(data1)
//    val bytes2 = MotorStatePacket.toByteArray(data2)
//    val bytes3 = MotorStatePacket.toByteArray(data3)
    val data1 = MotorSpeedPacket(FloatArray(6) { Random.nextDouble(-1.0, 1.0).toFloat() })
    val data2 = MotorSpeedPacket(FloatArray(6) { Random.nextDouble(-1.0, 1.0).toFloat() })
    val data3 = MotorSpeedPacket(FloatArray(6) { Random.nextDouble(-1.0, 1.0).toFloat() })
    val bytes1 = MotorSpeedPacket.toByteArray(data1)
    val bytes2 = MotorSpeedPacket.toByteArray(data2)
    val bytes3 = MotorSpeedPacket.toByteArray(data3)
    SerialManager.startup()
    var i = 0
    Runtime.getRuntime().addShutdownHook(thread(false) {
        globalLogger.info {
            "Sent $i."
        }
    })
    while (true) {
        val bytes = when (++i % 3) {
            0    -> bytes3
            1    -> bytes1
            2    -> bytes2
            else -> throw RuntimeException()
        }
        globalLogger.info {
            SerialManager.send(bytes)
            bytes.map { it.toInt() and 0xff }.joinToString()
        }
        Thread.sleep(args.first().toLong())
    }
}

