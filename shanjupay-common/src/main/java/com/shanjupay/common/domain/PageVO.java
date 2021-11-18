package com.shanjupay.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class PageVO<T> implements Iterator<T>, Serializable {
    private List<T> items = new ArrayList<>();
    private long counts;
    private int page;
    private int pageSize;

    public PageVO() {}

    public PageVO(List<T> items, long counts, int page, int pageSize) {
        this.items = items;
        this.counts = counts;
        this.page = page;
        this.pageSize = pageSize;
    }

    public boolean hasPrevious() {
        return getPage() >0;
    }

    public boolean hasNext() {
        return getPage() + 1 < getPages();
    }

    @Override
    public T next() {
        return null;
    }

    @Override
    public void remove() {
        Iterator.super.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        Iterator.super.forEachRemaining(action);
    }

    public boolean isFirst() {
        return !hasPrevious();
    }

    public boolean isLast() {
        return !hasNext();
    }

    public long getCounts() {
        return counts;
    }

    public void setCounts(long counts) {
        this.counts = counts;
    }

    public int getPages() {
        return getPageSize() == 0 ? 1 : (int) Math.ceil((double) counts / (double) getPageSize());
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public boolean hasItems() {
        return getItemsSize() > 0;
    }

    public int getItemsSize() {
        return items.size();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Iterator<T> iterator() {
        return getItems().iterator();
    }


}
