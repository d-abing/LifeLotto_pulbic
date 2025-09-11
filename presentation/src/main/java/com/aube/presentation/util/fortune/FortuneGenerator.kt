package com.aube.presentation.util.fortune

import com.aube.presentation.model.Fortune
import java.time.LocalDate
import kotlin.random.Random

object FortuneGenerator {
    private val summaries = listOf(
        "작은 기회가 큰 수익으로 이어져요.",
        "지출보다 수입에 집중하면 좋아요.",
        "사람을 통해 재물이 들어옵니다.",
        "모아온 경험이 수익 전환점을 만듭니다.",
        "의외의 절약이 큰 힘이 됩니다.",
        "오늘의 선택이 내일의 행운을 만듭니다.",
        "작은 투자에서 큰 기쁨이 생깁니다.",
        "뜻밖의 행운이 지갑을 채워줄 거예요.",
        "꾸준함이 결국 수익으로 돌아옵니다.",
        "마음의 여유가 재물 운을 불러옵니다.",
        "오늘은 도전이 보상으로 연결됩니다.",
        "기대치 않은 선물이 다가옵니다.",
        "웃음이 재물 운을 끌어당겨요.",
        "소소한 행운이 쌓여 큰 행운이 됩니다.",
        "낯선 만남이 재물의 시작점이에요.",
        "긍정적인 마음이 부를 부릅니다.",
        "오늘의 행운은 가까운 곳에 있어요.",
        "잊었던 기회가 다시 찾아옵니다.",
        "작은 손실 뒤에 큰 이득이 따라옵니다.",
        "오늘은 행운의 숫자가 함께해요.",
        "소신 있는 선택이 수익을 가져옵니다.",
        "기다림 끝에 기쁜 소식이 있어요.",
        "예상 밖의 보너스가 다가옵니다.",
        "오늘의 행운은 용기 있는 행동에 있습니다.",
        "주변 사람의 말이 재물 운을 높입니다.",
        "오늘은 작은 것에 감사하면 커집니다.",
        "새로운 도전이 보상을 불러옵니다.",
        "숨겨진 기회가 드러나는 날이에요.",
        "작은 선택이 큰 결과로 이어집니다.",
        "준비한 만큼 수익이 돌아옵니다.",
        "오늘은 행운의 기운이 강해요.",
        "재물 운이 상승세를 타고 있습니다.",
        "주저하지 말고 한 발 더 나아가세요.",
        "뜻하지 않은 곳에서 금전운이 들어옵니다.",
        "지금의 결정이 길게 이익이 됩니다.",
        "오늘은 숫자가 행운을 알려줍니다.",
        "마음을 열면 기회가 다가옵니다.",
        "작은 성취가 큰 행운을 부릅니다.",
        "조금의 모험이 이익을 가져와요.",
        "금전운이 기대 이상으로 따라옵니다.",
        "친구와의 대화가 재물 운을 키워줍니다.",
        "오늘의 행운은 평소와 다른 길에서 옵니다.",
        "작은 즐거움이 큰 보상이 됩니다.",
        "성실함이 금전운으로 이어집니다.",
        "놓쳤던 기회가 다시 열립니다.",
        "웃는 얼굴이 재물 운을 불러요.",
        "오늘은 마음먹은 일이 술술 풀립니다.",
        "숫자가 행운의 열쇠가 되는 날이에요.",
        "의외의 상황에서 기쁜 소식이 옵니다.",
        "작은 행운이 모여 큰 기적을 만듭니다."
    )


    fun generate(date: LocalDate, seedKey: String = ""): Fortune {
        val rnd = Random((date.toEpochDay().toString() + seedKey).hashCode())
        val score = rnd.nextInt(40, 101)
        val nums = buildSet { while (size < 3) add(rnd.nextInt(1, 46)) }.toList().sorted()
        val hour = listOf("09:00","11:00","13:00","15:00","17:00","19:00")[rnd.nextInt(6)]
        return Fortune(
            dateEpochDay = date.toEpochDay(),
            score = score,
            summary = summaries[rnd.nextInt(summaries.size)],
            luckyNumbers = nums,
            luckyTime = "$hour - ${hour.replace(":00",":30")}",
        )
    }
}

