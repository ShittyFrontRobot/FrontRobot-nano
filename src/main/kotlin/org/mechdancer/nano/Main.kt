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
    require(MotorStatePacket.fromByteArray(MotorStatePacket.toByteArray(data)) == data)
    val data2 = MotorSpeedPacket(floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f))
    require(MotorSpeedPacket.fromByteArray(MotorSpeedPacket.toByteArray(data2)) == data2)
    val data3 = EncoderDataPacket(floatArrayOf(23f, 233f, 2333f, 2333f, 33f, 333f))
    require(EncoderDataPacket.fromByteArray(EncoderDataPacket.toByteArray(data3)) == data3)
    val data4 = RobotResetPacket
    require(RobotResetPacket.fromByteArray(RobotResetPacket.toByteArray(data4)) == data4)
}


fun main(args: Array<String>) {
    val data1 = MotorStatePacket(Array(6) { MotorState.Break })
    val data2 = MotorStatePacket(Array(6) { MotorState.Stop })
    val data3 = MotorStatePacket(Array(6) { MotorState.Speed })
    val bytes1 = MotorStatePacket.toByteArray(data1)
    val bytes2 = MotorStatePacket.toByteArray(data2)
    val bytes3 = MotorStatePacket.toByteArray(data3)
    val data4 = MotorSpeedPacket(FloatArray(6) { Random.nextDouble(-1.0, 1.0).toFloat() })
    val data5 = MotorSpeedPacket(FloatArray(6) { Random.nextDouble(-1.0, 1.0).toFloat() })
    val data6 = MotorSpeedPacket(FloatArray(6) { Random.nextDouble(-1.0, 1.0).toFloat() })
    val bytes4 = MotorSpeedPacket.toByteArray(data4)
    val bytes5 = MotorSpeedPacket.toByteArray(data5)
    val bytes6 = MotorSpeedPacket.toByteArray(data6)
    SerialManager.startup()
    var i = 0
    var j = 0
    Runtime.getRuntime().addShutdownHook(thread(false) {
        globalLogger.info {
            "Sent $i packet(s)."
        }
        globalLogger.info {
            "Received $j packet(s)."
        }
    })
    SerialManager.setPacketListener {
        if (it.hasValue()) {
            globalLogger.info(it)
            ++j
        }
    }
    while (true) {
        val bytes = when (++i % 6) {
            0    -> bytes6
            1    -> bytes1
            2    -> bytes2
            3    -> bytes3
            4    -> bytes4
            5    -> bytes5
            else -> throw RuntimeException()
        }
        globalLogger.info {
            globalLogger.info("Send result ${SerialManager.send(bytes)} byte(s).")
            bytes.map { it.toInt() and 0xff }.joinToString()
        }
        Thread.sleep(args.getOrElse(0) { "50" }.toLong())
    }
}

//
//fun main() {
//
//    val buffer= listOf(188,162,0,0,128,63,0,0,0,64,0,0,64,64,0,0,128,64,0,0,160,64,0,0,192,64,253)
////    val buffer = EncoderDataPacket.toByteArray(EncoderDataPacket(floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f))).toList()
//    val e = buildEngine()
//    e(buffer.map { it.toByte() }) {
//        println(it)
//    }
//}