![SMS Hooks](https://repository-images.githubusercontent.com/284747433/11358f80-df97-11ea-900f-8fb2b7a6f620)
============================================================================================================

SMS Hooks is an app that listens for incoming SMS messages then sends a `POST` request to your webhook URL containing the SMS details.

> ⚠️ **Warning:** Since SMS messages can contain sensetive information, this app only alows HTTPS protocol URLs. 

Request Body
------------
For each SMS message a POST request is made with a JSON body containing the SMS message and its metadata.
```jsonc
{
  "body": "<SMS Message>",
  "from": "<SMS Sender>",
  "timestamp": 1598773970403, // Milliseconds
}
```

Screenshots
-----------

![App settings](screenshots/screenshot.png "App settings")
