package com.cookandroid.block7

class Event_independence_weWantToBathe (GameActivity: GameActivity, eventName: String,  type: Int, weight: Int, isAvailable: Boolean)
    : Event(GameActivity, eventName, type, weight, isAvailable) {

    // 생성자 - 기본 preScript, postScript 설정
    init {
        setPreScript(getScript("event_independence_weWantToBathe_pre"))
        setPostScript(getScript("event_independence_weWantToBathe_post"))
    }

    override fun setIsAvailable() {
        if(GameActivity.member_dameun.isNeedMedkit()||GameActivity.member_soun.isNeedMedkit()
            ||GameActivity.member_eunju.isNeedMedkit()||GameActivity.member_hyundong.isNeedMedkit()){
            isAvailable = false
        }
        else isAvailable = true

    }
    // 이벤트 효과 메소드
    override fun eventEffect(choose_value: Boolean) {
        if(choose_value) {
            GameActivity.item_medkit.loseItem()

            GameActivity.member_dameun.stateChangeNotSick()
            GameActivity.member_soun.stateChangeNotSick()
            GameActivity.member_eunju.stateChangeNotSick()
            GameActivity.member_hyundong.stateChangeNotSick()

        } else {
            val selectedMembers = GameActivity.memberListIsIn.shuffled().take(1)
            // 선택된 멤버에 대해 stateChangeSick() 적용
            selectedMembers.forEach { member ->
                member.stateChangeSick()
            }
            setPostScript(getScript("event_independence_weWantToBathe_post"))
        }
    }
}