package com.mizuho.matsuri.pricestore.service;

import com.mizuho.matsuri.pricestore.service.impl.InstrumentDataCache.IndexType;

import java.util.Collection;

/**
 * Indexed data access object
 */
public interface IDataCache<T> {
    /**
     * Add a piece of data to the store.
     * @param item item to store
     */
    void indexData(T item);

    /**
     * Rebuild the index with the passed data
     *
     * @param data data to index
     */
    void rebuildIndex(Collection<T> data);

    /**
     * Retrieve data using the passed criteria.
     *
     * @param indexType how to retrieve the data
     * @param indexKey key for the data to retrieve
     * @return data retrieved
     */
    Collection<T> getData(IndexType indexType, String indexKey);

    /**
     * Remove data too old from the index
     */
    void purge();

    /**
     * Return the number of days for which data is retained in the index.
     * @return number of days data is kept in the index
     */
    int getRetentionPeriod();
}
