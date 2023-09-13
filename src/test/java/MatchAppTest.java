import com.fsbtech.interviews.client.Client;
import com.fsbtech.interviews.client.impl.ClientImpl;
import com.fsbtech.interviews.entities.Category;
import com.fsbtech.interviews.entities.Event;
import com.fsbtech.interviews.entities.MarketRefType;
import com.fsbtech.interviews.entities.SubCategory;
import com.fsbtech.interviews.repository.EventRepository;
import com.fsbtech.interviews.repository.impl.EventRepositoryImpl;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class MatchAppTest {

    private final EventRepository eventRepository = new EventRepositoryImpl();

    private final Client client = new ClientImpl(eventRepository);

    @Test
    public void A_positiveSaveEvent() throws NoSuchFieldException, IllegalAccessException {
        final Event event = new Event(1, "Manchester United v Arsenal", new SubCategory(1, "Football", new Category(1, "Premier League")), new ArrayList<>(), false);
        client.addEvent(event);
        Map<Integer, Event> refMap = reflection();
        assertThat(refMap.size()).isEqualTo(1);
    }

    @Test
    public void B_positiveChangeEventStatus() throws NoSuchFieldException, IllegalAccessException {
        final Event event = new Event(1, "Manchester United v Arsenal", new SubCategory(1, "Football", new Category(1, "Premier League")), new ArrayList<>(), false);
        client.addEvent(event);
        client.eventCompleted(1);
        Map<Integer, Event> refMap = reflection();
        assertThat(refMap.size()).isEqualTo(1);
        assertThat(refMap.get(1).getCompleted()).isTrue();
    }

    @Test
    public void C_negativeChangeEventStatusMissingId() throws NoSuchFieldException, IllegalAccessException {
        client.eventCompleted(1);
        Map<Integer, Event> refMap = reflection();
        assertThat(refMap.size()).isEqualTo(0);
    }

    @Test
    public void D_positiveAttachMarketRefTypeToEvent() throws NoSuchFieldException, IllegalAccessException {
        final Event event = new Event(1, "Manchester United v Arsenal", new SubCategory(1, "Football", new Category(1, "Premier League")), new ArrayList<>(), false);
        client.addEvent(event);
        client.attachMarketRefTypeToEvent(1, new MarketRefType(1, "Home/Draw/Away"));
        final Map<Integer, Event> refMap = reflection();
        assertThat(refMap.size()).isEqualTo(1);
        assertThat(refMap.get(1).getCompleted()).isFalse();
        assertThat(refMap.get(1).getMarketRefTypes().size()).isEqualTo(1);
    }

    @Test
    public void E_NegativeAttachMarketRefTypeToEventMissingId() throws NoSuchFieldException, IllegalAccessException {
        final Event event = new Event(1, "Manchester United v Arsenal", new SubCategory(1, "Football", new Category(1, "Premier League")), new ArrayList<>(), false);
        client.addEvent(event);
        client.attachMarketRefTypeToEvent(2, new MarketRefType(1, "Home/Draw/Away"));
        final Map<Integer, Event> refMap = reflection();
        assertThat(refMap.size()).isEqualTo(1);
        assertThat(refMap.get(1).getMarketRefTypes().size()).isEqualTo(0);
    }

    @Test
    public void F_positiveAttachMarketRefTypeToEvent() throws NoSuchFieldException, IllegalAccessException {
        final Event event = new Event(1, "Manchester United v Arsenal", new SubCategory(1, "Football", new Category(1, "Premier League")), new ArrayList<>(), false);
        client.addEvent(event);
        client.removeMarketRefTypeFromEvent(1, new MarketRefType(1, "Home/Draw/Away"));
        final Map<Integer, Event> refMap = reflection();
        assertThat(refMap.size()).isEqualTo(1);
        assertThat(refMap.get(1).getCompleted()).isFalse();
        assertThat(refMap.get(1).getMarketRefTypes().size()).isEqualTo(0);
    }

    @Test
    public void G_positiveAttachMarketRefTypeToEventMissingId() throws NoSuchFieldException, IllegalAccessException {
        final Event event = new Event(1, "Manchester United v Arsenal", new SubCategory(1, "Football", new Category(1, "Premier League")), new ArrayList<>(), false);
        client.addEvent(event);
        client.removeMarketRefTypeFromEvent(2, new MarketRefType(1, "Home/Draw/Away"));
        final Map<Integer, Event> refMap = reflection();
        assertThat(refMap.size()).isEqualTo(1);
        assertThat(refMap.get(1).getCompleted()).isFalse();
        assertThat(refMap.get(1).getMarketRefTypes().size()).isEqualTo(0);
    }


    private Map<Integer, Event> reflection() throws NoSuchFieldException, IllegalAccessException {
        final Field field = eventRepository.getClass().getDeclaredField("events");
        field.setAccessible(true);
        final Map<Integer, Event> refMap = (ConcurrentHashMap<Integer, Event>) field.get(eventRepository);
        return refMap;
    }

    @Test
    public void H_positiveFindAllEvents() throws NoSuchFieldException, IllegalAccessException {
        initData();
        final Map<Integer, Event> refMap = reflection();
        assertThat(refMap.size()).isEqualTo(2);
        final Collection<String> events = client.futureEventNamesCollection("Premier", "Football", "Home/Draw/Away");
        assertThat(events.size()).isEqualTo(1);
    }

    @Test
    public void I_positiveFindAllEventsWithNullValues() throws NoSuchFieldException, IllegalAccessException {
        initData();
        final Map<Integer, Event> refMap = reflection();
        assertThat(refMap.size()).isEqualTo(2);
        final Collection<String> events = client.futureEventNamesCollection(null, null, null);
        assertThat(events.size()).isEqualTo(2);
    }

    @Test
    public void I_positiveDump() throws NoSuchFieldException, IllegalAccessException {
        initData();
        final Map<Integer, Event> refMap = reflection();
        assertThat(refMap.size()).isEqualTo(2);
        final String dump = client.dumpFullStructure();
        System.out.println(dump);
    }

    private void initData() {
        final MarketRefType marketRefType1 = new MarketRefType(1, "Home/Draw/Away");
        final MarketRefType marketRefType2 = new MarketRefType(1, "Home/Away");
        final List<MarketRefType> marketRefTypeList1 = Arrays.asList(marketRefType1);
        final List<MarketRefType> marketRefTypeList2 = Arrays.asList(marketRefType2);
        final Event event1 = new Event(1, "Manchester United v Arsenal",
                new SubCategory(1, "Football",
                        new Category(1, "Premier League")), marketRefTypeList1, false);
        final Event event2 = new Event(2, "Andy Murray v Novak Djokovic",
                new SubCategory(2, "Tennis",
                        new Category(2, "French Open")), marketRefTypeList2, false);
        client.addEvent(event1);
        client.addEvent(event2);
    }

}
