Android SMS Webhooks
====================

Sends a POST request to a custom endpoint each time you receive an SMS message.

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
