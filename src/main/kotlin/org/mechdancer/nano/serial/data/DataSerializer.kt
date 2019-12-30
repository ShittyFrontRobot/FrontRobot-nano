package org.mechdancer.nano.serial.data

import org.mechdancer.nano.RidiculousConstants
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.xor

/**
 * 数据包编解码
 */
abstract class DataSerializer<T>(
    /**
     * 帧类型
     */
    val type: Byte,
    /**
     * 帧大小
     */
    val size: Int
) {
    /**
     * 编码
     */
    abstract fun toByteArray(data: T): ByteArray

    /**
     * 解码
     */
    abstract fun fromByteArray(array: ByteArray): T?

    protected fun OutputStream.write(byte: Byte) = write(byte.toInt())

    protected fun OutputStream.writeType() = write(type)

    protected fun OutputStream.writeHead() = write(RidiculousConstants.PACKET_HEAD)

    protected fun ByteArray.xorAll() = foldRight(0, Byte::xor)

    protected fun ByteArray.xorTail() = sliceArray(1..lastIndex).xorAll()

    protected inline fun newStreamedByteArray(size: Int = 32, block: ByteArrayOutputStream.() -> Unit): ByteArray =
        ByteArrayOutputStream(size).apply(block).toByteArray()

    protected inline fun newLEArray(size: Int, block: ByteBuffer.() -> Unit): ByteArray =
        ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN).apply(block).array()

    protected fun ByteArray.toLEArray(): ByteBuffer = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN)

    /**
     * 检查编码后数据是否属于该数据包
     *
     * 1. 判断大小
     * 2. 判断帧头
     * 3. 去掉最后校验位后异或
     */
    protected fun ByteArray.checkPacket(expectXOR: Byte) =
        size == this@DataSerializer.size &&
            first() == RidiculousConstants.PACKET_HEAD &&
            get(1) == type &&
            sliceArray(1 until lastIndex).xorAll() == expectXOR

    /**
     * 将编码后数据拆为三部分
     * (帧头, 数据, 校验位)
     */
    protected inline fun <T> ByteArray.splitPacket(block: (Byte, ByteArray, Byte) -> T): T =
        // 丢弃超过定长项
        take(size).run {
            block(
                get(1),
                sliceArray(2 until lastIndex),
                last()
            )
        }
}