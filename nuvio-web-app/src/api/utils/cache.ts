type CacheEntry = {
    response: any;
    timestamp: number;
};

const CACHE_TTL = 1000 * 60 * 5;

function createApiCache() {
    const cache = new Map<string, CacheEntry>();

    function getKey(config: any): string {
        const { method, url, params, data } = config;
        const paramsStr = JSON.stringify(params || {});
        const dataStr = JSON.stringify(data || {});
        return `${method}:${url}?params=${paramsStr}&data=${dataStr}`;
    }

    function get(config: any): any | null {
        const key = getKey(config);
        const entry = cache.get(key);

        if(entry && Date.now() - entry.timestamp < CACHE_TTL) {
            return entry.response;
        }

        return null;
    }

    function set(config: any, response: any): void {
        const key = getKey(config);
        cache.set(key, {
            response,
            timestamp: Date.now(),
        });
    }

    function clear(): void {
        cache.clear();
    }

    function debug() {
        console.log("API Cache Contents:");
        for (const [key, value] of cache.entries()) {
            console.log(key, value);
        }
    }

    return { get, set, clear, debug };
}

export const apiCache = createApiCache();