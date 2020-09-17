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

Recieving SMS Webhooks in Google Sheets
---------------------------------------
If you have a Google account you can combine Google Sheets with Google Apps
Script to create a simple webhook server that responds to the SMS webhooks and
appends each SMS as a new record in the spreadsheet.

1. [Create](https://docs.google.com/spreadsheets/create) a spreadsheet in Google
   Sheets.
2. Create a sheet-bound Google Apps Script by selecting
   **Tools > Script Editor** from the spreadsheet.
3. This should open the newly created Google Apps Script project. Inside the
   `Code.gs` file delete any existing code.
4. Add the following function to the file:
```javascript
/**
 * This function is executed when a POST request is made to the published
 * script URL. It appends the SMS details as a record in first sheet in the
 * spreadhseet bound to the script.
 *
 * For documentation about the request paramater `e` please see:
 * https://developers.google.com/apps-script/guides/web#request_parameters
 */
function doPost(e) {
  let sms = JSON.parse(e.postData.contents);
  let sheet = SpreadsheetApp.getActive().getSheets()[0];
  sheet.appendRow([sms.timestamp, sms.from, sms.body]);
  return ContentService
    .createTextOutput(JSON.stringify({}))
    .setMimeType(ContentService.MimeType.JSON);
}
```
5. Select **Publish > Deploy as Web App** from the script.
6. Change `Who has access to the app:` to **"Anyone, even anonymous"**
7. Click **Deply** then complete the authorization flow.
8. Use the generated web app URL in SMS Hooks as the webhook server URL.

Screenshots
-----------

![App settings](screenshots/screenshot.png "App settings")
