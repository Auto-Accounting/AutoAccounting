package net.ankio.xtools

import android.content.Context
import java.lang.Exception

abstract class HookItem {
    /**
     * 在[HookBase]中调用
     */
    fun hook(mContext: Context?, mAppClassLoader: ClassLoader?){
        try {
            if(System.getProperty(javaClass.name)=="true"){
                XTools.Xposed.log("hook: ${javaClass.name} 已经执行")
                return
            }
            System.setProperty(javaClass.name,"true")
            hookMain(mContext,mAppClassLoader)
        }catch (e : Exception){
            System.setProperty(javaClass.name,"false")
            XTools.Xposed.logError("hook出错:$e")
        }
    }

    /**
     * 主要的hook函数
     */
    abstract fun hookMain(mContext: Context?, mAppClassLoader: ClassLoader?)

}