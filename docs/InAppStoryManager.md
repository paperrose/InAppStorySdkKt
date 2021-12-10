## InAppStoryManager
Main SDK class. Must be initialized before loading stories from any point.

### Initialization

InAppStoryManager can be initialized from any point with `Context` access (`Application`, `Activity`, `Fragment`, etc.) through `Builder` pattern
```
  new InAppStoryManager.Builder()
      .apiKey(apiKey) //String
		  .context(context) //Context
		  .userId(userId) //String
      .create();
```

>**Attention!**  
>Method `create()` can generate `DataException` if SDK was not initialized. Strictly recommend to catch `DataException` for additional info.

Context and userId - is not optional parameters. Api key is a SDK authorization key. It can be set through `Builder` or in `values/constants.xml`
```
	<string name="csApiKey">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</string>
```

You can also specify another SDK settings for `InAppStoryManager.Builder`

After initialization you can use `InAppStoryManager` class via `InAppStoryManager.getInstance()`.


### Methods

### Callbacks
