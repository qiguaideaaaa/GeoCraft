package top.qiguaiaaaa.geocraft.api.util.config;

import java.util.Collection;

public interface CollectionEquivalentContain<T> extends Collection<T> {
    /**
     * 和 contains 类似，但是逻辑是是对于给定的元素o，Collection内是否存在一个元素e使得 (o==null ? e==null : e.equals(o)).
     */
    boolean containsEquivalent(Object o);
}
