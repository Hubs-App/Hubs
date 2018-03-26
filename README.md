Hubs is a content crawler application on Android. It provides apis to crawl web content and display data. So you can use them to make your own crawler plugin (or 3rd party client) for any web service. And the most important thing is that with the help of these apis, you can also block the ads, clean up the content, and relayout the page.

## Installing Hubs

We call the crawler plugin 'Hub'. You can browse Hubs in our Hub repository. Click the link of Hub on the webpage to install it directly, or download it to your mobile to install it manually.

## Making a Hub

You can make your own Hub with lua script. We provide a simple [example](test_hub) that crawls articles from the [jandan.net](http://jandan.net/). You can run the [test_hub/debug.py](test_hub/debug.py) script to install it to your client application and see the effect.

### Config script

Firstly, you need to create a `config.lua` in the Hub's root directory. It need to include all the below necessary configs:

```lua
NAME="xxx"  -- The display name.
ID="xxx.xxx.xxx"  -- The identifier of the Hub.
TYPE="article"  -- Only support article type now.
VERSION="x.x"  -- The version.
ENTRY="xxx.lua"  -- The entry script.
```

When the client application try to install a `.hub` file, it will show a reinstalling alert if the target Hub has a ID the same with and a VERSION different with one of the installed Hubs.

### Entry script

The entry script need to include all the callback functions that the client will call. Such as the `getItems(page)` which the client will call to fetch the content (articles/images...). 

### Accessing Java classes

In the lua script, you can use the `luajava` lib to access Java classes. Check the [`HubsClassLoader.java`](app/src/main/java/cn/nekocode/hubs/luaj/sandbox/HubsClassLoader.java) to see which classes are accessible.

### Debuging

You can use the [test_hub/debug.py](test_hub/debug.py) script to install/reinstall a Hub to your mobile without packing it. It means that if you change some code of your Hub on your computer, you can run the script to see the effect immediately on your mobile. Watch the videos below to see how it works.

<img src="image/video.png" width="320" height="180"/> Youtube: [Click Here](https://www.youtube.com/watch?v=piOAlxkK4CQ)    Bilibili: [Click Here](http://www.bilibili.com/video/av18368324/)

### Packing

Lastly, you need to pack all the lua scripts and html files to a zip file. And then change the file extension to `.hub`.
