package net.center.upload_plugin.helper

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * Created by 钟新
 * Time:2022/9/1.
 * Desc:
 */
object CmdHelper2 {
    private const val LOG_MAX_COUNT = 50
    private const val LOG_MIN_COUNT = 10
    private const val GIT_LOG_BASIC_CMD = "git log --oneline --pretty=format:\"%an:%s\" --no-merges"
    const val gitLogCmd = GIT_LOG_BASIC_CMD + " --since=\"2022-8-30\" --until=\"2022-10-1\""

    /**
     * %ai 是时间，格式：2022-08-31 23:18:48 +0800
     */
    const val gitLogCmd2 = "git log --oneline --pretty=format:\"%ai,%an:%s\" --no-merges --since=2days"
    const val gitLogCmd3 = GIT_LOG_BASIC_CMD + " --max-count=10"
    fun getGitLogByTimeAndCount(logDayTime: Int, logMaxCount: Int): String {
        var logMaxCount = logMaxCount
        val logBuilder = StringBuilder(GIT_LOG_BASIC_CMD)
        if (logDayTime >= 1) {
            logBuilder.append(" --since=").append(logDayTime).append("days")
            logMaxCount = LOG_MAX_COUNT
        } else {
            if (logMaxCount <= 0) {
                logMaxCount = LOG_MIN_COUNT
            } else if (logMaxCount > LOG_MAX_COUNT) {
                logMaxCount = LOG_MAX_COUNT
            }
        }
        logBuilder.append(" --max-count=").append(logMaxCount)
        return exeCmd(logBuilder.toString())
    }

    @JvmStatic
    fun main(args: Array<String>) {
        exeCmd(gitLogCmd)
    }

    fun exeCmd(commandStr: String?): String {
        val p = Runtime.getRuntime().exec(commandStr)
        val ret = BufferedReader(InputStreamReader(p.inputStream, StandardCharsets.UTF_8)).useLines { it.mapIndexed { i, s -> "$i. $s" }.joinToString("\n") }
        println("ExeCmd result：\n$ret")
        return ret
    }
}