package com.example.stock.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.stock.R
import com.example.stock.data.ChartData
import com.example.stock.data.RetrofitClient
import com.example.stock.data.response.stock.Item
import com.example.stock.databinding.ActivityStockInfoBinding
import com.example.stock.view.dialog.BuyStockDialog
import com.example.stock.viewmodel.StockInfoViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.google.firebase.database.*
import java.text.DecimalFormat

class StockInfoActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProvider(this, StockInfoViewModel.Factory(
            RetrofitClient.STOCK_KEY, 30,
            itemName
        ))[StockInfoViewModel::class.java]
    }
    private lateinit var itemName: String
    private lateinit var binding: ActivityStockInfoBinding
    private lateinit var database: DatabaseReference
    private lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStockInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        item = intent.getSerializableExtra("info") as Item
        itemName = intent.getStringExtra("name").toString()
        binding.stockInfoStockName.text = itemName
        binding.stockName.text = itemName

        binding.buyingButton.setOnClickListener {
            // 다이얼로그 생성
            BuyStockDialog(this, item).show()
        }

        val candle = arrayListOf<ChartData>()
        viewModel.getStockInfo()
        viewModel.getStockInfoLiveData.observe(this) { response ->
            if (response != null) {
                val list = response.response?.body?.items?.item!!
                for (i in list.indices) {
                    val candleData = ChartData(
                        i.toLong(),
                        list[i].mkp.toFloat(),
                        list[i].clpr.toFloat(),
                        list[i].hipr.toFloat(),
                        list[i].lopr.toFloat()
                    )
                    candle.add(candleData)
                }
                initChart()
                setChartData(candle)

                // set data
                setData(list[0])
            }
        }
    }

    // candle stick chart
    @SuppressLint("SetTextI18n")
    private fun setData(item: Item) {
        val dec = DecimalFormat("#,###")    // 천단위 구분

        binding.presentPrice.text = "${dec.format(item.clpr.toInt())}원"
        binding.highPrice.text = "${dec.format(item.hipr.toInt())}원"
        binding.lowPrice.text = "${dec.format(item.lopr.toInt())}원"
        binding.startPrice.text = "${dec.format(item.mkp.toInt())}원"
        binding.trade.text = dec.format(item.trqu.toInt())
        if (item.fltRt.toDouble() > 0) {
            // 변동률
            binding.fluctutaionRate.setTextColor(Color.RED)
            if (item.fltRt.toDouble() < 1) {
                binding.fluctutaionRate.text = "+0${item.fltRt}%"
            } else binding.fluctutaionRate.text = "+${item.fltRt}%"
            // 변동값
            binding.fluctutaionAmount.setTextColor(Color.RED)
            binding.fluctutaionAmount.text = "▲ ${dec.format(item.vs.toInt())}"
        } else if (item.fltRt.toDouble() < 0) {
            binding.fluctutaionRate.setTextColor(Color.BLUE)
            if (item.fltRt.toDouble() > -1) {
                val fltRt = item.fltRt
                val res = fltRt.substring(0,1) + "0" + fltRt.substring(1)
                binding.fluctutaionRate.text = "$res%"
            } else binding.fluctutaionRate.text = "${item.fltRt}%"

            binding.fluctutaionAmount.setTextColor(Color.BLUE)
            binding.fluctutaionAmount.text = "▼ ${item.vs.substring(1)}"
        } else {
            binding.fluctutaionRate.text = "${item.fltRt}%"
            binding.fluctutaionAmount.text = "- ${dec.format(item.vs.toInt())}"
        }
    }

    private fun initChart() {
        binding.apply {
            candleStickChart.description.isEnabled = false
            candleStickChart.setMaxVisibleValueCount(30)
            candleStickChart.setPinchZoom(false)
            candleStickChart.setDrawGridBackground(false)
            // x축
            candleStickChart.xAxis.apply {
                textColor = Color.TRANSPARENT
                position = XAxis.XAxisPosition.BOTTOM
                // 세로선 표시 여부
                this.setDrawGridLines(true)
                axisLineColor = Color.rgb(50, 59, 76)
                gridColor = Color.rgb(50, 59, 76)
            }
            // 왼쪽 y축
            candleStickChart.axisLeft.apply {
                textColor = Color.BLACK
                isEnabled = false
            }
            // 오른쪽 y축 설정
            candleStickChart.axisRight.apply {
                setLabelCount(7, false)
                textColor = Color.BLACK
                // 가로선 표시 여부
                setDrawGridLines(true)
                // 차트의 오른쪽 테두리
                setDrawAxisLine(true)
                axisLineColor = Color.rgb(50, 59, 76)
                gridColor = Color.rgb(50, 59, 76)
            }
            candleStickChart.legend.isEnabled = false
        }
    }

    private fun setChartData(candles: ArrayList<ChartData>) {
        val priceEntry = arrayListOf<CandleEntry>()
        for (candle in candles) {
            priceEntry.add(
                CandleEntry(
                    candle.createdAt.toFloat(),
                    candle.shadowHigh,
                    candle.shadowLow,
                    candle.open,
                    candle.close
                )
            )
        }

        val priceDataSet = CandleDataSet(priceEntry, "").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            // 심지 부분 설정
            shadowColor = Color.GRAY
            shadowWidth = 0.9F
            // 음봉 설정
            decreasingColor = Color.RED
            decreasingPaintStyle = Paint.Style.FILL
            // 양봉 설정
            increasingColor = Color.BLUE
            increasingPaintStyle = Paint.Style.FILL

            neutralColor = Color.rgb(6, 18, 34)
            setDrawValues(false)
            // 터치시 노란 선 제거
            highLightColor = Color.TRANSPARENT
        }

        binding.candleStickChart.apply {
            this.data = CandleData(priceDataSet)
            invalidate()
        }
    }
}