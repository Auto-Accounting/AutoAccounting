package net.ankio.xtools

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

interface XToolsInit : IXposedHookLoadPackage {
    fun getHookList():ArrayList<HookBase>
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        for (hook in getHookList()) {
            hook.onLoadPackage(lpparam)
        }
    }

}