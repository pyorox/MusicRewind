# MusicRewind
 Generates a summary of your top artists/songs streamed through YouTube Music (for those jealous of Spotify's "Wrapped" feature).

# Instructions
 **These instructions are kinda lengthy but you need to make sure to follow every step correctly**, otherwise this thing won't work. Please read everything carefully.
 1. Download the [latest MusicRewind release](https://github.com/pyorox/MusicRewind/releases) and extract it somewhere.
 2. Head to [Google Takeout](https://takeout.google.com/settings/takeout).
 3. Make sure the right account is selected on the top right corner. You'll want to pick the specific account you use with YouTube Music, which may be a brand account and not your main Google account.
 4. Under "create a new export", hit "deselect all".
 5. Scroll down until you find "YouTube and YouTube Music". Select this item by clicking the checkbox.
 6. Click "Multiple formats", change the history format to JSON and click OK.
 7. Click "All YouTube data included" and click "deselect all". Then, select only "history" and click OK.
 8. Proceed to the next step and finalize your data export request.
 9. Eventually you'll get an email with a link to download a ZIP file. Download it.
 10. Within the ZIP file there will be a "history" folder with a file in it called "watch-history.json". Extract that file somewhere (for your own convenience, extract it to the same folder where you extracted MusicRewind).
 11. Run MusicRewind. If you have Java installed, you can double-click the JAR and run it directly. If you're on windows and don't have java installed, you can run the EXE instead.
 12. Adjust the options to your liking and click "Rewind!". This will generate an HTML file and then open it in your default browser. Optionally, take a screenshot and post it to social media. \#YTMusicRewind

# Settings
 Optionally, you may also customize a few settings, namely:
 - The year to "rewind" (the current year will be filled in by default).
 - The number of lines to include (e.g., 10 will make it a top 10). If your history doesn't have enough artists or songs to fill all the spots, the list will be cut short.
 - Show totals, if checked, will include the total number of artists and songs played as well as how many times each individual artist and song was played.

# Limitations
 This program is limited to working with the data contained in the history file provided by Google Takeout. There are mainly two problems associated with that, namely:
 - For some reason, some entries only have a URL as the title and no actual song title or artist information; those entries are ignored.
 - When it comes to files uploaded by the user, the entries contain the name of the song but not the artist. So, in these cases, the artist is ignored.

# Building
 If you want to build it yourself from the source code, you'll need the [JSON-Java](https://github.com/stleary/JSON-java) and [Apache Commons IO](https://commons.apache.org/proper/commons-io/download_io.cgi) packages.
