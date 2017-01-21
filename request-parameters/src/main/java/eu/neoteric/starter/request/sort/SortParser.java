package eu.neoteric.starter.request.sort;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class SortParser {
    private static final Logger LOG = LoggerFactory.getLogger(SortParser.class);

    private SortParser() {
        // Prevents instantiation of the class.
    }

    public static List<RequestSort> parseSort(Map<String, Object> rawSort) {
        List<RequestSort> unorderedSort = findSortFields(rawSort);
        List<RequestSort> orderedSort = applyOrder(unorderedSort, rawSort);

        LOG.info("Parsed sorts: {}", orderedSort);
        return orderedSort;
    }

    private static List<RequestSort> findSortFields(Map<String, Object> rawSort) {
        List<RequestSort> sort = Lists.newArrayList();
        rawSort.forEach((key, entry) -> {
            if (isNotFieldNorSortOperator(key)) {
                throw new IllegalStateException(key + " isNotFieldNorSortOperator");
            }
            if (isField(key)) {
                if (!(entry instanceof String)) {
                    throw new IllegalStateException("Bad value (" + entry + ") type for field: " + entry.getClass().toString());
                }
                sort.add(RequestSort.of(key, (String) entry));
            }
        });
        return sort;
    }

    private static List<RequestSort> applyOrder(List<RequestSort> unorderedSort, Map<String, Object> rawSort) {
        if (unorderedSort.size() <= 1) {
            return unorderedSort;
        }
        List<String> orderEntry = (List<String>) rawSort.get(SortOperator.ORDER.getValue());
        if (orderEntry == null) {
            throw new IllegalStateException("SortOperator: " + SortOperator.ORDER + " not specified for multiple sort");

        } else if (unorderedSort.size() != orderEntry.size() || unorderedSort.size() != Sets.newHashSet(unorderedSort).size()) {
            throw new IllegalArgumentException("Order operator has incorrect number of values");
        }
        List<RequestSort> orderedSort = Lists.newArrayList();
        orderEntry.forEach((key) -> {
            RequestSort requestSort = unorderedSort.stream()
                    .filter(entry -> entry.getFieldName().equals(key))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Sort type: " + key + " not specified"));
            orderedSort.add(requestSort);
        });
        return orderedSort;
    }


    private static boolean isField(String key) {
        return !key.startsWith("$");
    }

    private static boolean isNotFieldNorSortOperator(String key) {
        return !(isField(key) || SortOperator.contains(key));
    }
}
