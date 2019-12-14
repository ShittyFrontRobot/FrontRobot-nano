package org.mechdancer.nano.device.motor

/**
 * 电机状态
 */
enum class MotorState {
    /**
     * 停止
     */
    Stop,
    /**
     * 制动
     */
    Break,
    /**
     * 闭速度环
     */
    Speed;
}