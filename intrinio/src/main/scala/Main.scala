import akka.stream.scaladsl.Source
import com.stock.intrinio.model.New

object Main extends App {

  //Choose a company
  //Load news from https://api-v2.intrinio.com/companies/AAPL/news?api_key=OmEzNjA4OTdjM2M2ZGE5NGQyZGNhNDg0ODlkNWY1YmJj
  val news: Source[New, _] = ???


  //Load stock values from https://api-v2.intrinio.com/companies/AAPL/historical_data/marketcap
  //Store both info in cassandra
  //Combine together
  //Perform some Big Data calculation

}
