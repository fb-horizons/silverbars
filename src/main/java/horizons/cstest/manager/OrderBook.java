package horizons.cstest.manager;

import horizons.cstest.model.AggregatedOrder;
import horizons.cstest.model.Order;
import horizons.cstest.model.OrderSide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;

import static horizons.cstest.model.OrderSide.BUY;
import static horizons.cstest.model.OrderSide.SELL;

public class OrderBook {
    private final List<Order> orders = new ArrayList<>();
    /*
     Based on assumption there is more frequent request for a view of order book than order book manipulation(add/remove),
     updated maps of sell and buy aggregated orders are maintained.
     Otherwise it would have been better to maintain just the list of plain orders and then compute aggregated orders on
     the fly using streams when requested.
     Added/Removed could also be used as callbacks on a Listenable orders list to update aggregated maps.

     Could have used treemap for maps with suitable comparator to have them always sorted.
     * */
    private final Map<Long, AggregatedOrder> aggregatedBuyOrders = new HashMap<>();
    private final Map<Long, AggregatedOrder> aggregatedSellOrders = new HashMap<>();


    public void addOrder(Order order){
        orders.add(order);
        added(order);
    }

    public void cancelOrder(Order order){
        orders.remove(order);
        removed(order);
    }

    public List<AggregatedOrder> getAggregatedBuyOrders(){ // descending order
        List<AggregatedOrder> aggregatedOrders = new ArrayList<>(aggregatedBuyOrders.values());
        aggregatedOrders.sort(Comparator.comparing(AggregatedOrder::getPrice).reversed());
        return aggregatedOrders;
    }

    public List<AggregatedOrder> getAggregatedSellOrders(){ //
        List<AggregatedOrder> aggregatedOrders = new ArrayList<>(aggregatedSellOrders.values());
        aggregatedOrders.sort(Comparator.comparing(AggregatedOrder::getPrice));
        return aggregatedOrders;
    }

    public List<AggregatedOrder> getAllAggregatedOrders(){ //
        List<AggregatedOrder> aggregatedOrders = getAggregatedBuyOrders();
        aggregatedOrders.addAll(getAggregatedSellOrders());
        return aggregatedOrders;
    }

    public List<Order> getOrders(){
        return Collections.unmodifiableList(orders);
    }

    // this can be implemented for a callback when using listenable list
    private void added(Order order) {
        AggregatedOrder aggregatedOrder = null;
        Map<Long, AggregatedOrder> aggregatedOrders = null;
        if(OrderSide.BUY == order.getSide()) {
            aggregatedOrder = aggregatedBuyOrders.get(order.getPrice());
            aggregatedOrders = aggregatedBuyOrders;
        }
        else {
            aggregatedOrder = aggregatedSellOrders.get(order.getPrice());
            aggregatedOrders = aggregatedSellOrders;
        }
        // we shall not have an entry where we add null value against a price
        if(null == aggregatedOrder) {
            aggregatedOrders.put(order.getPrice(), new AggregatedOrder(order.getSide(), order.getPrice(), order.getQuantity()));
        }
        else{
            aggregatedOrders.put(order.getPrice(), new AggregatedOrder(order.getSide(),
                                                        order.getPrice(),
                                                aggregatedOrder.getQuantity() + order.getQuantity()));
        }
    }

    // this can be implemented for a callback when using listenable list
    private void removed(Order order) {
        AggregatedOrder aggregatedOrder = null;
        Map<Long, AggregatedOrder> aggregatedOrders = null;
        if(OrderSide.BUY == order.getSide()) {
            aggregatedOrder = aggregatedBuyOrders.get(order.getPrice());
            aggregatedOrders = aggregatedBuyOrders;
        }
        else {
            aggregatedOrder = aggregatedSellOrders.get(order.getPrice());
            aggregatedOrders = aggregatedSellOrders;
        }
        // we shall not have an entry where we add null value against a price
        if(null == aggregatedOrder) {
            // do nothing, theoretically this won't happen
        }
        else{
            if(aggregatedOrder.getQuantity() == order.getQuantity()){
                // remove since this was the only remaining order
                aggregatedOrders.remove(aggregatedOrder.getPrice());
            }
            else {
                aggregatedOrders.put(order.getPrice(), new AggregatedOrder(order.getSide(),
                                                            order.getPrice(),
                                                    aggregatedOrder.getQuantity() - order.getQuantity()));
            }
        }
    }

    /* following is for demo purposes only */
    public static void main(String[] args){
        Order sellOrder1, sellOrder2, sellOrder3, sellOrder4;
        Order buyOrder1, buyOrder2, buyOrder3, buyOrder4;

        sellOrder1 = new Order(SELL, 306, 3.5, "user1");
        sellOrder2 = new Order(SELL, 310, 1.2, "user2");
        sellOrder3 = new Order(SELL, 307, 4.0, "user1");
        sellOrder4 = new Order(SELL, 306, 1.5, "user3");

        buyOrder1 = new Order(BUY, 305, 3.5, "user1");
        buyOrder2 = new Order(BUY, 307, 4.0, "user1");
        buyOrder3 = new Order(BUY, 305, 2.5, "user3");
        buyOrder4 = new Order(BUY, 304, 6.2, "user2");

        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(sellOrder1);
        System.out.println("Added " + sellOrder1);
        orderBook.addOrder(sellOrder2);
        System.out.println("Added " + sellOrder2);
        orderBook.addOrder(sellOrder3);
        System.out.println("Added " + sellOrder3);
        orderBook.addOrder(buyOrder1);
        System.out.println("Added " + buyOrder1);
        orderBook.addOrder(buyOrder2);
        System.out.println("Added " + buyOrder2);
        orderBook.addOrder(buyOrder4);
        System.out.println("Added " + buyOrder4);
        printOrderBookStatus(orderBook);

        orderBook.addOrder(sellOrder4);
        System.out.println("Added " + sellOrder4);
        orderBook.addOrder(buyOrder3);
        System.out.println("Added " + buyOrder3);
        printOrderBookStatus(orderBook);

        orderBook.cancelOrder(sellOrder1);
        System.out.println("Removed " + sellOrder1);
        orderBook.cancelOrder(buyOrder1);
        System.out.println("Removed " + buyOrder1);
        printOrderBookStatus(orderBook);

    }

    private static void printOrderBookStatus(OrderBook orderBook){
        System.out.println("\nLive Order Board");
        System.out.println("BUY ORDERS");
        orderBook.getAggregatedBuyOrders().stream().forEach(System.out::println);
        System.out.println("SELL ORDERS");
        orderBook.getAggregatedSellOrders().stream().forEach(System.out::println);
        System.out.println("END\n");
    }
}


