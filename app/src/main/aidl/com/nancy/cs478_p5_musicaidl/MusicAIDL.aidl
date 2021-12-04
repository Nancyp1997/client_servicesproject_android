// MusicAIDL.aidl
package com.nancy.cs478_p5_musicaidl;

// Declare any non-default types here with import statements


   interface MusicAIDL {
            String getSongUrl(int id);
                        Bundle getAllSongs();
                        Bundle getSingleSong(int index);
             }