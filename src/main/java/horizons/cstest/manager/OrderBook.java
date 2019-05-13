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
}


