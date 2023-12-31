package com.cookandroid.block7

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView.FindListener
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView


class GameActivity : BaseActivity() {
    lateinit var blackBackground : ImageView
    lateinit var dayText : TextView

    lateinit var member_dameun: Member
    lateinit var member_eunju: Member
    lateinit var member_soun: Member
    lateinit var member_hyundong: Member

    lateinit var item_axe: Item
    lateinit var item_book: Item
    lateinit var item_card: Item
    lateinit var item_drone: Item
    lateinit var item_medkit: Item
    lateinit var item_flashlight: Item
    lateinit var item_gasmask: Item
    lateinit var item_lock: Item
    lateinit var item_map: Item
    lateinit var item_pesticide: Item
    lateinit var item_phone: Item
    lateinit var item_rattle: Item
    lateinit var item_toolbox: Item

    lateinit var food_kimbap: Food
    lateinit var food_water: Food

    lateinit var memberList: List<Member>

    lateinit var memberListIsIn: MutableList<Member>

    lateinit var itemList: List<Item>
    val itemListOwned = mutableListOf<Item>()
    val itemListNotOwned = mutableListOf<Item>() //초기에는 모든 아이템 없는걸로 취급
    val itemListBroken = mutableListOf<Item>()

    lateinit var item_choose_button : ImageButton

    /* 무작위로 객체를 반환하는 메소드 */
    fun getRandomMemberFromListIsIn(): Member { // 무작위 멤버 : 안에 있는 멤버 중 무작위로 1명을 골라 반환함
        return memberListIsIn.random()
        // Item과 달리 null은 고려하지 않음.
    }
    fun getRandomItemitemListOwned(): Item? { // 무작위 아아템 : 무작위로 가지고 있는 아이템 중 하나의 아이템을 선택하는 메소드
        return if (itemListOwned.isNotEmpty()) { itemListOwned.random() }
        else { null } // 리스트가 비어있을 경우 null 반환
    }
    fun getRandomItemitemListNotOwned(): Item? {
        return if (itemListNotOwned.isNotEmpty()) { itemListNotOwned.random() }
        else { null}
    }
    fun selectRandomFood(): Food { // 무작위로 김밥, 물 중 하나를 선택하는 메소드
        val randomIndex = (0..1).random() // 0 또는 1 중에서 무작위 선택
        return if (randomIndex == 0) { food_kimbap }
        else { food_water }
    }

    // 멤버 외출
    fun explore(member: Member) {
        member.goExploring()
    }

    // 멤버 dayPase - 하루가 지날 때 호출
    fun memberDayPase() { for(member in memberList) member.dayPase() }

    // 아이템과 멤버의 visiblity 업데이트 메소드 - finishbutton 클릭 리스너에서 호출 + ...
    fun updateAllVisibility() {
        for(item in itemListOwned) item.updateItemVisibility()
        for(item in itemListBroken) item.updateItemVisibility()
        for(item in itemListNotOwned) item.updateItemVisibility()
        for(member in memberList) member.updateVisibility()
        food_kimbap.updateVisibility()
        food_water.updateVisibility()
    }

    lateinit var eventHandler: EventHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)



        val musicIntent = Intent(this, MusicService::class.java)
        musicIntent.action = "CHANGE_MUSIC"
        musicIntent.putExtra("MUSIC_RES_ID", R.raw.game_background_music) // 새 배경음악 리소스 ID
        startService(musicIntent)

        member_dameun  = Member(this,"dameun", "담은", findViewById(R.id.member_dameun))
        member_eunju  = Member(this,"eunju", "은주", findViewById(R.id.member_eunju))
        member_soun  = Member(this,"sowoon", "소운", findViewById(R.id.member_sowoon))
        member_hyundong = Member(this,"hyundong", "현동", findViewById(R.id.member_hyundong))

        memberList = listOf(member_dameun, member_eunju, member_soun, member_hyundong)
        memberListIsIn = mutableListOf(member_dameun, member_eunju, member_soun, member_hyundong)

        item_axe = Item(this, "axe", "도끼", findViewById(R.id.item_axe))
        item_book = Item(this, "book", "전공책", findViewById(R.id.item_book))
        item_card = Item(this, "card", "할리갈리", findViewById(R.id.item_card))
        item_drone = Item(this, "drone", "드론", findViewById(R.id.item_drone))
        item_medkit = Item(this, "medkit", "구급상자", findViewById(R.id.item_firstaidkit))
        item_flashlight = Item(this, "flashlight", "손전등",findViewById(R.id.item_flashlight))
        item_gasmask = Item(this, "gasmask", "방독면", findViewById(R.id.item_gasmask))
        item_lock = Item(this, "lock", "자물쇠", findViewById(R.id.item_lock))
        item_map = Item(this, "map", "지도", findViewById(R.id.item_map))
        item_pesticide = Item(this, "pesticide", "살충제", findViewById(R.id.item_pesticide))
        item_phone = Item(this, "phone", "휴대폰", findViewById(R.id.item_phone))
        item_rattle = Item(this, "rattle", "딸랑이", findViewById(R.id.item_rattle))
        item_toolbox = Item(this, "toolbox", "도구상자", findViewById(R.id.item_toolbox))

        itemList = listOf(item_axe, item_book, item_card, item_drone, item_medkit, item_flashlight,
            item_gasmask, item_lock, item_map, item_pesticide, item_phone, item_rattle, item_toolbox)
        //모든 아이템들에 대한 lose처리
        for(item in itemList) item.loseItem()

        food_kimbap = Food(this, "kimbap", "김밥", findViewById(R.id.food_kimbap))
        food_water = Food(this, "water", "물", findViewById(R.id.food_water))

        blackBackground = findViewById(R.id.black_background)
        dayText = findViewById(R.id.day_text)

        var date = 1
        /* 1일차에 기본적으로 수행되는 로직들 */
        dayText.setText("${date}일차") //dayText 업데이트
        randomizeItems() // 아이템 랜덤 생성
        updateAllVisibility()

        firstdayfadeout(blackBackground, dayText)

        // 인게임 옵션 버튼
        val ingame_option_button : ImageButton = findViewById(R.id.ingame_option_button)
        ingame_option_button.setOnClickListener{
            val intent = Intent(this, OptionDialog::class.java)
            startActivity(intent)
        }

        eventHandler = EventHandler(this)

        /* Page 관련 .. */
        var current_page = 1

        val closebutton_page1 : ImageButton = findViewById(R.id.close_button_page1)
        val closebutton_page2 : ImageButton = findViewById(R.id.close_button_page2)
        val closebutton_page3 : ImageButton = findViewById(R.id.close_button_page3)
        val closebutton_page4 : ImageButton = findViewById(R.id.close_button_page4)

        var pre_script : TextView = findViewById(R.id.pre_event_script)
        var post_script : TextView = findViewById(R.id.post_event_script)

        var selectedEvent = eventHandler.getRandomEvent()

        pre_script.text = selectedEvent.getPreScript()
        post_script.text = "우리가 무사히 살아나갈 수 있을까?"

        val survival_note_page1 : View = findViewById(R.id.page1)
        val survival_note_page2 : View = findViewById(R.id.page2)
        val survival_note_page3 : View = findViewById(R.id.page3)
        val survival_note_page4 : View = findViewById(R.id.page4)

        survival_note_page1.visibility = View.GONE
        survival_note_page2.visibility = View.GONE
        survival_note_page3.visibility = View.GONE
        survival_note_page4.visibility = View.GONE

        val page1to2button : ImageButton = findViewById(R.id.next_button_page1to2)
        val page2to3button : ImageButton = findViewById(R.id.next_button_page2to3)
        val page3to4button : ImageButton = findViewById(R.id.next_button_page3to4)

        val page2to1button : ImageButton = findViewById(R.id.previous_button_page2to1)
        val page3to2button : ImageButton = findViewById(R.id.previous_button_page3to2)
        val page4to3button : ImageButton = findViewById(R.id.previous_button_page4to3)



        page1to2button.setOnClickListener { switchPages(survival_note_page1, survival_note_page2) }
        page2to3button.setOnClickListener { switchPages(survival_note_page2, survival_note_page3) }
        page3to4button.setOnClickListener { switchPages(survival_note_page3, survival_note_page4) }
        page4to3button.setOnClickListener { switchPages(survival_note_page4, survival_note_page3) }
        page3to2button.setOnClickListener { switchPages(survival_note_page3, survival_note_page2) }
        page2to1button.setOnClickListener { switchPages(survival_note_page2, survival_note_page1) }

        closebutton_page1.setOnClickListener {
            survival_note_page1.visibility = View.GONE
            current_page = 1
        }
        closebutton_page2.setOnClickListener {
            survival_note_page2.visibility = View.GONE
            current_page = 2
        }
        closebutton_page3.setOnClickListener {
            survival_note_page3.visibility = View.GONE
            current_page = 3
        }
        closebutton_page4.setOnClickListener {
            survival_note_page4.visibility = View.GONE
            current_page = 4
        }

        val pc_button : ImageButton = findViewById(R.id.pc_button)
        pc_button.setOnClickListener {
            when (current_page) {
                1 -> survival_note_page1.visibility = View.VISIBLE
                2 -> survival_note_page2.visibility = View.VISIBLE
                3 -> survival_note_page3.visibility = View.VISIBLE
                4 -> survival_note_page4.visibility = View.VISIBLE
            }
        }

        var is_soun_kimbap_check = false
        var is_dameun_kimbap_check = false
        var is_eunju_kimbap_check = false
        var is_hyundong_kimbap_check = false

        var is_soun_water_check = false
        var is_dameun_water_check = false
        var is_eunju_water_check = false
        var is_hyundong_water_check = false

        var is_soun_medkit_check = false
        var is_dameun_medkit_check = false
        var is_eunju_medkit_check = false
        var is_hyundong_medkit_check = false

        val soun_medkit : ImageButton = findViewById(R.id.soun_medkit_check)
        val dameun_medkit : ImageButton = findViewById(R.id.dameun_medkit_check)
        val eunju_medkit : ImageButton = findViewById(R.id.eunju_medkit_check)
        val hyundong_medkit : ImageButton = findViewById(R.id.hyundong_medkit_check)

        soun_medkit.visibility = View.GONE
        dameun_medkit.visibility = View.GONE
        eunju_medkit.visibility = View.GONE
        hyundong_medkit.visibility = View.GONE

        if(member_dameun.isNeedMedkit()) dameun_medkit.visibility = View.VISIBLE
        if(member_soun.isNeedMedkit()) soun_medkit.visibility = View.VISIBLE
        if(member_eunju.isNeedMedkit()) eunju_medkit.visibility = View.VISIBLE
        if(member_hyundong.isNeedMedkit()) hyundong_medkit.visibility = View.VISIBLE

        dameun_medkit.setOnClickListener{
            if(is_dameun_medkit_check){
                is_dameun_medkit_check = true
                dameun_medkit.alpha = 1.0f
            }
            else{
                is_dameun_medkit_check = false
                dameun_medkit.alpha = 0.5f
            }
        }

        soun_medkit.setOnClickListener{
            if(is_soun_medkit_check){
                is_soun_medkit_check = true
                soun_medkit.alpha = 1.0f
            }
            else{
                is_soun_medkit_check = false
                soun_medkit.alpha = 0.5f
            }
        }

        eunju_medkit.setOnClickListener{
            if(is_eunju_medkit_check){
                is_eunju_medkit_check = true
                eunju_medkit.alpha = 1.0f
            }
            else{
                is_eunju_medkit_check = false
                eunju_medkit.alpha = 0.5f
            }
        }

        hyundong_medkit.setOnClickListener{
            if(is_hyundong_medkit_check){
                is_hyundong_medkit_check = true
                hyundong_medkit.alpha = 1.0f
            }
            else{
                is_hyundong_medkit_check = false
                hyundong_medkit.alpha = 0.5f
            }
        }

        val soun_kimbap : ImageButton = findViewById(R.id.soun_kimbap_check)
        val dameun_kimbap : ImageButton = findViewById(R.id.dameun_kimbap_check)
        val eunju_kimbap : ImageButton = findViewById(R.id.eunju_kimbap_check)
        val hyundong_kimbap : ImageButton = findViewById(R.id.hyundong_kimbap_check)

        val soun_water : ImageButton = findViewById(R.id.soun_water_check)
        val dameun_water : ImageButton = findViewById(R.id.dameun_water_check)
        val eunju_water : ImageButton = findViewById(R.id.eunju_water_check)
        val hyundong_water : ImageButton = findViewById(R.id.hyundong_water_check)

        val finishbutton : ImageButton = findViewById(R.id.next_button_toend)

        val pre_event_o_button : ImageButton = findViewById(R.id.o_button)
        val pre_event_x_button : ImageButton = findViewById(R.id.x_button)
        var item_choose_button : ImageButton = findViewById(R.id.item_choose_button)

        var choose_value = false

        var is_ox_event = false
        var is_item_event = false

        /* Page 관련 메소드 */

        // 멤버 스크립트 반환
        fun getAllMemberScript(): String {
            return member_dameun.memberScript + member_soun.memberScript + member_hyundong.memberScript + member_eunju.memberScript
        }

        // 식량 배급 메소드
        fun feed() {
            if(is_soun_kimbap_check) member_soun.eatKimbab()
            if(is_dameun_kimbap_check) member_dameun.eatKimbab()
            if(is_eunju_kimbap_check) member_eunju.eatKimbab()
            if(is_hyundong_kimbap_check) member_hyundong.eatKimbab()
            if(is_soun_water_check) member_soun.drinkWater()
            if(is_dameun_water_check) member_dameun.drinkWater()
            if(is_eunju_water_check) member_eunju.drinkWater()
            if(is_hyundong_water_check) member_hyundong.drinkWater()
        }

        // 페이지 초기화 메소드
        fun initPage() {
            is_soun_kimbap_check = false
            is_dameun_kimbap_check = false
            is_eunju_kimbap_check = false
            is_hyundong_kimbap_check = false

            is_soun_water_check = false
            is_dameun_water_check = false
            is_eunju_water_check = false
            is_hyundong_water_check = false

            soun_kimbap.alpha = 0.5f
            dameun_kimbap.alpha = 0.5f
            eunju_kimbap.alpha = 0.5f
            hyundong_kimbap.alpha = 0.5f

            soun_water.alpha = 0.5f
            dameun_water.alpha = 0.5f
            eunju_water.alpha = 0.5f
            hyundong_water.alpha = 0.5f

            choose_value = false
            item_choose_button.alpha = 0.5f // 아이템 버튼의 alpha 값을 0.5로 설정
            pre_event_o_button.alpha = 0.5f // 'o' 버튼의 alpha 값을 0.5로 설정
            pre_event_x_button.alpha = 0.5f // 'o' 버튼의 alpha 값을 0.5로 설정
        }

        fun setEventType() {
            // 이벤트 타입 파악
            if(selectedEvent.type == 1) {
                is_ox_event = true
                is_item_event = false
            } else if(selectedEvent.type == 0) {
                is_ox_event = false
                is_item_event = true
            }
            // 이벤트 타입에 따른 버튼 View 설정
            if(is_ox_event) { // OX 이벤트의 경우
                pre_event_o_button.visibility = View.VISIBLE
                pre_event_x_button.visibility = View.VISIBLE
                item_choose_button.visibility = View.GONE
                finishbutton.visibility = View.GONE
            } else if(is_item_event) { // item 이벤트의 경우
                pre_event_o_button.visibility = View.GONE
                pre_event_x_button.visibility = View.GONE
                item_choose_button.visibility = View.VISIBLE
                finishbutton.visibility = View.VISIBLE
            } else { // 이벤트가 선택되지 않은 경우 - 첫째날 등 ..
                pre_event_o_button.visibility = View.GONE
                pre_event_x_button.visibility = View.GONE
                item_choose_button.visibility = View.GONE
                finishbutton.visibility = View.VISIBLE
            }
        }
        setEventType() // 1일차를 위해 남겨둠

        /* Page 관련 클릭 리스너 */

        soun_kimbap.setOnClickListener {
            is_soun_kimbap_check = !is_soun_kimbap_check
            soun_kimbap.alpha = if (is_soun_kimbap_check) 1f else 0.5f }
        dameun_kimbap.setOnClickListener {
            is_dameun_kimbap_check = !is_dameun_kimbap_check
            dameun_kimbap.alpha = if (is_dameun_kimbap_check) 1f else 0.5f }
        eunju_kimbap.setOnClickListener {
            is_eunju_kimbap_check = !is_eunju_kimbap_check
            eunju_kimbap.alpha = if (is_eunju_kimbap_check) 1f else 0.5f }
        hyundong_kimbap.setOnClickListener {
            is_hyundong_kimbap_check = !is_hyundong_kimbap_check
            hyundong_kimbap.alpha = if (is_hyundong_kimbap_check) 1f else 0.5f }
        soun_water.setOnClickListener {
            is_soun_water_check = !is_soun_water_check
            soun_water.alpha = if (is_soun_water_check) 1f else 0.5f }
        dameun_water.setOnClickListener {
            is_dameun_water_check = !is_dameun_water_check
            dameun_water.alpha = if (is_dameun_water_check) 1f else 0.5f }
        eunju_water.setOnClickListener {
            is_eunju_water_check = !is_eunju_water_check
            eunju_water.alpha = if (is_eunju_water_check) 1f else 0.5f }
        hyundong_water.setOnClickListener {
            is_hyundong_water_check = !is_hyundong_water_check
            hyundong_water.alpha = if (is_hyundong_water_check) 1f else 0.5f }

        // item 이벤트 - 클릭 리스너
        item_choose_button.setOnClickListener {
            if(choose_value == false){
                choose_value = true
                item_choose_button.alpha = 1f
            } else {
                choose_value = false
                item_choose_button.alpha = 0.5f
            }
        }

        // OX 이벤트 - O 버튼 클릭 리스너
        pre_event_o_button.setOnClickListener {
            choose_value = true // 'o' 버튼이 선택됨
            finishbutton.visibility = View.VISIBLE

            pre_event_o_button.alpha = 1f // 'o' 버튼의 alpha 값을 1로 설정
            pre_event_x_button.alpha = 0.5f // 'x' 버튼의 alpha 값을 0.5로 설정
        }

        //  OX 이벤트 - X 버튼 클릭 리스너
        pre_event_x_button.setOnClickListener {
            choose_value = false // 'x' 버튼이 선택됨
            finishbutton.visibility = View.VISIBLE

            pre_event_o_button.alpha = 0.5f // 'o' 버튼의 alpha 값을 0.5로 설정
            pre_event_x_button.alpha = 1f // 'x' 버튼의 alpha 값을 1로 설정
        }

        // 클릭 리스너
        finishbutton.setOnClickListener {  //일차 종료 버튼
            survival_note_page4.visibility = View.GONE

            post_script.text = ""
            pre_script.text = ""

            // 1. 이벤트 클릭 리스너 결과 받아오기 --> 클릭 리스너에 처리되어 있음

            // 2. 이벤트 실행
            selectedEvent.executeEventEffect(choose_value)

            var str: String = ""

            // 3. Page1의 이벤트 스크립트 설정하기
            post_script.text = str + selectedEvent.getPostScript() + '\n' + getAllMemberScript()

            // 4. 이벤트 선택
            selectedEvent = eventHandler.getRandomEvent()
            setEventType()

            // 5. Page4의 이벤트 스크립트 설정하기
            pre_script.text = selectedEvent.getPreScript()

            // 필요한 동작 수행
            feed()

            // 페이지 초기화
            initPage()

            date++
            dayText.setText("${date}일차")
            animateScreenTransition(blackBackground, dayText)

            memberDayPase() // 모든 멤버 dayPase
            updateAllVisibility() // 아이템, 멤버, 식량 visivility 업데이트

            if(is_soun_medkit_check) {member_soun.stateChangeNotSick(); member_soun.stateChangeNotHurt()}
            if(is_eunju_medkit_check) {member_eunju.stateChangeNotSick(); member_eunju.stateChangeNotHurt()}
            if(is_dameun_medkit_check) {member_dameun.stateChangeNotSick(); member_dameun.stateChangeNotHurt()}
            if(is_hyundong_medkit_check) {member_hyundong.stateChangeNotSick(); member_hyundong.stateChangeNotHurt()}

        }
    }

    /*--------------------------------------------------------------------------------------------*/

    // 1일차에 아이템을 랜덤으로 생성하는 함수

    fun randomizeItems() {
        // 식량 랜덤으로 생성
        food_kimbap.getFoodRandom(3,5)
        food_water.getFoodRandom(3,5)

        // 아이템을 랜덤으로 6개 골라 얻음
        val randomItems = itemList.shuffled().take(6)
        randomItems.forEach { it.getItem() }
    }

    // 일차가 넘어갈 때 페이드인 애니메이션 ?
    fun animateScreenTransition(background: ImageView, dayText: TextView) {
        // 배경의 페이드 인 애니메이션
        ObjectAnimator.ofFloat(background, "alpha", 0f, 1f).apply {
            duration = 1000 // n초 동안
            start()
        }.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // 배경 애니메이션이 끝나면 텍스트의 페이드 인 애니메이션 시작
                ObjectAnimator.ofFloat(dayText, "alpha", 0f, 1f).apply {
                    duration = 500 // 3초 동안
                    start()
                }.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // 텍스트 애니메이션이 끝나면 2초 동안 유지
                        dayText.postDelayed({
                            // 텍스트의 페이드 아웃 애니메이션
                            ObjectAnimator.ofFloat(dayText, "alpha", 1f, 0f).apply {
                                duration = 2000 // 3초 동안
                                start()
                            }
                            // 배경의 페이드 아웃 애니메이션
                            ObjectAnimator.ofFloat(background, "alpha", 1f, 0f).apply {
                                duration = 2000 // 3초 동안
                                start()
                            }
                        }, 2000) // 2초 동안 대기
                    }
                })
            }
        })
    }

    // 첫째날 페이드인 애니메이션 ?
    fun firstdayfadeout(background: ImageView, dayText: TextView) {
        // 배경의 페이드 인 애니메이션
        ObjectAnimator.ofFloat(background, "alpha", 0f, 1f).apply {
            duration = 0 // n초 동안
            start()
        }.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // 배경 애니메이션이 끝나면 텍스트의 페이드 인 애니메이션 시작
                ObjectAnimator.ofFloat(dayText, "alpha", 0f, 1f).apply {
                    duration = 2000 // 3초 동안
                    start()
                }.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // 텍스트 애니메이션이 끝나면 2초 게이동안 유지
                        dayText.postDelayed({
                            // 텍스트의 페이드 아웃 애니메이션
                            ObjectAnimator.ofFloat(dayText, "alpha", 1f, 0f).apply {
                                duration = 2000 // 3초 동안
                                start()
                            }
                            // 배경의 페이드 아웃 애니메이션
                            ObjectAnimator.ofFloat(background, "alpha", 1f, 0f).apply {
                                duration = 2000 // 3초 동안
                                start()
                            }
                        }, 2000) // 2초 동안 대기
                    }
                })
            }
        })
    }

    private fun switchPages(fromPage: View, toPage: View) {
        // 두 페이지 모두에 애니메이션을 적용하기 위해 공통의 부모 뷰를 선택합니다
        val parent = fromPage.parent as ViewGroup

        // 페이드 아웃 및 페이드 인 애니메이션 설정
        val transition = Fade()
        transition.duration = 600 // 애니메이션 지속 시간 설정

        // 애니메이션 시작
        TransitionManager.beginDelayedTransition(parent, transition)

        // 이전 페이지를 숨기고 새 페이지를 보여줍니다
        fromPage.visibility = View.GONE
        toPage.visibility = View.VISIBLE
    }

    fun getItemChooseButton(): ImageButton {
        return item_choose_button
    }
}