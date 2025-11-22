package urfu.student.helper.db.parents;

import java.util.Collection;

public class CollectionToAiStringAdapter {
    public CollectionToAiStringAdapter(Collection<?> collection) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object o : collection) {
            stringBuilder.append(o.toString()).append(", ");
        }
        aiCollectionString = stringBuilder.toString();
    }

    String aiCollectionString;

    @Override
    public String toString() {
        return aiCollectionString;
    }
}
