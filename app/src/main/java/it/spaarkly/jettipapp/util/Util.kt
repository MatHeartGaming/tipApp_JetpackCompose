package it.spaarkly.jettipapp.util

fun calculateTotalTip(totalBill: Double, percentage: Int): Double {
    return if(totalBill.toString().isNotEmpty() && totalBill > 1)
        (totalBill * percentage) / 100 else 0.0
}

fun calculateTotalPerPerson(totalBill: Double, splitBy : Int, tipPercentage : Int): Double {
    val bill = calculateTotalTip(totalBill, tipPercentage) + totalBill
    return bill / splitBy
}