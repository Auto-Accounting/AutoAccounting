package net.ankio.auto

import android.app.Application
import android.content.Context
import net.ankio.utils.logutils.LogUtils

class App :Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        initTools()
        LogUtils.write("___ begin Startup")
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.write("App Create")
    }

    override fun onTerminate() {
        super.onTerminate()
        LogUtils.write("___ App Terminate")
    }
    private fun initTools(){
        //日志记录工具初始化
        LogUtils.init(this@App)

    }
}