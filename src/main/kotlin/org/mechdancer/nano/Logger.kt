package org.mechdancer.nano

import org.mechdancer.common.SimpleLogger


/** 全局日志器 **/
val globalLogger by lazy { SimpleLogger("nano", "globalLog") }

fun SimpleLogger.info(vararg msg: Any?) =
    log("[Info]", *msg)

inline fun SimpleLogger.info(block: () -> Any?) =
    log("[Info]", block())

inline fun SimpleLogger.debug(block: () -> Any?) {
    if (RidiculousConstants.DEBUG)
        log("[Debug]", block())
}