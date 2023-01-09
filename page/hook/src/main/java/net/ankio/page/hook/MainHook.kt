package net.ankio.page.hook

import net.ankio.page.hook.app.wechat.WeChatMain
import net.ankio.xtools.HookBase
import net.ankio.xtools.XToolsInit

class MainHook : XToolsInit {
    override fun getHookList(): ArrayList<HookBase> {
        val arrayList: ArrayList<HookBase> = ArrayList()
        arrayList.add(WeChatMain())
        return arrayList
    }
}