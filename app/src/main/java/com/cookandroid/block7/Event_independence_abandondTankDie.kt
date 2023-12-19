package com.cookandroid.block7

import kotlin.random.Random

class Event_independence_abandondTankDie (GameActivity: GameActivity, eventName: String,  type: Int, weight: Int, isAvailable: Boolean)
    : Event(GameActivity, eventName, type, weight, isAvailable) {

    // 생성자 - 기본 preScript, postScript 설정
    init {
        setPreScript(getScript("event_independence_abandondTankDie_pre"))
        setPostScript(getScript("event_independence_abandondTankDie_post"))
    }

    override fun setIsAvailable() {
        if(!GameActivity.itemListNotOwned.isEmpty()) {isAvailable = true}
        else {isAvailable = false}
    }

    // 이벤트 효과 메소드
    override fun eventEffect(choose_value: Boolean) {
        if(choose_value) {
            val randomMember = if (GameActivity.memberListIsIn.isNotEmpty())
                GameActivity.memberListIsIn[Random.nextInt(GameActivity.memberListIsIn.size)] else null

            randomMember?.die()
        } else {
            setPostScript(getScript("event_independence_abandondTankGetItem_post_tmp"))
        }
    }
}