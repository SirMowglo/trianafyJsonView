package com.salesianostriana.dam.trianafy.dto.playlistDto;

import com.salesianostriana.dam.trianafy.dto.songDto.GetSongDto;
import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlaylistDtoConverter {
    public Playlist createPlaylistDtoToPlaylist(CreatePlaylistDto p){

        return new Playlist(
                p.getName(),
                p.getDescription()
        );
    }
    public GetPlaylistDto playlistToGetPlaylistDto(Playlist p){
        int nSongs;
        if(p.getSongs()== null){
            nSongs = 0;
        }else{
            nSongs = p.getSongs().size();
        }

        return GetPlaylistDto
                .builder()
                .id(p.getId())
                .name(p.getName())
                .numberOfSongs(nSongs)
                .build();
    }
}
