package com.fractaldev.literaku

object Numbers {
    fun getAllNumbersText(): MutableList<List<String>> {
        var numbersText: MutableList<List<String>> = mutableListOf()
        numbersText.add(one)
        numbersText.add(two)
        numbersText.add(three)
        numbersText.add(four)
        numbersText.add(five)
        numbersText.add(six)
        numbersText.add(seven)
        numbersText.add(eight)
        numbersText.add(nine)
        numbersText.add(ten)
        numbersText.add(eleven)
        // ...dst

        return numbersText
    }

    internal var one = listOf<String>(
        "1",
        "satu",
        "kesatu",
        "ke satu",
        "pertama"
    )
    internal var two = listOf<String>(
        "2",
        "dua",
        "kedua",
        "ke dua"
    )
    internal var three = listOf<String>(
        "3",
        "tiga",
        "ketiga",
        "ke tiga"
    )
    internal var four = listOf<String>(
        "4",
        "empat",
        "keempat",
        "ke empat"
    )
    internal var five = listOf<String>(
        "5",
        "lima",
        "kelima",
        "ke lima"
    )
    internal var six = listOf<String>(
        "6",
        "enam",
        "keenam",
        "ke enam"
    )
    internal var seven = listOf<String>(
        "7",
        "tujuh",
        "ketujuh",
        "ke tujuh"
    )
    internal var eight = listOf<String>(
        "8",
        "delapan",
        "kedelapan",
        "ke delapan"
    )
    internal var nine = listOf<String>(
        "9",
        "sembilan",
        "kesembilan",
        "ke sembilan"
    )
    internal var ten = listOf<String>(
        "10",
        "sepuluh",
        "kesepuluh",
        "ke sepuluh"
    )

    internal var eleven = listOf<String>(
        "11",
        "sebelas",
        "kesebelas",
        "ke sebelas"
    )
    // ...dst
}