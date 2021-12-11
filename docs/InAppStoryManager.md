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

Other `Builder` parameters are optional.

Besides userId for user you can specify tags and placeholders. Tags used for targeting stories in `StoriesList` or onboardings. Placeholders - for replacing special variables in the story content.

You can also set amount of space which SDK can use for caching files (images, games, videos) with cacheSize parameter. In can be set with one of constants.
```
	CacheSize.SMALL = 15mb; 
	CacheSize.MEDIUM = 110mb; 
	CacheSize.LARGE = 210mb;
```

Parameter testKey allows you to test stories in moderation status. 

After initialization you can use `InAppStoryManager` class and its methods via `InAppStoryManager.getInstance()`.

### Methods

InAppStoryManager class contains static and non-static methods

#### Static methods

| Method                                        | Return type		| Description							|
|-----------------------------------------------|-----------------------|---------------------------------------------------------------|
| `closeStoryReader()`				| void			| Use to force close stories reader (for example in button click callbacks) |
| `destroy()`			                | void			| Use to clear InAppStoryManager if you want to stop it's background work |
| `logout()`                       		| void			| Same as `destroy()`						|
| `getLibraryVersion()`				| Pair<String, Integer>	| returns version name and version code				|
| `isNull()`      				| boolean		| use to check if InAppStoryManager is not created		|

#### Non-static methods
InAppStoryManager is a singleton. You can use it's non-static methods like this:
```
	InAppStoryManager.getInstance().<method>
```

| Method                                        	| Return type		| Description							|
|-------------------------------------------------------|-----------------------|---------------------------------------------------------------|
| `setTags(ArrayList<String> tags)`			| void			| Use to force close stories reader (for example in button click callbacks) |
| `addTags(ArrayList<String> tags)`			| void			| Use to clear InAppStoryManager if you want to stop it's background work |
| `removeTags(ArrayList<String> tags)`			| void			| Same as `destroy()`						|
| `setPlaceholders(Map<String, String> placeholders)`	| void			| returns version name and version code				|
| `setPlaceholder(String key, String value)`		| void			| returns version name and version code				|
| `setTestKey(@NonNull String testKey)`			| void			| returns version name and version code				|
| `setUserId(@NonNull String userId)`			| void			| returns version name and version code				|
| `showOnboardingStories(Context context, AppearanceManager manager)`	| void			| returns version name and version code				|
| `showOnboardingStories(List<String> tags, Context context, AppearanceManager manager)`	| void			| returns version name and version code	|
| `showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback)`	| void	| returns version name and version code		|
| `clearCache()`      					| boolean		| use to check if InAppStoryManager is not created		|

>**Attention!**  
>Method `setUserId(@NonNull String userId)` automatically refresh all storiesList instances in application if userId is changed. It may lead to events generation or callback responses. 

Besides this methods there are some callbacks setters

### Callbacks
