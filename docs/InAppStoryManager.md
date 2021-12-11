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

| Method                                        	| Description							|
|-------------------------------------------------------|---------------------------------------------------------------|
| `setTags(ArrayList<String> tags)`			| Set or replace tags list 					|
| `addTags(ArrayList<String> tags)`			| Add tags to current tags list 				|
| `removeTags(ArrayList<String> tags)`			| Remove passed tags from current tags list			|
| `setPlaceholders(Map<String, String> placeholders)`	| Set or replace placeholders list			|
| `setPlaceholder(String key, String value)`		| Set placeholder to the placeholders list. If you pass null, then the placeholder will be removed|
| `Map<String, String> getPlaceholders()`		| returns current placeholder list				|
| `setTestKey(@NonNull String testKey)`			| Set testKey to test stories in moderation status		|
| `setUserId(@NonNull String userId)`			| Change current userId. UserId can't be null or longer than 255 characters	|
| `showOnboardingStories(Context context, AppearanceManager manager)`	| load and show reader with onboarding stories. Pass context from screen where you want to show reader. AppearanceManager can be null (common AppearanceManager will be used in this case) |
| `showOnboardingStories(List<String> tags, Context context, AppearanceManager manager)`	| same as previous, but you can specify tags for onboardings list |
| `showStory(String storyId, Context context, AppearanceManager manager)`	| load and show story in stories reader by it's id.  |
| `clearCache()`      					| use to check if InAppStoryManager is not created		|

>**Attention!**  
>Method `setUserId(@NonNull String userId)` automatically refresh all storiesList instances in application if userId is changed. It may lead to events generation or callback responses. 

Besides this methods there are some callbacks setters

### Callbacks

####
```
InAppStoryManager.getInstance().setUrlClickCallback(UrlClickCallback callback);
```

The `UrlClickCallback` interface contains the `onUrlClick(String url)` method, which must be overrided.
Example:
```
InAppStoryManager.getInstance().setUrlClickCallback(new InAppStoryManager.UrlClickCallback() {
    @Override
    public void onUrlClick(String link) {
        Toast.makeText(context, link, Toast.LENGTH_LONG).show();
    }
});
```
If you need to close the reader when the handler is triggered, you need to call static method ` InAppStoryManager.closeStoryReader()` in `onUrlClick`:
```
InAppStoryManager.getInstance().setUrlClickCallback(new InAppStoryManager.UrlClickCallback() {
    @Override
    public void onUrlClick(String link) {
        InAppStoryManager.closeStoryReader();
    }
});
```
The SDK has a default link handler:
```
Intent i = new Intent(Intent.ACTION_VIEW);
i.setData(Uri.parse(object.getLink().getTarget()));
startActivity(i);
```

It is not used during overriding, so if you want to keep the processing of links that are not required by the application in their default form, then you need to take them into account when overriding.

You can also override the handler for clicking on the sharing button as follows:
```
InAppStoryManager.getInstance().setShareCallback(new InAppStoryManager.ShareCallback() {
    @Override
    public void onShare(String url, String title, String description, String shareId) {
        doAction(url, title, description);
    }
});
```

```
InAppStoryManager.getInstance().setShowStoryCallback(ShowStoryCallback showStoryCallback); 


public interface ShowStoryCallback {
        void showStory(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   SourceType source);
}
```

2) 
```
InAppStoryManager.getInstance().setCloseStoryCallback(CloseStoryCallback closeStoryCallback); 
//equivalent to 'CloseStory' event

public interface CloseStoryCallback {

        void closeStory(int id,
                    String title,
                    String tags,
                    int slidesCount,
                    int index,
                    CloseReader action,
                    SourceType source);
}
```

3) 
```
InAppStoryManager.getInstance().setCallToActionCallback(CallToActionCallback callToActionCallback); 
//equivalent to 'CallToAction' event

public interface CallToActionCallback {
        void callToAction(int id,
                      String title,
                      String tags,
                      int slidesCount,
                      int index,
                      String link,
                      ClickAction action);
}
```

4) 
```
InAppStoryManager.getInstance().setShowSlideCallback(ShowSlideCallback showSlideCallback); 
//equivalent to 'ShowSlide' event

public interface ShowSlideCallback {
        void showSlide(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   int index);
}
```

5) 
```
InAppStoryManager.getInstance().setClickOnShareStoryCallback(ClickOnShareStoryCallback clickOnShareStoryCallback); 
//equivalent to 'ClickOnShareStory' event

public interface ClickOnShareStoryCallback {
        void shareClick(int id,
                    String title,
                    String tags,
                    int slidesCount,
                    int index);
}
```

6) 
```
InAppStoryManager.getInstance().setLikeDislikeStoryCallback(LikeDislikeStoryCallback likeDislikeStoryCallback); 
//equivalent to 'LikeStory' and 'DislikeStory' event

public interface LikeDislikeStoryCallback {
        void likeStory(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   int index,
                   boolean value);

        void dislikeStory(int id,
                      String title,
                      String tags,
                      int slidesCount,
                      int index,
                      boolean value);
}
```

7) 
```
InAppStoryManager.getInstance().setFavoriteStoryCallback(FavoriteStoryCallback favoriteStoryCallback); 
//equivalent to 'FavoriteStory' event

public interface FavoriteStoryCallback {
        void favoriteStory(int id,
                       String title,
                       String tags,
                       int slidesCount,
                       int index,
                       boolean value);
}
```

8) 
```
InAppStoryManager.getInstance().setSingleLoadCallback(SingleLoadCallback singleLoadCallback) ; 
//equivalent to 'SingleLoad' event

    
public interface SingleLoadCallback {
        void singleLoad(String storyId);
}
```

9) 
```
InAppStoryManager.getInstance().setOnboardingLoadCallback(OnboardingLoadCallback onboardingLoadCallback); 
//equivalent to 'OnboardingLoad' event

public interface OnboardingLoadCallback {
        void onboardingLoad(int count);
}
```

10) 
```
InAppStoryManager.getInstance().setErrorCallback(ErrorCallback errorCallback); 
//equivalent to events that send different errors
//can be set with custom implementation or with ErrorCallbackAdapter class

public interface ErrorCallback {
        void loadListError();
        void loadOnboardingError();
        void loadSingleError();
        void cacheError();
        void readerError();
        void emptyLinkError();
        void sessionError();
        void noConnection();
}
```

11) 
```
InAppStoryManager.getInstance().setGameCallback(GameCallback gameCallback); 
//equivalent to 'StartGame', 'CloseGame' and 'FinishGame' events
//can be set with custom implementation or with GameCallbackAdapter class

public interface GameCallback {
        void startGame(int id,
                       String title,
                       String tags,
                       int slidesCount,
                       int index);

        void finishGame(int id,
                        String title,
                        String tags,
                        int slidesCount,
                        int index,
                        String result);

        void closeGame(int id,
                       String title,
                       String tags,
                       int slidesCount,
                       int index);
}
```
Enums used in methods:
```
public enum SourceType {
    SINGLE, ONBOARDING, LIST, FAVORITE
}    

public enum CloseReader {
    AUTO, CLICK, SWIPE, CUSTOM
}

public enum ClickAction {
    BUTTON, SWIPE, GAME
}
```
