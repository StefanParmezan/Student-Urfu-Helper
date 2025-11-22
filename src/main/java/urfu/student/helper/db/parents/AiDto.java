package urfu.student.helper.db.parents;

import java.util.Collection;

public interface AiDto {
    default String collectionToString(Collection<?> collection) {
        StringBuilder b = new StringBuilder();
        for (Object o : collection) {
            b.append(o.toString()).append(", ");
        }
        return b.toString();
    }
}
