package com.aube.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aube.presentation.R
import com.aube.presentation.ui.component.home.LottoBall
import com.aube.presentation.viewmodel.LottoViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    lottoViewModel: LottoViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lottoUiState by lottoViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("로또 번호 등록")

        Button(
            onClick =
            {
                lottoViewModel.saveMyLottoNumbers(listOf(
                    listOf(1, 2, 3, 4, 5, 6),
                    listOf(7, 8, 9, 10, 11, 12),
                    listOf(13, 14, 15, 16, 17, 18),
                    listOf(5, 13, 26, 29, 37, 40)
                ))
            }
        ) {
            Text("번호 랜덤 등록")
        }

        lottoUiState.myNumbers?.forEach {
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            )  {
                Icon(
                    painter = painterResource(R.drawable.keep),
                    contentDescription = "Keep",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {}
                )

                Spacer(Modifier.width(8.dp))

                it.forEach {
                    LottoBall(number = it)
                    Spacer(Modifier.width(4.dp))
                }

                Spacer(Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { lottoViewModel.deleteMyLottoNumbers(0) }
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
