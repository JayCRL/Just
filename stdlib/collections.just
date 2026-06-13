/*
 * Just 2.0 标准库 - 泛型集合
 *
 * 这些是用 Just 语言编写的标准库
 * 编译器会将它们编译到运行时
 */

// ==================== List<T> ====================

class List<T> {
    private T[] data;
    private int size;
    private int capacity;

    List() {
        this.capacity = 10;
        this.size = 0;
        this.data = new T[this.capacity];
    }

    List(int initialCapacity) {
        this.capacity = initialCapacity;
        this.size = 0;
        this.data = new T[this.capacity];
    }

    void add(T element) {
        if (this.size >= this.capacity) {
            this.resize();
        }
        this.data[this.size] = element;
        this.size++;
    }

    T get(int index) {
        if (index < 0 || index >= this.size) {
            // TODO: 抛出异常
            return null;
        }
        return this.data[index];
    }

    void set(int index, T element) {
        if (index < 0 || index >= this.size) {
            return;
        }
        this.data[index] = element;
    }

    int size() {
        return this.size;
    }

    bool isEmpty() {
        return this.size == 0;
    }

    void clear() {
        this.size = 0;
    }

    bool contains(T element) {
        for (int i : [0, this.size)) {
            if (this.data[i] == element) {
                return true;
            }
        }
        return false;
    }

    void remove(int index) {
        if (index < 0 || index >= this.size) {
            return;
        }

        for (int i : [index, this.size - 1)) {
            this.data[i] = this.data[i + 1];
        }
        this.size--;
    }

    private void resize() {
        this.capacity *= 2;
        T[] newData = new T[this.capacity];
        for (int i : [0, this.size)) {
            newData[i] = this.data[i];
        }
        this.data = newData;
    }

    // 高阶函数
    List<T> filter(bool predicate(T)) {
        List<T> result = new List<T>();
        for (int i : [0, this.size)) {
            T element = this.data[i];
            if (predicate(element)) {
                result.add(element);
            }
        }
        return result;
    }

    <R> List<R> map(R transformer(T)) {
        List<R> result = new List<R>();
        for (int i : [0, this.size)) {
            result.add(transformer(this.data[i]));
        }
        return result;
    }

    void forEach(void action(T)) {
        for (int i : [0, this.size)) {
            action(this.data[i]);
        }
    }

    // 索引访问
    T operator[](int index) {
        return this.get(index);
    }
}

// ==================== Map<K, V> ====================

class Map<K, V> {
    private const int INITIAL_CAPACITY = 16;
    private Entry<K, V>[] buckets;
    private int size;
    private int capacity;

    Map() {
        this.capacity = INITIAL_CAPACITY;
        this.size = 0;
        this.buckets = new Entry<K, V>[this.capacity];
    }

    void put(K key, V value) {
        int hash = this.hash(key);
        int index = hash % this.capacity;

        Entry<K, V> entry = this.buckets[index];
        while (entry != null) {
            if (entry.key == key) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }

        // 添加新条目
        Entry<K, V> newEntry = new Entry<K, V>(key, value);
        newEntry.next = this.buckets[index];
        this.buckets[index] = newEntry;
        this.size++;

        // 检查是否需要扩容
        if (this.size > this.capacity * 0.75) {
            this.resize();
        }
    }

    V get(K key) {
        int hash = this.hash(key);
        int index = hash % this.capacity;

        Entry<K, V> entry = this.buckets[index];
        while (entry != null) {
            if (entry.key == key) {
                return entry.value;
            }
            entry = entry.next;
        }

        return null;
    }

    bool containsKey(K key) {
        return this.get(key) != null;
    }

    void remove(K key) {
        int hash = this.hash(key);
        int index = hash % this.capacity;

        Entry<K, V> entry = this.buckets[index];
        Entry<K, V> prev = null;

        while (entry != null) {
            if (entry.key == key) {
                if (prev == null) {
                    this.buckets[index] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                this.size--;
                return;
            }
            prev = entry;
            entry = entry.next;
        }
    }

    int size() {
        return this.size;
    }

    bool isEmpty() {
        return this.size == 0;
    }

    private int hash(K key) {
        // 简化的哈希函数
        return key.hashCode();
    }

    private void resize() {
        int oldCapacity = this.capacity;
        Entry<K, V>[] oldBuckets = this.buckets;

        this.capacity *= 2;
        this.buckets = new Entry<K, V>[this.capacity];
        this.size = 0;

        // 重新哈希所有条目
        for (int i : [0, oldCapacity)) {
            Entry<K, V> entry = oldBuckets[i];
            while (entry != null) {
                this.put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }

    // 索引访问
    V operator[](K key) {
        return this.get(key);
    }
}

// Map 内部条目
class Entry<K, V> {
    K key;
    V value;
    Entry<K, V> next;

    Entry(K key, V value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }
}

// ==================== Set<T> ====================

class Set<T> {
    private Map<T, bool> map;

    Set() {
        this.map = new Map<T, bool>();
    }

    void add(T element) {
        this.map.put(element, true);
    }

    bool contains(T element) {
        return this.map.containsKey(element);
    }

    void remove(T element) {
        this.map.remove(element);
    }

    int size() {
        return this.map.size();
    }

    bool isEmpty() {
        return this.map.isEmpty();
    }
}

// ==================== Optional<T> ====================

class Optional<T> {
    private T value;
    private bool hasValue;

    Optional() {
        this.hasValue = false;
        this.value = null;
    }

    Optional(T value) {
        this.hasValue = true;
        this.value = value;
    }

    bool isPresent() {
        return this.hasValue;
    }

    T get() {
        if (!this.hasValue) {
            // TODO: 抛出异常
            return null;
        }
        return this.value;
    }

    T orElse(T defaultValue) {
        if (this.hasValue) {
            return this.value;
        }
        return defaultValue;
    }

    <R> Optional<R> map(R transformer(T)) {
        if (!this.hasValue) {
            return new Optional<R>();
        }
        return new Optional<R>(transformer(this.value));
    }
}

// ==================== Pair<A, B> ====================

class Pair<A, B> {
    public A first;
    public B second;

    Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }
}

// ==================== Result<T, E> ====================

class Result<T, E> {
    private T value;
    private E error;
    private bool isSuccess;

    Result(T value, E error, bool isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    static <T, E> Result<T, E> success(T value) {
        return new Result<T, E>(value, null, true);
    }

    static <T, E> Result<T, E> failure(E error) {
        return new Result<T, E>(null, error, false);
    }

    bool isSuccess() {
        return this.isSuccess;
    }

    bool isFailure() {
        return !this.isSuccess;
    }

    T getValue() {
        return this.value;
    }

    E getError() {
        return this.error;
    }
}
