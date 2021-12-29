## Single stories
    
SDK allows to open one story by its id or slug.
    
```
InAppStoryManager.getInstance().showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback /*optional, may be null*/);

interface IShowStoryCallback {
    void onShow(); //Calls after loading data about story from server
    void onError(); //Calls if loading fails
}
```

In case of a successful / unsuccessful attempt to load stories, events are raised that the developer can subscribe to change the states of any external elements in the application:
```
SingleLoad - sent when loading a single story by id (by `InAppStoryManager.getInstance().showStory` method). 
SingleLoadError - sent when loading a single story by id in case of some error. 
```

The function allows you to load all stories, including those that are not in the stories list returned to the user. 
