package org.mechdancer.nano.apriltag

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer


@Suppress("FunctionName")
/**
 * JNA 调用 C++ 获取位姿缓存
 */
interface AprilTagCUDA : Library {

    fun get_tag_pose(tx: Pointer, ty: Pointer, tz: Pointer, rx: Pointer, ry: Pointer, rz: Pointer)

    companion object {
        val instance: AprilTagCUDA by lazy {
            Native.load("apriltag_cuda_native", AprilTagCUDA::class.java)
        }
    }
}