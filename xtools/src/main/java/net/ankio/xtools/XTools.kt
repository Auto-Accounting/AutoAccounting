package net.ankio.xtools

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import de.robv.android.xposed.XposedBridge
import net.ankio.utils.MultiprocessSharedPreferences
import net.ankio.utils.SpUtils
import net.ankio.utils.logutils.LogType
import net.ankio.utils.logutils.LogUtils
import net.ankio.xtools.reflection.ClassObjects

/**
 * Xposed工具类，操作Xposed相关的API
 */
object XTools  {

    object Xposed{
        /**
         * 获取当前 Hook 框架的名称
         *
         */
        fun getFramework(): String {
            return runCatching {
                ClassObjects(XposedBridge::class.java)
                    .getField("TAG").toString()
                    .replace( "Bridge","")
                    .replace("-","")
                    .trim()
            }.getOrNull() ?: "Unknown"
        }
        /**
         * 写日志
         */
        fun log(msg:String){
            //日志直接写入目标app的缓存目录，不再写入XposedBridge.log
            LogUtils.write(msg, LogType.Warning,"AnkioHook")
        }
        /**
         * 写日志
         */
        fun logError(msg:String){
            //日志直接写入目标app的缓存目录，不再写入XposedBridge.log
            LogUtils.write(msg, LogType.Error,"AnkioHookError")
            XposedBridge.log(msg)//出错后写入XposedBridge.log
        }
    }




    object App{
        /**
         * 获取版本号
         */
        fun getVersionCode(context: Context): Int {
            var versionCode = 0
            try {
                //获取软件版本号，对应AndroidManifest.xml下android:versionCode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    versionCode = context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
                        .toInt()
                }else{
                    versionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                Xposed.logError("获取版本号出错：$e")
            }
            return versionCode
        }

        /**
         * 获取版本名称
         */
        fun getVersionName(context: Context): String? {
            var versionName: String? = ""
            try {
                //获取软件版本号，对应AndroidManifest.xml下android:versionName
                versionName =
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                Xposed.logError("获取版本名称出错：$e")
            }
            return versionName
        }

        /**
         * 获取应用名称
         */
        fun getName(context: Context): String {return context.applicationInfo.name}

        /**
         * 获取应用包名
         */
        fun getPackage(context: Context): String {return context.packageName}
    }

    object Plugin{
        /**
         * 从插件同步数据到宿主App
         */
        fun syncXposedData(context: Context){
            MultiprocessSharedPreferences.setAuthority("net.ankio.utils.provider")
            val data = MultiprocessSharedPreferences.getSharedPreferences(
                context,"XposedData", Context.MODE_PRIVATE
            )
            //所有同步数据存储到本地
            for (item in data.all){
                SpUtils.saveData(context,item.key,item.value)
            }

        }

        /**
         * 获取存储的Xposed数据
         * !!!插件本身调用方法
         */
        fun getXposedData(context:Context,key:String): String? {
            val data = MultiprocessSharedPreferences.getSharedPreferences(
                context,"XposedData", Context.MODE_PRIVATE
            )
            return data.getString(key,"");
        }
        /**
         * 设置存储的Xposed数据
         * !!!插件本身调用方法
         */
        fun setXposedData(context:Context,key:String,value: String){
            val data = MultiprocessSharedPreferences.getSharedPreferences(
                context,"XposedData", Context.MODE_PRIVATE
            )
            data.edit().putString(key, value).apply();
        }

    }








}