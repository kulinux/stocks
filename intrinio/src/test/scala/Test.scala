import org.scalatest.FunSuite
import java.util.Properties

import com.stock.intrinio.sentiment.SentimentAnalyzer
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.util.CoreMap


class Test extends FunSuite {

  test("multi1 can use common sub-project") {
    val entity = Entity("id", NestedEntity("value"))
  }

  test("multi1 can use monocle dependency ") {
  }

  test("sentiment must work") {
    val input = "Scala is a the best programming language."
    val res = SentimentAnalyzer.mainSentiment(input)
    println(s"Res $input -> $res")
  }
}





