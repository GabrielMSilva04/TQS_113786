package deti.tqs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(MockitoExtension.class)
public class StockPortfolioTest 
{
    @Mock
    private IStockMarketService stockMarketService;

    @InjectMocks
    private StocksPortfolio stocksPortfolio;
    
    @Test
    public void testTotalValue() {
        Stock stock1 = new Stock("Stock1", 10);
        Stock stock2 = new Stock("Stock2", 20);
        Stock stock3 = new Stock("Stock3", 30);

        stocksPortfolio.addStock(stock1);
        stocksPortfolio.addStock(stock2);
        stocksPortfolio.addStock(stock3);

        when(stockMarketService.lookUpPrice("Stock1")).thenReturn(100.0);
        when(stockMarketService.lookUpPrice("Stock2")).thenReturn(200.0);
        when(stockMarketService.lookUpPrice("Stock3")).thenReturn(300.0);

        double totalValue = stock1.getQuantity() * stockMarketService.lookUpPrice("Stock1") +
                            stock2.getQuantity() * stockMarketService.lookUpPrice("Stock2") +
                            stock3.getQuantity() * stockMarketService.lookUpPrice("Stock3");

        assertEquals(totalValue, stocksPortfolio.totalValue(), 0.001);
    }

    @Test
    public void testTotalValueWithNoStocks() {
        assertEquals(0, stocksPortfolio.totalValue(), 0.001);
    }

    @Test
    public void testMostValuableStocks() {
        Stock stock1 = new Stock("Stock1", 10);
        Stock stock2 = new Stock("Stock2", 20);
        Stock stock3 = new Stock("Stock3", 30);
        Stock stock4 = new Stock("Stock4", 40);
        
        stocksPortfolio.addStock(stock1);
        stocksPortfolio.addStock(stock2);
        stocksPortfolio.addStock(stock3);
        stocksPortfolio.addStock(stock4);

        List<Stock> top3Stocks = stocksPortfolio.mostValuableStocks(3);
        assertEquals(3, top3Stocks.size());
        assertEquals(stock4, top3Stocks.get(0));
        assertEquals(stock3, top3Stocks.get(1));
        assertEquals(stock2, top3Stocks.get(2));
    }
}
