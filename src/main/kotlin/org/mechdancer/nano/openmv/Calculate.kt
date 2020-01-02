package org.mechdancer.nano.openmv

import org.mechdancer.algebra.implement.vector.vector3DOfZero
import org.mechdancer.common.Pose3D
import org.mechdancer.common.toTransformation
import org.mechdancer.geometry.angle.toDegree
import org.mechdancer.geometry.angle.toRad
import org.mechdancer.geometry.rotation3d.Angle3D
import org.mechdancer.geometry.rotation3d.AxesOrder
import org.mechdancer.geometry.transformation.Transformation

val idealTagToTag =
    Transformation.fromInhomogeneous(Angle3D(90.0.toDegree(), 0.toRad(), 0.toDegree(), AxesOrder.XYZ).matrix, vector3DOfZero())

// Axes order of *AprilTag*: Z, Y, X
// Intrinsic
fun tagToCamera(aprilTag: Pose3D) = aprilTag.toTransformation()

//Axes order of Camera: X, Y, Z
// Extrinsic
fun cameraToRobot(camera: Pose3D) = camera.toTransformation()

fun tagToRobot(aprilTag: Pose3D, camera: Pose3D) =
    (cameraToRobot(camera) * tagToCamera(aprilTag))

fun idealTagToRobot(aprilTag: Pose3D, camera: Pose3D) =
    cameraToRobot(camera) * tagToCamera(aprilTag) * idealTagToTag