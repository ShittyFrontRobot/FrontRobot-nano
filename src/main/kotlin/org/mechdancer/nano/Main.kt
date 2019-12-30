package org.mechdancer.nano

import org.mechdancer.nano.device.motor.MotorState
import org.mechdancer.nano.serial.SerialManager
import org.mechdancer.nano.serial.data.arduino.EncoderDataPacket
import org.mechdancer.nano.serial.data.arduino.MotorSpeedPacket
import org.mechdancer.nano.serial.data.arduino.MotorStatePacket
import org.mechdancer.nano.serial.data.arduino.RobotResetPacket
import kotlin.concurrent.thread


fun test() {
    val data = MotorStatePacket(Array(6) { MotorState.Break })
    require(MotorStatePacket.fromByteArray(data.serialize()) == data)
    require(MotorStatePacket.fromByteArray(MotorStatePacket.toByteArray(data)) == data)
    val data2 = MotorSpeedPacket(floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f))
    require(MotorSpeedPacket.fromByteArray(data2.serialize()) == data2)
    val data3 = EncoderDataPacket(intArrayOf(23, 233, 2333, 2333, 33, 333))
    require(EncoderDataPacket.fromByteArray(data3.serialize()) == data3)
    val data4 = RobotResetPacket
    require(RobotResetPacket.serializer.fromByteArray(RobotResetPacket.serialize()) == data4)
}

fun main() {
    SerialManager.startup()
    SerialManager.setPacketListener {
        if (it.hasValue())
            println(it)
    }
    Runtime.getRuntime().addShutdownHook(thread(false) {
        SerialManager.stop()
    })
}

//
//fun main(args: Array<String>) {
//    val data1 = MotorStatePacket(Array(6) { MotorState.Break })
//    val data2 = MotorStatePacket(Array(6) { MotorState.Stop })
//    val data3 = MotorStatePacket(Array(6) { MotorState.Speed })
//    val bytes1 = MotorStatePacket.toByteArray(data1)
//    val bytes2 = MotorStatePacket.toByteArray(data2)
//    val bytes3 = MotorStatePacket.toByteArray(data3)
//    val data4 = MotorSpeedPacket(FloatArray(6) { Random.nextDouble(-1.0, 1.0).toFloat() })
//    val data5 = MotorSpeedPacket(FloatArray(6) { Random.nextDouble(-1.0, 1.0).toFloat() })
//    val data6 = MotorSpeedPacket(FloatArray(6) { Random.nextDouble(-1.0, 1.0).toFloat() })
//    val bytes4 = MotorSpeedPacket.toByteArray(data4)
//    val bytes5 = MotorSpeedPacket.toByteArray(data5)
//    val bytes6 = MotorSpeedPacket.toByteArray(data6)
//    SerialManager.startup()
//    var i = 0
//    var j = 0
//    Runtime.getRuntime().addShutdownHook(thread(false) {
//        globalLogger.info {
//            "Sent $i packet(s)."
//        }
//        globalLogger.info {
//            "Received $j packet(s)."
//        }
//    })
//    SerialManager.setPacketListener {
//        if (it.hasValue()) {
//            globalLogger.info(it)
//            ++j
//        }
//    }
//    while (true) {
//        val bytes = when (++i % 6) {
//            0    -> bytes6
//            1    -> bytes1
//            2    -> bytes2
//            3    -> bytes3
//            4    -> bytes4
//            5    -> bytes5
//            else -> throw RuntimeException()
//        }
//        globalLogger.info {
//            globalLogger.info("Send result ${SerialManager.send(bytes)} byte(s).")
//            bytes.map { it.toInt() and 0xff }.joinToString()
//        }
//        Thread.sleep(args.getOrElse(0) { "50" }.toLong())
//    }
//}