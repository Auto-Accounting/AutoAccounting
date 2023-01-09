package net.ankio.page.hook.app.wechat

import android.content.Context
import net.ankio.page.hook.app.wechat.hooks.Settings
import net.ankio.xtools.HookBase

class WeChatMain:HookBase() {
    /**
     * hook的包名
     */
    override fun getPackPageName(): String {
        return "mm.tencent.wechat"
    }

    /**
     * hook代码的执行位置
     */
    override fun hookLoadPackage(mContext: Context?, mAppClassLoader: ClassLoader?) {
        Settings().hook(mContext,mAppClassLoader)
    }



}