package org.mechdancer.nano.serial.data

import java.io.OutputStream
import kotlin.experimental.xor

/**
 * 数据包编解码
 */
abstract class DataSerializer<T>(
    /**
     * 帧头
     */
    val head: Byte,
    /**
     * 帧大小
     */
    val size: Int
) {
    /**
     * 编码
     */
    abstract fun toArray(data: T): ByteArray

    /**
     * 解码
     */
    abstract fun fromArray(array: ByteArray): T?

    protected fun OutputStream.write(byte: Byte) = write(byte.toInt())

    protected fun OutputStream.writeHead() = write(head)

    protected fun ByteArray.xorAll() = foldRight(0, Byte::xor)

    /**
     * 检查编码后数据是否属于该数据包
     *
     * 1. 判断大小
     * 2. 判断帧头
     * 3. 去掉最后校验位后异或
     */
    protected fun ByteArray.checkPacket(expectXOR: Byte) =
        size == this@DataSerializer.size && first() == head && dropLast(1).toByteArray().xorAll() == expectXOR

    /**
     * 将编码后数据拆为三部分
     * (帧头, 数据, 校验位)
     */
    protected fun <T> ByteArray.splitPacket(block: (Byte, ByteArray, Byte) -> T): T =
        // 丢弃超过定长项
        take(size).run {
            block(
                first(),
                drop(1).dropLast(1).toByteArray(),
                last()
            )
        }
}