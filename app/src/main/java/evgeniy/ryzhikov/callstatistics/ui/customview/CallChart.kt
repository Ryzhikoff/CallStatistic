package evgeniy.ryzhikov.callstatistics.ui.customview

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import evgeniy.ryzhikov.callstatistics.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CallChart @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    private var contactName: String = ""
    private var phoneNumber: String = ""
    private var countCalls: Int = 0

    private var sizeTextContactName = 20f
    private var sizeTextPhoneNumber = 20f

    private var spaceBetweenText = 10f
    private var spaceBetweenTextAndChart = 10f
    private var spaceAroundChart = 10f

    private var roundX = 20f
    private var roundY = 20f
    private var colorChart = Color.BLUE
    private var colorPhoneNumber = Color.BLUE
    private var colorContactName = Color.BLUE
    private var colorCountCalls = Color.YELLOW

    private lateinit var phoneNumberPaint: Paint
    private lateinit var contactNamePaint: Paint
    private lateinit var countCallsPaint: Paint
    private lateinit var chartPaint: Paint

    private var delayAppearance = 500L

    private var resolvedWidth = 0
    private var resolvedHeight = 0

    private val items = mutableListOf<ChartItem>()
    private val drawItems = mutableListOf<ChartItemDraw>()
    private var offSetYForItem = 0f

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        val attributes =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.CallChart, 0, 0)
        try {
            contactName = attributes.getString(R.styleable.CallChart_contactName).toString()
            phoneNumber = attributes.getString(R.styleable.CallChart_phoneNumber).toString()
            countCalls = attributes.getInt(R.styleable.CallChart_countCalls, countCalls)

            sizeTextContactName = attributes.getFloat( R.styleable.CallChart_sizeTextContactName, sizeTextContactName ).toPx
            sizeTextPhoneNumber = attributes.getFloat( R.styleable.CallChart_sizeTextPhoneNumber, sizeTextPhoneNumber ).toPx

            roundX = attributes.getFloat(R.styleable.CallChart_roundX, roundX).toPx
            roundY = attributes.getFloat(R.styleable.CallChart_roundY, roundY).toPx

            colorChart = attributes.getColor(R.styleable.CallChart_colorChart, colorChart)
            colorPhoneNumber = attributes.getColor(R.styleable.CallChart_colorPhoneNumber, colorPhoneNumber)
            colorContactName = attributes.getColor(R.styleable.CallChart_colorContactName, colorContactName)
            colorCountCalls = attributes.getColor(R.styleable.CallChart_colorCountCalls, colorCountCalls)

            spaceBetweenText = attributes.getFloat(R.styleable.CallChart_spaceBetweenText, spaceBetweenText).toPx
            spaceBetweenTextAndChart = attributes.getFloat( R.styleable.CallChart_spaceBetweenTextAndChart, spaceBetweenTextAndChart).toPx
            spaceAroundChart = attributes.getFloat(R.styleable.CallChart_spaceAroundChart, spaceAroundChart).toPx

            delayAppearance = attributes.getInt(R.styleable.CallChart_delayAppearance, delayAppearance.toInt()).toLong()
        } finally {
            attributes.recycle()
        }
        initPaint()
    }

    private fun initPaint() {
        chartPaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL
            strokeWidth = 20f
        }

        contactNamePaint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2f
            setShadowLayer(5f, 0f, 0f, Color.DKGRAY)
            textSize = sizeTextContactName
            typeface = Typeface.SANS_SERIF
            color = colorContactName
            isAntiAlias = true
        }

        phoneNumberPaint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2f
            setShadowLayer(5f, 0f, 0f, Color.DKGRAY)
            textSize = sizeTextPhoneNumber
            typeface = Typeface.SANS_SERIF
            color = colorPhoneNumber
            isAntiAlias = true
        }

        countCallsPaint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2f
            setShadowLayer(5f, 0f, 0f, Color.DKGRAY)
            textSize = sizeTextContactName
            typeface = Typeface.SANS_SERIF
            color = colorContactName
            isAntiAlias = true
        }

    }

    fun addItem(item: ChartItem) {
        items.add(item)
    }

    fun create() {

        items.sortByDescending {item ->
            item.counterCall
        }

        val maxCountCall = items[0].counterCall

        scope.launch {
            repeat(items.size) { index ->

                val widths = FloatArray(phoneNumber.length)
                phoneNumberPaint.getTextWidths(phoneNumber, widths)
                var xOffsetChart = 0f
                for (width in widths) {
                    xOffsetChart += width
                }

                val chartLeft = xOffsetChart + spaceBetweenTextAndChart + spaceAroundChart
                val chartTop = offSetYForItem + spaceAroundChart
                val chartRight = resolvedWidth.toFloat() - spaceAroundChart
                val chartBottom = offSetYForItem + sizeTextContactName * 2 + spaceBetweenText * 2 + spaceBetweenText - spaceAroundChart

                val sizeChart = resolvedWidth - (resolvedWidth - chartRight) - chartLeft

                drawItems.add(
                    ChartItemDraw(
                        items[index].contactName,
                        items[index].phoneNumber,
                        items[index].counterCall,
                        xContactName = spaceBetweenText,
                        yContactName = offSetYForItem + sizeTextContactName + spaceBetweenText,
                        xPhoneNumber = spaceBetweenText,
                        yPhoneNumber = offSetYForItem + sizeTextContactName + sizeTextPhoneNumber + spaceBetweenText * 2,
                        chartLeft = chartLeft,
                        chartTop = chartTop,
                        chartRight = chartLeft + (sizeChart / 100 * (items[index].counterCall / maxCountCall.toFloat() * 100)),
                        chartBottom = chartBottom

                    )
                )
                offSetYForItem += sizeTextContactName + sizeTextPhoneNumber + spaceBetweenText * 3
                invalidate()
                delay(delayAppearance)
            }
        }
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Считаем полный размер с паддингами
        val heightSize = MeasureSpec.getSize(heightMeasureSpec) + paddingBottom + paddingTop
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) + paddingLeft + paddingRight
        //Получаем конечные размеры View, с учетом режима
        resolvedWidth = resolveSize(widthSize, widthMeasureSpec)
        resolvedHeight = resolveSize(heightSize, heightMeasureSpec)
        //Устанавливаем итоговые размеры
        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawItems.forEach { item ->
            drawText(canvas, item)
            drawChart(canvas, item)
        }

    }

    private fun drawText(canvas: Canvas, item: ChartItemDraw) {
        canvas.drawText(
            item.contactName,
            item.xContactName,
            item.yContactName,
            contactNamePaint
        )
        canvas.drawText(
            item.phoneNumber,
            item.xPhoneNumber,
            item.yPhoneNumber,
            phoneNumberPaint
        )
    }

    private fun drawChart(canvas: Canvas, item: ChartItemDraw) {
        canvas.drawRoundRect(
            item.chartLeft,
            item.chartTop,
            item.chartRight,
            item.chartBottom,
            roundX,
            roundY,
            chartPaint
        )
    }

    //sp to px
    private val Float.toPx get() = this * Resources.getSystem().displayMetrics.density

    private data class ChartItemDraw(
        val contactName: String,
        val phoneNumber: String,
        val counterCall: Int,
        val xContactName: Float,
        val yContactName: Float,
        val xPhoneNumber: Float,
        val yPhoneNumber: Float,
        val chartLeft: Float,
        val chartTop: Float,
        val chartRight: Float,
        val chartBottom: Float
    )
}

data class ChartItem(val contactName: String, val phoneNumber: String, val counterCall: Int)
