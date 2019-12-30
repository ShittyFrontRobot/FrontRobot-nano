package org.mechdancer.nano.device

import org.mechdancer.nano.RidiculousConstants
import kotlin.math.PI

class Encoder(override val index: Int) : Device {

    init {
        require(index in 0 until RidiculousConstants.MOTOR_SIZE)
    }

    private var offset = .0

    var raw = .0

    var cpr = 1.0

    fun getPosition() = (raw - offset) / cpr * 2 * PI

    override fun reset() {
        offset = raw
        raw = .0
    }
}