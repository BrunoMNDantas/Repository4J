<h1>Repository4J</h1>

Repository4J is a repository utilitaries library.

  #### Gradle
  
```groovy
implementation group: 'com.github.brunomndantas', name: 'repository4j', version: '1.0.0'
```

#### Maven

```xml
<dependency>
   <groupId>com.github.brunomndantas</groupId>
   <artifactId>repository4j</artifactId>
   <version>1.0.0</version>
</dependency>

```

<h2>Table of Contents</h2>

- [Interface](#interface)
- [Implementations](#implementations)
- [MemoryRepository](#memoryrepository)
- [DiskRepository](#diskrepository)
  - [JsonDiskRepository](#jsondiskrepository)
- [CacheRepository](#cacherepository)
  - [ValidCacheRepository](#validcacherepository)
  - [TimedCacheRepository](#timedcacherepository)
- [CloneRepository](#clonerepository)
- [LoggerRepository](#loggerrepository)
  - [SimpleLoggerRepository](#simpleloggerrepository)
- [NullFreeRepository](#nullfreerepository)
- [ThreadSafeRepository](#threadsaferepository)
- [ValidatorRepository](#validatorrepository)

# Interface

The interface `IRepository` defines the contract for all repositories.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/IRepository.png" >
</p>

Every repository should implement the following methods:
- **getAll(): Collection\<E>** - Returns a `Collection` containing all entities stored on repository.
- **get(K key): E**  - Returns the entity represented by the parameter `key` or null if there is no entity for that `key`.
- **insert(E entity): void** - Stores the `entity` received as parameter on the repository. This method can throw `DuplicatedEntityException` if we try to insert an entity which its key is already stored on the repository.
- **update(E entity): void** - Updates the `entity` received as parameter. This method can throw `NonExistentEntityException` if the key of the `entity` is not present on the repository.
- **delete(K key): void** - Delete the entity with the `key` received as parameter. This method can throw `NonExistentEntityException` if the `key` is not present on the repository.

# Implementations

Let's assume that there is a class `Person` with two properties (`id` and `name`).
In this section, you will see the **usage** examples using this class just to illustrate how to create instances of these repositories for the `Person` entity.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/Person.png">
</p>

## MemoryRepository

`MemoryRepository` is an implementation of repository based on memory. This implementation will use a `Map` to store the entities.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/MemoryRepository.png">
</p>

#### Methods with relevant details
- **insert(E entity): void** - Throws `DuplicatedEntityException` if we try to insert an entity which its key is already stored on the repository.
- **update(E entity): void** - Throws `NonExistentEntityException` if the key of the `entity` is not present on the repository.
- **delete(K key): void** - Throws `NonExistentEntityException` if the `key` is not present on the repository.

#### Usage
```java
IRepository<String,Person> repository = new MemoryRepository<>(person -> person.id);
```

## DiskRepository

`DiskRepository` is an abstract implementation of repository that relies on the file system. This implementation will store each entity in a distinct file within the directory specified on the constructor. The file name of each entity will be constructed based on the key of the element (key.toString() + "." + fileExtension)

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/DiskRepository.png">
</p>

#### Methods with relevant details
- **serialize(E entity): String** - Convert the `entity` into a `String`.
- **deserialize(String entityAsString): E** - Converts the `String` into a entity.

### JsonDiskRepository

`JsonDiskRepository` is an implementation of `DiskRepository`  that will use Json format to store the entities.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/JsonDiskRepository.png">
</p>

#### Usage
```java
String directory = "./path/to/directory";
Function<Person,String> keyExtractor = person -> person.id;
IRepository<String,Person> repository = new JsonDiskRepository<>(directory, keyExtractor, Person.class);
```

## CacheRepository

`CacheRepository` provides a caching mechanism for repositories, enhancing performance by reducing direct interactions with the source repository. It wraps around two repositories: a **cache repository** and a **source repository**, ensuring the cache stays synchronized with the source. 

#### Methods with relevant details
- **getAll(): Collection\<E>** - This method will allways fetch all elements from source repository and insert them (if not present) on cache repository. 
- **get(K key): E** - This method will get the entity from cache repository, if not present it will fetch it from the source repository and insert it on the cache.
- **insert(E entity): void** - This operation will be reflected on both repositories (first on source then on cache).
- **update(E entity): void** - This operation will be reflected on both repositories (first on source then on cache).
- **delete(K key): void** - This operation will be reflected on both repositories (first on cache then on source).

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/CacheRepository.png">
</p>

#### Usage
```java
Function<Person,String> keyExtractor = person -> person.id;
IRepository<String,Person> diskRepository = createDiskRepository();
IRepository<String,Person> memoryRepository = createMemoryRepository();
IRepository<String,Person> repository = new CacheRepository<>(memoryRepository, diskRepository, keyExtractor);
```

### ValidCacheRepository

`ValidCacheRepository` is an abstract class that will ensure that before retrieving an element from the cache , it is first validated to confirm that the element is still valid.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/ValidCacheRepository.png">
</p>

#### Methods with relevant details
- **isValid(E entity): boolean** - Returns whether or not the cached `entity` is valid.

### TimedCacheRepository

`TimedCacheRepository` is an implementation of `ValidCacheRepository` that checks the validity of entities based on the time they are cached. If the cached time of an entity exceeds the `expirationTime`, the entity is considered not valid and will be fetched from the source repository.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/TimedCacheRepository.png">
</p>

#### Usage
```java
Function<Person,String> keyExtractor = person -> person.id;
IRepository<String,Person> diskRepository = createDiskRepository();
IRepository<String,Person> memoryRepository = createMemoryRepository();
IRepository<K,Entry<K>> metadataRepository = new MemoryRepository<>(TimedCacheRepository.Entry::getKey);
long expirationTime = 60 * 1000

IRepository<String,Person> repository = new TimedCacheRepository<>(memoryRepository, diskRepository, keyExtractor, metadataRepository, expirationTime);
```

## CloneRepository

`CloneRepository` is a wrapper repository that ensures that the entities sent to and received by the source repository will only be handled by the source repository itself. This implementation is helpful when we want to be able to change the objects returned by the repository without altering the repository's state.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/CloneRepository.png">
</p>

#### Methods with relevant details
- **getAll(): Collection\<E>** - Returns a copy of the `Collection` provided by source repository. Each element on the `Collection` will be a clone of the original element.
- **get(K key): E** - Returns a clone of the entity provided by source repository.
- **insert(E entity): void** - Sends a clone of the received `entity` to the source repository.
- **update(E entity): void** - Sends a clone of the received `entity` to the source repository.

#### Usage
```java
IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
Function<Person,Person> cloneFunction = person -> new Person(person.id, person.name);
IRepository<String,Person> repository = new CloneRepository<>(sourceRepository, cloneFunction);
```

## LoggerRepository

`LoggerRepository` is an abstract wrapper repository that logs all stages of each operation of source repository.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/LoggerRepository.png">
</p>

### SimpleLoggerRepository

`SimpleLoggerRepository` is an implementation of `LoggerRepository`  that will send a simple message on each event for the supplied `Consumer`.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/SimpleLoggerRepository.png">
</p>

Here are some examples for the supplied messages:
- Entering Get All
- Get returning entity for key:[key of element]
- Get returning null for key:[key of element]
- Leaving Insert with key:[key of element]
- Exception on Update with key:[key of element] message:[message of Exception]

#### Usage
```java
Function<Person,String> keyExtractor = person -> person.id;  
IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);  
IRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, System.out::println);
```

## NullFreeRepository

`NullFreeRepository` is a wrapper repository that ensures that null values are not accepted or returned by the source repository. This implementation is helpful when we are working with repositories that are not null safe.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/NullFreeRepository.png">
</p>

#### Methods with relevant details
- **getAll(): Collection\<E>** - If source repository returns null, an empty `Collection` will be returned instead, otherwise it will return the output of source repository.
- **get(K key): E** - Throws an `IllegalArgumentException` if `key` is null.
- **insert(E entity) : void** - Throws an `IllegalArgumentException` if `entity` is null.
- **update(E entity): void** - Throws an `IllegalArgumentException` if `entity` is null.
- **delete(K key): void** - Throws an `IllegalArgumentException` if `key` is null.

#### Usage
```java
IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
IRepository<String,Person> repository = new NullFreeRepository<>(sourceRepository);
```

## ThreadSafeRepository

`ThreadSafeRepository` is a wrapper repository that guarantees thread-safe access to the source repository. This implementation uses a `ReentrantReadWriteLock`, ensuring that multiple read operations can occur simultaneously, while permitting only one write operation at any given time.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/ThreadSafeRepository.png">
</p>

#### Usage
```java
IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);  
IRepository<String,Person> repository = new ThreadSafeRepository<>(sourceRepository);
```

## ValidatorRepository

`ValidatorRepository` is a wrapper repository that ensures that the entities stored on the source repository are validated before their storage.

<p align="center"> 
	<img src = "https://raw.githubusercontent.com/BrunoMNDantas/Repository4J/master/docs/ValidatorRepository.png">
</p>

#### Methods with relevant details
- **insert(E entity) : void** -  Validates the provided `entity` before sending it to source repository. If `validator` throws an Exception, it will be rethrown and source repository will not be called.
- **update(E entity): void** - Validates the provided `entity` before sending it to source repository. If `validator` throws an Exception, it will be rethrown and source repository will not be called.

#### Usage
```java
IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);  
IValidator<Person> validator = person -> {
	if(person.name == null) {
		throw new RepostoryException("Name property cannot be null!");
	}
};  
IRepository<String,Person> repository = new ValidatorRepository<>(sourceRepository, validator);
```