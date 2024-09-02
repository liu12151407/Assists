package com.ven.assists.simple.step

import android.content.ComponentName
import android.content.Intent
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import com.blankj.utilcode.util.TimeUtils
import com.ven.assists.Assists
import com.ven.assists.Assists.click
import com.ven.assists.Assists.findByTags
import com.ven.assists.Assists.findFirstParentClickable
import com.ven.assists.Assists.getBoundsInScreen
import com.ven.assists.Assists.log
import com.ven.assists.Assists.paste
import com.ven.assists.simple.App
import com.ven.assists.simple.OverManager
import com.ven.assists.stepper.Step
import com.ven.assists.stepper.StepCollector
import com.ven.assists.stepper.StepImpl
import kotlinx.coroutines.delay

class PublishSocial : StepImpl() {
    override fun onImpl(collector: StepCollector) {
        collector.next(StepTag.STEP_1) { it ->
            OverManager.log("启动微信")
            Intent().apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
                Assists.service?.startActivity(this)
            }
            it.data?.let {
                OverManager.log("PublishSocial STEP_1 收到数据：$it")
            }
            return@next Step.get(StepTag.STEP_2, data = "字符串数据：2")
        }.next(StepTag.STEP_2) {

            it.data?.let {
                OverManager.log("收到数据：$it")
            }
            Assists.findByText("发现").forEach {
                val screen = it.getBoundsInScreen()
                if (screen.left > 630 && screen.top > 1850) {
                    OverManager.log("已打开微信主页，点击【发现】")
                    it.parent.parent.click()
                    return@next Step.get(StepTag.STEP_3, data = "字符串数据：3333")

                }
            }

            if (Assists.getPackageName() == App.TARGET_PACKAGE_NAME) {
                Assists.back()
                return@next Step.get(StepTag.STEP_2, data = "字符串数据：22222")
            }

            if (it.repeatCount == 5) {
                return@next Step.get(StepTag.STEP_1, data = "字符串数据：1111111")
            }

            return@next Step.repeat
        }.next(StepTag.STEP_3) {

            it.data?.let {
                OverManager.log("收到数据：$it")
            }
            Assists.findByText("朋友圈").forEach {
                it.log()
                val screen = it.getBoundsInScreen()
                if (screen.left > 140 && screen.top > 240) {
                    OverManager.log("点击朋友圈")
                    it.findFirstParentClickable()?.let {
                        it.log()
                        it.click()
                    }
                }
                return@next Step.get(StepTag.STEP_4)
            }
            return@next Step.none
        }.next(StepTag.STEP_4) {
            Assists.findByText("朋友圈封面，再点一次可以改封面").forEach {
                OverManager.log("已进入朋友圈")
                return@next Step.get(StepTag.STEP_5)
            }
            Assists.findByText("朋友圈封面，点按两次修改封面").forEach {
                OverManager.log("已进入朋友圈")
                return@next Step.get(StepTag.STEP_5)

            }
            if (it.repeatCount == 5) {
                OverManager.log("未进入朋友圈")
            }
            return@next Step.repeat
        }.next(StepTag.STEP_5) {
            runIO { delay(2000) }
            Assists.findByText("拍照，记录生活").firstOrNull()?.let {
                Assists.findByText("我知道了").firstOrNull()?.click()
                runIO {
                    delay(1000)
                }
            }

            OverManager.log("点击拍照分享按钮")
            Assists.findByText("拍照分享").forEach {
                it.click()
                return@next Step.get(StepTag.STEP_6)
            }
            return@next Step.none
        }.next(StepTag.STEP_6) {
            OverManager.log("从相册选择")
            Assists.findByText("从相册选择").forEach {
                it.findFirstParentClickable()?.let {
                    it.click()
                    return@next Step.get(StepTag.STEP_7)
                }
            }
            return@next Step.none
        }.next(StepTag.STEP_7) {
            Assists.findByText("我知道了").forEach {
                it.click()
                return@next Step.get(StepTag.STEP_7)
            }
            Assists.findByText("权限申请").forEach {
                Assists.findByText("确定").forEach {
                    it.click()
                    return@next Step.get(StepTag.STEP_7)
                }
            }
            Assists.findByText("允许").forEach {
                it.click()
                return@next Step.get(StepTag.STEP_8)
            }
            OverManager.log("选择第一张相片")
            return@next Step.get(StepTag.STEP_8)
        }.next(StepTag.STEP_8) {
            Assists.findByTags("android.support.v7.widget.RecyclerView").forEach {
                for (index in 0 until it.childCount) {
                    if (TextUtils.equals("android.widget.RelativeLayout", it.getChild(index).className)) {
                        it.getChild(index).let { child ->
                            child.findByTags("android.widget.TextView").firstOrNull() ?: let {
                                child.findByTags("android.widget.CheckBox").forEach { it.click() }
                                return@next Step.get(StepTag.STEP_9)
                            }
                        }
                    }
                }
            }
            Assists.findByTags("androidx.recyclerview.widget.RecyclerView").forEach {
                for (index in 0 until it.childCount) {
                    if (TextUtils.equals("android.widget.RelativeLayout", it.getChild(index).className)) {
                        it.getChild(index).let { child ->
                            child.findByTags("android.widget.TextView").firstOrNull() ?: let {
                                child.findByTags("android.widget.CheckBox").forEach { it.click() }
                                return@next Step.get(StepTag.STEP_9)
                            }
                        }

                    }
                }
            }
            return@next Step.none
        }.next(StepTag.STEP_9) {
            OverManager.log("点击完成")
            Assists.findByText("完成").forEach {
                it.click()
                return@next Step.get(StepTag.STEP_10)

            }
            return@next Step.none

        }.next(StepTag.STEP_10) {
            OverManager.log("输入发表内容")
            Assists.findByTags("android.widget.EditText").forEach {
                it.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                it.paste("${TimeUtils.getNowString()}: Assists发的一条私密朋友圈")
                return@next Step.get(StepTag.STEP_11)

            }
            return@next Step.none

        }.next(StepTag.STEP_11) {
            OverManager.log("点击谁可以看")
            Assists.findByText("谁可以看").forEach {
                it.findFirstParentClickable()?.let { it.click() }
                return@next Step.get(StepTag.STEP_12)
            }
            return@next Step.none

        }.next(StepTag.STEP_12) {
            OverManager.log("点击仅自己可见")
            Assists.findByText("仅自己可见").forEach {
                it.findFirstParentClickable()?.let { it.click() }
                return@next Step.get(StepTag.STEP_13)
            }
            return@next Step.none

        }.next(StepTag.STEP_13) {
            OverManager.log("点击完成")
            Assists.findByText("完成").forEach {
                it.click()
                return@next Step.get(StepTag.STEP_14)
            }
            return@next Step.none

        }.next(StepTag.STEP_14) {
            OverManager.log("点击发表")
            Assists.findByText("发表").forEach {
                it.click()
            }
            return@next Step.none
        }
    }

}