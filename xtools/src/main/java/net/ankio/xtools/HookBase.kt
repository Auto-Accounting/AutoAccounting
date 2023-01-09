package net.ankio.xtools

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


abstract class HookBase {
    /**
     * hook的包名
     */
    abstract fun getPackPageName(): String
    /**
     * 是否存在壳，有壳先hook壳，没有壳返回null
     */
    open fun getShellName(): String? {return null}

    /**
     * 是否查找context
     */
    open fun findContext(): Boolean {return true}

    /**
     * hook入口
     */
    fun onLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?){
        val pkg = lpparam!!.packageName
        val processName = lpparam.processName
        hook(pkg,processName,lpparam.classLoader)
    }

    protected var mAppClassLoader: ClassLoader? = null
    protected var mContext: Context? = null

    /**
     * hook代码的执行位置
     */
    abstract fun hookLoadPackage(mContext:Context?,mAppClassLoader:ClassLoader?)
    /**
     * @param packageName String 需要hook的包名
     * @param processName  String 序号hook的进程名
     */
    private fun hook(packageName: String, processName: String, classLoader: ClassLoader) {
        val pack = getPackPageName()
        if (pack != packageName) {
            return
        }
        if (pack != processName) {
            return
        }
        XTools.Xposed.log("hook : packageName:$packageName, processName:$packageName, define:$pack")
        if(findContext()){
            hookMainInOtherAppContext(classLoader)
        }else{
            init()
        }

    }

    /**
     * 查找Context，一般是寻找App入口
     */
    private  fun hookMainInOtherAppContext(classLoader: ClassLoader) {
        XTools.Xposed.log("___ begin hook")
        //堆栈隐藏Xposed
        XposedHelpers.findAndHookMethod(
            StackTraceElement::class.java,
            "getClassName",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val className = param.result as String
                    if (className.contains("xposed")) {
                        param.result = "android.os.Handler"
                    }
                    super.afterHookedMethod(param)
                }
            })
        val shell = getShellName()
        if (shell!==null) {
           XTools.Xposed.log("hook $shell...")
           XposedHelpers.findAndHookMethod(getShellName(), classLoader,
                    "attachBaseContext", Context::class.java, object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun afterHookedMethod(param: MethodHookParam) {
                            super.afterHookedMethod(param)
                            XTools.Xposed.log("hook attachBaseContext")
                            mContext = param.args[0] as Context
                            mAppClassLoader = mContext!!.classLoader
                            init()
                        }
                    })
        } else {
            try {
                XposedHelpers.findAndHookMethod(
                    ContextWrapper::class.java, "attachBaseContext",
                    Context::class.java, object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun afterHookedMethod(param: MethodHookParam) {
                            super.afterHookedMethod(param)
                            XTools.Xposed.log("hook attachBaseContext")
                            mContext = param.args[0] as Context
                            mAppClassLoader = mContext!!.classLoader
                            init()
                        }
                    })
            } catch (e: Throwable) {
                try {
                    XposedHelpers.findAndHookMethod(
                        Application::class.java, "attach",
                        Context::class.java, object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            override fun afterHookedMethod(param: MethodHookParam) {
                                super.afterHookedMethod(param)
                                XTools.Xposed.log("hook attach")
                                mContext = param.args[0] as Context
                                mAppClassLoader = mContext!!.classLoader
                                init()
                            }
                        })
                } catch (e2: Throwable) {
                    XTools.Xposed.log("hook error $e2")
                }
            }
        }
        XTools.Xposed.log("___ end hook")
    }

    /**
     * 初始化Xposed加载器
     */
    private fun init() {
        //存在mContext再进行数据同步
        mContext?.let { XTools.Plugin.syncXposedData(it) };
        XTools.Xposed.log("hook   ${getPackPageName()}, framework: ${XTools.Xposed.getFramework()}" )
        XTools.Xposed.log("插件加载成功！")
        try {
            hookLoadPackage(mContext,mAppClassLoader)
        } catch (e: Error) {
            XTools.Xposed.logError("hook 出现严重错误！$e")
        } catch (e: Exception) {
            XTools.Xposed.logError("hook 出现严重错误！$e")
        }
    }
}