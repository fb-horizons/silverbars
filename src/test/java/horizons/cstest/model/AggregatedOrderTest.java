package horizons.cstest.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static horizons.cstest.model.OrderSide.SELL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AggregatedOrderTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void orderIsSuccessfullyCreatedTest(){
        AggregatedOrder order = new AggregatedOrder(SELL, 306, 5.5);
        assertThat(order.getQuantity(), is(5.5));
        assertThat(order.getPrice(), is(306L));
        assertThat(order.getSide(), is(SELL));
        assertThat(order.toString(), is("5.5 kg for £306"));
    }

    @Test
    public void orderConstructorThrowsExceptionForNullOrderSideTest(){
        exception.expect(IllegalArgumentException.class);
        AggregatedOrder order = new AggregatedOrder(null, 306, 3.5);
    }

    @Test
    public void orderConstructorThrowsExceptionForIllegalPriceTest(){
        exception.expect(IllegalArgumentException.class);
        AggregatedOrder order = new AggregatedOrder(SELL, -2, 3.5);
    }

    @Test
    public void orderConstructorThrowsExceptionForIllegalQuantityTest(){
        exception.expect(IllegalArgumentException.class);
        AggregatedOrder order = new AggregatedOrder(SELL, 306, 0);
    }
}
