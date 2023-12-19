package com.cookandroid.block7

import android.widget.ImageButton
import kotlin.random.Random

class Event_independence_insomnia (GameActivity: GameActivity, eventName: String,  type: Int, weight: Int, isAvailable: Boolean)
    : Event(GameActivity, eventName, type, weight, isAvailable) {

    // 생성자 - 기본 preScript, postScript 설정
    init {
        setPreScript(getScript("event_independence_insomnia_pre"))
        setPostScript(getScript("event_independence_insomnia_post"))
    }

    override fun setIsAvailable() {
        isAvailable = true
    }

    // 이벤트 효과 메소드
    override fun eventEffect(choose_value: Boolean) {
        if(choose_value) {
            GameActivity.item_medkit.loseItem()
        } else {
            GameActivity.member_dameun.stateChangeFatigued()
            GameActivity.member_soun.stateChangeFatigued()
            GameActivity.member_eunju.stateChangeFatigued()
            GameActivity.member_hyundong.stateChangeFatigued()
            setPostScript(getScript("event_independence_insomnia_post_tmp"))
        }
    }
}