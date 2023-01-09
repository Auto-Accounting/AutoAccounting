package net.ankio.utils.logutils

import android.app.Application
import android.util.Log
import net.ankio.utils.SpUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.deleteIfExists
import kotlin.io.path.getLastModifiedTime


object LogUtils {
    private var log_enable = true
    private var log_file  = ""

    /**
     * 初始化日志配置并删除过期日志，日志文件按天计算
     */
    fun init(application: Application){
        val now = System.currentTimeMillis() / 1000L
        val daySecond = (60 * 60 * 24).toLong()
        val dayTime = now - (now + 8 * 3600) % daySecond
        log_enable = SpUtils.getBooleanData(application, "log_enable")
        val logDir = application.cacheDir.absolutePath + "/logs/"
        log_file = "$logDir$dayTime.txt"
        //删除过期日志
        val dir = File(logDir)
        if(!dir.isDirectory){
            dir.mkdirs()
        }

        Files.walk(Paths.get(logDir))
            .filter { Files.isRegularFile(it) }
            .forEach {
              if(it.getLastModifiedTime().toMillis() <  dayTime){
                  it.deleteIfExists()
              }
            }
    }

    /**
     * 构建日志文本并写入文件
     */
    private fun buildMsg(tag:String,level: String,msg: String): String {
        val buffer = StringBuilder()
        buffer.append("[ ");
        buffer.append(Thread.currentThread().name)
        buffer.append(" ] ");
        buffer.append(msg)
        val str = buffer.toString()
        if(log_file !==""){
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            val formatted = current.format(formatter)
            val file = File(log_file)
            if(!file.exists()){
                file.writeText("___ begin Log \n")
            }
            //写入日志文件
            Files.write(Paths.get(log_file), "[ $formatted ] [ $level ][ $tag ]$str \n".toByteArray(), StandardOpenOption.APPEND)
        }
        return str
    }

    /**
     * 自动获取日志标签
     */
    private fun getLogTag(): String {
        val prefix = "AutoAccount_"
        var i = 4//需要忽略几个
       for(trace in Thread.currentThread().stackTrace){
           if(i<0){
               return prefix + trace.className.substringAfterLast(".")
           }
           i--
       }
        return prefix + "MainThread"
   }

    /**
     * 写日志
     * @param msg 日志内容
     * @param level 日志等级，使用 [LogType] 指定
     * @param tag 日志标签，如果没有就自动生成
     */

    fun write(msg: String, level: LogType = LogType.Info, tag: String? = null){
        var t = tag
        if (t===null){
            t = getLogTag()
        }
            if(level=== LogType.Info){
                buildMsg(t,"Info",msg).let { Log.d(t, it) }
            }else if(level=== LogType.Warning){
                buildMsg(t,"Warning",msg).let { Log.w(t, it) }
            }else{
                buildMsg(t,"Error",msg).let { Log.e(t, it) }
                buildMsg(t,"Error","___ begin Trace").let { Log.e(t, it) }
                //报错需要打印堆栈
                for (trace in Thread.currentThread().stackTrace){
                    buildMsg(t,"Error","${trace.className}:${trace.methodName}()#line ${trace.lineNumber}").let { Log.e(t, it) }
                }
                buildMsg(t,"Error","___ end Trace").let { Log.e(t, it) }
            }
   }
}