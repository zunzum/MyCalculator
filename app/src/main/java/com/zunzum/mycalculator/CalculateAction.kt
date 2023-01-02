package com.zunzum.mycalculator

//계산기 액션 이넘 클래스
enum class CalculateAction(val info : String) {
    Plus("더하기"), // 더하기
    Minus("빼기"), // 빼기
    Divide("나누기"), // 나누기
    Multiply("곱하기"), // 곱하기
    AllClear("모두삭제"), // 모두삭제
    Del("지우기"), // 지우기
    Calculate("계산하기") // 계산하기
}