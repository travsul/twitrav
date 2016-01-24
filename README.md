# twitrav

Prereqs:
* scala
* sbt

A parser/analytics system for twitter streams.

Requires a secrets.json file in the root project directory that looks like this:

```
{
        "consumerKey": "...",
        "consumerSecret": "...",
        "accessToken": "...",
        "accessTokenSecret": "..."
}
```

Once this json file is in you can ```sbt``` in the root directory.
Once the sbt console is up (this will be noted by a ```> ```) you can run it by running ```re-start```.
* This will put the API up on `127.0.0.1:8080`
* You should also see these message:
 * ```Connection established.```
 * ```Receiving status stream.```

To end the API just type ```re-stop``` and exit sbt with ```exit```
