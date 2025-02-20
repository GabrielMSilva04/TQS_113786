package deti.tqs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StocksPortfolio {
    private IStockMarketService stockMarketService;
    private List<Stock> stocks;

    public StocksPortfolio(IStockMarketService stockMarketService) {
        this.stockMarketService = stockMarketService;
        this.stocks = new ArrayList<>();
    }

    public double totalValue() {
        double total = 0;
        for (Stock stock : stocks) {
            total += stock.getQuantity() * stockMarketService.lookUpPrice(stock.getLabel());
        }
        return total;
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    /**
     * @param topN the number of most valuable stocks to return
     * @return a list with the topN most valuable stocks in the portfolio
     */
    public List<Stock> mostValuableStocks(int topN) {
        return stocks.stream()
            .sorted((s1, s2) -> s2.getQuantity() - s1.getQuantity())
            .limit(topN)
            .collect(Collectors.toList());
    }
}
