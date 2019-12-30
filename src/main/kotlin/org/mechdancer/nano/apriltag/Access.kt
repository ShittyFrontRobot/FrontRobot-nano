package org.mechdancer.nano.apriltag

import com.sun.jna.ptr.DoubleByReference
import org.mechdancer.algebra.implement.vector.vector3DOf
import org.mechdancer.common.Pose3D
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder

/**
 * 获取当前位姿缓存
 */
fun getTagOnCamera(): Pose3D {
    val tx = DoubleByReference()
    val ty = DoubleByReference()
    val tz = DoubleByReference()
    val rx = DoubleByReference()
    val ry = DoubleByReference()
    val rz = DoubleByReference()

    AprilTagCUDA.instance.get_tag_pose(
        tx.pointer,
        ty.pointer,
        tz.pointer,
        rx.pointer,
        ry.pointer,
        rz.pointer
    )
    return Pose3D(
        vector3DOf(
            tx.value,
            ty.value,
            tz.value
        ),
        Angle3D(
            rx.value.toRad(),
            ry.value.toRad(),
            rz.value.toRad(),
            AxesOrder.XYZ
        )
    )
}