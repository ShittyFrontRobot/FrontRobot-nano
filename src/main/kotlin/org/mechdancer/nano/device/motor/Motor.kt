package org.mechdancer.nano.device.motor

import org.mechdancer.nano.RidiculousConstants

class Motor(val index: Int) {

    init {
        require(index in 0 until RidiculousConstants.MOTOR_SIZE)
    }

    var state = MotorState.Stop

    var speed = 0f

    var encoderValue: Float = 0f

}