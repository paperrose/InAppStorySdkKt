## InAppStoryManager
Main SDK class. Must be initialized before loading stories from any point.

### Initialization

InAppStoryManager can be initialized from any point with `Context` access (`Application`, `Activity`, `Fragment`, etc.) through `Builder` pattern
```
  new InAppStoryManager.Builder()
      .apiKey(apiKey) //String
      .context(context) //Context
      .userId(userId) //String
      .tags(tags) //ArrayList<String>
      .placeholders(placeholders) //Map<String, String>
      .cacheSize(cacheSize) //int, has defined constants
      .testKey(testKey) //String
      .create();
```

>**Attention!**  
>Method `create()` can generate `DataException` if SDK was not initialized. Strictly recommend to catch `DataException` for additional info.

Context and userId - is not optional parameters. UserId can't be longer than 255 characters. Api key is a SDK authorization key. It can be set through `Builder` or in `values/constants.xml`
```
	<string name="csApiKey">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
```
Besides userId you can specify tags and placeholders for user. Tags used for targeting stories in `StoriesList` or onboardings. Placeholders - for replacing special variables in the story content.

You can also set amount of space which SDK can use for caching files (images, games, videos) with cacheSize parameter. In can be set with one of constants.
```
	CacheSize.SMALL = 15mb; 
	CacheSize.MEDIUM = 110mb; 
	CacheSize.LARGE = 210mb;
```

After initialization you can use `InAppStoryManager` class and its methods via `InAppStoryManager.getInstance()`.

### Methods

InAppStoryManager class contains static and non-static methods

#### Static methods

#### Non-static methods

### Callbacks
