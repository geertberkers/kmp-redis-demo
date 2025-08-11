@file:Suppress("EXTERNAL_DECLARATION_IN_INAPPROPRIATE_FILE")

package geet.berkers.kmpredis

@JsFun("console.log")
external fun consoleLog(msg: String)

@JsFun("console.error")
external fun consoleError(msg: String)

@JsFun("console.warn")
external fun consoleWarn(msg: String)
