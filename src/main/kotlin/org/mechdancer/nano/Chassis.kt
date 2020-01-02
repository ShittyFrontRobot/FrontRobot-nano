package org.mechdancer.nano

import org.mechdancer.algebra.core.Vector
import org.mechdancer.algebra.function.matrix.inverse
import org.mechdancer.algebra.function.matrix.times
import org.mechdancer.algebra.function.matrix.transpose
import org.mechdancer.algebra.function.vector.*
import org.mechdancer.algebra.implement.matrix.builder.matrix
import org.mechdancer.algebra.implement.vector.*
import org.mechdancer.common.Pose2D
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.nano.device.Robot
import kotlin.math.abs
import kotlin.math.sign

object Chassis {

    private val lfMotor = Robot.motors[0]
    private val lbMotor = Robot.motors[1]
    private val rfMotor = Robot.motors[2]
    private val rbMotor = Robot.motors[3]

    private val lfEncoder = Robot.encoders[0]
    private val lbEncoder = Robot.encoders[0]
    private val rfEncoder = Robot.encoders[0]
    private val rbEncoder = Robot.encoders[0]

    private var lastEncoderValues: ListVector = listVectorOfZero(4)

    var pose: Pose2D = Pose2D.zero()
        private set

    fun showEncoderValues() = """
        
        LF: ${lfEncoder.getPosition()},
        LB: ${lbEncoder.getPosition()},
        RF: ${rfEncoder.getPosition()},
        RB: ${rbEncoder.getPosition()}
    """.trimIndent()

    fun reset() {
        lfMotor.reset()
        lbMotor.reset()
        rfMotor.reset()
        rbMotor.reset()

        lfEncoder.reset()
        lbEncoder.reset()
        rfEncoder.reset()
        rbEncoder.reset()

        lastEncoderValues = listVectorOfZero(4)
        pose = Pose2D.zero()
    }

    fun setPower(speed: Vector3D) {
        val wheelSpeeds = coefficient * speed
        val standardizedSpeeds = wheelSpeeds.standardizeBy(1.0)
        standardizedSpeeds.forEachIndexed { index: Int, d: Double ->
            Robot.motors[index].speed = d.toFloat()
        }
    }

    fun update() {
        val currentEncoderValues = listVectorOf(
            lfEncoder.getPosition(),
            lbEncoder.getPosition(),
            rfEncoder.getPosition(),
            rbEncoder.getPosition()) * TRACK
        val (x, y, w) = solverMatrix * (currentEncoderValues - lastEncoderValues)
        lastEncoderValues = currentEncoderValues
        pose = pose plusDelta Pose2D(vector2DOf(x, y), w.toRad())
    }

    private fun Vector.standardizeBy(maxPower: Double) =
        toList().map(::abs).max()!!.let {
            if (it <= abs(maxPower))
                maxPower.sign
            else
                maxPower / it
        }.let {
            DoubleArray(dim) { i ->
                this[i] * it
            }
        }

    private const val TREAD_XY = 0.277
    const val TRACK = 0.039312

    private val coefficientWithTread = matrix {
        row(+1, -1, -TREAD_XY)
        row(+1, +1, -TREAD_XY)
        row(+1, +1, +TREAD_XY)
        row(+1, -1, +TREAD_XY)
    }
    private val coefficient = matrix {
        row(+1, -1, -1)
        row(+1, +1, -1)
        row(+1, +1, +1)
        row(+1, -1, +1)
    }


    private val transposed = coefficientWithTread.transpose()

    private val solverMatrix = (transposed * coefficientWithTread).inverse() * transposed


}