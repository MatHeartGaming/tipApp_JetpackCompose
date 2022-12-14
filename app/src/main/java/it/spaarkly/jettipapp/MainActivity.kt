package it.spaarkly.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.spaarkly.jettipapp.components.InputField
import it.spaarkly.jettipapp.ui.theme.JetTipAppTheme
import it.spaarkly.jettipapp.util.calculateTotalPerPerson
import it.spaarkly.jettipapp.util.calculateTotalTip
import it.spaarkly.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipAppTheme {
                MyApp {
                    Column(modifier = Modifier.padding(16.dp)) {
                        MainContent()
                    }
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        content()
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = RoundedCornerShape(12.dp)),
        //.clip(shape = CircleShape.copy(all = CornerSize(12.dp)))
        color = Color(0XFFE9D7F7),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text("Total per person", style = MaterialTheme.typography.h5)
            Text("$$total", style = MaterialTheme.typography.h4, fontWeight = FontWeight.ExtraBold)
        }
    }
}


@Preview
@Composable
fun MainContent() {
    val tipAmountState = remember {
        mutableStateOf(10.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    val splitNumber = remember {
        mutableStateOf(2)
    }
    BillForm(tipAmountState = tipAmountState, totalPerPersonState = totalPerPersonState, splitByState = splitNumber) {}
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    splitByState : MutableState<Int>,
    tipAmountState : MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val sliderPosition = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPosition.value * 100).toInt()

    val keyboardController = LocalSoftwareKeyboardController.current

    TopHeader(totalPerPerson = totalPerPersonState.value)
    Spacer(modifier = Modifier.height(15.dp))
    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
    ) {
        Column(modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions

                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()

                },
                onChange = {
                    if(totalBillState.value.isEmpty()) return@InputField
                    tipAmountState.value = calculateTotalTip(totalBill = totalBillState.value.toDouble(), percentage = tipPercentage)
                    totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage, splitBy = splitByState.value)
                }
            )

            if(validState) {
                Row(modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start,
                    ) {
                    Text("Split", modifier = Modifier.align(Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End,
                        ) {
                        RoundIconButton(
                            modifier = modifier,
                            imageVector = Icons.Default.Remove,
                            onClickAction = {
                                Log.d("Icon", "BillForm: Remove")
                                val newValueSplit = splitByState.value - 1;
                                if(newValueSplit < 1) {
                                    return@RoundIconButton
                                }
                                splitByState.value = newValueSplit
                                totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage, splitBy = splitByState.value)
                            }
                        )
                        Text(text = splitByState.value.toString(),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 9.dp))
                        RoundIconButton(
                            modifier = modifier,
                            imageVector = Icons.Default.Add,
                            onClickAction = {
                                Log.d("Icon", "BillForm: Add")
                                val newValueSplit = splitByState.value + 1;
                                splitByState.value = newValueSplit
                                totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage, splitBy = splitByState.value)
                            }
                        )
                    }
                }
                //Tip Row
                Row(modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .padding(vertical = 12.dp)) {
                    Text("Tip")
                    Spacer(modifier = Modifier.width(200.dp))
                    Text("€${tipAmountState.value}")
                }
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$tipPercentage%")
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(modifier = Modifier.padding(horizontal = 16.dp),
                        value = sliderPosition.value,
                        steps = 5,
                        onValueChange = {newVal ->
                            sliderPosition.value = newVal
                            tipAmountState.value = calculateTotalTip(totalBill = totalBillState.value.toDouble(), percentage = tipPercentage)
                            totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage, splitBy = splitByState.value)
                        })

                    Button(
                        onClick = {
                            sliderPosition.value = 0f
                            totalBillState.value = "0"
                            splitByState.value = 2
                            tipAmountState.value = calculateTotalTip(totalBill = totalBillState.value.toDouble(), percentage = tipPercentage)
                            totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage, splitBy = splitByState.value)
                    }) {
                        Text("Reset", color = MaterialTheme.colors.background)
                    }
                }
            } else {
                Box(modifier)
            }


        }

    }
}




@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipAppTheme {
        MyApp {
            Column {
                MainContent()
            }
        }
    }
}