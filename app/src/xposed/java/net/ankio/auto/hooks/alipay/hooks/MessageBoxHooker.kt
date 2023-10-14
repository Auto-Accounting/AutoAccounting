/*
 * Copyright (C) 2023 ankio(ankio@ankio.net)
 * Licensed under the Apache License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-3.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package net.ankio.auto.hooks.alipay.hooks

import android.content.Context
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import net.ankio.auto.api.Hooker
import net.ankio.auto.api.PartHooker
import net.ankio.auto.hooks.android.AccountingService


class MessageBoxHooker(hooker: Hooker) :PartHooker(hooker) {
    override fun onInit(classLoader: ClassLoader?, context: Context?) {
        val msgboxInfoServiceImpl = XposedHelpers.findClass(
            "com.alipay.android.phone.messageboxstatic.biz.sync.d",
            classLoader
        )
        val syncMessage = XposedHelpers.findClass(
            "com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage",
            classLoader
        )

        XposedHelpers.findAndHookMethod(
            msgboxInfoServiceImpl,
            "onReceiveMessage",
            syncMessage,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    val syncMessageObject = param.args[0]
                    val getDataMethod = syncMessage.methods.find { it.name == "getData" }
                    getDataMethod?.let {
                        val result = it.invoke(syncMessageObject) as String
                        Log.w(hooker.appName, "Received Msg =>  $result")
                        val billInfo = hooker.mService.analyzeData(result)
                        if(billInfo!=null){
                            //分析结果不是null，唤起自动记账
                            hooker.mService.launchApp(billInfo)
                        }
                    }
                }
            })
    }
}