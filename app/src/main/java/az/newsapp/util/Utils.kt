package az.newsapp.util

object Utils {
    fun getTimeFromTimeStamp(timeStamp: String?): List<String?>? {
        val time = timeStamp?.substringAfter('T')
            ?.substringBeforeLast(':')
        val date = timeStamp?.substringBefore('T')
        return listOf(time,date)
    }


    private var colorIndex = -1
    fun pickColor(): Int {
        val colorsArray = listOf(
            android.R.color.holo_orange_dark,
            android.R.color.holo_green_light,
            android.R.color.holo_red_light
        )
        if (colorIndex < colorsArray.size - 1) colorIndex++
        else colorIndex = 0
        return colorsArray[1]
    }

}