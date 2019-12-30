package org.mechdancer.nano.serial.data

abstract class Packet<T : Packet<T>>(val serializer: DataSerializer<T>) {

    fun serialize() = serializer.toByteArray(this as T)

}