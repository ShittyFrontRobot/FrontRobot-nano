package org.mechdancer.nano.serial

import org.mechdancer.nano.serial.data.DataSerializer

class SerialDataListener<R, T : DataSerializer<R>>(
    private val serializer: T,
    private val onNewData: (R?) -> Unit
) {
    fun update(data: ByteArray) {
        onNewData(serializer.fromArray(data))
    }
}