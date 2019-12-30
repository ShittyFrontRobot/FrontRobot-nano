package org.mechdancer.nano.device

import org.mechdancer.nano.RidiculousConstants

class Motor(override val index: Int) : Device {

    init {
        require(index in 0 until RidiculousConstants.MOTOR_SIZE)
    }

    var state = State.Stop

    var speed = 0f

    override fun reset() {
        state = State.Stop
        speed = 0f
    }

    /**
     * 电机状态
     */
    enum class State {
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
}