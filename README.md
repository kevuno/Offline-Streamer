# Offline video streamer
This is a video player made in Java where a server can edit the playlist remotely with the use of a Websocket on real time.
The purpose of this project is to be able to play videos locally according to a list located in the computer without having to worry
about internet connection. The availability of an internet connection will be only needed whenever new videos (specified by
a list that the server sends) are available to download. Bandwidth/Internet connection is only used once every time there are new videos for the 
player or whenever there is a change to the playlist.
