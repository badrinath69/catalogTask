import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import java.math.BigInteger

fun main() {
    val testCases = listOf("src/json/testcase1.json", "src/json/testcase2.json")

    for (testCase in testCases) {
        val jsonContent = File(testCase).readText()
        val gson = Gson()
        val jsonObject = gson.fromJson(jsonContent, JsonObject::class.java)

        val keys = jsonObject.getAsJsonObject("keys")
        val n = keys.get("n").asInt
        val k = keys.get("k").asInt

        val points = mutableListOf<Pair<BigInteger, BigInteger>>()

        for (entry in jsonObject.entrySet()) {
            if (entry.key != "keys") {
                val x = entry.key.toBigInteger()
                val base = entry.value.asJsonObject.get("base").asInt
                val value = entry.value.asJsonObject.get("value").asString
                val y = BigInteger(value, base)
                points.add(Pair(x, y))
            }
        }

        val secret = lagrangeInterpolation(points.take(k), BigInteger.ZERO)
        println("Secret for $testCase: $secret")
    }
}

fun lagrangeInterpolation(points: List<Pair<BigInteger, BigInteger>>, x: BigInteger): BigInteger {
    var result = BigInteger.ZERO

    for (i in points.indices) {
        var term = points[i].second

        for (j in points.indices) {
            if (i != j) {
                val xi = points[i].first
                val xj = points[j].first
                term = term.multiply(x.subtract(xj)).divide(xi.subtract(xj))
            }
        }

        result = result.add(term)
    }

    return result
}
