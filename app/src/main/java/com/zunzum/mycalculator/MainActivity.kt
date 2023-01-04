@file:OptIn(ExperimentalMaterial3Api::class)

package com.zunzum.mycalculator

import android.app.Notification.Action
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.zunzum.mycalculator.ui.theme.Purple40
import com.zunzum.mycalculator.ui.theme.Purple80
import kotlinx.coroutines.launch

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

    //첫번째 입력
    var firstInput by remember { mutableStateOf("0") }

    //두번째 입력
    var secondInput by remember { mutableStateOf("") }

    //현재 활성화된 액션
    val selectedAction : MutableState<CalculateAction?> = remember {
        mutableStateOf(null)
    }

    //현재 선택된 액션 심볼
    val selectedSymbol : String = selectedAction.value?.symbol?:""

    //계산 기록
    val calculateHistories : MutableState<List<String>> = remember { mutableStateOf(emptyList()) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    // 계산기록 보여주기 여부
    var isCalculateHistoryVisible by remember { mutableStateOf(true) }

    val watchHistoryToggleTitle : String = if (isCalculateHistoryVisible) "계산 기록 숨기기" else "계산기록 보기"

    val verticalSpacerWeight : Float = if (isCalculateHistoryVisible) 0.1f else 1f

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            onClick = {
                isCalculateHistoryVisible = !isCalculateHistoryVisible
            }
        ) {
            Text(text = watchHistoryToggleTitle,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .padding(3.dp)
            )
        }

        AnimatedVisibility(
            visible = isCalculateHistoryVisible,
            modifier = Modifier.weight(1f)
            ) {
            LazyColumn(
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(10.dp),
                reverseLayout = true,
                content = {
                    items(calculateHistories.value) { aHistory ->
                        Text(text = aHistory, modifier = Modifier.background(Purple80))
                    }
                })
        }

        Spacer(modifier = Modifier.weight(verticalSpacerWeight))

        LazyVerticalGrid(
            //Adaptive(숫자.dp) -> 숫자.dp만큼 최대한 맞춰서
            //Fixed(4) -> 괄호안에 칸에 맞춰서 4인경우 4칸
            columns = GridCells.Fixed(4),
            //가로 간격주기
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = {

                item(span = {GridItemSpan(maxLineSpan) }) {
                    NumberText(
                        firstInput,
                        secondInput,
                        selectedSymbol,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                item(span = {GridItemSpan(2) }) {
                    ActionButton(
                        action = CalculateAction.AllClear,
                        onClicked = {
                            firstInput = "0"
                            secondInput = ""
                            selectedAction.value = null
                        }
                    )
                }

                item(span = {GridItemSpan(1) }) {
                    ActionButton(action = CalculateAction.Del,
                        onClicked = {
                            // 두번째 입력을 지울 때
                            if (secondInput.length>0){
                                secondInput = secondInput.dropLast(1)
                                return@ActionButton
                            }

                            //선택된 연산을 지울때
                            if (selectedAction.value != null) {
                                selectedAction.value = null
                                return@ActionButton
                            }

                            // 연산도 없고 두번째 입력도 없을때
                            firstInput = if (firstInput.length == 1) "0" else firstInput.dropLast(1)
                        })
                }

                items(buttons) {aButton ->
                    //숫자 Int, CalculateAction 구분
                    when(aButton){
                        //Action버튼
                        is  CalculateAction -> ActionButton(aButton, selectedAction.value,
                            onClicked = {
                                selectedAction.value = aButton
                            })
                        //숫자버튼
                        is Int -> NumberButton(aButton,
                            onClicked = {

                                if (selectedAction.value == null) {
                                    if (firstInput == "0") firstInput = aButton.toString() else firstInput += aButton
                                } else {//연산 액션이 선택되었을때 두번째 입력으로 값을 넣기
                                    secondInput += aButton
                                }



                            })
                    }
                }
                item(span = {GridItemSpan(3) }) {
                    ActionButton(action = CalculateAction.Calculate,
                        onClicked = {
                            // 두번째 입력값이 없을 경우 예외처리
                            if (secondInput.isEmpty()) {
                                return@ActionButton
                            }

                            selectedAction.value?.let {
                                val result = doCalculate(
                                    firstNumber = firstInput.toFloat(),
                                    secondNumber = secondInput.toFloat(),
                                    action = it
                                )

                                // 계산 기록 업데이트
                                val calculateHistory = "$firstInput $selectedSymbol $secondInput = $result"
                                calculateHistories.value += calculateHistory

                                // 맨 위로 스크롤 시키기
                                coroutineScope.launch { scrollState.animateScrollToItem(calculateHistories.value.size)}

                                firstInput = result.toString()
                                secondInput = ""
                                selectedAction.value = null

                                Log.d(TAG, "$result")
                            } ?: Log.d(TAG, "태그된 연산이 없습니다")

                        })
                }

//                item(span = {GridItemSpan(maxLineSpan)}) {
//                    Text(text = "구글 광고 자리",
//                        modifier = Modifier
//                            .border(1.dp, Color.Blue)
//                            .height(60.dp)
//                            .wrapContentSize()
//
//                    )
//                }
            })
    }
}

//계산 처리
fun doCalculate(
    firstNumber: Float,
    secondNumber : Float,
    action: CalculateAction) : Float?{

    // + - * /
    return when(action){
        CalculateAction.Plus -> firstNumber + secondNumber
        CalculateAction.Minus -> firstNumber - secondNumber
        CalculateAction.Multiply -> firstNumber * secondNumber
        CalculateAction.Divide -> firstNumber / secondNumber
        else -> null
    }
}

@Composable
fun ActionButton(action: CalculateAction,
                 selectedAction : CalculateAction? = null,
                 onClicked: (() -> Unit)? = null){

    //로직 나누기 더 보기 편하게
    val isSelected : Boolean = selectedAction == action
    val cardContainerColor : Color = if (isSelected) Purple40 else ActionButtonBgColor
    val cardContentColor : Color = if (isSelected) Color.White else Color.Black

    Card(
        //그라데이션
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(cardContainerColor,cardContentColor),
        onClick = {
            Log.d(TAG,"카드 클릭되었다")
            onClicked?.invoke()
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
fun NumberText(
    firstInput : String,
    secondInput : String,
    selectedSymbol : String,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier,
    verticalAlignment = Alignment.Bottom,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = firstInput,
            //배경색 노랑
//            modifier = Modifier.background(Color.Yellow),
            //글자 사이즈
            fontSize = 50.sp,
            //볼드체
            fontWeight = FontWeight.Bold,
            //가운데정렬
            textAlign = TextAlign.Center,
            //높이
            lineHeight = 50.sp,
            //최대 줄
            maxLines = 1,
            //글자색
            color = Purple40
        )
        Text(selectedSymbol,
            //배경색 노랑
//            modifier = Modifier.background(Color.Yellow),
            //글자 사이즈
            fontSize = 50.sp,
            //볼드체
            fontWeight = FontWeight.Bold,
            //가운데정렬
            textAlign = TextAlign.Center,
            //높이
            lineHeight = 50.sp,
            //최대 줄
            maxLines = 1,
            //글자색
            color = Color.Black
        )
        Text(text = secondInput,
            //배경색 노랑
//            modifier = Modifier.background(Color.Yellow),
            //글자 사이즈
            fontSize = 50.sp,
            //볼드체
            fontWeight = FontWeight.Bold,
            //가운데정렬
            textAlign = TextAlign.Center,
            //높이
            lineHeight = 50.sp,
            //최대 줄
            maxLines = 1,
            //글자색
            color = Color.Black
        )
    }
}

@Composable
fun NumberButton(number: Int, onClicked : () -> Unit) {
    Card(
        //그라데이션
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = onClicked
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