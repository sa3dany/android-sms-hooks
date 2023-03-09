# [![SMS Hooks](https://repository-images.githubusercontent.com/284747433/11358f80-df97-11ea-900f-8fb2b7a6f620)](https://wolfia.com/magic-link/05424ec8-8ecb-4b2d-9458-61225da7f47c?emulatorSessionId=2dbb0dac-3a76-4415-8e1e-bb3cfba812f8)

## Inactivity Notice
Due to time constrains, I am not able to work on SMS Hooks at the time being. Another alternative app is [Android Incoming SMS Gateway Webhook](https://github.com/bogkonstantin/android_income_sms_gateway_webhook).


SMS Hooks is an app that listens for incoming SMS messages then sends a `POST` request to your webhook URL containing the SMS details.

> **Warning**:
> Since SMS messages can contain sensitive information, this app only allows HTTPS protocol URLs.

## Online Demo

Try this app now using your browser:

[![Wolfia's online Android emulator](art/wolfia-button.webp)](https://wolfia.com/magic-link/05424ec8-8ecb-4b2d-9458-61225da7f47c?emulatorSessionId=2dbb0dac-3a76-4415-8e1e-bb3cfba812f8)

## Request Body

For each SMS message a POST request is made with a JSON body containing the SMS message and its metadata.

```jsonc
{
  "body": "<SMS Message>",
  "from": "<SMS Sender>",
  "timestamp": 1598773970403, // UTC Milliseconds
}
```

## Receiving SMS Webhooks in Google Sheets

If you have a Google account you can combine Google Sheets with Google Apps
Script to create a simple webhook server that responds to the SMS webhooks and
appends each SMS as a new row in the spreadsheet.

1. [Create](https://docs.google.com/spreadsheets/create) a spreadsheet in Google
   Sheets.
2. Create a sheet-bound Google Apps Script by selecting
   **Extensions > Apps Script** from the spreadsheet.
3. This should open the newly created Google Apps Script project. Inside the
   `Code.gs` file delete any existing code.
4. Add the following function to the file:

    ```javascript
    /**
     * This function is executed when a POST request is made to the published
     * script URL. It appends the SMS details as a row in first sheet in the
     * spreadsheet bound to the script.
     *
     * For documentation about the request parameter `e` please see:
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

5. Click the **Deploy** button and select **New Deployment** from the script.
6. For deployment type select **Web App**
7. Change `Who has access to the app:` to **"Anyone"**
   > **Warning**: This means anyone with the URL can perform a POST request to this script
8. Click **Deploy** then complete the authorization flow.
9. Use the generated web app URL in SMS Hooks as the webhook server URL.

## Screenshots

![App settings](screenshots/screenshot.png "App settings")

## Credits

- Katerina Limpitsouni's [unDraw](https://undraw.co/) for the vector illustrations
- [Wolfia](https://www.wolfia.com/) for the live APK demo!
