import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aube.domain.util.estimateLatestRoundForRegister
import com.aube.domain.util.formatRoundDate
import kotlinx.coroutines.launch

@Composable
fun RoundSelectorSimple(
    modifier: Modifier = Modifier,
    onRoundSelected: (Int) -> Unit
) {
    val latestRound = remember { estimateLatestRoundForRegister() } // 다음주(발표 전 포함)
    val candidates = remember(latestRound) {
        listOf(latestRound - 1, latestRound, latestRound + 1)
    }

    val pagerState = rememberPagerState(initialPage = 2, pageCount = { candidates.size })
    val scope = rememberCoroutineScope()

    // 스와이프나 버튼으로 페이지 바뀌면 콜백 실행
    LaunchedEffect(pagerState.currentPage) {
        val round = candidates[pagerState.currentPage]
        onRoundSelected(round)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 🔹 왼쪽 버튼
            IconButton(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage > 0) pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                enabled = pagerState.currentPage > 0,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "이전 회차"
                )
            }

            // 🔹 중앙 영역: 스와이프 가능
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val round = candidates[page]
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "${round}회 (${formatRoundDate(round)})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 🔹 오른쪽 버튼
            IconButton(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < candidates.lastIndex)
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                enabled = pagerState.currentPage < candidates.lastIndex,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "다음 회차"
                )
            }
        }
    }
}
