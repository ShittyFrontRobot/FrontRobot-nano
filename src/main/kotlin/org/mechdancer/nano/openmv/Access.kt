package org.mechdancer.nano.openmv

import org.mechdancer.algebra.function.vector.div
import org.mechdancer.algebra.implement.vector.*
import org.mechdancer.common.*
import org.mechdancer.geometry.angle.rotate
import org.mechdancer.geometry.angle.toDegree
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder

private fun Pose3D.magic() =
    Pose2D(vector2DOf(p.x, p.y), d.third.rotate((-90).toDegree()))
        .let { rotate90.invoke(it) }

private val rotate90 = Pose2D(vector2DOfZero(), (-90).toDegree()).toTransformation()

private val camera = Pose3D(vector3DOfZero(), Angle3D(0.toDegree(), 180.toDegree(), (76 + 19 / 60).toDegree(), AxesOrder.ZYX))

private const val MAGIC_PER_METER = 15.0

fun resolveFromOpenMV(data: ByteArray) =
    String(data).trim().split(",").map { it.toDouble() }.let {
        // Intrinsic Z-Y-X -> Extrinsic X-Y-Z
        Pose3D(
            // 1m -> 20.0
            (vector3DOf(it[0], it[1], it[2]) / MAGIC_PER_METER).to3D(),
            Angle3D(it[3].toRad(), it[4].toRad(), it[5].toRad(), AxesOrder.XYZ)
        )
    }.let {
        idealTagToRobot(it, camera).toPose3D(AxesOrder.XYZ).magic()
    }