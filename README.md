A number of vendors provide price information for traded instruments; a traded instrument will have different prices from different vendors.
Price data from each vendor is to be cached in a local data store and then distributed to interested downstream systems.
The cache will have services to allow clients to publish and retrieve data from the store.
Clients are interested in getting all prices from a particular vendor or prices for a single instrument from various vendors.
Prices older than 30 days must be removed from the cache.
