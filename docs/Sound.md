
### Work with sound

The method `InAppStoryManager.getInstance().soundOn(boolean isSoundOn)` flag is responsible for on/off sound playback in stories (`true` - sound is on, `false` – sound is off). The default value of the flag is written in the `constants.xml` file in the `defaultMuted` variable (by default `true` - the sound is off) and can be reloaded. Please note that the `soundOn` value is set as `!defaultMuted` (it will be `false` by default). 
Example:
```
InAppStoryManager.getInstance().soundOn(true);
```
