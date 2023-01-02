@file:OptIn(ExperimentalMaterial3Api::class)

package com.zunzum.mycalculator

import android.app.Notification.Action
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zunzum.mycalculator.MainActivity.Companion.TAG
import com.zunzum.mycalculator.ui.theme.ActionButtonBgColor
import com.zunzum.mycalculator.ui.theme.MyCalculatorTheme

class MainActivity : ComponentActivity() {

    //const val : 컴파일 중에 상수로 결정
    //val : 런타임 중에 상수로 결정
    companion object{
        const val TAG = "메인"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Calculator()
                }
            }
        }
    }
}

@Composable
fun Calculator(){

    //숫자 담는 배열 생성
    val numbers : List<Int> = listOf<Int>(0,1,2,3,4,5,6,7,8,9)

    //enum class에서 요소 가져오기
    val actions : Array<CalculateAction> = CalculateAction.values()

    val buttons = listOf(
        CalculateAction.Divide,
        7, 8, 9, CalculateAction.Multiply,
        4, 5, 6, CalculateAction.Minus,
        1, 2, 3, CalculateAction.Plus,
        0
    )

    LazyVerticalGrid(
        //Adaptive(숫자.dp) -> 숫자.dp만큼 최대한 맞춰서
        //Fixed(4) -> 괄호안에 칸에 맞춰서 4인경우 4칸
        columns = GridCells.Fixed(4),
        //가로 간격주기
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {

            item(span = {GridItemSpan(2) }) {
                ActionButton(action = CalculateAction.AllClear)
            }

            item(span = {GridItemSpan(1) }) {
                ActionButton(action = CalculateAction.Del)
            }

            items(buttons) {aButton ->
                //숫자 Int, CalculateAction 구분
                when(aButton){
                    is  CalculateAction -> ActionButton(aButton) //Action버튼
                    is Int -> NumberButton(aButton) //숫자버튼
                }
            }
            item(span = {GridItemSpan(3) }) {
                ActionButton(action = CalculateAction.Calculate)
            }
    })
}


@Composable
fun ActionButton(action: CalculateAction){
    Card(
        //그라데이션
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            //ui.theme에서 설정해둔 색으로 설정
            containerColor = ActionButtonBgColor
        ),
        onClick = {
            Log.d(TAG,"카드 클릭되었다")
        }
    ) {
        Text(action.symbol, modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NumberButton(number: Int) {
    Card(
        //그라데이션
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = {
            Log.d(TAG,"카드 클릭되었다")
        }
    ) {
        Text(number.toString(), modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyCalculatorTheme {
        Greeting("Android")
    }
}