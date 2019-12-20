package org.mechdancer.nano

/**
 * 荒谬的常量
 */
object RidiculousConstants {
    /**
     * 电机个数
     */
    const val MOTOR_SIZE = 6

    /**
     * 启用 Debug
     */
    const val DEBUG = true

    /**
     * 主动通信周期
     */
    const val SEND_PERIOD = 5L

    /**
     * 数据帧-负载大小
     */
    const val PACKET_INFO_SIZE = 3

    /**
     * 帧头
     */
    const val PACKET_HEAD = 0xff.toByte()
}