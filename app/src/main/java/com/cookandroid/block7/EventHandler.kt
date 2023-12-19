package com.cookandroid.block7

import kotlin.random.Random

// EventHandler : 이벤트 객체 생성, 확률 조정 등
class EventHandler(GameActivity: GameActivity) {
    var GameActivity: GameActivity = GameActivity

    var defaultWeight: Int = 5
    var defaultIsAvailable: Boolean = true

    val typeOX: Int = 1
    val typeItem: Int = 0

    var events = mutableListOf<Event>()

    // 클래스의 생성자 또는 초기화 블록
    init {
        val event_invation_robbery = Event_invasion_robbery(GameActivity, "event_invasion_robbery", typeOX, defaultWeight, defaultIsAvailable)
        val event_invasion_grandma = Event_invasion_grandma(GameActivity, "event_invasion_grandma", typeOX, defaultWeight, defaultIsAvailable)
        val event_invasion_firefighter = Event_invasion_firefighter(GameActivity, "event_invasion_firefighter", typeOX, defaultWeight, defaultIsAvailable)
        val event_plunder_oldMan = Evnet_plunder_oldMan(GameActivity, "evnet_plunder_oldMan", typeOX, defaultWeight, defaultIsAvailable)
        val event_independence_abandondTankGetItem = Event_independence_abandondTankGetItem(GameActivity, "event_independence_abandondTankGetItem", typeOX, defaultWeight, defaultIsAvailable)
        val event_independence_abandondTankDie = Event_independence_abandondTankDie(GameActivity, "event_independence_abandondTankDie", typeOX, defaultWeight, defaultIsAvailable)
        val event_independence_goraniHouseGetKimbap = Event_independence_goraniHouseGetKimbap(GameActivity, "event_independence_goraniHouseGetKimbap", typeOX, defaultWeight, defaultIsAvailable)
        val event_independence_fightDameunEunju = Event_independence_fightDameunEunju(GameActivity, "event_independence_fightDameunEunju", typeOX, defaultWeight, defaultIsAvailable)
        val event_independence_fightEunjuHyundong = Event_independence_fightEunjuHyundong(GameActivity, "event_independence_fightEunjuHyundong", typeOX, defaultWeight, defaultIsAvailable)
        val event_independence_fightSounDameun = Event_independence_fightSounDameun(GameActivity, "event_independence_fightSounDameun", typeOX, defaultWeight, defaultIsAvailable)
        val event_independence_fightSounEunju = Event_independence_fightSounEunju(GameActivity, "event_independence_fightSounEunju", typeOX, defaultWeight, defaultIsAvailable)

        events.add(event_invation_robbery)
        events.add(event_invasion_grandma)
        events.add(event_invasion_firefighter)
        events.add(event_plunder_oldMan)
        events.add(event_independence_abandondTankGetItem)
        events.add(event_independence_abandondTankDie)
        events.add(event_independence_goraniHouseGetKimbap)
        events.add(event_independence_fightDameunEunju)
        events.add(event_independence_fightEunjuHyundong)
        events.add(event_independence_fightSounDameun)
        events.add(event_independence_fightSounEunju)

    }



    // 탐험 시 발생할 수 있는 이벤트
    var events_exploring = mutableListOf<Event>()

    /* 랜덤으로 이벤트 선택 및 실행 */

    // 이벤트 실행
    fun runSelectedEvent(selectedEvent: Event, choose_value: Boolean) {
        selectedEvent.executeEventEffect(choose_value)
    }
    // 랜덤 이벤트 선택
    fun selectRandomEvent(): Event {
        // do-while 루프를 통해 선택된 이벤트 객체가 실행 불가능한 경우 다시 뽑도록 함
        // isAvailable가 true인 이벤트 객체가 뽑힐 때까지 실행
        var selectedEvent: Event

        do {
            selectedEvent = getRandomEvent()
        } while (!selectedEvent.isAvailable)

        return  selectedEvent
    }

    // 가중치를 기반으로 랜덤으로 이벤트 선택
    fun getRandomEvent(): Event {
        val totalWeight = events.sumBy { it.weight }

        var randomValue = Random.nextInt(totalWeight)
        var selectedEvent: Event = events.first()

        for (event in events) {
            if (randomValue < event.weight) {
                selectedEvent = event
                break
            }
            randomValue -= event.weight
        }

        return selectedEvent
    }

    /* events_scripts.txt에서 읽어오는 코드 */
    private val eventScripts: Map<String, String> = readEventScriptsFromResources(R.raw.event_scripts)

    private fun readEventScriptsFromResources(resourceId: Int): Map<String, String> {
        return try {
            val scriptText = GameActivity.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
            parseEventScripts(scriptText)
        } catch (e: Exception) {
            mapOf()
        }
    }

    private fun parseEventScripts(scriptText: String): Map<String, String> {
        val scripts = mutableMapOf<String, String>()
        val lines = scriptText.split("\n")

        var currentEventKey: String? = null
        var currentEventScript: StringBuilder? = null

        for (line in lines) {
            if (line.startsWith("event_")) {
                currentEventKey?.let { key ->
                    currentEventScript?.let { script ->
                        scripts[key] = script.toString()
                    }
                }
                currentEventKey = line.trim()
                currentEventScript = StringBuilder()
            } else {
                currentEventScript?.append(line)?.append("\n")
            }
        }

        currentEventKey?.let { key ->
            currentEventScript?.let { script ->
                scripts[key] = script.toString()
            }
        }

        return scripts
    }

    fun getScript(eventKey: String): String {
        return eventScripts[eventKey] ?: ""
    }
}