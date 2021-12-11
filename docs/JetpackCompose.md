## Add StoriesList via Jetpack Compose

`StoriesList` extends RecyclerView class and available like Compose component. If you use Jetpack Compose in your project, instead you can add `StoriesList` like [AndroidView](https://developer.android.com/jetpack/compose/interop/interop-apis#views-in-compose)

```
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)   
        setContent {
            ComposeAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    storiesList()
                }
            }
        }
    }
}

@Composable
fun storiesList() {
    AndroidView(factory = { ctx ->
        StoriesList(ctx).apply {
            setAppearanceManager(AppearanceManager().csHasFavorite(true))
            setOnFavoriteItemClick { Toast.makeText(ctx, "fav click", Toast.LENGTH_SHORT).show() }
            loadStories()
        }
    })
}              
```
