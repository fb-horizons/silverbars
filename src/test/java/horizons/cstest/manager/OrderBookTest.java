package horizons.cstest.manager;

import horizons.cstest.model.AggregatedOrder;
import horizons.cstest.model.Order;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static horizons.cstest.model.OrderSide.BUY;
import static horizons.cstest.model.OrderSide.SELL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OrderBookTest {

    private Order sellOrder1, sellOrder2, sellOrder3, sellOrder4;
    private Order buyOrder1, buyOrder2, buyOrder3, buyOrder4;
    private AggregatedOrder expectedSellOrder1, expectedSellOrder2, expectedSellOrder3;
    private AggregatedOrder expectedBuyOrder1, expectedBuyOrder2, expectedBuyOrder3;

    @Before
    public void setup(){
        sellOrder1 = new Order(SELL, 306, 3.5, "user1");
        sellOrder2 = new Order(SELL, 310, 1.2, "user2");
        sellOrder3 = new Order(SELL, 307, 4.0, "user1");
        sellOrder4 = new Order(SELL, 306, 2.0, "user3");

        buyOrder1 = new Order(BUY, 305, 3.5, "user1");
        buyOrder2 = new Order(BUY, 307, 4.0, "user1");
        buyOrder3 = new Order(BUY, 305, 2.0, "user3");
        buyOrder4 = new Order(BUY, 304, 6.2, "user2");

        expectedSellOrder1 = new AggregatedOrder(SELL, 306, 5.5);
        expectedSellOrder2 = new AggregatedOrder(SELL, 307, 4.0);
        expectedSellOrder3 = new AggregatedOrder(SELL, 310, 1.2);

        expectedBuyOrder1 = new AggregatedOrder(BUY, 307, 4.0);
        expectedBuyOrder2 = new AggregatedOrder(BUY, 305, 5.5);
        expectedBuyOrder3 = new AggregatedOrder(BUY, 304, 6.2);
    }

    @Test
    public void shouldSuccessfullyAddOrder(){
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(sellOrder1);

        List<Order> orders = orderBook.getOrders();
        assertThat(orders.size(), is(1));
        Order order = orders.get(0);
        assertThat(order.getTrader(), is("user1"));
        assertThat(order.getPrice(), is(306L));
        assertThat(order.getQuantity(), is(3.5));
        assertThat(order.getSide(), is(SELL));
        assertThat(order.toString(), is("SELL: 3.5 kg for £306 [user1]"));

        List<AggregatedOrder> aggregatedOrders = orderBook.getAggregatedSellOrders();
        assertThat(aggregatedOrders.size(), is(1));
        assertThat(aggregatedOrders.get(0).toString(), is("3.5 kg for £306"));
    }

    @Test
    public void shouldSuccessfullyCancelOrderTest(){
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(sellOrder1);

        List<Order> orders = orderBook.getOrders();
        assertThat(orders.size(), is(1));
        Order order = orders.get(0);
        assertThat(order.toString(), is("SELL: 3.5 kg for £306 [user1]"));

        List<AggregatedOrder> aggregatedOrders = orderBook.getAllAggregatedOrders();
        assertThat(aggregatedOrders.size(), is(1));
        assertThat(aggregatedOrders.get(0).toString(), is("3.5 kg for £306"));

        orderBook.cancelOrder(order);
        aggregatedOrders = orderBook.getAllAggregatedOrders();
        assertThat(aggregatedOrders.size(), is(0));

    }

    // tests for sell orders

    @Test
    public void shouldSuccessfullyAggregateSellOrdersTest(){
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder3);
        orderBook.addOrder(sellOrder4);

        List<Order> orders = orderBook.getOrders();
        assertThat(orders.size(), is(3));

        List<AggregatedOrder> aggregatedSellOrders = orderBook.getAggregatedSellOrders();
        assertThat(aggregatedSellOrders.size(), is(2));

        // remove expected orders and assert on size
        aggregatedSellOrders.remove(expectedSellOrder1);
        aggregatedSellOrders.remove(expectedSellOrder2);
        assertThat(aggregatedSellOrders.size(), is(0));

        // there shouldn't be any buy order created
        List<AggregatedOrder> aggregatedBuyOrders = orderBook.getAggregatedBuyOrders();
        assertThat(aggregatedBuyOrders.size(), is(0));
    }

    @Test
    public void shouldSuccessfullyAggregateSellOrdersAndSortAscendingTest(){
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(sellOrder3);
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder4);

        List<Order> orders = orderBook.getOrders();
        assertThat(orders.size(), is(3));

        List<AggregatedOrder> aggregatedSellOrders = orderBook.getAggregatedSellOrders();
        assertThat(aggregatedSellOrders.size(), is(2));

        // assert if orders are aggregated and in ascending order
        assertThat(aggregatedSellOrders.get(0).toString(), is(expectedSellOrder1.toString()));
        assertThat(aggregatedSellOrders.get(1).toString(), is(expectedSellOrder2.toString()));

        // there shouldn't be any buy order created
        List<AggregatedOrder> aggregatedBuyOrders = orderBook.getAggregatedBuyOrders();
        assertThat(aggregatedBuyOrders.size(), is(0));
    }

    // tests for buy orders

    @Test
    public void shouldSuccessfullyAggregateBuyOrdersTest(){
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);

        List<Order> orders = orderBook.getOrders();
        assertThat(orders.size(), is(3));

        List<AggregatedOrder> aggregatedBuyOrders = orderBook.getAggregatedBuyOrders();
        assertThat(aggregatedBuyOrders.size(), is(2));

        // remove expected orders and assert on size
        aggregatedBuyOrders.remove(expectedBuyOrder1);
        aggregatedBuyOrders.remove(expectedBuyOrder2);
        assertThat(aggregatedBuyOrders.size(), is(0));

        // there shouldn't be any buy order created
        List<AggregatedOrder> aggregatedSellOrders = orderBook.getAggregatedSellOrders();
        assertThat(aggregatedSellOrders.size(), is(0));
    }

    @Test
    public void shouldSuccessfullyAggregateBuyOrdersAndSortDescendingTest(){
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);

        List<Order> orders = orderBook.getOrders();
        assertThat(orders.size(), is(3));

        List<AggregatedOrder> aggregatedBuyOrders = orderBook.getAggregatedBuyOrders();
        assertThat(aggregatedBuyOrders.size(), is(2));

        // assert if orders are aggregated and in ascending order
        assertThat(aggregatedBuyOrders.get(0).toString(), is(expectedBuyOrder1.toString()));
        assertThat(aggregatedBuyOrders.get(1).toString(), is(expectedBuyOrder2.toString()));

        // there shouldn't be any buy order created
        List<AggregatedOrder> aggregatedSellOrders = orderBook.getAggregatedSellOrders();
        assertThat(aggregatedSellOrders.size(), is(0));
    }

    @Test
    public void shouldSuccessfullyAddAndRemoveBuyAndSellOrdersTest(){
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);
        orderBook.addOrder(buyOrder4);
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);
        orderBook.addOrder(sellOrder4);

        assertThat(orderBook.getOrders().size(), is(8));
        assertThat(orderBook.getAggregatedSellOrders().size(), is(3));
        assertThat(orderBook.getAggregatedBuyOrders().size(), is(3));
        List<AggregatedOrder> aggregatedOrders = orderBook.getAllAggregatedOrders();
        assertThat(aggregatedOrders.size(), is(6));
        assertThat(aggregatedOrders.get(0).toString(), is(expectedBuyOrder1.toString()));
        assertThat(aggregatedOrders.get(1).toString(), is(expectedBuyOrder2.toString()));
        assertThat(aggregatedOrders.get(2).toString(), is(expectedBuyOrder3.toString()));
        assertThat(aggregatedOrders.get(3).toString(), is(expectedSellOrder1.toString()));
        assertThat(aggregatedOrders.get(4).toString(), is(expectedSellOrder2.toString()));
        assertThat(aggregatedOrders.get(5).toString(), is(expectedSellOrder3.toString()));

        orderBook.cancelOrder(buyOrder1);
        orderBook.cancelOrder(sellOrder2);
        aggregatedOrders = orderBook.getAllAggregatedOrders();
        assertThat(orderBook.getAggregatedSellOrders().size(), is(2));
        assertThat(orderBook.getAggregatedBuyOrders().size(), is(3));
        assertThat(aggregatedOrders.size(), is(5));
        assertThat(aggregatedOrders.get(1).toString(), is("2.0 kg for £305"));
    }
}
