package brooklyn.location.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import brooklyn.location.PortRange;
import brooklyn.util.flags.TypeCoercions;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class PortRanges {

    public static final int MAX_PORT = 65535;
    public static final PortRange ANY_HIGH_PORT = new LinearPortRange(1024, MAX_PORT);
    
    private static class SinglePort implements PortRange {
        int port;
        public SinglePort(int port) { this.port = port; }
        
        @Override
        public Iterator<Integer> iterator() {
            return Collections.singletonList(port).iterator();
        }
    }

    @Deprecated
    public static class BasicPortRange extends LinearPortRange {
        public static final int MAX_PORT = PortRanges.MAX_PORT;
        public static final PortRange ANY_HIGH_PORT = PortRanges.ANY_HIGH_PORT;
        public BasicPortRange(int start, int end) { super(start, end); }
    }
    
    private static class LinearPortRange implements PortRange {
        int start, end, delta;
        public LinearPortRange(int start, int end, int delta) {
            this.start = start;
            this.end = end;
            this.delta = delta;
            assert delta!=0;
        }
        public LinearPortRange(int start, int end) {
            this(start, end, (start<=end?1:-1));
        }
        
        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<Integer>() {
                int next = start;
                boolean hasNext = true;
                
                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public Integer next() {
                    if (!hasNext)
                        throw new NoSuchElementException("Exhausted available ports");
                    int result = next;
                    next += delta;
                    if ((delta>0 && next>end) || (delta<0 && next<end)) hasNext = false;
                    return result;
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
    
    private static class AggregatePortRange implements PortRange {
        List<PortRange> ranges;
        public AggregatePortRange(List<PortRange> ranges) {
            this.ranges = Collections.unmodifiableList(new ArrayList(ranges));
        }
        @Override
        public Iterator<Integer> iterator() {
            return Iterables.concat(ranges).iterator();
        }
    }

    public static PortRange fromInteger(int x) {
        return new SinglePort(x);
    }
    
    public static PortRange fromCollection(Collection c) {
        List<PortRange> l = new ArrayList<PortRange>();
        for (Object o: c) {
            if (o instanceof Integer) l.add(fromInteger((Integer)o));
            else if (o instanceof String) l.add(fromString((String)o));
            else if (o instanceof Collection) l.add(fromCollection((Collection)o));
            else l.add(TypeCoercions.coerce(o, PortRange.class));
        }
        return new AggregatePortRange(l);
    }

    /** parses a string representation of ports, as "80,8080,8000,8080-8099" */
    public static PortRange fromString(String s) {
        List<PortRange> l = new ArrayList<PortRange>();
        for (String si: s.split(",")) {
            si = si.trim();
            int start, end;
            if (si.endsWith("+")) {
                String si2 = si.substring(0, si.length()-1).trim();
                start = Integer.parseInt(si2);
                end = MAX_PORT;
            } else if (si.indexOf('-')>0) {
                int v = si.indexOf('-');
                start = Integer.parseInt(si.substring(0, v).trim());
                end = Integer.parseInt(si.substring(v+1).trim());
            } else {
                //number on its own
                l.add(new SinglePort(Integer.parseInt(si)));
                continue;
            }
            l.add(new LinearPortRange(start, end));
        }
        return new AggregatePortRange(l);
    }

    //TODO string
    
    private static AtomicBoolean done = new AtomicBoolean(false);
    
    /** performs the language extensions required for this project */
    public static void init() {
        if (done.getAndSet(true)) return;
        TypeCoercions.registerAdapter(Integer.class, PortRange.class, new Function<Integer,PortRange>() {
            public PortRange apply(Integer x) { return fromInteger(x); }
        });
        TypeCoercions.registerAdapter(String.class, PortRange.class, new Function<String,PortRange>() {
            public PortRange apply(String x) { return fromString(x); }
        });
        TypeCoercions.registerAdapter(Collection.class, PortRange.class, new Function<Collection,PortRange>() {
            public PortRange apply(Collection x) { return fromCollection(x); }
        });
    }
    
    static {
        init();
    }
    
}
